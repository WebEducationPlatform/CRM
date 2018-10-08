//Fill values on notification status configuration modal show up.
$('#payment-notification-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/message-template',
        success: function (response) {
            $("#payment-notification-template").empty().append(
                $('<option>').val('').text('Не выбрано')
            );
            $.each(response, function(i, item) {
                $("#payment-notification-template").append(
                    $('<option>').val(item.id).text(item.name)
                )
            });
            $.ajax({
                type: 'GET',
                url: '/rest/properties',
                success: function (response) {
                    if (response.paymentMessageTemplate == null) {
                        $("#payment-notification-template option[value='']").prop('selected', true)
                    } else {
                        $("#payment-notification-template option[value=" + response.paymentMessageTemplate.id + "]").prop('selected', true);
                    }
                    $("#payment-notification-time").val(response.paymentNotificationTime);
                    $("#payment-notification-enable").prop('checked', response.paymentNotificationEnabled);
                }
            })
        }
    });
});

//Set notification properties
$("#update-payment-notification").click( function () {
    let data = {
        paymentMessageTemplate: $("#payment-notification-template").val(),
        paymentNotificationTime: $("#payment-notification-time").val(),
        paymentNotificationEnabled: $("#payment-notification-enable").prop('checked')
    };
    if (!validate_input(data)) {return}
    $.ajax({
        type: 'POST',
        url: '/rest/properties/email-notification',
        data: data,
        success: function () {
        }
    })
});

//Validate input data
function validate_input(data) {
    console.log(data);
    if ((data.paymentNotificationEnabled == true) && (data.paymentMessageTemplate == '')) {
        alert("Выберите шаблон или отключите оповещение!");
        return false;
    }
    if (data.paymentNotificationTime == '') {
        alert("Задайте время оповещения!");
        return false;
    }
    return true;
}