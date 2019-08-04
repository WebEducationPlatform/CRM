// Небольшой скрипт для определения текущей открытой вкладки (нативщина, без зависимостей)

function setActiveTab() {
    var bars = document.getElementById('nav-bar-list'), i, j;

    if (!bars) return;

    var link;
    var curPathName = window.location.pathname;

    // Берём все табы по навигации и ищем текущий
    for (i = 0; i < bars.childNodes.length; i++) {
        if (bars.childNodes[i].nodeName === 'LI') {
            // Ищем среди дочерних
            for (j = 0; j < bars.childNodes[i].childNodes.length; j++) {
                link = bars.childNodes[i].childNodes[j];
                if (link.nodeName === 'A' && link.pathname === curPathName) {
                    link.classList.add('nav__active-tab');

                    // Отписываемся от событие
                    document.removeEventListener('DOMContentLoaded', setActiveTab);

                    return;
                }
            }
        }
    }
}
document.addEventListener('DOMContentLoaded', setActiveTab);