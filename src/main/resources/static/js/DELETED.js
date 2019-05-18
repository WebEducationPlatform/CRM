/*
$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        let clientId = $(this).data('clientId');
        let url = "/user/notification/postnope/getAll";
        console.log("clientId", clientId);
        let formData = {
            clientId: clientId
        };
        $.ajax({
            type: "POST",
            url: url,
            data: formData,

            success: function (result) {
                console.log("крутяк")
            },
            error: function (e) {
                console.log(e)
            }
        });
    });
});*/

$(".change-student-status").on('click', function () {
    let clientId = $(this).attr("id");
    let statusId = $(this).attr("value");
    // let currentStatusId = $(this).attr("name");
    let url = "/rest/status/client/change";

    let formData = {
        clientId: clientId,
        statusId: statusId
    };
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function () {
            let x = document.getElementById(clientId);
            $('#status-column'+statusId).append(x);
        },
        error: function () {
            alert('Не задан статус по-умолчанию для нового студента!');
        }
    });
});

$(function () {
    $('.portlet-content').on('click', function (e) {
        var clientId = $(this).parents('.common-modal').data('cardId');
        var currentModal = $('#main-modal-window');
        currentModal.data('clientId', clientId);
        currentModal.modal('show');
    });
});

//Отправка выбранных чекбоксов на контроллер отрпавки сообщений в email.SMS, VK,FB.
$(function () {
    $('.save_value').on('click', function (event) {
        var sel = $('input[type="checkbox"]:checked').map(function (i, el) {
            return $(el).val();
        });
        var boxList = sel.get();
        console.log(sel.get());

        $.ajax({
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify(boxList),
            url: "/rest/sendSeveralMessage",
            success: function (result) {
                alert('sucess')
            }
        });
    })
});

//Отправка  фиксированного сообщения на email из расширенной модалки
$(function () {
    $('.internal-send-email').on('click', function () {
        var clientId = $(this).parents('.main-modal').data('clientId');
        var templateId = $(this).data('templateId');
        let url = '/rest/sendEmail';
        let formData = {
            clientId: clientId,
            templateId: templateId,
            body: $('#custom-EmaileTemplate-body').val()
        };
        var currentStatus = document.getElementById("sendEmailTemplateStatus");
        $.ajax({
            type: "POST",
            url: url,
            data: formData,


            success: function (result) {
                currentStatus.style.color = "limegreen";
                currentStatus.textContent = "Отправлено";
            },
            error: function (e) {
                currentStatus.style.color = "red";
                currentStatus.textContent = "Ошибка";
                console.log(e)
            }
        });
    });
});

$(function () {
    $('.custom-email-btn').on('click', function () {
        var clientId = $(this).parents('.main-modal').data('clientId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customEmailMessageTemplate');
        var btn = currentModal.find('.send-email-btn');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

// Отправка кастомного сообщения в email
$(function () {
    $('.send-email-btn').on('click', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var currentStatus = $(this).prev('.send-email-err-status');
        let url = '/rest/sendEmail';
        let formData = {
            clientId: clientId,
            templateId: templateId,
            body: $('#custom-EmaileTemplate-body').val()
        };
        $.ajax({
            type: "POST",
            url: url,
            data: formData,


            success: function (result) {
                $(".modal").modal('hide');
                currentStatus.css('color', 'limegreen');
                currentStatus.text("Отправлено");
            },
            error: function (e) {
                currentStatus.css('color', 'red');
                currentStatus.text("Ошибка");
                console.log(e)
            }
        });
    });
});



//Отправка выбранных чекбоксов на контроллер отрпавки сообщений в email.SMS, VK,FB.
$(function () {
    $('.save_value').on('click', function (event) {
        var sel = $('input[type="checkbox"]:checked').map(function (i, el) {
            return $(el).val();
        });
        var boxList = sel.get();
        console.log(sel.get());

        $.ajax({
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify(boxList),
            url: "/rest/sendSeveralMessage",
            success: function (result) {
                alert('sucess')
            }
        });
    })
});

$(function () {
    $('.сustom-vk-btn').on('click', function () {
        var clientId = $(this).parents('.main-modal').data('clientId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customVKMessageTemplate');
        var btn = currentModal.find('.send-vk-btn');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

//Отправка фиксированного сообщения во вконтакте из расширенной модалки.
$(function () {
    $('.internal-vkontakte-message').on('click', function () {
        var clientId = $(this).parents('.main-modal').data('clientId');
        var templateId = $(this).data('templateId');
        let url = '/rest/vkontakte';
        let formData = {
            clientId: clientId,
            templateId: templateId,
            body: $('#custom-VKTemplate-body').val()
        };
        var currentStatus = document.getElementById("sendSocialTemplateStatus");
        $.ajax({
            type: "POST",
            url: url,
            data: formData,

            success: function (result) {
                currentStatus.style.color = "limegreen";
                currentStatus.textContent = "Отправлено";

            },
            error: function (e) {
                currentStatus.style.color = "red";
                currentStatus.textContent = "Ошибка";
                console.log(e)
            }
        });
    });
});

function createNewUser() {
    let url = '/rest/user/addUser';

    let wrap = {
        name: $('#new-user-first-name').val(),
        lastName: $('#new-user-last-name').val(),
        phoneNumber: $('#new-user-phone-number').val(),
        email: $('#new-user-email').val(),
        age: $('#new-user-age').val(),
        sex: $('#sex').val()
    };


    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        success: function (result) {
            location.reload();
        },
        error: function (e) {

        }
    });
}

function hideOption(clientId) {
    $("#option_" + clientId).hide();
}

$(function () {
    $(".hide-main-modal").click(function (e) {
        $(".main-modal .close").click()
    });
});