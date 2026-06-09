package com.fox.taskmanager.config;

public final class AppConstants {

    private AppConstants() {
    }

    public static final class Auth {

        public static final int LOGIN_MIN_LENGTH = 3;
        public static final int LOGIN_MAX_LENGTH = 100;
        public static final int PASSWORD_MIN_LENGTH = 6;
        public static final int PASSWORD_MAX_LENGTH = 255;
        public static final int EMAIL_MAX_LENGTH = 255;
        public static final String DEFAULT_LANGUAGE_CODE = "uk";
        public static final String LOGIN_FAILED_MESSAGE = "Неправильний логін або пароль";
        public static final String PASSWORD_MISMATCH_MESSAGE = "Паролі не збігаються";
        public static final String USER_EXISTS_MESSAGE = "Користувач з таким логіном вже існує";
        public static final String ACCOUNT_DISABLED_MESSAGE = "Обліковий запис вимкнено";
        public static final String ACCOUNT_LOCKED_MESSAGE = "Обліковий запис заблоковано";
        public static final String TELEGRAM_AUTH_FAILED_MESSAGE = "Не вдалося підтвердити Telegram";
        public static final String LOGIN_SUCCESS_MESSAGE = "Вхід виконано успішно";
        public static final String REGISTER_SUCCESS_MESSAGE = "Реєстрацію виконано успішно";
        public static final String REGISTER_FAILED_MESSAGE = "Перевір дані реєстрації";
        public static final String LOGOUT_SUCCESS_MESSAGE = "Вихід виконано успішно";

        private Auth() {
        }
    }

    public static final class Asset {

        public static final String VERSION = "7";

        private Asset() {
        }
    }

    public static final class Telegram {

        public static final int AUTH_SESSION_EXPIRATION_MINUTES = 10;
        public static final int AUTH_SESSION_RETENTION_DAYS = 1;
        public static final String START_PAYLOAD_PREFIX = "auth_";
        public static final String CONFIRM_CALLBACK_PREFIX = "confirm:";
        public static final String TELEGRAM_LINK_BASE_URL = "https://t.me/";

        private Telegram() {
        }
    }

    public static final class Cookie {

        public static final String ACCESS_TOKEN_NAME = "access_token";
        public static final String REFRESH_TOKEN_NAME = "refresh_token";
        public static final String DEVICE_ID_NAME = "device_id";
        public static final int DEVICE_ID_MAX_AGE_DAYS = 365;
        public static final String PATH = "/";
        public static final String SAME_SITE = "Lax";

        private Cookie() {
        }
    }

    public static final class DateTime {

        public static final String UI_PATTERN = "dd.MM.yyyy HH:mm";
        public static final String UTC_SUFFIX = "Z";

        private DateTime() {
        }
    }

    public static final class Note {

        public static final int TITLE_MAX_LENGTH = 255;
        public static final int CONTENT_MAX_LENGTH = 10000;
        public static final String NOT_FOUND_MESSAGE_PREFIX = "Note not found with id: ";

        private Note() {
        }
    }

    public static final class Route {

        public static final String ROOT = "/";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REGISTER = "/register";
        public static final String NOTE_LIST = "/note/list";
        public static final String NOTE_VIEW = "/note/view";

        private Route() {
        }
    }

}
