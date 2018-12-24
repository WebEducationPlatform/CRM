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

function scroll_down() {
    conversations.scrollTop(conversations.prop("scrollHeight"));
}

function mark_as_read(last_read) {
    let messages = $(".sent");
    if (messages.length === 0) {return;}
    for (let value of messages) {
        let id = parseInt(value.id.substring(8));
        if (id <= last_read) {
            let img = $("#" + value.id);
            img.prop('src', '/images/rad.png');
            img.prop('class', 'rad');
        }
    }
}

function update_chat() {
    let clientId = $("#main-modal-window").data('clientId');
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/messages/chat/unread',
        data: {clientId: clientId},
        success: function (response) {
            let messages = response.messages.messages;
            let last_read = response.chat.lastReadOutboxMessageId;
            if (response.totalCount === 0) {return true}
            let data = messages.reverse();
            for (let i in data) {
                let message_id = data[i].id;
                let send_date = new Date(data[i].date * 1000);
                let text = data[i].content.hasOwnProperty('text') ? data[i].content.text.text : 'Stickers/photo!';
                let is_outgoing = data[i].isOutgoing;
                append_message(message_id, send_date, text, is_outgoing, last_read);
            }
            mark_as_read(last_read);
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
        success: function (response) {
            $("#conversations-text").val('');
            append_message(response.id, new Date(), text, true);
        }
    })
}

function append_all_chats_message(message_id, send_date, text, is_outgoing, isRead, sn_type) {
    if (interlocutor_profiles === undefined) {
        let clientId = $("#main-modal-window").data('clientId');
        get_interlocutors(clientId);
    }
    let chat = $("#chat-messages");
    let sendDate = send_date.toLocaleDateString() + ' ';
    if (send_date.getDate() === new Date().getDate()){
        sendDate = "";
    }
    sendDate += send_date.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
    let avatar = "";
    let alt = "";
    let is_read = "";
    let full_name = "";
    if (is_outgoing) {
        alt = telegram_me.firstName[0] + telegram_me.lastName[0];
        full_name = telegram_me.firstName + " " + telegram_me.lastName;
        avatar = "<img class='tg-im-photo img-circle' src='data:image/jpeg;base64," + telegram_me_photo + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
        if (message_id <= last_read) {
            is_read = "<img id='is_read_" + message_id + "' class='rad' src='/images/rad.png' style='height: 15px; width: 15px' />";
        } else {
            is_read = "<img id='is_read_" + message_id + "' class='sent' src='/images/sent.png' style='height: 15px; width: 15px' />";
        }
    } else if (telegram_user_photo == null) {
        alt = telegram_user.firstName[0] + telegram_user.lastName[0];
        full_name = telegram_user.firstName + " " + telegram_user.lastName;
        avatar = "<img class='tg-im-photo img-circle' src='/images/t_logo.png' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    } else {
        alt = telegram_user.firstName[0] + telegram_user.lastName[0];
        full_name = telegram_user.firstName + " " + telegram_user.lastName;
        avatar = "<img class='tg-im-photo img-circle' src='data:image/jpeg;base64," + telegram_user_photo + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    }
    let dom = $("<div class='container message-chat "+ ' ' +"' id='telegram_message_id_" + message_id + "' style='padding-top: 10px;'>"+
        "<div class='row'> "+
        "<div class='col-xs-1'>"+
        avatar +
        "</div>"+
        "<div class='col-xs-11'>"+
        "<div class='row-xs-12'>" +
        "<div class='col-sm-4' style='font-weight: bold'>" +
        full_name +
        "</div>"+
        "<div class='col-sm-8' style='color: grey'>" +
        sendDate + " " + is_read +
        "</div>"+
        "</div>"+
        "<div class='row-xs-auto'>"+
        "<div class='col-sm-11' id='message_id"+ message_id +"' style='width: 500px;white-space: pre-line;'>" +
        text +
        "</div>"+
        "</div>"+
        "</div>"+
        "</div>");
    chat.append(dom);

    scroll_down();
}


function append_message(message_id, send_date, text, is_outgoing, last_read) {
    if (telegram_user === undefined) {
        let clientId = $("#main-modal-window").data('clientId');
        get_tg_user(clientId);
    }
    let chat = $("#chat-messages");
    let sendDate = send_date.toLocaleDateString() + ' ';
    if (send_date.getDate() === new Date().getDate()){
        sendDate = "";
    }
    sendDate += send_date.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
    let avatar = "";
    let alt = "";
    let is_read = "";
    let full_name = "";
    if (is_outgoing) {
        alt = telegram_me.firstName[0] + telegram_me.lastName[0];
        full_name = telegram_me.firstName + " " + telegram_me.lastName;
        avatar = "<img class='tg-im-photo img-circle' src='data:image/jpeg;base64," + telegram_me_photo + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
        if (message_id <= last_read) {
            is_read = "<img id='is_read_" + message_id + "' class='rad' src='/images/rad.png' style='height: 15px; width: 15px' />";
        } else {
            is_read = "<img id='is_read_" + message_id + "' class='sent' src='/images/sent.png' style='height: 15px; width: 15px' />";
        }
    } else if (telegram_user_photo == null) {
        alt = telegram_user.firstName[0] + telegram_user.lastName[0];
        full_name = telegram_user.firstName + " " + telegram_user.lastName;
        avatar = "<img class='tg-im-photo img-circle' src='/images/t_logo.png' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    } else {
        alt = telegram_user.firstName[0] + telegram_user.lastName[0];
        full_name = telegram_user.firstName + " " + telegram_user.lastName;
        avatar = "<img class='tg-im-photo img-circle' src='data:image/jpeg;base64," + telegram_user_photo + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
    }
    let dom = $("<div class='container message-chat "+ ' ' +"' id='telegram_message_id_" + message_id + "' style='padding-top: 10px;'>"+
        "<div class='row'> "+
        "<div class='col-xs-1'>"+
        avatar +
        "</div>"+
        "<div class='col-xs-11'>"+
        "<div class='row-xs-12'>" +
        "<div class='col-sm-4' style='font-weight: bold'>" +
        full_name +
        "</div>"+
        "<div class='col-sm-8' style='color: grey'>" +
        sendDate + " " + is_read +
        "</div>"+
        "</div>"+
        "<div class='row-xs-auto'>"+
        "<div class='col-sm-11' id='message_id"+ message_id +"' style='width: 500px;white-space: pre-line;'>" +
        text +
        "</div>"+
        "</div>"+
        "</div>"+
        "</div>");
    chat.append(dom);

    scroll_down();
}

function set_telegram_id_by_phone(phone) {
    if (phone === undefined || phone === '' || phone === null) {return;}
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/id-by-phone',
        data: {phone: phone},
        success: function (response) {
            return parseInt(response);
        }
    })
}