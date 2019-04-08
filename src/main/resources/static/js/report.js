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

$('textarea').each(function () {
    this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
}).on('click', function () {
    this.style.height = 'auto';
    this.style.height = (this.scrollHeight) + 'px';
});

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

$('#load-data-button').on('click', function () {
    let wrap;
    if (selectedDateStart === undefined || selectedDateEnd === undefined) {
        let dates = $('#mailingDate').val().split(' - ');
        let start = dates[0].split('.');
        let end = dates[1].split('.');
        selectedDateStart = start[2] + '-' + start[1] + '-' + start[0];
        selectedDateEnd = end[2] + '-' + end[1] + '-' + end[0];
    } else {
        let selectedExcludes = [];
        $('.exclude-status-checkboxes:checked').each(function(){
            selectedExcludes.push($(this).val());
        });
        switch (selectedReport) {
            case 1:
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
                        $('#reportArea').val(response);
                    }
                });
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
                        $('#reportArea').val(response);
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
                        $('#reportArea').val(response);
                    }
                });
                break;
        }
    }
});

$(document).ready(function () {
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

