function clearClientSmsNotifications(id) {
    let request = "/user/notification/sms/clear/" + id;
    $.ajax({
        type: "POST",
        dataType : "json",
        url : request,
        success: function () {
            location.reload();
        },
        error : function (error) {
            console.log(error);
        }
    })
}

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
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}
