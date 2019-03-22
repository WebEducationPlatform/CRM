//Fill values on notification status configuration modal show up.
$('#payment-notification-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/message-template',
        success: function (response) {
            $("#payment-notification-template").empty().append(
                $('<option>').val('').text('Не выбрано')
            );
            $.each(response, function (i, item) {
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
$("#update-payment-notification").click(function () {
    let data = {
        paymentMessageTemplate: $("#payment-notification-template").val(),
        paymentNotificationTime: $("#payment-notification-time").val(),
        paymentNotificationEnabled: $("#payment-notification-enable").prop('checked')
    };
    if (!validate_input(data)) {
        return
    }
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

//Send sms to phone number
$("#telegram-auth-send-phone").click(function () {
    if (!$("#telegram-auth-phone")[0].checkValidity()) {
        alert("Не верный формат телефона: +74951234567");
        return
    }
    let phone = $("#telegram-auth-phone").val();
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/phone-code',
        data: {phone: phone},
        success: function () {
            $("#telegram-auth-send").prop("disabled", null);
        }
    })
});

//Fill values on auto-answer modal shows up
$('#auto-answer-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/message-template',
        success: function (response) {
            $("#auto-answer-template").empty().append(
                $('<option>').val('').text('Не выбрано')
            );
            $.each(response, function (i, item) {
                $("#auto-answer-template").append(
                    $('<option>').val(item.id).text(item.name)
                )
            });
            $.ajax({
                type: 'GET',
                url: '/rest/properties',
                success: function (response) {
                    if (response.autoAnswerTemplate == null) {
                        $("#auto-answer-template option[value='']").prop('selected', true)
                    } else {
                        $("#auto-answer-template option[value=" + response.autoAnswerTemplate.id + "]").prop('selected', true);
                    }
                    $("#auto-answer-enable").prop('checked', response.isAutoAnswerEnabled);
                }
            })
        }
    });
});

//Set notification properties
$("#update-auto-answer").click(function () {
    let data = {
        autoAnswerTemplate: $("#auto-answer-template").val()
    };
    if (!validate(data)) {
        return
    }
    $.ajax({
        type: 'POST',
        url: '/rest/properties/auto-answer',
        data: data,
        success: function () {
        }
    })
});

//Validate input data
function validate(data) {
    console.log(data);
    if ((data.autoAnswerTemplate == '')) {
        alert("Внимание: Автоответ Отключен!");
    }
    return true;
}


//send SMS authorization code
$("#telegram-auth-send").click(function () {
    let code = $("#telegram-auth-code").val();
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/sms-code',
        data: {code: code},
        success: function () {
            location.reload();
        }
    })
});

//Logout from Telegram
$("#telegram-logout-button").click(function () {
    if (!confirm("На данный момент сервер авторизован в Telegram.\r\nВы уверены, что хотите выйти?")) {
        return
    }
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/logout',
        success: function () {
            location.reload();
        }
    })
});

//Fill values on new student configuration modal show up.
$('#new-student-config-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/properties',
        dataType: 'JSON',
        success: function (response) {
            $("#month-price").val(response.defaultPricePerMonth);
            $.ajax({
                type: 'GET',
                url: '/rest/student/status',
                dataType: 'JSON',
                success: function (statuses) {
                    $("#new-student-status").empty().append(
                        $('<option>').val('0').text('Не выбрано')
                    );
                    $.each(statuses, function (i, item) {
                        $("#new-student-status").append(
                            $('<option>').val(item.id).text(item.status)
                        )
                    });
                    if (response.defaultStudentStatus == null) {
                        $("#new-student-status option[value='0']").prop('selected', true)
                    } else {
                        $("#new-student-status option[value=" + response.defaultStudentStatus.id + "]").prop('selected', true);
                    }
                }
            });
        }
    });
});

//Update new student creation properties
$("#update-new-student-settings").click(function () {
    let price = $("#month-price").val();
    let status_id = $("#new-student-status").val();
    if (!validate_new_student_parameters(price, status)) {
        return
    }
    if (status_id === '0') {
        sessionStorage.setItem('student_default_status', "false");
    }
    $.ajax({
        type: 'POST',
        url: '/rest/properties/new-student-properties',
        data: {price: price, id: status_id},
    })
});

function validate_new_student_parameters(price, status) {
    if (price === '') {
        alert("Введите корректную цену!");
        return false;
    }
    return true;
}

//Fill values on contract user setting
$('#contract-user-setting-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/message-template',
        success: function (response) {
            $("#contract-mail-template").empty().append(
                $('<option>').val('').text('Не выбрано')
            );
            $.each(response, function (i, item) {
                $("#contract-mail-template").append(
                    $('<option>').val(item.id).text(item.name)
                )
            });
            $.ajax({
                type: 'GET',
                url: '/rest/properties',
                success: function (response) {
                    if (response.contractTemplate == null) {
                        $("#contract-mail-template option[value='']").prop('selected', true)
                    } else {
                        $("#contract-mail-template option[value=" + response.contractTemplate.id + "]").prop('selected', true);
                    }
                    $('#input-contract-last-id').empty().val(response.contractLastId);
                }
            })
        }
    });
});

$("#update-contract-user-setting").click(function () {
    let data = {
        contractTemplate: $("#contract-mail-template").val(),
        contractLastId: $('#input-contract-last-id').val()
    };
    if (data.contractTemplate == '') {
        alert("Внимание: Отправка сообщения отключена!");
    }
    $.ajax({
        type: 'POST',
        url: '/rest/properties/contractUserSetting',
        data: data,
        success: function () {
        }
    })
});