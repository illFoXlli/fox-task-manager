const SORT_STATE_PREFIX = 'fox-task-manager:sort:';
const SORT_ASC = 'asc';
const SORT_DESC = 'desc';

function getCellValue(row, key) {
    const cell = row.querySelector(`[data-sort-key="${key}"]`);

    if (!cell) {
        return '';
    }

    return cell.dataset.sortValue || cell.textContent || '';
}

function compareValues(firstValue, secondValue, key) {
    if (key === 'createdAt' || key === 'updatedAt') {
        return new Date(firstValue).getTime() - new Date(secondValue).getTime();
    }

    return firstValue.localeCompare(secondValue, 'uk', {
        numeric: true,
        sensitivity: 'base'
    });
}

function readStoredState(table) {
    const storageKey = table.dataset.sortStorageKey;

    if (!storageKey) {
        return null;
    }

    try {
        return JSON.parse(localStorage.getItem(`${SORT_STATE_PREFIX}${storageKey}`));
    } catch (error) {
        return null;
    }
}

function saveStoredState(table, state) {
    const storageKey = table.dataset.sortStorageKey;

    if (!storageKey) {
        return;
    }

    try {
        localStorage.setItem(`${SORT_STATE_PREFIX}${storageKey}`, JSON.stringify(state));
    } catch (error) {
        // Sorting still works for the current page even when storage is unavailable.
    }
}

function updateSortButtons(table, activeState) {
    table.querySelectorAll('.notes-sort-button').forEach((button) => {
        const isActive = button.dataset.sortKey === activeState.key;
        const sortDirection = isActive ? activeState.direction : 'none';

        button.classList.toggle('notes-sort-button--active', isActive);
        button.setAttribute('aria-sort', sortDirection);
    });
}

function sortTable(table, state, shouldSave) {
    const body = table.tBodies[0];

    if (!body || !state.key) {
        return;
    }

    const rows = Array.from(body.rows);
    const directionMultiplier = state.direction === SORT_DESC ? -1 : 1;

    rows.sort((firstRow, secondRow) => {
        const firstValue = getCellValue(firstRow, state.key);
        const secondValue = getCellValue(secondRow, state.key);

        return compareValues(firstValue, secondValue, state.key) * directionMultiplier;
    });

    rows.forEach((row) => body.append(row));
    updateSortButtons(table, state);

    if (shouldSave) {
        saveStoredState(table, state);
    }
}

function getInitialState(table) {
    return readStoredState(table) || {
        key: table.dataset.defaultSortKey || 'updatedAt',
        direction: table.dataset.defaultSortDirection || SORT_DESC
    };
}

function getNextDirection(button, currentState) {
    if (button.dataset.sortKey !== currentState.key) {
        return button.dataset.sortKey === 'updatedAt' || button.dataset.sortKey === 'createdAt'
            ? SORT_DESC
            : SORT_ASC;
    }

    return currentState.direction === SORT_ASC ? SORT_DESC : SORT_ASC;
}

function initTableSort(table) {
    let currentState = getInitialState(table);

    sortTable(table, currentState, false);

    table.querySelectorAll('.notes-sort-button').forEach((button) => {
        button.addEventListener('click', () => {
            currentState = {
                key: button.dataset.sortKey,
                direction: getNextDirection(button, currentState)
            };

            sortTable(table, currentState, true);
        });
    });
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-sort-table]').forEach(initTableSort);
});
