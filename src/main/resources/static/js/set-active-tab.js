// Небольшой скрипт для определения текущей открытой вкладки (нативщина, без зависимостей)

function setActiveTab() {
    var bars = document.getElementById('nav-bar-list'), i, j;

    if (!bars) return;

    var curPathName = window.location.pathname;

    $.each($("#nav-bar-list li a"), function () {
        if ($(this).attr("href").toLowerCase() === (curPathName.toLowerCase())) {
            if ($(this).attr("class") === 'dropdown-item') {
                $(this).parent().parent().parent().addClass('nav__active-tab');
            }else {
                $(this).addClass('nav__active-tab');
            }
            document.removeEventListener('DOMContentLoaded', setActiveTab);
            return;
        }
    });
}
document.addEventListener('DOMContentLoaded', setActiveTab);