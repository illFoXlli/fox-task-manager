function buildHomeUrl() {
    const protocol = window.location.protocol;
    const port = window.location.port;

    return port
        ? `${protocol}//home.fox.kh.ua:${port}`
        : `${protocol}//home.fox.kh.ua`;
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-home-link]').forEach((link) => {
        link.href = buildHomeUrl();
    });
});
