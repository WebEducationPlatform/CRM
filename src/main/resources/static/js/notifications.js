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