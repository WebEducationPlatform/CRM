var userID;
var groupID;
var accessToken;
var version;
var url;

var userData;
var fldUserName = 'first_name';
var fldLastName = 'last_name';
var fldUserNameIns = 'first_name_ins';
var fldLastNameIns = 'last_name_ins';
var groupData;
var fldName = 'name';
var fldPhotoSize = 'photo_50';
var addQueryFld = "photo_50,first_name_ins, last_name_ins";
var count = 40;
var rev = 0; //порядок сортировки сообщений
var intervalID;
var unreadMessages = [];
var vkIdMappingClientId = {};
var vkPhotodef = "https://vk.com/images/camera_50.png?ava=1";

$(function () {

    $('#customVKIMMessage').on('show.bs.modal', function () {

        return; //пока ничего не делаем.

        //$('#customVKIMMessage').css('width', '90%');
        // $('#customVKIMMessage').css('margin', '100px auto 100px auto');
        //init paramatres
        //id берем из данных установленных при формировании карточки в main-table.js
        userID = $('#vk-im-button').data().userID;

        $.when($.ajax(vk_getUserDataRequest(),{dataType:'jsonp'}), $.ajax(vk_getGroupDataRequest(),{dataType:'jsonp'})).done(function (userDataRes, groupDataRes) { //вытаскиваем данные по группе и юзеру
            userData = userDataRes[0].response[0];
            groupData = groupDataRes[0].response[0];

            $('#VK-IM-HEADER').text("Общение с "+ userData[fldUserNameIns]+" "+userData[fldLastNameIns]);
            $('#VK-IM-LINK').attr('href','https://vk.com/id'+userID);

            $('.message-vk-im').remove();
            vk_getMessages();
            intervalID = setInterval(vk_getMessages, 5000);
        });

    });

    $('#customVKIMMessage').on('hide.bs.modal', function () {
        return;
        clearInterval(intervalID);
    });

    $('#IM-VK-Text').focus(function () {
        return;
        unreadMessages.sort();

        for(var i = 0; i < unreadMessages.length; i++){
            //отправляются все сообщения как помеченные, по мануалу можно отправлять одно, но одно не работает.
            sendRequest('messages.markAsRead', {peer_id: userID, start_message_id: unreadMessages[i], group_id: groupID,access_token:accessToken, v:version}, function (){})
        }
        unreadMessages = [];
    });
});

$(document).on("click","#send-vk-im-btn", function (){
    var text = $('#IM-VK-Text').val();
    vk_sendMessage(text);
});

$(document).ready(function () {
    vkIdMappingClientId = {};
    $.when($.get('/rest/vkontakte/connectParam')).done(function(dataParam){//сначала получаем параметры подключения
        accessToken = dataParam.accessToken;
        groupID     = dataParam.groupID;
        version     = dataParam.version;
        url         = dataParam.url;

        setInterval(vk_getUnreadMessages,5000);
    });
});

function vk_getUserDataRequest(){
    return url+"users.get"+'?'+$.param({user_id:userID, fields:addQueryFld, access_token:accessToken, v:version});
}
function vk_getGroupDataRequest(){
    return url+"groups.getById"+'?'+$.param({group_id:groupID, fields:fldPhotoSize, access_token:accessToken, v:version});
}

function sendRequest(methodName, param, callback){
    var urlRequest = url+methodName+'?'+$.param(param);

    return $.ajax({
        method: 'GET',
        url: urlRequest,
        dataType: "jsonp",
        success: function (vk_data) {
            callback(vk_data);
        },
        error: function(error){
            console.log(error);
        }
    })
}

function vk_getMessages() {
    return sendRequest("messages.getHistory", {user_id:userID, group_id:groupID, count: count, rev:rev, access_token:accessToken, v:version}, vk_getMessages_CallBack);
}

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
    $('.vk-notifications.glyphicon.glyphicon-send.pull-right').each(function(i, elem){
        $(elem).hide();
    });

    $('.vk-items').each(function(i, elem){
        $(elem).remove();
    });
    var dom = $('#VKNavbarDropdown');
    dom.hide();

    $('#vk-im-count').text('');

    var arr = vk_data.response.items;
    for(var i = 0; i < arr.length; i++){
        var unreadUserID = arr[i].conversation.peer.id;
        var unreadCount  = arr[i].conversation.unread_count;

        var clientId = vkIdMappingClientId[unreadUserID];
    //     if (clientId == undefined){
    //         ///{ss}/{link}
    //         $.when($.get("/rest/client/socialID",{socialProfileType:"vk", userID: unreadUserID, unread: unreadCount})).done(function (result){
    //             clientId = result.clientID;
    //             unreadCount = result.unreadCount;
    //             unreadUserID = result.userID;
    //
    //             showVkNotification(clientId, unreadUserID, unreadCount);
    //         });
    //     }
    //     else{
    //         showVkNotification(clientId, unreadUserID, unreadCount);
    //     }
    // }
}

function showVkNotification(clientId, unreadUserID, unreadCount){
    if (clientId != "" && unreadCount != ""){
        vkIdMappingClientId[unreadUserID] = clientId;

        //show notification
        var dom = $('#VK-notification'+clientId);
        dom.text(unreadCount);
        dom.show();

        //add notification in menu
        var clientName = $('#ClientName'+clientId).text();

        var msgDiv = $('#VKNewMessageMenu');

        var notify = $('#VKNotifyItem'+clientId);

        var dom = $('#VKNavbarDropdown');
        dom.show();

        if (notify.length == 0){
            var dom = $("<p><div class='dropdown-item vk-items' role='button' data-clientId='"+clientId+"' id='VKNotifyItem"+clientId+"' onclick='showModal("+clientId+")'>"+clientName+"</div></p>");
            msgDiv.prepend(dom);
        }

        //snow notification in modal form
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

function showModal(ClientId){
    $('#main-modal-window').data("clientId", ClientId);
    $('#main-modal-window').modal('show');
}