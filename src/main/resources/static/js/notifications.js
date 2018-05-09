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

//Перенесено из comment.js
function markAsRead(clientId) {
    var url = "/rest/comment/markAsRead";
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id: clientId
        },
        success: function () {
            $('#info-client' + clientId).find(".notification").remove();
            $('.menu' + clientId).remove();

        },
        error : function (error) {
            console.log(error);
        }
    })
}

//Перенесено из comment.js
function markAsReadMenu(clientId) {
    var url = "/rest/comment/markAsRead";
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id: clientId
        },
        success: function () {
            $('#info-client' + clientId).find(".notification").remove();
            $('.menu' + clientId).remove();
        },
        error : function (error) {
            console.log(error);
        }
    })
}