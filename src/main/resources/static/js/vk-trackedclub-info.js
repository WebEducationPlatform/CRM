function deleteVkTrackedClub(id) {
    let url = '/rest/vkontakte/trackedclub/delete';
    let formData = {
        deleteId: id
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {
            location.reload();
        },
        error: function (e) {

        }
    });
}

//TODO Почему отправляет объект, а на контроллере 3 строки
function updateVkTrackedClub(id) {
    let url = '/rest/vkontakte/trackedclub/update';
    let vkTrackedClub = {
        id: id,
        groupId: $('#groupId').val(),
        groupName: $('#groupName').val(),
        clientId: $('#clientId').val(),
        token: $('#token').val()
    };

    $.ajax({
        type: "POST",
        url: url,
        data: vkTrackedClub,
        success: function () {
            location.reload();
        },
        error: function (e) {

        }
    });
}

function addVkTrackedClub() {
    let url = '/rest/vkontakte/trackedclub/add';
    let vkTrackedClub = {
        groupId: $('#newGroupId').val(),
        groupName: $('#newGroupName').val(),
        clientId: $('#newClientId').val(),
        token: $('#newToken').val()
    };

    $.ajax({
        type: "POST",
        url: url,
        data: vkTrackedClub,
        success: function () {
            location.reload();
        },
        error: function (e) {

        }
    });
}