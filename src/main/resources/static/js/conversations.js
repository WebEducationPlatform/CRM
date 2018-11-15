$("#conversations-send-btn").click(function sendMessage() {
    let text = $("#conversations-text").val();
    let clientId = $("#main-modal-window").data('clientId');
    let sn = $("#send-selector").prop('value');
    switch(sn) {
        case 'vk':
            break;
        case 'telegram':
            send_telegram(clientId, text);
            break;
        case 'whatsapp':
            break;
    }

});

function update_chat() {
    let clientId = $("#main-modal-window").data('clientId');
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/messages/unread',
        data: {clientId: clientId},
        success: function (response) {
            let data = response.messages.reverse();
            for (let i in data) {
                let message_id = data[i].id;
                let send_date = new Date(data[i].date * 1000);
                let text = data[i].content.hasOwnProperty('text') ? data[i].content.text.text : 'Sticker!';
                append_message(message_id, send_date, text);
            }
            $("#send-selector").prop('value', 'telegram');
        }
    })
};

function send_telegram(clientId, text) {
    $.ajax({
        type: 'POST',
        url: '/rest/telegram/message/send',
        data: {clientId: clientId, text: text},
        success: function () {
            $("#conversations-text").val('');
            append_message(0, new Date(), text);
        }
    })
}

function append_message(message_id, send_date, text) {
    let chat = $("#chat-messages");
        let sendDate = send_date.toLocaleDateString() + ' ';
        if (send_date.getDate() === new Date().getDate()){
            sendDate = "";
        }
        sendDate += send_date.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
        var dom = $("<div class='container message-chat "+ ' ' +"' id='message_id" + message_id + "' style='padding-top: 10px;'>"+
            "<div class='row'> "+
            "<div class='col-xs-1'>"+
            // "<a href='https://vk.com/"+currentUnit+currentID+"' target='_blank'>" +
            "<img class='vk-im-photo img-circle' src='"+ 'photo' +"' class='img-circle' id='vkPhotoId"+ 'fromid' +"'/>" +
            "</a>"+
            "</div>"+
            "<div class='col-xs-11'>"+
            "<div class='row-xs-12'>" +
            "<div class='col-sm-4'>" +
            // "<a href='https://vk.com/"+currentUnit+currentID+"' target='_blank'>"+name+"</a>"+
            "</div>"+
            "<div class='col-sm-8'>" +
            sendDate + " " +
            "</div>"+
            "</div>"+
            "<div class='row-xs-auto'>"+
            "<div class='col-sm-11' id='message_id"+ message_id +"'>" +
            text +
            "</div>"+
            "</div>"+
            "</div>"+
            "</div>");
        chat.append(dom);

        chat.stop().animate({
            scrollTop: 100000
        }, 800);
}
