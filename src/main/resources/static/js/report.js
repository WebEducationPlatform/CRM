var selectedDateStart;
var selectedDateEnd;
var selectedReport = 1;
var lastReportCount = 0;

$('#mailingDate').daterangepicker({
    locale: {
        format: 'DD.MM.YYYY'
    },
    ranges: {
        'Today': [moment(), moment()],
        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
        'This Month': [moment().startOf('month'), moment().endOf('month')],
        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    },
    "startDate": moment().startOf('month'),
    "endDate": moment()
}, function (start, end, label) {
    selectedDateStart = start.format('YYYY-MM-DD');
    selectedDateEnd = end.format('YYYY-MM-DD');
    console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
});

function setVisibility(i) {
    let btn = $('#load-data-button');
    switch (i) {
        case 1:
            $('.hideable').show();
            $('.hideable_1').show();
            $('.hideable_2').hide();
            $('.hideable_3').hide();
            btn.prop('disabled', false);
            break;
        case 2:
        case 3:
            $('.hideable').hide();
            $('.hideable_1').show();
            $('.hideable_2').hide();
            $('.hideable_3').hide();
            btn.prop('disabled', false);
            break;
        case 4:
            $('.hideable').hide();
            $('.hideable_1').hide();
            $('.hideable_2').hide();
            $('.hideable_3').show();
            btn.prop('disabled', false);
            break;
        case 5:
            $('.hideable').hide();
            $('.hideable_1').hide();
            $('.hideable_2').show();
            $('.hideable_3').hide();
            btn.prop('disabled', true);
            break;
    }
}

$('#report-type-1').on('click', function () {
    setVisibility(1);
    selectedReport = 1;
});

$('#report-type-2').on('click', function () {
    setVisibility(2);
    selectedReport = 2;
});

$('#report-type-3').on('click', function () {
    setVisibility(3);
    selectedReport = 3;
});

$('#report-type-4').on('click', function () {
    setVisibility(4);
    selectedReport = 4;
    $('.reports-checkboxes').prop('checked', false);
});

$('#report-type-5').on('click', function () {
    setVisibility(5);
    selectedReport = 5;
    $('.reports-checkboxes').prop('checked', false);
});

$('#reportList').on('change', '.reports-checkboxes', function () {
    let btn = $('#load-data-button');
    if ($('.reports-checkboxes:checked').length === 2) {
        btn.prop('disabled', false);
    } else {
        btn.prop('disabled', true);
    }
});

$('#from-all-checkbox').on('change', function () {
    if (this.checked) {
        $('#statusFromSelect').prop('disabled', true);
    } else {
        $('#statusFromSelect').prop('disabled', false);
    }
});

$('#load-data-button').on('click', function () {
    let wrap;
    if (selectedDateStart === undefined || selectedDateEnd === undefined) {
        let dates = $('#mailingDate').val().split(' - ');
        let start = dates[0].split('.');
        let end = dates[1].split('.');
        selectedDateStart = start[2] + '-' + start[1] + '-' + start[0];
        selectedDateEnd = end[2] + '-' + end[1] + '-' + end[0];
    }
    let selectedExcludes = [];
    $('.exclude-status-checkboxes:checked').each(function(){
        selectedExcludes.push($(this).val());
    });
    switch (selectedReport) {
        case 1:
            if ($('#from-all-checkbox').is(':checked')) {
                wrap = {
                    "firstReportDate" : selectedDateStart,
                    "lastReportDate" : selectedDateEnd,
                    "toId" : $('#statusToSelect').val(),
                    "excludeIds" : selectedExcludes
                };
                $.ajax({
                    url: '/rest/report/countFromAny',
                    type: 'GET',
                    async: true,
                    data: wrap,
                    traditional: true,
                    success: function (response) {
                        showAndFillTable(response);
                    }
                });
            } else {
                wrap = {
                    "firstReportDate" : selectedDateStart,
                    "lastReportDate" : selectedDateEnd,
                    "fromId" : $('#statusFromSelect').val(),
                    "toId" : $('#statusToSelect').val(),
                    "excludeIds" : selectedExcludes
                };
                $.ajax({
                    url: '/rest/report/count',
                    type: 'GET',
                    async: true,
                    data: wrap,
                    traditional: true,
                    success: function (response) {
                        showAndFillTable(response);
                    }
                });
            }
            break;
        case 2:
            wrap = {
                "firstReportDate" : selectedDateStart,
                "lastReportDate" : selectedDateEnd,
                "excludeIds" : selectedExcludes
            };
            $.ajax({
                url: '/rest/report/countNew',
                type: 'GET',
                async: true,
                data: wrap,
                traditional: true,
                success: function (response) {
                    showAndFillTable(response);
                }
            });
            break;
        case 3:
            wrap = {
                "firstReportDate" : selectedDateStart,
                "lastReportDate" : selectedDateEnd,
                "excludeIds" : selectedExcludes
            };
            $.ajax({
                url: '/rest/report/countFirstPayments',
                type: 'GET',
                async: true,
                data: wrap,
                traditional: true,
                success: function (response) {
                    showAndFillTable(response);
                }
            });
            break;
    }
});

$('#panel-tabs-data').on('click', '.report-tab', function () {
    let report = $(this)[0]['id'].split("_")[2];
    $('.report-tab').removeClass('active');
    $('#report_tab_' + report).addClass('active');
    $('.report-panel').hide();
    $('#report_panel_' + report).show();
});

$('#exclude-statuses-btn').on('click', function () {
    let btnicon = $('#exclude-statuses-btn-icon');
    if (btnicon.hasClass('glyphicon-plus')) {
        btnicon.removeClass('glyphicon-plus');
        btnicon.addClass('glyphicon-minus');
        $('#statusExcludeSelect').show();
    } else {
        btnicon.removeClass('glyphicon-minus');
        btnicon.addClass('glyphicon-plus');
        $('#statusExcludeSelect').hide();
    }
});

function showAndFillTable(data) {
    lastReportCount++;
    if (lastReportCount === 2) {
        $('.hideable_once').show();
    }
    $('#reportList').append(
        '<label><input type="checkbox" value="null" class="reports-checkboxes" aria-label="Отчет #' + lastReportCount + '"/>Отчет #' + lastReportCount + '</label><br />'
    );
    $('#report_selector_1').append(
        '<option value="' + lastReportCount + '">Отчет #' + lastReportCount + '</option>'
    );
    $('#report_selector_2').append(
        '<option value="' + lastReportCount + '">Отчет #' + lastReportCount + '</option>'
    );
    $('.report-tab').removeClass('active');
    $('#panel-tabs-data').append(
        '<li role="presentation" id="report_tab_' + lastReportCount + '" class="report-tab active"><a href="#">Отчет #' + lastReportCount + '</a></li>'
    );
    $('.report-panel').hide();
    $('#panels').append(
        '<div id="report_panel_' + lastReportCount + '" class="panel panel-default report-panel">' +
            '<div class="panel-heading">Отчет #' + lastReportCount + '</div>' +
            '<div class="panel-body">' +
                '<p id="report-text-' + lastReportCount + '">' +
                '</p>' +
                '</div>' +
                '<table id="client-cards-table' + lastReportCount + '" class="table table-hover table-condensed">' +
                    '<tr>' +
                        '<th>#</th>' +
                        '<th>Имя</th>' +
                        '<th>Фамилия</th>' +
                        '<th>Телефон</th>' +
                        '<th>E-mail</th>' +
                    '</tr>' +
                '</table>' +
            '</div>' +
        '</div>'
    );
    if (data['message']) {
        $('#report-text-' + lastReportCount).text(data['message']);
    }
    if (data['clients'].length > 0) {
        let i = 1;
        $.each(data['clients'], function fill() {
            let client = $(this)[0];
            $('#client-cards-table' + lastReportCount + ' tr:last').after(
                '<tr id="t' + lastReportCount + 'client_' + client["id"] + '" class="client-row">' +
                '<td>' + i + '</td>' +
                '<td>' + client["name"] + '</td>' +
                '<td>' + client["lastName"] + '</td>' +
                '<td>' + client["phoneNumber"] + '</td>' +
                '<td>' + client["email"] + '</td>' +
                '</tr>'
            );
            i++;
        });
        $('#report_panel_' + lastReportCount).show();
    }
    window.scrollTo(0, 0);
    $('#client-cards-holder').show();
}

$('#panels').on('click', '.client-row', function () {
    let id = $(this)[0]['id'].split("_")[1];
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', id);
    currentModal.modal('show');
});

$(document).ready(function () {
    let statusFromSelector = $('#statusFromSelect');
    let statusToSelector = $('#statusToSelect');
    let statusExcludeSelector = $('#statusExcludeSelect');
    $('.hideable_once').hide();
    $.ajax({
        url: "/rest/status",
        type: "GET",
        async: true,
        success: function (response) {
            statusFromSelector.empty();
            statusToSelector.empty();
            statusExcludeSelector.empty();
            $('#reportList').empty();
            $('#report_selector_1').empty();
            $('#report_selector_2').empty();
            for (var i = 0; i < response.length; i++) {
                statusFromSelector.append('<option value="' + response[i].id + '">' + response[i].name + '</option>');
                statusToSelector.append('<option value="' + response[i].id + '">' + response[i].name + '</option>');
                statusExcludeSelector.append('<label><input type="checkbox" class="exclude-status-checkboxes" value="' + response[i].id + '" aria-label="' + response[i].name + '"/>' + response[i].name + '</label><br />');
            }
        }
    });
    statusExcludeSelector.hide();
});

function updateReportsStatus() {
    let wrap = {
        id: 0,
        inLearningStatus: Number($('#select0').val()),
        endLearningStatus: Number($('#select1').val()),
        dropOutStatus: Number($('#select2').val()),
        pauseLearnStatus: Number($('#select3').val()),
        trialLearnStatus: Number($('#select4').val())
    };
    $.ajax({
        url: "/rest/report/setReportsStatus",
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify(wrap),
        success: function (response) {
            location.reload();
        },
        error: function (response) {
            alert('error ' + response);
        }
    });
}

function sendReport() {
    var data = {
        report: document.getElementById('reportArea').value,
        email: document.getElementById('email').value
    };
    $.ajax({
        url: "/rest/report/sendReportToEmail",
        type: "POST",
        data: data,
        success: function (response) {
            location.reload();
        },
        error: function (response) {
            alert('error ' + response);
        }
    });
}

