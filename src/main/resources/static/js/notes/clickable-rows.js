function isInteractiveElement(element) {
    return Boolean(element.closest('a, button, input, textarea, select, label, form'));
}

function openRow(row) {
    const targetUrl = row.dataset.rowHref;

    if (targetUrl) {
        window.location.href = targetUrl;
    }
}

function initClickableRow(row) {
    row.addEventListener('click', (event) => {
        if (!isInteractiveElement(event.target)) {
            openRow(row);
        }
    });

    row.addEventListener('keydown', (event) => {
        if (event.key !== 'Enter' && event.key !== ' ') {
            return;
        }

        if (isInteractiveElement(event.target)) {
            return;
        }

        event.preventDefault();
        openRow(row);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-row-href]').forEach(initClickableRow);
});
