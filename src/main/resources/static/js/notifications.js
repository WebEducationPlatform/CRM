var clearNotifications = function clearClientSmsNotifications(id) {
    let request = "/user/notification/sms/clear/" + id;
    $.ajax({
        type: "POST",
        dataType: "json",
        url: request,
        success: function () {
            $(".sms-error-btn[data-id=" + id + "]").hide();
            $('.menu' + id).remove();
        },
        error: function (error) {
            console.log(error);
        }
    })
};

function markAsReadMenu(clientId, showClientModal) {

    if ($('.notify').length) {

        if (showClientModal == 1) {
            showModal(clientId);
        }

        var url = "/user/notification/comment/clear/" + clientId;
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: url,
            success: function () {
                //$('#not-bar').load(location.href + ' #not-bar');
                $('#info-client' + clientId).find(".notification").remove();
                $('.menu' + clientId).remove();
                $('#notification-postpone' + clientId).hide();
                if ($('.notify').length == 0) {
                    $("#bell").css("color", "");
                    $("#bucket").hide();
                    $("#noNotify").text("У вас нет новых оповещений");
                }
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function cleanAll() {
    if ($('.notify').length) {

        var url = "/user/notification/comment/cleanAll";
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: url,
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    $('#info-client' + data[i].id).find(".notification").remove();
                    $('.menu' + data[i].id).remove();
                    $('#notification-postpone' + data[i].id).hide();
                }
                $("#bell").css("color", "");
                $("#bucket").hide();
                $("#noNotify").text("У вас нет новых оповещений");
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}
