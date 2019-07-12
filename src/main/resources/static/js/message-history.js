$(function() {
    $('#message-history-modal').on('show.bs.modal', function ShowEmailMessages() {
        var clientId = getAllUrlParams(window.location.href).id;
        let url = '/client/history/rest/getEmailHistory/' + clientId;
        $.get(url, function (messageList) {
            $("#message-data").empty();
            for (var i = 0; i < messageList.length; i++) {
                var message = messageList[i];
                var dstr = message[0].substring(0, 24);
                let d = new Date(message[0].substring(0, 25));
                let date = ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." +
                    d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
                var content = message[2].replace(/<img[^>]*>/gi,"");

                $("#message-data").append('Автор: ' + message[1] + ' | ' + 'Дата: ' + date + '<br>' + 'Сообщение: '  +  '<pre>' + content +  '</pre>');
            }
        })

    });
});