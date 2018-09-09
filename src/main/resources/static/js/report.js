
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
}, function(start, end, label) {
    console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
});

$('#mailingDate').on('apply.daterangepicker', function () {
    var date = document.getElementById("mailingDate").value;

    $.ajax({
        url: "/rest/report/last-days",
        type: "POST",
        data: date,
        success: function (response) {
            var formCreate = document.getElementById('formToSend');
            formCreate.style.visibility = "visible";
            var reportArea = document.getElementById('reportArea');
            reportArea.value = response;
        },
        error: function (response) {
            alert('error ' + response);
        }
    });
});

$(document).ready(function () {
    var formCreate = document.getElementById('formToSend');
    formCreate.style.visibility = "hidden";
});

$('textarea').each(function () {
    this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
}).on('click', function () {
    this.style.height = 'auto';
    this.style.height = (this.scrollHeight) + 'px';
});

$(document).ready(function () {
    $.ajax({
        url: "/rest/report/getReportsStatus",
        type: "POST",
        success: function (response) {
            var field = [response.inLearningStatus, response.endLearningStatus, response.dropOutStatus, response.pauseLearnStatus, response.trialLearnStatus ];
            for (var i = 0; i < field.length; i++) {
                var select = $(document.getElementById("select"+i));
                select.find('option').each(function () {
                    var val = $(this).attr('value');
                    if (val == field[i]) {
                        $(this).attr("selected", "selected");
                    }
                 });
            }
        },
        error: function (response) {
            alert('error ' + response);
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

