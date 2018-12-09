
function vk_sendMessage(text) {
    sendRequest("messages.send", {user_id:userID, message:text, access_token:accessToken, v:version}, vk_sendMessage_CallBack);
}

function vk_getUnreadMessages() {
    return sendRequest("messages.getConversations", {filter:'unread', group_id:groupID, access_token:accessToken, v:version}, vk_getUnreadMessages_CallBack);
}

function vk_getMessages_CallBack(vk_data){
    var arr = vk_data.response.items.reverse();

    for(var i = 0; i < arr.length; i++){
        vk_addMessage(arr[i].body, arr[i].from_id,arr[i].id, arr[i].date, arr[i].read_state, arr[i].out);
    }
}

function vk_sendMessage_CallBack(vk_data) {
    var textBox =$('#IM-VK-Text');
    var msgid = vk_data.response;
    var text = textBox.val();
    vk_addMessage(text, '-'+groupID, msgid, (new Date().getTime())/1000, 0, 1);
    textBox.val('');
}

function vk_getUnreadMessages_CallBack(vk_data) {

    $('.vk-notification.glyphicon.glyphicon-send.pull-right').each(function(i, elem){
        $(elem).text('');
        $(elem).hide();
    });
    $('#vk-im-count').text('');

    var arr = vk_data.response.items;
    for(var i = 0; i < arr.length; i++){
        var unreadUserID = arr[i].conversation.peer.id;
        var unreadCount  = arr[i].conversation.unread_count;

        var clientId = vkIdMappingClientId[unreadUserID];
        if (clientId == undefined){
            ///{ss}/{link}
            $.when($.get("/rest/client/socialID",{socialProfileType:"vk", userID: unreadUserID, unread: unreadCount})).done(function (result){
                clientId = result.clientID;
                unreadCount = result.unreadCount;
                unreadUserID = result.userID;

                showVkNotification(clientId, unreadUserID, unreadCount);
            });
        }
        else{
            showVkNotification(clientId, unreadUserID, unreadCount);
        }
    }
}

function showVkNotification(clientId, unreadUserID, unreadCount){
    if (clientId != "" && unreadCount != ""){
        vkIdMappingClientId[unreadUserID] = clientId;
        var dom = $('#VK-notification'+clientId);
        dom.text(unreadCount);
        dom.show();

        if ($('#main-modal-window').is(':visible')){
            if ($('#vk-im-button').attr("clientid") == clientId){
                $('#vk-im-count').text(unreadCount);
            }
        }
    }
}

function vk_addMessage(msg, fromid, msgid, date, read_state, out) {
    var vkBody = $("#im-vk-body");

    var currentID = fromid;
    var currentUnit = "id";
    //var style = '';
    var alertInfo ='';//выделение голубым новых сообщений

    if (read_state == 0){
        addAsUnread(msgid);
        unreadMessages.push();
        alertInfo = "alert-info";
    }

    var photo = userData[fldPhotoSize];
    var name  = userData[fldUserName];
    if (out == 1){ //сходящее сообщение
        photo = groupData[fldPhotoSize];
        currentID = groupID;
        name  = groupData[fldName];
        currentUnit = "club";
    }

    var dateTime = new Date(date*1000);
    var now = new Date().getDate();

    var OutTimeTimeString = dateTime.toLocaleDateString()+" ";
    if (dateTime.getDate() == now){
        OutTimeTimeString = "";
    }
    OutTimeTimeString += dateTime.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');

    var msgDiv = $('#vkMsgId'+msgid);
    if (msgDiv.length == 0){


        var dom = $("<div class='conteiner message-vk-im "+alertInfo+"' id='vkMsgId"+msgid+"' style='padding-top: 10px;'>"+
            "<div class='row'> "+
                "<div class='col-xs-1'>"+
                    "<a href='https://vk.com/"+currentUnit+currentID+"' target='_blank'>" +
                        "<img class='vk-im-photo img-circle' src='"+photo+"' class='img-circle' id='vkPhotoId"+fromid+"'/>" +
                    "</a>"+
                "</div>"+
                "<div class='col-xs-11'>"+
                    "<div class='row-xs-12'>" +
                        "<div class='col-sm-4'>" +
                            "<a href='https://vk.com/"+currentUnit+currentID+"' target='_blank'>"+name+"</a>"+
                        "</div>"+
                        "<div class='col-sm-8'>" +
                            OutTimeTimeString +
                        "</div>"+
                    "</div>"+
                    "<div class='row-xs-auto'>"+
                        "<div class='col-sm-11' id='vkTextId"+msgid+"'>" +
                            msg +
                        "</div>"+
                    "</div>"+
                "</div>"+
            "</div>");
        vkBody.append(dom);

        //крутим скролл
        vkBody.stop().animate({
            scrollTop: 100000
        }, 800);
    }
    else{
        var textDiv = $('#vkTextId'+msgid);
        if (textDiv.text() != msg){
            textDiv.text(msg);
        }
        if (read_state == 1){
            msgDiv.removeClass("alert-info");
        }
    }
}

function addAsUnread(msgid){
    var exist = false;
    for(var i = 0; i < unreadMessages.length; i++){
        if (unreadMessages[i] == msgid){
            exist = true;
        }
    }

    if (!exist){
        unreadMessages.push(msgid);
    }
}