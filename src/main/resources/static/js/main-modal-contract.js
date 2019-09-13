function createContractSetting() {
    var baseUrl = window.location.host;
    var url = '/rest/client/contract/create';

    var clientId = getAllUrlParams(window.location.href).id;
    var hash = (+new Date).toString(36);
    var setting = {
        hash: hash,
        clientId: clientId,
        oneTimePayment: !!$('#contract-client-setting-one-time-payment-radio').prop("checked"),
        monthPayment: !!$('#contract-client-setting-month-payment-radio').prop("checked"),
        diploma: !!$('#contract-client-setting-diploma-checkbox').prop("checked"),
        stamp: !!$('#contract-client-setting-stamp-checkbox').prop("checked"),
        paymentAmount: $('#contract-client-setting-payment-amount-form').val()
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: url,
        data: JSON.stringify(setting),
        success: function () {
            var contractLink = 'https://' + baseUrl + '/contract/' + hash;
            $('#contract-client-setting-contract-link').val(contractLink);
            navigator.clipboard.writeText(contractLink);
            $('#contract-copy-modal').modal('show');
            setTimeout(function(){
                $('#contract-copy-modal').modal('hide');
            }, 1500);
        },
        error: function () {
            console.log('error save contract setting');
            alert('Ошибка создания ссылки!')
        }
    });
}

$("#deleteContractButton").on('click', function () {
    let clientId = getAllUrlParams(window.location.href).id;
    let url = "/contract/deleteContract?id=";
    $.ajax({
        type: "DELETE",
        url: url + clientId,
        success: function () {
            console.log("contract delete from client" + clientId);
            location.reload();
        },
        error: function () {
            console.log("error delete contract in client" + clientId);
        }
    });
});