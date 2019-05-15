$("#conversations-send-btn").click(function sendMessage() {
    let text = $("#conversations-text").val();
    let sn = $("#send-selector").prop('value');
    send_message(text, sn, get_sn_by_type(sn).id);
});

function get_sn_by_type(type) {
    for (i = 0; i < interlocutor_profiles.length; i++) {
        if (interlocutor_profiles[i].chatType === type) {
            return interlocutor_profiles[i];
        }
    }
    return null;
}

function scroll_down() {
    conversations.scrollTop(conversations.prop("scrollHeight"));
}

function mark_as_read() {
    let clientId = $("#main-modal-window").data('clientId');
    $.ajax({
        type: 'GET',
        url: '/rest/conversation/last-read',
        data: {id: clientId},
        success: function (response) {
            let telegram_sent = $(".telegram_sent");
            if (response.telegram != "" && telegram_sent.length != 0) {
                for (let value of telegram_sent) {
                    let id = parseInt(value.id.substring(17));
                    let last_read = parseInt(response.telegram);
                    if (id <= last_read) {
                        let img = $("#" + value.id);
                        img.prop('src', '/images/rad.png');
                        img.prop('class', 'telegram_rad');
                    }
                }
            }
            let vk_sent = $(".vk_sent");
            if (response.vk != "" && vk_sent.length != 0) {
                for (let value of vk_sent) {
                    let id = parseInt(value.id.substring(11));
                    let last_read = parseInt(response.vk);
                    console.log(value.id);
                    console.log(last_read);
                    if (id <= last_read) {
                        let img = $("#" + value.id);
                        img.prop('src', '/images/rad.png');
                        img.prop('class', 'vk_rad');
                    }
                }
            }

            let whatsapp_sent = $(".whatsapp_sent");
            if (response.whatsapp != "" && whatsapp_sent.length != 0) {
                for (let value of whatsapp_sent) {
                    let id = parseInt(value.id.substring(17));
                    let last_read = parseInt(response.whatsapp);
                    console.log(value.id);
                    console.log(last_read);
                    if (id <= last_read) {
                        let img = $("#" + value.id);
                        img.prop('src', '/images/rad.png');
                        img.prop('class', 'whatsapp_rad');
                    }
                }
            }
        }
    })
}



function update_chat() {
    let clientId = $("#main-modal-window").data('clientId');
    $.ajax({
        type: 'GET',
        url: '/rest/conversation/all-new',
        data: {id: clientId},
        success: function (response) {
            // console.log(response);
            if (response.totalCount === 0) {return true}
            for (let i in response) {
                let message_id = response[i].id;
                let send_date = new Date(response[i].time);
                let text = response[i].text;
                let is_outgoing = response[i].outgoing;
                let is_read = response[i].read;
                let sn_type = response[i].chatType;
                append_all_chats_message(message_id, send_date, text, is_outgoing, is_read, sn_type);
            }
            mark_as_read();
        },
        complete: function(){
            let display = $('#conversations-modal').css('display');
            if (display === 'block') {
                setTimeout(update_chat,3000);
            }
        }
    })
};

function send_message(text, chat_type, chat_id) {
    $.ajax({
        type: 'POST',
        url: '/rest/conversation/send',
        data: {text: text, type: chat_type, chatId: chat_id},
        success: function (response) {
            $("#conversations-text").val('');
            append_all_chats_message(response.id, new Date(), text, true, false, chat_type);
        }
    })
}

function select_interlocutor(profiles) {
    if (interlocutor_profiles === undefined) {
        let clientId = $("#main-modal-window").data('clientId');
        get_interlocutors(clientId);
        profiles = interlocutor_profiles;
    }
    if (logged_in_profiles === undefined) {
        get_us();
        profiles = logged_in_profiles;
    }
    let result = new Map();
    let i;
    for (i = 0; i < profiles.length; i++) {
        switch (profiles[i].chatType) {
            case "vk":
                if (profiles[i].profileUrl !== "" && profiles[i].avatarUrl !== "") {
                    result.set(1, profiles[i]);
                }
                break;
            case "whatsapp":
                if (profiles[i].profileUrl !== "" && profiles[i].avatarUrl !== "") {
                    result.set(2, profiles[i]);
                }
                break;
            case "telegram":
                if (profiles[i].profileUrl !== "" && profiles[i].avatarUrl !== "") {
                    result.set(3, profiles[i]);
                }
                break;
            case "slack":
                if (profiles[i].profileUrl !== "" && profiles[i].avatarUrl !== "") {
                    result.set(4, profiles[i]);
                }
                break;
        }
    }
    let mapAsc = new Map([...result.entries()].sort());
    return mapAsc.values().next().value;
}

function get_sn_picture(sn_type) {
    let icon = "/images/";
    switch (sn_type) {
        case "vk":
            icon += "vk.png";
            break;
        case "whatsapp":
            icon += "whatsapp.png";
            break;
        case "telegram":
            icon += "telegram.png";
            break;
        case "slack":
            icon += "slack_on.png";
            break;
    }
    return "<img class='sn-icon img-circle' src='" + icon + "' alt='?' style='height: 15px; width: 15px'/>";
}

function append_all_chats_message(message_id, send_date, text, is_outgoing, isRead, sn_type) {
    let interlocutor = select_interlocutor(interlocutor_profiles);
    let current_profile = select_interlocutor(logged_in_profiles);
    let chat = $("#chat-messages");
    let sendDate = send_date.toLocaleDateString() + ' ';
    let now = new Date();
    let messageDay = new Date(send_date.getFullYear(), send_date.getMonth(), send_date.getDate());
    let today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    if (messageDay >= today){
        sendDate = "";
    }
    sendDate += send_date.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
    let avatar = "";
    let alt = "";
    let is_read = "";
    let full_name = "";
    let chat_img = get_sn_picture(sn_type);
    if (is_outgoing) {
        alt = current_profile.representation[0];
        if (current_profile.chatType === "vk") {
            avatar = "<a href='" + current_profile.profileUrl + "'><img class='out-photo img-circle' src='" + current_profile.avatarUrl + "' alt='" + alt + "' style='height: 50px; width: 50px'/></a>";
            full_name = "<a href='" + current_profile.profileUrl + "'>" + current_profile.representation + "</a>";
        } else {
            avatar = "<img class='out-photo img-circle' src='data:image/jpeg;base64," + current_profile.avatarUrl + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
            full_name = current_profile.representation;
        }
        if (isRead) {
            is_read = "<img id='" + sn_type + "_is_read_" + message_id + "' class='" + sn_type + "_rad' src='/images/rad.png' style='height: 15px; width: 15px' />";
        } else {
            is_read = "<img id='" + sn_type + "_is_read_" + message_id + "' class='" + sn_type + "_sent' src='/images/sent.png' style='height: 15px; width: 15px' />";
        }
    } else {
        alt = interlocutor.representation;
        if (interlocutor.chatType === "vk") {
            avatar = "<a href='" + interlocutor.profileUrl + "'><img class='out-photo img-circle' src='" + interlocutor.avatarUrl + "' alt='" + alt + "' style='height: 50px; width: 50px'/></a>";
            full_name = "<a href='" + interlocutor.profileUrl + "'>" + interlocutor.representation + "</a>";
        } else {
            avatar = "<img class='out-photo img-circle' src='data:image/jpeg;base64," + interlocutor.avatarUrl + "' alt='" + alt + "' style='height: 50px; width: 50px'/>";
            full_name = interlocutor.representation;
        }
    }
    let dom = $("<div class='container message-chat "+ ' ' +"' id='" + sn_type + "_message_id_" + message_id + "' style='padding-top: 10px;'>"+
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
        sendDate + " " + chat_img + " " + is_read +
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

    $("#send-selector").prop('value', sn_type);

    scroll_down();
}

function set_telegram_id_by_phone(phone) {
    if (phone === undefined || phone === '' || phone === null) {return;}
    $.ajax({
        type: 'GET',
        async: true,
        url: '/rest/telegram/id-by-phone',
        data: {phone: phone},
        success: function (response) {
            return parseInt(response);
        }
    })
}


function start_chats(clientId) {
    $.ajax({
        type: "GET",
        url: "/rest/conversation/all",
        data: {id: clientId},
        success: function (response) {
            $("#chat-messages").empty();
            for (let i in response) {
                let message_id = response[i].id;
                let send_date = new Date(response[i].time);
                let text = response[i].text;
                let is_outgoing = response[i].outgoing;
                let is_read = response[i].read;
                let sn_type = response[i].chatType;
                append_all_chats_message(message_id, send_date, text, is_outgoing, is_read, sn_type);
            }
            $("#send-selector").prop('value', response[response.length - 1].chatType);
            setTimeout(update_chat, 2000);
            setTimeout(scroll_down, 1000);
        }
    })
}

let conversations = $("#conversations-body");

$('#conversations-modal').on('show.bs.modal', function () {
    let clientId = $("#main-modal-window").data('clientId');
    set_send_selector(clientId);
    start_chats(clientId);
});

$('#conversations-modal').on('hidden.bs.modal', function () {
    $('#main-modal-window').css('overflow-y', 'auto');
    let clientId = $("#main-modal-window").data('clientId');
    $("#chat-messages").empty();
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/messages/chat/close',
        data: {clientId: clientId}
    });
});

function set_send_selector(clientId) {
    let selector = $("#send-selector");
    selector.empty();
    $.ajax({
        type: "GET",
        url: "/rest/client/" + clientId,
        success: function (client) {
            for (let i = 0; i < client.socialProfiles.length; i++) {
                switch (client.socialProfiles[i].socialNetworkType.name) {
                    case 'vk':
                        selector.append("<option id='send-vk' value='vk'>Отправить в ВК</option>");
                        break;
                    case 'telegram':
                        selector.append("<option id='send-telegram' value='telegram'>Отправить в Telegram</option>");
                        break;
                    case 'whatsapp':
                        selector.append("<option id='send-whatsapp' value='whatsapp'>Отправить в WhatsApp</option>");
                        break;
                    case 'slack':
                        selector.append("<option id='send-slack' value='slack'>Отправить в Slack</option>");
                        break;
                }
            }
        }
    })
}