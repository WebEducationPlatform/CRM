jQuery.noConflict();
//тут все при старте страницы
$(document).ready(function () {

    $.get("/rest/status")
        .done(function (statusesList) {
                drawStudentsInStatuses(drawStatusesColumns(statusesList))
            }
        );

    $('div').delegate('.portlet', "click", function () {
        openModalWindowWithUsersData($(this).attr('client_id'))
    });
});

function drawStatusesColumns(statuses) {
    var studentStatuses = [];
    $.each(statuses, function (i, status) {
        if (status.createStudent === true) {
            studentStatuses.push(status);
            $('<div></div>', {
                class: 'col-md-auto' + ' status',
                id: status.name,
                text: status.name
            }).appendTo('#statuses')
        }
    });
    return studentStatuses
}

function drawStudentsInStatuses(studentStatuses) {
    $.each(studentStatuses, function (i, status) {
        $.get("/rest/status/" + status.id)
            .done(function (clients) {
                $.each(clients, function (j, client) {
                    $.get("http://localhost:8080/student/lost?email=" + client.email)
                        .done(function (data) {
                           //if (data === true) {
                                $('<div></div>', {
                                    class: 'portlet common-modal panel panel-default',
                                    client_id: client.id,
                                    text: client.name + " " + client.lastName
                                }).appendTo('#' + status.name);
                                $("div").find(`[client_id='${client.id}']`).css({'background-color': "pink"})
                           //}
                        })
                        .fail(function () {
                            console.log("can't connect to SlackBot");
                            return false;
                        })

                })
            });
    })
}


