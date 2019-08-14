let botDomain = $("#slackBotDomain").val();
let botAccessProtocol = $("#slackBotAccessProtocol").val();
let mentorMaxStudents = $("#mentorMaxStudents").val();
const maxStudents = mentorMaxStudents * mentors.length;
let mentorsMap = new Map(Object.entries(JSON.parse(mentorsFromBotJson)));
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

    mentorsMap.forEach(function (value, key) {
        if (value.mentorMissed) {
            // let x = value.getKey();
            mentorsMap.delete(key);
            undefinedMentorsEmails.push(value.email);
        }


    });
    drawMentorTable();
});

function drawMentorTable() {
    let mentorsWithClientsMap = new Map();
    mentorsMap.forEach(function (mentorValue, mentorKey) {
        let mentorWithClientsMap = new Map();
        $('<div></div>', {
            class: 'text-center',
            id: 'mentor' + mentorKey,
            text: mentorValue.mentorName,
            style: "font-size: 120%"
        }).appendTo('#mentors-row');
        $('<div></div>', {
            class: 'text-center col-md-auto',
            id: 'mentor-column' + mentorKey,
        }).appendTo('#mentors-row');
        $('<div></div>', {
            class: 'row center-block',
            id: 'mentor-row' + mentorKey,
            style: "display: flex; justify-content: center; flex-flow:row wrap;"
        }).appendTo('#mentor-column' + mentorKey);
        $.each(mentorValue, function (key, obj) {
            if (key.includes('emails')) {
                let studentsInStatusList = [];
                $('<div></div>', {
                    class: 'column ui-sortable',
                    id: 'column-' + mentorKey + "-" + key,
                    text: renameColumn(key)
                }).appendTo('#mentor-row' + mentorKey);
                let emailStudentsMap = new Map(Object.entries(studentsDto));
                if (obj.length !== 0) {
                    obj.forEach(function (email) {
                        let student = emailStudentsMap.get(email.toLowerCase());
                        if (student != null) {
                            studentsInStatusList.push(student);
                        } else {
                            undefinedStudentsEmails.push(email);
                        }
                    });
                }
                mentorWithClientsMap.set(key, studentsInStatusList);
            }
        });
        mentorsWithClientsMap.set(mentorKey, mentorWithClientsMap);
    });
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
        let url = botAccessProtocol + botDomain + "/student/get?emails=";
        $.each(undefinedEmails, function (i, email) {
            url = url + email + ',';
        });
        $.get(url)
            .done(function (data) {
                console.log(data);
                let url = "/rest/client/names?full_names=";
                $.each(data, function (i, clientFromBot) {
                    url = url + clientFromBot.name + ',';
                    $('<div></div>', {
                        //class: 'portlet panel panel-default',
                        id: "undefined" + clientFromBot.name.replace(/\s/g, ''),
                        text: clientFromBot.name + "  " + clientFromBot.email,
                        style: "color: red;"
                    }).appendTo('#right-column');
                });
                $.get(url.slice(0, -1))
                    .done(function (data) {
                        console.log(data)
                        $.each(data, function (i, client) {
                            if (client !== null) {
                                $('<div></div>', {
                                    class: 'portlet common-modal panel panel-default',
                                    id: 'undef' + client.id,
                                    value: client.id,
                                    'data-card-id': client.id,
                                }).appendTo('div#undefined' + client.name + client.lastName);
                                $('<div></div>', {
                                    class: 'portlet-body',
                                    'client-id': client.id,
                                    name: 'client-' + client.id + '-modal',
                                    onclick: 'showCurrentModal(' + client.id + ')',
                                    text: client.name + " " + client.lastName
                                }).appendTo('div#undef' + client.id + '.portlet');
                            }
                        })
                    })
            });
    }
    // $('<P></P>', {}).appendTo('#right-column');
    // $('<P></P>', {
    //     text: 'Расчеты по студентам неверны!',
    //     style: "font-size: 120%; color: red;"
    // }).appendTo('#right-column');
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

