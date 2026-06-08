document.addEventListener('DOMContentLoaded', () => {
    const toggleButtons = document.querySelectorAll('[data-password-toggle]');

    toggleButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const inputId = button.dataset.passwordToggle;
            const input = document.getElementById(inputId);

            if (!input) {
                return;
            }

            const isPassword = input.type === 'password';

            input.type = isPassword ? 'text' : 'password';
            button.textContent = isPassword ? 'hide' : 'show';
            button.setAttribute(
                'aria-label',
                isPassword ? 'Сховати пароль' : 'Показати пароль'
            );
        });
    });
});
