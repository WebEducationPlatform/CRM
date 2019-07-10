//Search clients in main
function clientsSearch() {
    $("#search-clients").keyup(function () {
        let jo = $(".portlet");
        let jo2 = jo.find($(".search_text"));
        let data = this.value.toLowerCase().split(" ");
        this.value.localeCompare("") === 0 ? jo.show() : jo.hide();

        for (let i = 0; i < jo2.length; i++) {
            let count = 0;
            for (let z = 0; z < data.length; z++) {
                if (jo2[i].innerText.toLowerCase().includes(data[z])) {
                    count++;
                }
            }
            if (count === data.length) {
                jo[i].style.display = 'block';
            }
        }
    });
}

//func responsible for the client's cards motion
$(document).ready(function () {
    $.ajaxSetup({
        xhrFields: {
            withCredentials: true
        }
    });
    getUserLoggedIn(true);
    get_us();
    $(".column").sortable({
        delay: 100,
        items: '> .portlet',
        connectWith: ".column",
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
            senReqOnChangeStatus(ui.item.attr('value'), ui.item.parent().attr('value'));
        }
    });


    $(document).ready(function () {
        $("#new-status-name").keypress(function (e) {
            if (e.keyCode === 13) {
                createNewStatus();
            }
        });
    });

    $(".portlet")
        .addClass("panel panel-default")
        .find(".portlet-header")
        .addClass("panel-heading");

    $("#create-new-status-btn").click(function () {
        $(this).hide();
        $("#new-status-form").show();
        document.getElementById("new-status-name").focus();
    });

    $("#create-new-status-cancelbtn").click(function () {
        $("#new-status-form").hide();
        $("#create-new-status-btn").show();
    });

    clientsSearch();

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
    })

    $("#sms_error_modal").on('hidden.bs.modal', function () {
        $('#main-modal-window').css('overflow-y', 'auto');
        let modal = $(this);
        modal.find("tbody").empty();
    })
});

//Поменять позицию статуса на доске
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

$(function () {
    $('.open-description-btn').on('click', function (event) {
        var id = $(this).data('id');
        var infoClient = $('#info-client' + id);
        var text = infoClient.find('.client-description').text();
        var clientModal = $('#clientDescriptionModal');
        $("#save-description").attr("data-id", id);

        clientModal.find('textarea').val(text);
        clientModal.modal('show');
    });
});

$(document).ready(function () {
    $("#createDefaultStatus").modal({
        backdrop: 'static',
        keyboard: false
    }, 'show');
    console.log("#createDefaultStatus");
});

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
            info_client.append(
                "<p class='user-icon_card' id='own-" + id + "' value=" + owner.firstName + " " + owner.lastName + ">" +
                owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</p>" +
                "<p style='display:none'>" + owner.firstName + " " + owner.lastName + "</p>"
            );
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
                "<span class='mentor-icon_card' id='mn-" + id + "' value=" + owner.firstName + " " + owner.lastName + ">" +
                "Ментор: " + owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</span>" +
                "<span style='display:none'>" + owner.firstName + " " + owner.lastName + "</span>"
            );
            fillFilterList()
        },
        error: function (error) {
        }
    });
}
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

//Change status button
function changeStatusName(id) {

    let url = '/admin/rest/status/edit';
    let statusName = $("#change-status-name" + id).val();
    let trial_offset = parseInt($("#trial_offset_" + id).val());
    let next_payment_offset = trial_offset +  parseInt($("#next_payment_offset_" + id).val());

    var $sel = $("#checkbox_status_roles_" + id ).find("input[type=checkbox]:checked");
    var stRoles = [];
    $sel.each(function (index, sel) {
        var obj = {};
        obj["id"] = sel.value;
        obj["roleName"] = sel.innerText;
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
        role: stRoles
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(formData),
        success: function (result) {
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

    if (typeof statusName === "undefined" || statusName === "") return;
    let formData = {
        statusName: statusName
    };

    if(statusName.length < 25) {
        $.ajax({
            type: "POST",
            url: url,
            data: formData,
            success: function (result) {
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

//Сохранить комментарий на лицевой стороне карточки
$("#save-description").on("click", function saveDescription() {
    let text = $('#clientDescriptionModal').find('textarea').val();
    let id = $(this).attr("data-id");
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
            $('#clientDescriptionModal').modal('hide');
        },
        error: function (error) {
            console.log(error.responseText);
            $('#clientDescriptionModal').modal('hide');
        }
    })
});
