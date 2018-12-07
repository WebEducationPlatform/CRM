let last_telegram_message_id = 0;

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
        url: '/rest/telegram/messages/chat/unread',
        data: {clientId: clientId},
        success: function (response) {
            if (response.totalCount === 0) {return true}
            last_telegram_message_id = response.messages[0].id;
            let data = response.messages.reverse();
            for (let i in data) {
                let message_id = data[i].id;
                let send_date = new Date(data[i].date * 1000);
                let text = data[i].content.hasOwnProperty('text') ? data[i].content.text.text : 'Error: Stickers and photos not supported!';
                append_message(message_id, send_date, text);
            }
            $("#send-selector").prop('value', 'telegram');
        },
        complete: function(){
            let display = $('#conversations-modal').css('display');
            if (display === 'block') {
                setTimeout(update_chat,3000);
            }
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

function append_message(message_id, send_date, text, is_outgoing) {
    let chat = $("#chat-messages");
    let sendDate = send_date.toLocaleDateString() + ' ';
    if (send_date.getDate() === new Date().getDate()){
        sendDate = "";
    }
    sendDate += send_date.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
    let avatar = "";
    if (is_outgoing) {
        let alt = telegram_me.firstName[0] + telegram_me.lastName[0];
        avatar = "<img class='tg-im-photo img-circle' src='data:image/jpeg;base64," + telegram_me_photo + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    } else if (telegram_user_photo == null) {
        let alt = telegram_user.firstName[0] + telegram_user.lastName[0];
        avatar = "<img class='tg-im-photo img-circle' src='/images/t_logo.png' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    } else {
        let alt = telegram_user.firstName[0] + telegram_user.lastName[0];
        avatar = "<img class='tg-im-photo img-circle' src='data:image/jpeg;base64," + telegram_user_photo + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    }
    let dom = $("<div class='container message-chat "+ ' ' +"' id='telegram_message_id_" + message_id + "' style='padding-top: 10px;'>"+
        "<div class='row'> "+
            "<div class='col-xs-1'>"+
        // "<img class='vk-im-photo img-circle' src='"+ 'photo' +"' class='img-circle' id='vkPhotoId"+ 'fromid' +"'/>" +
                avatar +
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
