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
                        "<tr>" +
                        "   <td>" + data.history[0].title + "</td>" +
                        "   <td class=\"client-history-date\">" + data.history[0].date + "</td>" +
                        "</tr>"
                    );
                });
        },
        error: function (error) {
        }
    });
}

$(document).ready(function () {
    $(".show-status-btn").on("click", function showStatus() {
        let val = $(this).attr("value");
        let
            button = $(this),
            url = '/admin/rest/status/visible/change',
            formData = {
                statusId: $(this).attr("value"),
                invisible: false
            };

        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            success: function (status) {
                $('#status-column'+ val).show();
                button.parents("tr").hide();
            },
            error: function (error) {
                console.log(error);
            }
        })
    });

    $(".hide-status-btn").on("click", function hideStatus() {
        let val = $(this).attr("value");
        let
            url = '/admin/rest/status/visible/change',
            formData = {
                statusId: val,
                invisible: true
            };

        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            success: function () {
                $('#status-column'+ val).hide();
                $('.show-status-btn[value='+ val +']').parents("tr").show();
            },
            error: function (error) {
                console.log(error);
            }
        })
    });

    $('.link-cursor-pointer').on("click", function returnClientToStatus() {
        let
            button = $(this),
            url = '/rest/status/client/change',
            formData = {
                clientId: button.parents(".dropdown").children("button").attr("value"),
                statusId: button.attr("value")
            };

        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            success: function () {
                button.parents(".dropdown").children("button").removeClass().addClass("btn btn-secondary").attr("disabled","disabled").text("Добавлено");
            },
            error: function (error) {
            }
        });
    });
});