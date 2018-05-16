//TODO add button in view after merge with Svyatoslav
function deleteClientStatus(clientId) {
    let url = "/admin/rest/status/client/delete";
    let requestParam = {
        clientId: clientId
    };
    $.ajax({
        type: "POST",
        url: url,
        data : requestParam,
        success : function () {
            $('.portlet[value="'+ clientId +'"]').remove();
        },
        error: function (e) {
            console.log(e);
        }
    })
}

function senReqOnChangeStatus(clientId, statusId) {
    let
        url = '/rest/status/client/change',
        formData = {
            clientId: clientId,
            statusId: statusId
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (data) {
            let
                url = '/rest/client/' + clientId;
            $.get(url,
                function (data) {
                    $('#client-' + data.id + 'history').prepend(
                        "<li>" +
                        "   <span>" + data.history[0].title + "</span>" +
                        "</li>"
                    );
                });
        },
        error: function (error) {
        }
    });
}

function returnClientToStatus(clientId, statusId) {
    let
        url = '/rest/status/client/change',
        formData = {
            clientId: clientId,
            statusId: statusId
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            $('#return-to-status-btn' + clientId).remove();
        },
        error: function (error) {
        }
    });
}

function hideStatus(statusId) {
    let url = '/admin/rest/status/hide',
        formData = {
            statusId : statusId
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            location.reload();
        },
        error: function (error) {
        }
    });

}