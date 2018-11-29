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

//Fill values on new student configuration modal show up.
$('#new-student-config-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/properties',
        dataType: 'JSON',
        success: function (response) {
            $("#month-price").val(response.defaultPricePerMonth);
            $("#new-student-status").val(response.defaultStudentStatusName);
        }
    });
});

//Update new student creation properties
$("#update-new-student-settings").click( function () {
    let price = $("#month-price").val();
    let status = $("#new-student-status").val();
    if (!validate_new_student_parameters(price, status)) {return}
    $.ajax({
        type: 'POST',
        url: '/rest/properties/new-student-properties',
        dataType: 'JSON',
        data: {price: price, status: status}
    });
});

function validate_new_student_parameters(price, status) {
    if (price === '') {
        alert("Введите корректную цену!");
        return false;
    }
    if (status === '') {
        alert("Введите имя статуса!");
        return false;
    }
    return true;
}