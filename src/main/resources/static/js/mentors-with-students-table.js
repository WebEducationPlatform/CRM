let botIp = $("#slackbotIp").val();
let botPort = $("#slackbotPort").val();
let mentorsMap = new Map();

$(document).ready(function () {
    $("#mentors-row").children().remove();
    $('<div></div>', {
        class: 'row',
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
            class: 'text-center col-md-auto',
            id: 'mentor-column' + mentor[0],
            text: mentor[1].mentorName
        }).appendTo('#mentors-row');
        $('<div></div>', {
            class: 'row center-block',
            id: 'mentor-row' + mentor[0],
            style: "display: flex; justify-content: center; flex-flow:row wrap;"
        }).appendTo('#mentor-column' + mentor[0]);
        $.each(mentor[1], function (key, obj) {
            if (key.includes('emails')) {
                let studentsInStatusList = [];
                $('<div></div>', {
                    class: 'column ui-sortable',
                    id: 'column-' + mentor[0] + "-" + key,
                    text: key.replace('emails','')
                }).appendTo('#mentor-row' + mentor[0]);
                $.each(obj, function (i, email) {
                    $.get("/rest/client?email=" + email)
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
            statuses[1].forEach(function (client, i, statuses) {
                //console.log(i + " " + student.client.name);
                $('<div></div>', {
                    class: 'portlet common-modal panel panel-default',
                    id: client.id,
                    onmouseover: 'displayOption(' + client.id + ')',
                    value: client.id,
                    'data-card-id': client.id,
                }).appendTo('#column-' + mentorWithClientsMap[0] + "-" + status);
                //
                $('<div></div>', {
                    class: 'portlet-body',
                    'client-id': client.id,
                    name: 'client-' + client.id + '-modal',
                    onclick: 'showCurrentModal(' + client.id + ')',
                    text: client.name + " " + client.lastName
                }).appendTo('div#' + client.id + '.portlet');
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

