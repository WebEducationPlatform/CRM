$("#telegram-send-btn").click(function sendMessage() {
    let text = $("#telegram-text").val();
    var clientId = $("#main-modal-window").data('clientId');
    console.log(text);
    console.log(clientId);
});

$("#telegram-li").click(function () {
    // let telegramId = $("#telegramId").val();
    // console.log(telegramId);
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/messages/chat',
        data: {chatId: 143568873},
        success: function () {
            console.log("success");
        }
    })
});