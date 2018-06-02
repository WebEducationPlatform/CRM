var clearNotifications = function clearClientSmsNotifications(id) {
    let request = "/user/notification/sms/clear/" + id;
    $.ajax({
        type: "POST",
        dataType : "json",
        url : request,
        success: function () {
            $(".sms-error-btn[data-id="+ id +"]").hide();
            $('.menu' + id).remove();
        },
        error : function (error) {
            console.log(error);
        }
    })
};

function markAsReadMenu(clientId) {
    if ($('.notify').length) {

        var url = "/user/notification/comment/clear/" + clientId;
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: url,
            success: function () {
                $('#info-client' + clientId).find(".notification").remove();
                $('.menu' + clientId).remove();
                $('#notification-postpone' + clientId).hide();
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}
