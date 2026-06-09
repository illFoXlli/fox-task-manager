function getClockParts(date) {
    const formatter = new Intl.DateTimeFormat(navigator.language || 'uk-UA', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        timeZoneName: 'short'
    });

    return formatter.formatToParts(date)
            .reduce((parts, part) => {
                parts[part.type] = part.value;
                return parts;
            }, {});
}

function formatClock(date) {
    const parts = getClockParts(date);
    const zone = parts.timeZoneName || Intl.DateTimeFormat()
            .resolvedOptions()
            .timeZone;

    return `${parts.day}.${parts.month}.${parts.year} | ${parts.hour}:${parts.minute}:${parts.second} ${zone}`;
}

function startClock(clock) {
    function tick() {
        clock.textContent = formatClock(new Date());
    }

    tick();
    window.setInterval(tick, 1000);
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-app-clock]').forEach(startClock);
});
