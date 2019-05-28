//тут все при старте страницы
$(document).ready(function () {
    $.get("/rest/status")
        .done(function (statusesList) {
                drawStudentsInStatuses(drawStatuses(statusesList))
            }
        )
});

function drawStatuses(statuses) {
    var studentStatuses = [];
    for (var i in statuses)
        if (statuses[i].createStudent === true) {
            studentStatuses.push(statuses[i])
            $('div[name=statuses]').append("<div id='" + statuses[i].name + "'/>")
        }
    return studentStatuses
}

function drawStudentsInStatuses(studentStatuses) {
    for (var i in studentStatuses) {
        $.get("/rest/status/" + studentStatuses[i].id)
            .done(function (clients) {
                for (var j in clients) {
                    $('#' + studentStatuses[i].name).append("<div name='" + clients[j].name + "'/>")
                }
            })
    }
}