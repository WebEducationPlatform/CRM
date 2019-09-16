// удаление карточки и уведомлений (в статус deleted)
function deleteClientStatus(deleteId) {
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
function invisibleClient(clientId, statusId) {
    let formData = {clientId: clientId};
    $.ajax({
        type: 'GET',
        url: 'rest/client/' + clientId,
        data: formData,
        success: function () {
            invisible(clientId, statusId);
        }
    });
}

function invisible(clientId, statusId) {
    let url = '/rest/status/client/change',
        formData = {
            clientId: clientId,
            statusId: statusId
        };
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            $('.portlet[value="' + clientId + '"]').remove();
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
                let deleteModal = $('#deleteStatusModal');
                deleteModal.modal('show');
            }
        }
    });
});

function deleteStatus() {
    let url = '/rest/admin/status/delete';
    let formData = {
        deleteId: deleteStatusId
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            $('#status-column' + deleteStatusId).remove();
            $('#deleteStatusModal').modal('hide');
            //location.reload();
        },
        error: function (e) {
            console.log(e);
        }
    });
}

//скрытие статуса вместе с карточками
var statusHideId;
function hideStatusWithClients(element) {
    statusHideId = element.getAttribute("value");
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
}

function hideStatus() {
    let val = statusHideId;
    let
        url = '/rest/admin/status/visible/change',
        formData = {
            statusId: val,
            invisible: true
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            $('#status-column' + statusHideId).remove();
            //location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function showStatus(statusId) {
    let url = '/rest/admin/status/visible/change',
        formData = {
            statusId: statusId,
            invisible: false
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (status) {
            $.ajax({
                type: 'GET',
                url: "/status/get/" + statusId,
                success: function (strHTML) {
                    var statusListHtml = document.getElementById('status-columns');
                    statusListHtml.insertAdjacentHTML('afterBegin', strHTML);
                    drawingClientsInStatus(statusId);
                }
            })
            $('#invisibleStatuses' + formData.statusId).remove();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

$(document).ready(function () {
    // обработчик кнопок для возврата клиента из отложки
    //  в all-clients-table
    $(document).on('click', '.from-postpone', function returnClientFromPostpone() {
        var button = $(this);
        var clientId = this.getAttribute('data-client');
        var url = '/rest/client/remove/postpone';
        $.ajax({
            type: 'POST',
            url: url,
            data: {clientId: clientId},
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
            id: this.value,
            create: this.checked
        }
    });
});

function showAllStatuses() {
    let element = $('#all-statuses-positions-table tbody');
    $.ajax({
        type: 'GET',
        url: "/rest/status/all/dto-position-id",
        success: function (dtoes) {
            element.empty();
            for (let i = 0; i < dtoes.length; i++) {
                element.append("<tr>" +
                    "<td hidden>" + dtoes[i].id + "</td>" +
                    "<td hidden>" + dtoes[i].position + "</td>" +
                    "<td>" + dtoes[i].statusName + "</td>" +
                    "</tr>")
            }
        }
    });
}

$(function () {
    $("#all-statuses-positions-table-body").sortable({
        connectWith: ".connectedSortable",
        stop: function () {
            $('#statuses-position-button').empty();
            var table = document.getElementById("all-statuses-positions-table");
            let dtos = [];
            for (var i = 0; i < table.rows.length; i++) {
                var row = table.rows[i];
                var id = row.cells[0].textContent;
                let position = row.cells[1].textContent;
                let dto = {
                    id: id,
                    position: position
                }
                dtos.push(dto);
            }
            let jsonDtos = JSON.stringify(dtos);
            $.ajax({
                url: "/rest/status/position/change",
                data: jsonDtos,
                contentType: "application/json",
                type: 'PUT',
                dataType: 'JSON',
                success: function (returnObj) {
                },
                error: function (error) {
                    console.log(error);
                }
            });
            $('#statuses-position-button').empty().append('<button class="btn btn-info btn-sm" id="button-statuses-position" onclick="reload()">Обновить страницу</button>');
        }
    }).disableSelection();
});

function reload() {
    window.location.reload();
}