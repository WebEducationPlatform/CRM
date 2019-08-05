$(document).ready(function () {
    $.datepicker.setDefaults($.datepicker.regional["ru"]);
    $("#date-from-picker").datepicker().datepicker('setDate', '-1m');
    $("#date-to-picker").datepicker().datepicker('setDate', 'today');
    $('.status-checkbox').on('click', function () {
        $('#show-btn').prop('disabled', $('.status-checkbox:checked:enabled').length < 1);
    });
});

function renderAnalyticsChart() {
    let dateComponents = $("#date-from-picker").val().split('.');
    const fromDate = new Date(dateComponents[2], dateComponents[1] - 1, dateComponents[0]);
    dateComponents = $("#date-to-picker").val().split('.');
    const toDate = new Date(dateComponents[2], dateComponents[1] - 1, dateComponents[0]);

    if (toDate.getTime() <= fromDate.getTime()) {
        alert("Неверно заданы временные рамки");
        return;
    }

    const maxSteps = 15;
    let labels = [];
    let values = [];
    let dates = [];
    let statuses = [];
    let daysSelected = (toDate.getTime() - fromDate.getTime()) / (60 * 60 * 24 * 1000);
    let numberOfSteps = ((maxSteps < daysSelected) ? maxSteps : daysSelected);
    for (let i = 0; i <= numberOfSteps; i++) {
        const day = new Date();
        day.setTime(fromDate.getTime() + (toDate.getTime() - fromDate.getTime()) / numberOfSteps * i);
        const dayRuFormatted = formatRuDate(day);
        dates.push(dayRuFormatted);
    }

    $.each($('.status-checkbox:checked:enabled'), function (k, v) {
        statuses.push($(this).val());
    });

    let wrap = {
        dates: dates,
        statuses: statuses
    };

    $.ajax({
        url: '/rest/student/count',
        method: 'get',
        contentType: "text/plain;charset=UTF-8",
        dataType: 'json',
        traditional: true,
        data: wrap,
        success: function (data) {
            $.each(data, function (date, count) {
                labels.push(date);
                values.push(count);
            });
            showAnalyticsChart({labels: labels, values: values});
        }
    });

}

function formatRuDate(day) {
    return ('0' + day.getDate()).substr(-2, 2) + '.' + ('0' + (day.getMonth() + 1)).substr(-2, 2) + '.' + day.getFullYear();
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
