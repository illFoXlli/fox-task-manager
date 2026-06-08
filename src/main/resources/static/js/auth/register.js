import {
    hideAuthAlert,
    normalizeLogin,
    showAuthAlert,
    validateMinLength,
    validatePasswordMatch
} from './form-validation.js?v=1';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    const loginInput = document.getElementById('login');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const alertElement = document.getElementById('registerAlert');
    const telegramButton = document.getElementById('telegramRegisterButton');
    const telegramMessage = document.getElementById('telegramMessage');

    if (!form || !loginInput || !passwordInput || !confirmPasswordInput) {
        return;
    }

    loginInput.addEventListener('input', () => {
        loginInput.value = normalizeLogin(loginInput.value);
        validateMinLength(loginInput);
        hideAuthAlert(alertElement);
    });

    passwordInput.addEventListener('input', () => {
        validateMinLength(passwordInput);

        if (confirmPasswordInput.value.length > 0) {
            validatePasswordMatch(passwordInput, confirmPasswordInput);
        }

        hideAuthAlert(alertElement);
    });

    confirmPasswordInput.addEventListener('input', () => {
        validatePasswordMatch(passwordInput, confirmPasswordInput);
        hideAuthAlert(alertElement);
    });

    form.addEventListener('submit', (event) => {
        event.preventDefault();

        const isLoginValid = validateMinLength(loginInput);
        const isPasswordValid = validateMinLength(passwordInput);
        const isConfirmPasswordValid = validatePasswordMatch(passwordInput, confirmPasswordInput);

        if (!isLoginValid || !isPasswordValid || !isConfirmPasswordValid) {
            showAuthAlert(alertElement, 'Перевір дані для реєстрації.');
            return;
        }

        showAuthAlert(
            alertElement,
            'API реєстрації ще не підключено. Наступним етапом додамо backend-логіку.'
        );
    });

    if (telegramButton && telegramMessage) {
        telegramButton.addEventListener('click', () => {
            telegramMessage.classList.toggle('auth-note--hidden');
        });
    }
});
