$(document).ready(function () {
    $.datepicker.setDefaults($.datepicker.regional["ru"]);
    $("#date-from-picker").datepicker();
    $("#date-to-picker").datepicker();
});

function renderAnalyticsChart() {
    let dateComponents = $("#date-from-picker").val().split('.');
    const fromDate = new Date(dateComponents[2], dateComponents[1] - 1, dateComponents[0]);
    dateComponents = $("#date-to-picker").val().split('.');
    const toDate = new Date(dateComponents[2], dateComponents[1] - 1, dateComponents[0]);
    const step = (toDate.getDate() - fromDate.getDate()) / 10;

    const labels = [];
    const values = [];
    const urls = [];
    const dataUrl = "/rest/student/count?day=";

    for (let i = 0; i < 10; i++) {
        const day = fromDate;
        day.setDate(fromDate.getDate() + step * i);
        const dayRuFormatted = ('0' + day.getDate()).substr(-2, 2) + '.' + ('0' + (day.getMonth() + 1)).substr(-2, 2) + '.' + day.getFullYear();
        labels.push(dayRuFormatted);
        urls.push(dataUrl + dayRuFormatted);
    }

    $.get("/rest/student/count", {day: dayRuFormatted})
        .then(function (value) {
            values.push(value);
            showAnalyticsChart({labels: labels, values: values});
        });
}

function showAnalyticsChart(data) {
    const ctx = document.getElementById('analytics-chart').getContext('2d');
    const config = {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                data: data.values,
                label: "Студенты",
                borderColor: "#3e95cd",
                fill: false
            }]
        },
        options: {
            title: {
                display: true,
                text: 'Динамика роста количества студентов'
            }
        }
    };

    new Chart(ctx, config);
}

/* Russian (UTF-8) initialisation for the jQuery UI date picker plugin. */
/* Written by Andrew Stromnov (stromnov@gmail.com). */
(function (factory) {
    if (typeof define === "function" && define.amd) {

        // AMD. Register as an anonymous module.
        define(["../widgets/datepicker"], factory);
    } else {

        // Browser globals
        factory(jQuery.datepicker);
    }
}(function (datepicker) {

    datepicker.regional.ru = {
        closeText: "Закрыть",
        prevText: "&#x3C;Пред",
        nextText: "След&#x3E;",
        currentText: "Сегодня",
        monthNames: ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
        monthNamesShort: ["Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"],
        dayNames: ["воскресенье", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота"],
        dayNamesShort: ["вск", "пнд", "втр", "срд", "чтв", "птн", "сбт"],
        dayNamesMin: ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
        weekHeader: "Нед",
        dateFormat: "dd.mm.yy",
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix: ""
    };
    datepicker.setDefaults(datepicker.regional.ru);

    return datepicker.regional.ru;

}));
