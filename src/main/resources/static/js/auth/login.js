import {
    hideAuthAlert,
    normalizeLogin,
    showAuthAlert,
    validateMinLength
} from './form-validation.js?v=1';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const loginInput = document.getElementById('login');
    const passwordInput = document.getElementById('password');
    const alertElement = document.getElementById('loginAlert');
    const telegramButton = document.getElementById('telegramLoginButton');
    const telegramMessage = document.getElementById('telegramMessage');

    if (!form || !loginInput || !passwordInput) {
        return;
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

            window.location.assign(data.redirectUrl || '/note/list');
        } catch (error) {
            showAuthAlert(
                alertElement,
                'Сервіс тимчасово недоступний. Спробуйте пізніше.'
            );
        }
    });

    if (telegramButton && telegramMessage) {
        telegramButton.addEventListener('click', () => {
            telegramMessage.classList.toggle('auth-note--hidden');
        });
    }
});
