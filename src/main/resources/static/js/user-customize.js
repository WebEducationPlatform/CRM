$('#slack-settings-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/properties/get-slack-users',
        success: function (response) {
            $('#slack-users').val(response);
        }
    });
    $.ajax({
        type: 'GET',
        url: '/rest/properties/get-slack-link',
        success: function (response) {
            $('#slack-invite-link').val(response);
        }
    });
});

$('#update-slack').on('click', function () {
    $.ajax({
        type: 'POST',
        url: '/rest/properties/slack-set',
        data: {'users': $('#slack-users').val(),
               'slack-invite-link': $('#slack-invite-link').val()},
        success: function () {
            $('#slack-settings-modal').hide();
        }
    });
});

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

            $("#new-client-notification-template").empty().append(
                $('<option>').val('').text('Не выбрано')
            );
            $.each(response, function (i, item) {
                $("#new-client-notification-template").append(
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
                    if (response.newClientMessageTemplate == null) {
                        $("#new-client-notification-template option[value='']").prop('selected', true)
                    } else {
                        $("#new-client-notification-template option[value=" + response.newClientMessageTemplate.id + "]").prop('selected', true);
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
        paymentNotificationEnabled: $("#payment-notification-enable").prop('checked'),
        newClientMessageTemplate: $("#new-client-notification-template").val()
    };
    if (!validate_input(data)) {
        return
    };
    $.ajax({
        type: 'POST',
        url: '/rest/properties/notifications',
        data: data,
        success: function () {
        }
    });
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

// Fill values on birthday modal shows up
$('#birthday-modal').on('show.bs.modal', function () {
    $.ajax({
        type: 'GET',
        url: '/rest/message-template',
        success: function (response) {
            $("#birthday-template").empty().append(
                $('<option>').val('').text('Не выбрано')
            );
            $.each(response, function (i, item) {
                $("#birthday-template").append(
                    $('<option>').val(item.id).text(item.name)
                )
            });
            $.ajax({
                type: 'GET',
                url: '/rest/properties',
                success: function (response) {
                    if (response.birthdayTemplate == null) {
                        $("#birthday-template option[value='']").prop('selected', true)
                    } else {
                        $("#birthday-template option[value=" + response.birthdayTemplate.id + "]").prop('selected', true);
                    }
                }
            })
        }
    });
});

//Set notification properties to birthday
$("#update-birthday-modal").click(function () {
    let data = {
        birthdayTemplate: $("#birthday-template").val()
    };
    if (!validateBirthday(data)) {
        return
    }
    $.ajax({
        type: 'POST',
        url: '/rest/properties/birthday',
        data: data,
        success: function () {
        }
    })
});

//Validate input data to birthdayTemplate
function validateBirthday(data) {
    console.log(data);
    if ((data.birthdayTemplate === '')) {
        alert("Внимание: Отправка поздравления с днем рождения не использует никакого шаблона, установите шаблон!");
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
            $("#month-payment").val(response.defaultPayment);
            $.ajax({
                type: 'GET',
                url: '/rest/student/status',
                dataType: 'JSON',
                success: function (statuses) {
                    var newStudentStatusField = '#new-student-status';
                    var rejectStudentStatusField = '#reject-student-status';
                    fillStatuses(newStudentStatusField, statuses, response.defaultStudentStatus);
                    fillStatuses(rejectStudentStatusField, statuses, response.defaultRejectStudentStatus);
                }
            });
        }
    });
});

function fillStatuses(field, statuses, defaultStatus) {
    $(field).empty().append(
        $('<option>').val('0').text('Не выбрано')
    );
    $.each(statuses, function (i, item) {
        $(field).append(
            $('<option>').val(item.id).text(item.status)
        )
    });
    if (defaultStatus == null) {
        $(field + " option[value='0']").prop('selected', true)
    } else {
        $(field + " option[value=" + defaultStatus.id + "]").prop('selected', true);
    }
}

//Update new student creation properties
$("#update-new-student-settings").click(function () {
    let price = $("#month-price").val();
    let payment = $("#month-payment").val();
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
        data: {price: price, payment: payment, id: status_id}
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
                    $('#input-contract-inn').empty().val(response.inn);
                    $('#input-contract-ras-s').empty().val(response.checkingAccount);
                    $('#input-contract-kor-s').empty().val(response.correspondentAccount);
                    $('#input-contract-bic').empty().val(response.bankIdentificationCode);
                }
            })
        }
    });
});

$("#update-contract-user-setting").click(function () {
    let data = {
        contractTemplateId: $("#contract-mail-template").val(),
        contractLastId: $('#input-contract-last-id').val(),
        inn: $('#input-contract-inn').val(),
        checkingAccount: $('#input-contract-ras-s').val(),
        correspondentAccount: $('#input-contract-kor-s').val(),
        bankIdentificationCode: $('#input-contract-bic').val()
    };
    if (data.contractTemplateId == '') {
        alert("Внимание: Нужно указать шаблон!");
    }
    $.ajax({
        type: 'POST',
        url: '/rest/properties/contractUserSetting',
        data: data,
        success: function () {
        }
    })
});