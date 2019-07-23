let botDomain = $("#slackBotDomain").val();
let mentorMaxStudents = $("#mentorMaxStudents").val();
const maxStudents = mentorMaxStudents * mentors.length;
let mentorsMap = new Map();
let studentCounter = 0;
let learningStudents = 0;
let trialStudents = 0;
let lostStudents = 0;
let lostTrialStudents = 0;
let undefinedMentorsEmails = [];
let undefinedStudentsEmails = [];

$(document).ready(function () {
    $("#mentors-row").children().remove();
    $('<div></div>', {
        class: 'row',
        id: 'mentors-row'
    }).appendTo('#mentors-column');
    $.ajaxSetup({async: false});
    $.each(mentors, function (i, mentor) {
        $.get("https://" + botDomain + "/mentor/students?email=" + mentor.email)
            .done(function (response) {
                mentorsMap.set(mentor.id, response);
            })
            .fail(function () {
                undefinedMentorsEmails.push(mentor.email)
            })

    });
    drawMentorTable();
});

function drawMentorTable() {
    let mentorsWithClientsMap = new Map();
    for (const mentor of mentorsMap.entries()) {
        let mentorWithClientsMap = new Map();
        $('<div></div>', {
            class: 'text-center',
            id: 'mentor' + mentor[0],
            text: mentor[1].mentorName,
            style: "font-size: 120%"
        }).appendTo('#mentors-row');
        $('<div></div>', {
            class: 'text-center col-md-auto',
            id: 'mentor-column' + mentor[0],
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
                    text: renameColumn(key)
                }).appendTo('#mentor-row' + mentor[0]);
                $.each(obj, function (i, email) {
                    $.get("/rest/client?email=" + email)
                        .done(function (client) {
                            studentsInStatusList.push(client);
                        })
                        .fail(function () {
                            undefinedStudentsEmails.push(email);
                        })
                });
                mentorWithClientsMap.set(key, studentsInStatusList);
            }
        });
        mentorsWithClientsMap.set(mentor[0], mentorWithClientsMap);
    }
    drawClientsPortlet(mentorsWithClientsMap);
    drawInfoBlock();
    if (undefinedStudentsEmails.length > 0 || undefinedMentorsEmails.length > 0) {
        drawUndefinedEmailsBlock(undefinedStudentsEmails);
    }
}

function drawClientsPortlet(mentorsWithClientsMap) {
    for (const mentorWithClientsMap of mentorsWithClientsMap.entries()) {
        let counterStudentsOnMentor = 0;
        for (const statuses of mentorWithClientsMap[1].entries()) {
            let counter = 0;
            let status = statuses[0];
            $('<div></div>', {
                class: 'portlet panel panel-default',
                text: statuses[1].length,
                style: "color: red;"
            }).appendTo('#column-' + mentorWithClientsMap[0] + "-" + status);
            statuses[1].forEach(function (client, i, statuses) {
                counter++;
                $('<div></div>', {
                    class: 'portlet common-modal panel panel-default',
                    id: client.id,
                    value: client.id,
                    'data-card-id': client.id,
                }).appendTo('#column-' + mentorWithClientsMap[0] + "-" + status);
                $('<div></div>', {
                    class: 'portlet-body',
                    'client-id': client.id,
                    name: 'client-' + client.id + '-modal',
                    onclick: 'showCurrentModal(' + client.id + ')',
                    text: client.name + " " + client.lastName
                }).appendTo('div#' + client.id + '.portlet');
            });
            counterStudentsOnMentor += counter;
            countStudents(counter, status);
        }
        let q = $("#mentor" + mentorWithClientsMap[0]);
        $("#mentor" + mentorWithClientsMap[0]).text(q.text() + " - " + counterStudentsOnMentor + " студентов");
    }
}

function showCurrentModal(studentId) {
    var clientId = studentId;
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}

function renameColumn(oldName) {
    if (oldName === "emailsStudents") {
        return "Учатся"
    } else if (oldName === "emailsTrialStudents") {
        return "На пробных"
    } else if (oldName === "emailsLostStudents") {
        return "Пропали учатся"
    } else if (oldName === "emailsLostTrialStudents") {
        return "Пропали на пробных"
    } else {
        return oldName;
    }
}

function drawInfoBlock() {
    $('<P></P>', {
        text: 'Всего студентов: ' + studentCounter,
        style: "font-size: 120%"
    }).appendTo('#right-column');
    $('<P></P>', {
        text: 'Учатся студентов: ' + learningStudents,
        style: "font-size: 120%"
    }).appendTo('#right-column');
    $('<P></P>', {
        text: 'Студентов на пробных: ' + trialStudents,
        style: "font-size: 120%"
    }).appendTo('#right-column');
    $('<P></P>', {
        text: 'Пропавших студентов: ' + lostStudents,
        style: "font-size: 120%"
    }).appendTo('#right-column');
    $('<P></P>', {
        text: 'Пропавших на пробных: ' + lostTrialStudents,
        style: "font-size: 120%"
    }).appendTo('#right-column');
    $('<P></P>', {
        text: 'Максимальное число студентов: ' + maxStudents,
        style: "font-size: 120%"
    }).appendTo('#right-column');
    $('<P></P>', {
        text: 'Свободных мест: ' + (maxStudents - studentCounter),
        style: "font-size: 120%"
    }).appendTo('#right-column');
}

function drawUndefinedEmailsBlock(undefinedEmails) {
    $('<P></P>', {
        text: 'Имеются нераспознанные данные:',
        style: "font-size: 120%; color: red;"
    }).appendTo('#right-column');
    if (undefinedMentorsEmails.length > 0) {
        $('<P></P>', {
            text: 'Нераспознанные менторы:',
            style: "font-size: 120%; color: red;"
        }).appendTo('#right-column');
        $.each(undefinedMentorsEmails, function (i, email) {
            $('<div></div>', {
                //class: 'portlet panel panel-default',
                text: email,
                style: "color: red;"
            }).appendTo('#right-column');
        })
    }
    if (undefinedStudentsEmails.length > 0) {
        $('<P></P>', {
            text: 'Нераспознанные студенты:',
            style: "font-size: 120%; color: red;"
        }).appendTo('#right-column');
        $.each(undefinedEmails, function (i, email) {
            $('<div></div>', {
                //class: 'portlet panel panel-default',
                text: email,
                style: "color: red;"
            }).appendTo('#right-column');
        });
    }
    $('<P></P>', {}).appendTo('#right-column');
    $('<P></P>', {
        text: 'Расчеты по студентам неверны!',
        style: "font-size: 120%; color: red;"
    }).appendTo('#right-column');
}

function countStudents(counter, status) {
    studentCounter += counter;
    if (status === "emailsStudents") {
        learningStudents += counter;
    } else if (status === "emailsTrialStudents") {
        trialStudents += counter;
    } else if (status === "emailsLostStudents") {
        lostStudents += counter;
    } else if (status === "emailsLostTrialStudents") {
        lostTrialStudents += counter;
    }
}

