export function normalizeLogin(value) {
    return value.trim().toLowerCase();
}

export function setFieldState(input, isValid, message) {
    const formGroup = input.closest('.form-group');
    const formMessage = formGroup ? formGroup.querySelector('.form-message') : null;

    input.classList.remove('is-valid', 'is-invalid');

    if (formGroup) {
        formGroup.classList.remove('is-valid', 'is-invalid');
    }

    if (input.value.trim().length === 0) {
        if (formMessage && message) {
            formMessage.textContent = message;
        }

        return;
    }

    input.classList.add(isValid ? 'is-valid' : 'is-invalid');

    if (formGroup) {
        formGroup.classList.add(isValid ? 'is-valid' : 'is-invalid');
    }

    if (formMessage && message) {
        formMessage.textContent = message;
    }
}

export function validateMinLength(input) {
    const minLength = Number(input.dataset.minLength || 0);
    const value = input.value.trim();
    const isValid = value.length >= minLength;

    setFieldState(
        input,
        isValid,
        isValid ? 'Виглядає добре.' : `Мінімум ${minLength} символи.`
    );

    return isValid;
}

export function validatePasswordMatch(passwordInput, confirmPasswordInput) {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    const isValid = confirmPassword.length > 0 && password === confirmPassword;

    setFieldState(
        confirmPasswordInput,
        isValid,
        isValid ? 'Паролі збігаються.' : 'Паролі не збігаються.'
    );

    return isValid;
}

export function showAuthAlert(alertElement, message) {
    if (!alertElement) {
        return;
    }

    alertElement.textContent = message;
    alertElement.classList.remove('auth-alert--hidden');
}

export function hideAuthAlert(alertElement) {
    if (!alertElement) {
        return;
    }

    alertElement.classList.add('auth-alert--hidden');
}
