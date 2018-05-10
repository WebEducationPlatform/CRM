//TODO add button after merge with Svyatoslav
function deleteClientStatus(clientId) {
    let url = "/admin/status/client/delete";
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