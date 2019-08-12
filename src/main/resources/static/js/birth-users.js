function saveBirthUsersProperties() {
    let wrap = {
        chatId: $("#id-chat-birth-users").val(),
        templateId: $("#birth-users").find('option:selected').val(),
        time: $("#users-birth-notification-time").val(),
        message: $("#messageToUsersBirthday").val()
    };

    if (!validate_input(wrap)) {
        return;
    }

    console.log(wrap);
    $.ajax({
        type: "POST",
        url: "/rest/properties/birth-users",
        data: wrap,
        success: function (e) {
            location.reload();
        },
        error: function (e) {
            console.log(e);
        }
    })
}

function validate_input(data) {
    console.log(data);
    if ((data.templateId == "") && (data.message == '') || data.time == "" || data.chatId == "") {
        alert("Проверьте введенные данные");
        return false;
    }
    return true;
}