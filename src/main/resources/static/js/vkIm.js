
$(document).ready(function () {
    setInterval(getUnreadMessages,5000);
});

function getUnreadMessages() {
    $.when($.get('/rest/conversation/all-byClient')).done(function(dataParam){
        getUnreadMessages_CallBack(dataParam);
    });
}

function getUnreadMessages_CallBack(dataParam) {

    $('.chat-notification.glyphicon.glyphicon-send.pull-right').each(function(i, elem){
        $(elem).text('');
        $(elem).hide();
    });
    $('.chat-notifications.glyphicon.glyphicon-send.pull-right').each(function(i, elem){
        $(elem).hide();
    });

    $('.chat-items').each(function(i, elem){
        $(elem).remove();
    });
    var dom = $('#chatNavbarDropdown');
    dom.hide();

    $('#chat-im-count').text('');

    $.each(dataParam, function(clientId, unreadCount){
        showVkNotification(clientId, unreadCount)
    });
}

function showVkNotification(clientId, unreadCount){
    if (clientId != "" && unreadCount != ""){

        //show notification
        var dom = $('#chat-notification'+clientId);
        dom.text(unreadCount);
        dom.show();

        //add notification in menu
        var clientName = $('#ClientName'+clientId).text();

        var msgDiv = $('#chatNewMessageMenu');

        var notify = $('#chatNotifyItem'+clientId);

        var dom = $('#chatNavbarDropdown');
        dom.show();

        if (notify.length == 0){
            var dom = $("<p><div class='dropdown-item chat-items' role='button' data-clientId='"+clientId+"' id='chatNotifyItem"+clientId+"' onclick='showModal("+clientId+")'>"+clientName+"</div></p>");
            msgDiv.prepend(dom);
        }

        //snow notification in modal form
        if ($('#main-modal-window').is(':visible')){
            if ($('#chat-button').attr("clientid") == clientId){
                $('#chat-im-count').text(unreadCount);
            }
        }
    }
}

function showModal(ClientId){
    $('#main-modal-window').data("clientId", ClientId);
    $('#main-modal-window').modal('show');
}