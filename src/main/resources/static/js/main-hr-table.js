//тут все при старте страницы
$(document).ready(function () {
//Показать пропавших студентов в статусах
//     $('a#FIND_MISSING').on('click', function () {
    let statuses;
    $.get("/rest/status")
        .done(function (statusesFromServer) {
            statuses = statusesFromServer;
            $("#status-columns").children().remove();
            $.each(statuses, function (i, status) {
                if (status.createStudent === true) {
                    $('<div></div>', {
                        class: 'column ui-sortable',
                        id: 'status-column' + status.id,
                        text: status.name
                    }).appendTo('#status-columns');
                    drawMissingStudents(status);
                }
            })
        })
});

function drawMissingStudents(status) {
    let botIp = $("#slackbotIp").val();
    let botPort = $("#slackbotPort").val();
    let students;
    $.get("/rest/status/" + status.id)
        .done(function (studentsInStatus) {
            students = studentsInStatus;
            $.each(students, function (i, student) {
                if (student.email !== null) {
                    $.get("https://" + botIp + ":" + botPort + "/student/lost?email=" + student.email)
                        .done(function (isLost) {
                            if (isLost === true) {
                                drawClientsPortlet(student, status);
                            }
                        })
                }
            })
        });
}

function drawClientsPortlet(student, status) {
    $('<div></div>', {
        class: 'portlet common-modal panel panel-default',
        id: student.id,
        onmouseover: 'displayOption(' + student.id + ')',
        value: student.id,
        'data-card-id': student.id,
    }).appendTo('#status-column' + status.id);

    $('<div></div>', {
        class: 'portlet-body',
        'client-id': student.id,
        name: 'client-' + student.id + '-modal',
        onclick: 'showCurrentModal(' + student.id + ')',
        text: student.name + " " + student.lastName
    }).appendTo('div#' + student.id + '.portlet');
}

function showCurrentModal(studentId) {
    var clientId = studentId;
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}
