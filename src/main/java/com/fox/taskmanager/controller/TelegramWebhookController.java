package com.fox.taskmanager.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.TelegramProperties;
import com.fox.taskmanager.dto.telegram.TelegramBotUser;
import com.fox.taskmanager.exception.AuthException;
import com.fox.taskmanager.service.TelegramAuthSessionService;
import com.fox.taskmanager.service.TelegramBotClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TelegramWebhookController {

    private final TelegramAuthSessionService telegramAuthSessionService;
    private final TelegramBotClient telegramBotClient;
    private final TelegramProperties telegramProperties;

    public TelegramWebhookController(
            TelegramAuthSessionService telegramAuthSessionService,
            TelegramBotClient telegramBotClient,
            TelegramProperties telegramProperties) {
        this.telegramAuthSessionService = telegramAuthSessionService;
        this.telegramBotClient = telegramBotClient;
        this.telegramProperties = telegramProperties;
    }

    @PostMapping("/api/telegram/webhook/{secret}")
    public ResponseEntity<Void> telegramWebhook(
            @PathVariable String secret,
            @RequestBody JsonNode update) {
        if (!isWebhookSecretValid(secret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        handleMessage(update.path("message"));
        handleCallbackQuery(update.path("callback_query"));

        return ResponseEntity.ok().build();
    }

    private void handleMessage(JsonNode message) {
        if (message.isMissingNode()) {
            return;
        }

        String text = message.path("text").asText("");

        if (!text.startsWith("/start " + AppConstants.Telegram.START_PAYLOAD_PREFIX)) {
            return;
        }

        String token = text
                .substring(("/start " + AppConstants.Telegram.START_PAYLOAD_PREFIX).length())
                .trim();
        TelegramBotUser user = parseUser(message.path("from"));
        Long chatId = message.path("chat").path("id").asLong();

        try {
            telegramAuthSessionService.registerStart(token, user);
            telegramBotClient.sendConfirmationMessage(chatId, token);
        } catch (AuthException exception) {
            telegramBotClient.sendTextMessage(
                    chatId,
                    "Це посилання вже не працює. Почни вхід заново на сайті.");
        }
    }

    private void handleCallbackQuery(JsonNode callbackQuery) {
        if (callbackQuery.isMissingNode()) {
            return;
        }

        String data = callbackQuery.path("data").asText("");

        if (!data.startsWith(AppConstants.Telegram.CONFIRM_CALLBACK_PREFIX)) {
            return;
        }

        String token = data.substring(AppConstants.Telegram.CONFIRM_CALLBACK_PREFIX.length());
        String callbackQueryId = callbackQuery.path("id").asText();
        TelegramBotUser user = parseUser(callbackQuery.path("from"));
        Long chatId = callbackQuery.path("message").path("chat").path("id").asLong();

        try {
            String returnUrl = telegramAuthSessionService.confirm(token, user);
            telegramBotClient.answerCallback(callbackQueryId,
                    "Вхід підтверджено.");
            telegramBotClient.sendReturnMessage(chatId, returnUrl);
        } catch (AuthException exception) {
            telegramBotClient.answerCallback(callbackQueryId,
                    "Посилання неактивне. Почни вхід заново.");
        }
    }

    private TelegramBotUser parseUser(JsonNode from) {
        TelegramBotUser user = new TelegramBotUser();
        user.setId(from.path("id").asLong());
        user.setFirstName(emptyToNull(from.path("first_name").asText(null)));
        user.setLastName(emptyToNull(from.path("last_name").asText(null)));
        user.setUsername(emptyToNull(from.path("username").asText(null)));

        return user;
    }

    private boolean isWebhookSecretValid(String secret) {
        String configuredSecret = telegramProperties.getWebhookSecret();

        return configuredSecret == null
                || configuredSecret.isBlank()
                || configuredSecret.equals(secret);
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
