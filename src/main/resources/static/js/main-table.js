$(document).ready(function () {
    $(".column").sortable({
        connectWith: ".column",
        handle: ".portlet-header",
        cancel: ".portlet-toggle",
        start: function (event, ui) {
            ui.item.addClass('tilt');
            tilt_direction(ui.item);
        },
        stop: function (event, ui) {
            ui.item.removeClass("tilt");
            $("html").unbind('mousemove', ui.item.data("move_handler"));
            ui.item.removeData("move_handler");
            senReqOnChangeStatus(ui.item.attr('value'), ui.item.parent().attr('value'))
        }
    });

    $(".portlet")
        .addClass("panel panel-default")
        .find(".portlet-header")
        .addClass("panel-heading");

    $("#create-new-status-span").click(function () {
        $(this).hide();
        $("#new-status-form").show();
        document.getElementById("new-status-name").focus();
    });

   /* $("#new-status-form").focusout(
        function () {
            $(this).hide();
            $("#create-new-status-span").show();
        });*/
});

function deleteStatus(id) {
    let url = '/admin/rest/status/delete';
    let formData = {
        deleteId: id
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
};

function createNewUser() {
    let url = '/rest/user/addUser';

    let wrap = {
        name: $('#new-user-first-name').val(),
        lastName: $('#new-user-last-name').val(),
        phoneNumber: $('#new-user-phone-number').val(),
        email: $('#new-user-email').val(),
        age: $('#new-user-age').val(),
        sex: $('#sex').val()
    };


    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        success: function (result) {
            location.reload();
        },
        error: function (e) {

        }
    });
}

function createNewStatus() {
    let url = '/admin/rest/status/add';
    let statusName = $('#new-status-name').val();

    let formData = {
        statusName: statusName
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            window.location.reload();
        },
        error: function (e) {
        }
    });
}


function senReqOnChangeStatus(clientId, statusId) {
    let
        url = '/admin/rest/status/change',
        formData = {
            clientId: clientId,
            statusId: statusId
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (data) {
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

function assign(id) {
    let
        url = '/admin/rest/client/assign',
        formData = {
            clientId: id,
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            $('#assign-client' + id).remove();
            $('#info-client' + id).append(
                "<p class='user-icon'>" +
                    owner.firstName.substring(0,1) + owner.lastName.substring(0,1) +
                "</p>"
            );
        },
        error: function (error) {
        }
    });
}



