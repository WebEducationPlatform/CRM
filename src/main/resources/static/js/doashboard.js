$(document).ready(function () {
    $.ajaxSetup({
        xhrFields: {
            withCredentials: true
        }
    });

    getUserLoggedIn(true);
    get_us();
    clientsSearch();
    statusesSearch();

    //Отслеживаем нажатие клавиши Enter при создании нового статуса
    $("#new-status-name").keypress(function (e) {
        if (e.keyCode === 13) {
            createNewStatus();
        }
    });

    $("#create-new-status-btn").click(function () {
        $(this).hide();
        $("#new-status-form").show();
        document.getElementById("new-status-name").focus();
    });

    $("#create-new-status-cancelbtn").click(function () {
        $("#new-status-form").hide();
        $("#create-new-status-btn").show();
    });

    $("#create-new-board-btn").click(function () {
        $(this).hide();
        $("#new-board-form").show();
        document.getElementById("new-board-name").focus();
    });

    $("#create-new-board-cancelbtn").click(function () {
        $("#new-board-form").hide();
        $("#create-new-board-btn").show();
    });

    $(".sms-error-btn").on("click", function smsInfoModalOpen() {
        let modal = $("#sms_error_modal"),
            btn = $(this),
            url = '/user/notification/sms/error/' + btn.attr("data-id");
        $.get(url, function () {
        }).done(function (notifications) {
            let body = modal.find("tbody");
            for (let i = 0; i < notifications.length; i++) {
                body.append(
                    "<tr><td>" + notifications[i].information + "</td></tr>"
                )
            }
        });
        modal.find("#clear_sms_errors").attr("onClick", "clearNotifications(" + btn.attr("data-id") + ")");
        modal.modal();
    });

    $("#sms_error_modal").on('hidden.bs.modal', function () {
        $('#main-modal-window').css('overflow-y', 'auto');
        let modal = $(this);
        modal.find("tbody").empty();
    });

    $("#createDefaultStatus").modal({
        backdrop: 'static',
        keyboard: false
    }, 'show');
});

function drawingClientsInStatus(statusId) {
    $.get("/rest/client/order", {statusId: statusId})
        .done(function (order) {
            $("#" + order + statusId).addClass("active");
        });
    let url = "/status/" + statusId;
    $("#clients-for-status" + statusId).load(url, function() {
        cardsMotion(this);
    });
}

//Отрисовка карточек клиентов в статусах
$(document).ready(function () {
    showUsersInStatuses();
});
//Отрисовка карточек клиентов в статусах, как бы тоже самое, что и сверху,
// вытащил изнутри, чтобы превратить в отдельную функцию!
function showUsersInStatuses() {
    let statuses = $(".column");
    for (var i = 0; i < statuses.length; i++) {
        let statusId = $(statuses[i]).attr("value");
        drawingClientsInStatus(statusId);
    }
}

//func responsible for the client's cards motion
function cardsMotion(element) {
    $(element).sortable({
        delay: 100,
        items: '.portlet',
        connectWith: ".clients-cards",
        handle: ".portlet-title, .portlet-header",
        cancel: ".portlet-toggle",
        start: function (event, ui) {
            ui.item.addClass('tilt');
            tilt_direction(ui.item);
        },
        stop: function (event, ui) {
            ui.item.removeClass("tilt");
            $("html").unbind('mousemove', ui.item.data("move_handler"));
            ui.item.removeData("move_handler");
            senReqOnChangeStatus(ui.item.attr('value'), ui.item.parent().parent().attr('value'));
        }
    });
}

//Меняем стили (наклон - право/лево) при перетаскивании карточки клиента
function tilt_direction(item) {
    var left_pos = item.position().left,
        move_handler = function (e) {
            if (e.pageX >= left_pos) {
                item.addClass("right");
                item.removeClass("left");
            } else {
                item.addClass("left");
                item.removeClass("right");
            }
            left_pos = e.pageX;
        };
    $("html").bind("mousemove", move_handler);
    item.data("move_handler", move_handler);
}

//Поменять позицию статуса на доске - похоже не работает, есть другая функция!
$(".change-status-position").on('click', function () {
    let destinationId = $(this).attr("value");
    let sourceId = $(this).parents(".column").attr("value");
    let url = "/rest/status/position/change";
    let formData = {
        sourceId: sourceId,
        destinationId: destinationId
    };
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function () {
            location.reload();
        },
        error: function (error) {

        }
    });
});

function openDescriptionModal(id) {
    var infoClient = $('#info-client' + id);
    var text = infoClient.find('.client-description').text();
    var clientModal = $('#clientDescriptionModal');
    $("#save-description").attr("data-id", id);

    clientModal.find('textarea').val(text);
    clientModal.modal('show');
}

function assignUser(id, user, principalId) {
    var
        url = '/rest/client/assign/user',
        formData = {
            clientId: id,
            userForAssign: user
        },
        assignBtn = $('#assign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            let info_client = $('#info-client' + id),
                target_btn = $("a[href='/client/clientInfo/" + id + "']"),
                unassign_btn = $('#unassign-client' + id);
            info_client.find("p[style*='display:none']").remove();
            info_client.find(".user-icon_card").remove();

            //If admin assigned himself
            // if(principalId === user){
            //     //If admin assigned himself second time
            //     if(unassign_btn.length === 0){
            //         target_btn.before(
            //             "<button " +
            //             "   id='unassign-client" + id +"' " +
            //             "   onclick='unassign(" + id +")' " +
            //             "   class='btn btn-sm btn-warning'>Отказаться от карточки</button>"
            //         );
            //     }
            //If admin not assign himself, he don`t have unassign button
            // }else {
            //     unassign_btn.remove();
            // }
            assignBtn.remove();

            //Add Worker icon and info for search by worker
            let mentorCard = info_client.find(".mentor-icon_card");
            let html = "<span class='user-icon_card' id='own-" + id + "' value='" + owner.firstName + " " + owner.lastName + "'>" +
                owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</span>" +
                "<p style='display:none'>" + owner.firstName + " " + owner.lastName + "</p>";
            if (mentorCard.length != 0) {
                mentorCard.before(html);
            } else {
                info_client.append(html);
            }
            fillFilterList()
        },
        error: function (error) {
        }
    });
}

function assignMentor(id, user, principalId) {
    var
        url = '/rest/client/assign/mentor',
        formData = {
            clientId: id,
            userForAssign: user
        },
        assignBtn = $('#assign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            assignBtn.before(
                "<button " +
                "   id='unassign-client" + id + "' " +
                "   onclick='unassignMentor(" + id + ")' " +
                "   class='btn btn-sm btn-warning remove-tag'>Отказаться от карточки</button>"
            );
            assignBtn.remove();
            let info_client = $('#info-client' + id),
                target_btn = $("a[href='/client/clientInfo/" + id + "']"),
                unassign_btn = $('#unassign-client' + id);
            info_client.find("span[style*='display:none']").remove();
            info_client.find(".mentor-icon_card").remove();

            info_client.append(
                "<span class='mentor-icon_card' id='mn-" + id + "' value='" + owner.firstName + " " + owner.lastName + "'>" +
                "Ментор: " + owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</span>" +
                "<span style='display:none'>" + owner.firstName + " " + owner.lastName + "</span>" +
                "<span class='ownerMentorId' style='display:none'>" + owner.id + "</span>"
            );
            fillFilterList()
        },
        error: function (error) {
        }
    });
}

//Change status button
function changeStatusName(id) {

    let url = '/rest/admin/status/edit';
    let statusName = $("#change-status-name" + id).val();
    let trial_offset = parseInt($("#trial_offset_" + id).val());
    let next_payment_offset = trial_offset +  parseInt($("#next_payment_offset_" + id).val());
    let templateId = $("#edit-status-template" + id ).find("option:selected").val();

    var $sel = $("#checkbox_status_roles_" + id ).find("input[type=checkbox]:checked");
    var stRoles = [];
    $sel.each(function (index, sel) {
        var obj = {};
        obj["id"] = sel.value;
        obj["roleName"] = $(sel).next().text();
        stRoles.push(obj);
    });
    if (!validate_status_input(trial_offset, next_payment_offset)) {
        return
    };
    let formData = {
        id: id,
        name: statusName,
        trialOffset: trial_offset,
        nextPaymentOffset: next_payment_offset,
        templateId: templateId,
        role: stRoles
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(formData),
        success: function (result) {
            //Насколько тут нужна перезагрузка страницы??
            window.location.reload();
        },
        error: function (e) {
            alert(e.responseText);
        }
    });
}

//Status offset dates validation
function validate_status_input(trial_offset, next_payment_offset) {
    if (trial_offset > next_payment_offset) {
        alert("Отступ даты пробного периода не может быть больше отступа даты следующей оплаты!");
        return false;
    }
    return true;
}

function currentStatus(id) {
    var spans = $("#current_status_roles_" + id).find("span");
    var currRoles = [];
    spans.each(function () {
        currRoles.push($(this)[0].getAttribute("value"))
    });
    $("#checkbox_status_roles_" + id).find('input').each(function () {
        if (currRoles.indexOf($(this)[0].value) !== -1) {
            $(this).attr("checked", "checked");
        }
    })
}

function createNewStatus() {
    let url = '/rest/status/add';
    let statusName = $('#new-status-name').val() || $('#default-status-name').val();
    let currentStatus = document.getElementById("sendSocialTemplateStatus");
    let boardId = $('#create-new-status').val();

    if (typeof statusName === "undefined" || statusName === "") return;
    let formData = {
        statusName: statusName,
        boardId: boardId
    };

    if(statusName.length < 25) {
        $.ajax({
            type: "POST",
            url: url,
            data: formData,
            success: function (result) {
                //Насколько тут нужна перезагрузка страницы??
                window.location.reload();
            },
            error: function (e) {
                alert(e.responseText);
                console.log(e.responseText);
            }
        });
    }
    else {
        currentStatus.style.color = "red";
        currentStatus.textContent = "Название уменьши ка, будь человеком";
    }
}

function createNewBoard() {
    let url = '/rest/board/add';
    let boardName = $('#new-board-name').val() || $('#default-board-name').val();
    let currentBoard = document.getElementById("sendSocialTemplateBoard");

    if (typeof boardName === "undefined" || boardName === "") return;
    let formData = {
        boardName: boardName
    };

    if(boardName.length < 25) {
        $.ajax({
            type: "POST",
            url: url,
            data: formData,
            success: function (result) {
                //Насколько тут нужна перезагрузка страницы??
                window.location.reload();
            },
            error: function (e) {
                alert(e.responseText);
                console.log(e.responseText);
            }
        });
    }
    else {
        currentBoard.style.color = "red";
        currentBoard.textContent = "Название уменьши ка, будь человеком";
    }
}

//Сохранить комментарий на лицевой стороне карточки
//Не работает ввиду отсутствия класса .client-description у элементов страницы!
function saveDescription() {
    let text = $('#clientDescriptionModal').find('textarea').val();
    let id = $("#save-description").attr("data-id");
    let
        url = '/rest/client/addDescription',
        formData = {
            clientId: id,
            clientDescription: text
        };
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            $("#info-client" + id).find('.client-description').text(text);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
    $('#clientDescriptionModal').modal('hide');
}
