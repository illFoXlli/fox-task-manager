document.addEventListener('DOMContentLoaded', () => {
    const logoutButtons = document.querySelectorAll('[data-logout-button]');

    logoutButtons.forEach((button) => {
        button.addEventListener('click', async (event) => {
            event.preventDefault();

            try {
                await fetch('/api/auth/logout', {
                    method: 'POST',
                    credentials: 'include'
                });

                window.location.assign('/login');
            } catch (error) {
                window.location.assign(button.getAttribute('href') || '/logout');
            }
        });
    });
});
