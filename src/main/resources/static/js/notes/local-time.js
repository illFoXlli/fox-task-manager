function normalizeUtcValue(value) {
    if (!value) {
        return '';
    }

    if (/[zZ]|[+-]\d{2}:\d{2}$/.test(value)) {
        return value;
    }

    return `${value}Z`;
}

function formatLocalTime(value) {
    const date = new Date(normalizeUtcValue(value));

    if (Number.isNaN(date.getTime())) {
        return '';
    }

    return new Intl.DateTimeFormat(navigator.language || 'uk-UA', {
        dateStyle: 'short',
        timeStyle: 'short',
        timeZoneName: 'short'
    }).format(date);
}

function initLocalTime(element) {
    const formattedTime = formatLocalTime(element.dataset.utc);

    if (formattedTime) {
        element.textContent = formattedTime;
        element.title = `${element.dataset.utc} UTC`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-utc]').forEach(initLocalTime);
});
