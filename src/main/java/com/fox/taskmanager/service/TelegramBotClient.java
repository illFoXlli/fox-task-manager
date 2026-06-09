package com.fox.taskmanager.service;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.TelegramProperties;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class TelegramBotClient {

    private static final String TELEGRAM_API_BASE_URL = "https://api.telegram.org/bot";
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotClient.class);

    private final RestClient restClient;
    private final TelegramProperties telegramProperties;

    public TelegramBotClient(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
        this.restClient = RestClient.create();
    }

    public void sendConfirmationMessage(Long chatId, String token) {
        post("sendMessage", Map.of(
                "chat_id", chatId,
                "text", "Підтвердь вхід у Your Notes",
                "reply_markup", Map.of(
                        "inline_keyboard", List.of(
                                List.of(Map.of(
                                        "text", "Підтвердити вхід",
                                        "callback_data",
                                        AppConstants.Telegram.CONFIRM_CALLBACK_PREFIX + token))))));
    }

    public void answerCallback(String callbackQueryId, String text) {
        post("answerCallbackQuery", Map.of(
                "callback_query_id", callbackQueryId,
                "text", text));
    }

    public void sendReturnMessage(Long chatId, String returnUrl) {
        post("sendMessage", Map.of(
                "chat_id", chatId,
                "text", "Готово. Повернись у браузер, щоб відкрити кабінет.",
                "reply_markup", Map.of(
                        "inline_keyboard", List.of(
                                List.of(Map.of(
                                        "text", "Повернутися в кабінет",
                                        "url", returnUrl))))));
    }

    public void sendTextMessage(Long chatId, String text) {
        post("sendMessage", Map.of(
                "chat_id", chatId,
                "text", text));
    }

    private void post(String method, Object body) {
        try {
            restClient.post()
                    .uri(TELEGRAM_API_BASE_URL + telegramProperties.getBotToken() + "/" + method)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOGGER.warn("Telegram API call {} failed: {}", method, exception.getMessage());
        }
    }
}
