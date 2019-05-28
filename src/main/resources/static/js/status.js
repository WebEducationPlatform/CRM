var deleteId;

$('.delete-client').on('click', function (event) {
    deleteId = $(this).data('clientid');
    let formData = {clientId: deleteId};
    $.ajax({
        type: 'GET',
        url: 'rest/client/' + deleteId,
        data: formData,
        success: function (client) {
            deleteClientStatus();
        }
    });

});


// удаление карточки  и уведомлений
function deleteClientStatus() {
    let url = "/rest/status/client/delete";
    let requestParam = {
        clientId: deleteId
    };
    $.ajax({
        type: "POST",
        url: url,
        data: requestParam,
        success: function () {
            $('.portlet[value="' + deleteId + '"]').remove();
        },
        error: function (e) {
            console.log(e);
        }
    })
}

function senReqOnChangeStatus(clientId, statusId) {
    let
        url = '/rest/status/client/change',
        formData = {
            clientId: clientId,
            statusId: statusId
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (data) {
            let
                url = '/rest/client/' + clientId;
            $.get(url,
                function (data) {
                    $('#client-' + data.id + 'history').prepend(
                        "<tr>" +
                        "   <td>" + data.history[0].title + "</td>" +
                        "   <td class=\"client-history-date\">" + data.history[0].date + "</td>" +
                        "</tr>"
                    );
                });
        },
        error: function () {
            alert('Не задан статус по-умолчанию для нового студента!');
            location.reload();
        }
    });
}

$(document).on("show.bs.dropdown", ".statuses-by-dropdown", function () {
    let data = $("#statuses-list");
    $(this).find(".statuses-content").html(data.html());
});

//скрытие карточки в скрытый статус
var invisibleId;
var statusId;
$('.invisible-client').on('click', function (event) {
    invisibleId = $(this).data('clientid');
    statusId = $(this).data('statusid');
    let formData = {clientId: invisibleId};
    $.ajax({
        type: 'GET',
        url: 'rest/client/' + invisibleId,
        data: formData,
        success: function () {
            invisible();
        }
    });
});

function invisible() {
    let a = $(this),
        url = '/rest/status/client/change',
        clientid = invisibleId,
        formData = {
            clientId: invisibleId,
            statusId: statusId
        };
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            console.log(clientid);
            $('.portlet[value="' + clientid + '"]').remove();
        },
        error: function () {
            alert('Не задан статус по-умолчанию для нового студента!');
        }
    });
}

//Удаление статуса и карточек
var deleteStatusId;
$(".glyphicon-remove-circle").on("click", function dStatus() {
    deleteStatusId = $(this).attr("value");
    let formData = {clientId: deleteStatusId};
    $.ajax({
        type: 'GET',
        url: 'rest/status/' + deleteStatusId,
        data: formData,
        success: function (clients) {
            let hasnotify = false;
            for (var i = 0; i < clients.length; i++) {
                if (clients[i].notifications != 0) {
                    hasnotify = true;
                    break;
                }
            }
            if (hasnotify) {
                $('#delete-client-btn').attr('onclick', 'deleteStatus()');
                let deleteModal = $('#delete-client-modal');
                deleteModal.modal('show');
            } else {
               $('#delete-status-btn').attr('onclick', 'deleteStatus()');
               let deleteModal =  $('#deleteStatusModal');
               deleteModal.modal('show');
            }
        }
    });
});
function deleteStatus() {
    let url = '/admin/rest/status/delete';
    let formData = {
        deleteId: deleteStatusId
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            location.reload();
        },
        error: function (e) {

        }
    });
}
//скрытие статуса вместе с карточками
var statusHideId;
$(".hide-status-btn").on("click", function hStatus() {
    statusHideId = $(this).attr("value");
    let formData = {clientId: statusHideId};
    $.ajax({
        type: 'GET',
        url: 'rest/status/' + statusHideId,
        data: formData,
        success: function (clients) {
            let hasnotify = false;
            for (var i = 0; i < clients.length; i++) {
                if (clients[i].notifications != 0) {
                    hasnotify = true;
                    break;
                }
            }
             if (hasnotify) {
                 $('#delete-client-btn').attr('onclick', 'hideStatus()');
                 var deleteModal = $('#delete-client-modal');
                 deleteModal.modal('show');
             } else {
                 hideStatus();
             }
        }
    });
});

function hideStatus() {
    let val = statusHideId;
    let
        url = '/admin/rest/status/visible/change',
        formData = {
            statusId: val,
            invisible: true
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

$(document).ready(function () {
    $(".show-status-btn").on("click", function showStatus() {
        let
            url = '/admin/rest/status/visible/change',
            formData = {
                statusId: $(this).attr("value"),
                invisible: false
            };

        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            success: function (status) {
                location.reload();
            },
            error: function (error) {
                console.log(error);
            }
        })
    });
    // обработчик кнопок для возврата клиента из отложки
    //  в all-clients-table
    $(document).on('click', '.from-postpone', function returnClientFromPostpone() {
        var button = $(this);
        var clientId = this.getAttribute('data-client');
        var url = '/rest/client/remove/postpone';
        $.ajax({
            type: 'POST',
            url: url,
            data: {clientId : clientId},
            success: function () {
                button.parents('.button-return-from-postpone').children("button").addClass("btn btn-secondary").attr("disabled", "disabled").text("Выполнено");
            }
        })
    });
    // обработчик кнопок для возврата клиента из скрытых статусов
    // кнопки в all-clients-table
    $(document).on("click", '.return-to-visible-status', function returnClientToStatus() {
        let
            button = $(this),
            url = '/rest/status/client/change',
            formData = {
                clientId: button.parents(".dropdown").children("button").attr("data-client"),
                statusId: button.attr("value")
            };

        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            success: function () {
                button.parents(".dropdown").children("button").removeClass().addClass("btn btn-secondary").attr("disabled", "disabled").text("Выполнено");
            },
            error: function () {
                alert('Не задан статус по-умолчанию для нового студента!');
            }
        });
    });
});

//Set createStudent flag for Status
$(".create_student_checkbox").click(function () {
    $.ajax({
        type: 'POST',
        url: "/rest/status/create-student",
        data: {
            id : this.value,
            create : this.checked
        }
    });
});
