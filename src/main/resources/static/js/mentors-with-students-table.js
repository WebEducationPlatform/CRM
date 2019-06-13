let botIp = $("#slackbotIp").val();
let botPort = $("#slackbotPort").val();
var mentorsList = [];

$(document).ready(function () {
    $("#mentors-row").children().remove();
    $('<div></div>', {
        class: 'row flex-shrink-0',
        id: 'mentors-row'
    }).appendTo('#mentors-container');
    $.ajaxSetup({async: false});
    $.each(mentors, function (i, mentor) {
        $.get("http://" + botIp + ":" + botPort + "/mentor/students?email=" + mentor.email)
            .done(function (response) {
                mentorsList.push(response);
            })

    });
    console.log(mentorsList);
    drawMentorTable();
});

function drawMentorTable() {
    $.each(mentorsList, function (i, mentor) {
        $('<div></div>', {
            class: 'text-center col-md-' + 12 / mentorsList.length,
            id: 'mentor-column' + i,
            text: mentor.mentorName
        }).appendTo('#mentors-row');
        $('<div></div>', {
            class: 'row',
            id: 'mentor-row' + i
        }).appendTo('#mentor-column' + i);
        $.each(mentor, function (key, obj) {
            if (key.includes('emails')) {
                $('<div></div>', {
                    class: 'column ui-sortable',
                    id: 'column-' + key,
                    text: key
                }).appendTo('#mentor-row' + i);
                $.each(obj, function (i, email) {
                    $.get("/rest/student?email=" + email)
                        .done(function (client) {
                            drawClientsPortlet(client, key)
                        })
                })
            }
        })
    });


}

function drawClientsPortlet(student, key) {
    $('<div></div>', {
        class: 'portlet common-modal panel panel-default',
        id: student.id,
        onmouseover: 'displayOption(' + student.id + ')',
        value: student.id,
        'data-card-id': student.id,
    }).appendTo('#column-' + key);

    $('<div></div>', {
        class: 'portlet-body',
        'client-id': student.id,
        name: 'client-' + student.id + '-modal',
        onclick: 'showCurrentModal(' + student.client.id + ')',
        text: student.client.name + " " + student.client.lastName
    }).appendTo('div#' + student.id + '.portlet');
}

function showCurrentModal(studentId) {
    var clientId = studentId;
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}

