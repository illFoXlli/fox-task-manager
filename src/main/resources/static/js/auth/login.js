import {
    hideAuthAlert,
    normalizeLogin,
    showAuthAlert,
    validateMinLength
} from './form-validation.js?v=7';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const loginInput = document.getElementById('login');
    const passwordInput = document.getElementById('password');
    const alertElement = document.getElementById('loginAlert');
    if (!form || !loginInput || !passwordInput) {
        return;
    }

    const queryParams = new URLSearchParams(window.location.search);

    if (queryParams.has('telegramError')) {
        showAuthAlert(alertElement, 'Telegram не підтвердив вхід. Спробуйте ще раз.');
    }

    loginInput.addEventListener('input', () => {
        loginInput.value = normalizeLogin(loginInput.value);
        validateMinLength(loginInput);
        hideAuthAlert(alertElement);
    });

    passwordInput.addEventListener('input', () => {
        validateMinLength(passwordInput);
        hideAuthAlert(alertElement);
    });

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const isLoginValid = validateMinLength(loginInput);
        const isPasswordValid = validateMinLength(passwordInput);

        if (!isLoginValid || !isPasswordValid) {
            showAuthAlert(alertElement, 'Перевір логін і пароль.');
            return;
        }

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({
                    login: loginInput.value,
                    password: passwordInput.value
                })
            });

            const data = await response.json();

            if (!response.ok) {
                showAuthAlert(
                    alertElement,
                    data.message || 'Неправильний логін або пароль.'
                );
                return;
            }

            window.location.assign(data.redirectUrl || '/note/view');
        } catch (error) {
            showAuthAlert(
                alertElement,
                'Сервіс тимчасово недоступний. Спробуйте пізніше.'
            );
        }
    });

    const updateTelegramStartLink = (link) => {
        const url = new URL(link.href);

        url.searchParams.set('origin', window.location.origin);
        link.href = url.toString();
    };

    document.querySelectorAll('[data-telegram-start-link]').forEach((link) => {
        updateTelegramStartLink(link);

        link.addEventListener('click', () => {
            updateTelegramStartLink(link);
        });
    });
});
