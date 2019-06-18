var selectedDateStart;
var selectedDateEnd;
var selectedReport = 1;

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

$('#reportArea').each(function () {
    this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;width:100%;');
}).on('click', function () {
    this.style.height = 'auto';
    this.style.height = (this.scrollHeight) + 'px';
});

function reportAreaSetHeight() {
    let element = $('#reportArea');
    element[0].style.height = 'auto';
    element.height(element[0].scrollHeight);
}

function hideElements() {
    $('.hideable').hide();
}

function showElements() {
    $('.hideable').show();
}

$('#report-type-1').on('click', function () {
    showElements();
    selectedReport = 1;
});

$('#report-type-2').on('click', function () {
    hideElements();
    selectedReport = 2;
});

$('#report-type-3').on('click', function () {
    hideElements();
    selectedReport = 3;
});

$('#from-all-checkbox').on('change', function () {
    if (this.checked) {
        $('#statusFromSelect').prop('disabled', true);
    } else {
        $('#statusFromSelect').prop('disabled', false);
    }
});

$('#load-data-button').on('click', function () {
    hideAndClearTable();
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

function hideAndClearTable() {
    $('#report-area-holder').hide();
    $('#reportArea').val('');
    $('#client-cards-table').hide();
    $('.client-row').remove();
}

function showAndFillTable(data) {
    if (data['message']) {
        $('#reportArea').val(data['message']);
        $('#report-area-holder').show();
        reportAreaSetHeight();
    }
    if (data['clients'].length > 0) {
        let i = 1;
        $.each(data['clients'], function fill() {
            let client = $(this)[0];
            $('#client-cards-table tr:last').after(
                '<tr id="client_' + client["id"] + '" class="client-row">' +
                '<td>' + i + '</td>' +
                '<td>' + client["name"] + '</td>' +
                '<td>' + client["lastName"] + '</td>' +
                '<td>' + client["phoneNumber"] + '</td>' +
                '<td>' + client["email"] + '</td>' +
                '</tr>'
            );
            i++;
        });
        $('#client-cards-table').show();
    }
    window.scrollTo(0, 0);
}

$('#client-cards-table').on('click', '.client-row', function () {
    let id = $(this)[0]['id'].split("_")[1];
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', id);
    currentModal.modal('show');
});

$(document).ready(function () {
    hideAndClearTable();
    let statusFromSelector = $('#statusFromSelect');
    let statusToSelector = $('#statusToSelect');
    let statusExcludeSelector = $('#statusExcludeSelect');

    $.ajax({
        url: "/rest/status",
        type: "GET",
        async: true,
        success: function (response) {
            statusFromSelector.empty();
            statusToSelector.empty();
            statusExcludeSelector.empty();
            for (var i = 0; i < response.length; i++) {
                statusFromSelector.append('<option value="' + response[i].id + '">' + response[i].name + '</option>');
                statusToSelector.append('<option value="' + response[i].id + '">' + response[i].name + '</option>');
                statusExcludeSelector.append('<label><input type="checkbox" class="exclude-status-checkboxes" value="' + response[i].id + '" aria-label="' + response[i].name + '"/>' + response[i].name + '</label><br />');
            }
        }
    });
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

