let botIp = $("#slackbotIp").val();
let botPort = $("#slackbotPort").val();
let mentorsMap = new Map();

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
                mentorsMap.set(mentor.id, response);
            })

    });
    //console.log(mentorsMap);
    drawMentorTable();
});

function drawMentorTable() {
    let mentorsWithClientsMap = new Map();
    for (const mentor of mentorsMap.entries()) {
        let mentorWithClientsMap = new Map();
        $('<div></div>', {
            class: 'text-center col-md-' + 12 / mentorsMap.length,
            id: 'mentor-column' + mentor[0],
            text: mentor[1].mentorName
        }).appendTo('#mentors-row');
        $('<div></div>', {
            class: 'row',
            id: 'mentor-row' + mentor[0]
        }).appendTo('#mentor-column' + mentor[0]);
        $.each(mentor[1], function (key, obj) {
            if (key.includes('emails')) {
                let studentsInStatusList = [];
                $('<div></div>', {
                    class: 'column ui-sortable',
                    id: 'column-' + mentor[0] + "-" + key,
                    text: key
                }).appendTo('#mentor-row' + mentor[0]);
                $.each(obj, function (i, email) {
                    $.get("/rest/student?email=" + email)
                        .done(function (client) {
                            studentsInStatusList.push(client);
                        })
                });
                mentorWithClientsMap.set(key, studentsInStatusList);
            }
        });
        mentorsWithClientsMap.set(mentor[0], mentorWithClientsMap);
    }
    //console.log(mentorsWithClientsMap);
    drawClientsPortlet(mentorsWithClientsMap);
}

function drawClientsPortlet(mentorsWithClientsMap) {
    for (const mentorWithClientsMap of mentorsWithClientsMap.entries()) {
        //console.log(mentorWithClientsMap);
        for (const statuses of mentorWithClientsMap[1].entries()) {
            //console.log(statuses);
            let status = statuses[0];
            statuses[1].forEach(function (student, i, statuses) {
                console.log(i + " " + student.client.name);
                $('<div></div>', {
                    class: 'portlet common-modal panel panel-default',
                    id: student.id,
                    onmouseover: 'displayOption(' + student.id + ')',
                    value: student.id,
                    'data-card-id': student.id,
                }).appendTo('#column-' + mentorWithClientsMap[0]+ "-" + status);
                //
                $('<div></div>', {
                    class: 'portlet-body',
                    'client-id': student.id,
                    name: 'client-' + student.id + '-modal',
                    onclick: 'showCurrentModal(' + student.client.id + ')',
                    text: student.client.name + " " + student.client.lastName
                }).appendTo('div#' + student.id + '.portlet');
            });
        }
    }
}


function showCurrentModal(studentId) {
    var clientId = studentId;
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}

