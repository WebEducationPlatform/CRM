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

                }
            });
            drawMissingStudents();
        })
});

function drawMissingStudents() {
    let botDomain = $("#slackBotDomain").val();
    let url = "/rest/status/lost";
    $.get(`https://${botDomain}/student/lost`)
        .done(function (listLostStudentEmail) {
            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(listLostStudentEmail),
                contentType: 'application/json',
                success: function (students) {
                    $.each(students, function (i, student) {
                        drawClientsPortlet(student, student.status);
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
