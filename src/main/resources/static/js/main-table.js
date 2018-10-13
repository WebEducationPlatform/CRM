$('.fix-modal').on('show.bs.modal', function () {
    var currentForm = $(this).find('.box-modal');
    var clientId = $(this).find('.send-all-message').data('clientId');
    drawCheckbox(currentForm, clientId);
});


$('.custom-modal').on('show.bs.modal', function () {
    var currentForm = $(this).find('.box-modal');
    var clientId = $(this).find('.send-all-custom-message').data('clientId');
    drawCheckbox(currentForm, clientId);
});

// Отрисовка чекбоксов социальных сетей
function drawCheckbox(currentForm, clientId) {
    let formData = {clientId: clientId};
    $.ajax({
        type: 'GET',
        url: 'rest/client/' + clientId,
        data: formData,
        beforeSend: function () {
            if (currentForm.find('.my-checkbox-soc').is('.my-checkbox-soc')) {
                return false;
            }
        },
        success: function (data) {
            var soc = data.socialProfiles;
            var email = data.email;
            var phoneNumber = data.phoneNumber;

            for (let i = 0; i < soc.length; i++) {
                currentForm.prepend("<label class='checkbox-inline soc-network-box'>" +
                    "<input type='checkbox'  value=" + soc[i].socialProfileType.name + "  class='my-checkbox-soc' />" + soc[i].socialProfileType.name +
                    "</label>");
            }
            if (email !== null) {
                currentForm.prepend("<label class='checkbox-inline soc-network-box'>" +
                    "<input type='checkbox'  value=" + 'email' + "  class='my-checkbox-soc' />" + 'e-mail' +
                    "</label>");
            }
            if (phoneNumber !== null) {
                currentForm.prepend("<label class='checkbox-inline soc-network-box'>" +
                    "<input type='checkbox'  value=" + 'sms' + "  class='my-checkbox-soc' />" + 'sms' +
                    "</label>");
            }
        }
    });
}

$(function () {
    $(".hide-main-modal").click(function (e) {
        $(".main-modal .close").click()
    });
});

// Выбрать , отключить все чекбоксы в меню отправки сообщений в email.SMS, VK,FB.

$('.select_all').click(function () {
    var currentForm = $(this).parents('.box-modal');
    currentForm.find('.my-checkbox-soc').prop('checked', true);
});

$('.confirm-skype-interceptor').on('click', '.select_all_skype_boxes', function (e) {
    var currentForm = $(this).parents('.box-window');
    currentForm.find('.my-checkbox-soc').prop('checked', true);
});


$('.deselect_all').click(function () {
    var currentForm = $(this).parents('.box-modal');
    currentForm.find('.my-checkbox-soc').prop('checked', false);
});


//Сохранить комментарий на лицевой стороне карточки
$("#save-description").on("click", function saveDescription() {
    let text = $('#clientDescriptionModal').find('textarea').val();
    let id = $(this).attr("data-id");
    let
        url = 'rest/client/addDescription',
        formData = {
            clientId: id,
            clientDescription: text
        };
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function () {
            $("#info-client" + id).find('.client-description').text(text);
            $('#clientDescriptionModal').modal('hide');
        },
        error: function (error) {
            console.log(error.responseText);
            $('#clientDescriptionModal').modal('hide');
        }
    })
});


$(document).ready(function () {
    $(".column").sortable({
        delay: 100,
        items: '> .portlet',
        connectWith: ".column",
        handle: ".portlet-body",
        cancel: ".portlet-toggle",
        start: function (event, ui) {
            ui.item.addClass('tilt');
            tilt_direction(ui.item);
        },
        stop: function (event, ui) {
            ui.item.removeClass("tilt");
            $("html").unbind('mousemove', ui.item.data("move_handler"));
            ui.item.removeData("move_handler");
            senReqOnChangeStatus(ui.item.attr('value'), ui.item.parent().attr('value'))
        }
    });

    $(document).ready(function () {
        $("#new-status-name").keypress(function (e) {
            if (e.keyCode === 13) {
                createNewStatus();
            }
        });
    });

    $(".portlet")
        .addClass("panel panel-default")
        .find(".portlet-header")
        .addClass("panel-heading");

    $("#create-new-status-btn").click(function () {
        $(this).hide();
        $("#new-status-form").show();
        document.getElementById("new-status-name").focus();
    });

    $("#create-new-status-cancelbtn").click(function () {
        $("#new-status-form").hide();
        $("#create-new-status-btn").show();
    });

    /* $("#new-status-form").focusout(
         function () {
             $(this).hide();
             $("#create-new-status-span").show();
         });*/

    //Search clients in main
    $("#search-clients").keyup(function () {
        let jo = $(".portlet");
        let jo2 = jo.find($(".search_text"));
        let data = this.value.toLowerCase().split(" ");
        this.value.localeCompare("") === 0 ? jo.show() : jo.hide();

        for (let i = 0; i < jo2.length; i++) {
            let count = 0;
            for (let z = 0; z < data.length; z++) {
                if (jo2[i].innerText.toLowerCase().includes(data[z])) {
                    count++;
                }
            }
            if (count === data.length) {
                jo[i].style.display = 'block';
            }
        }
    });

    $(".sms-error-btn").on("click", function smsInfoModalOpen() {
        let modal = $("#sms_error_modal"),
            btn = $(this),
            url = '/user/notification/sms/error/' + btn.attr("data-id");
        $.get(url, function () {
        }).done(function (notifications) {
            let body = modal.find("tbody");
            for (let i = 0; i < notifications.length; i++) {
                body.append(
                    "<tr><td>" + notifications[i].information + "</td></tr>"
                )
            }
        });
        modal.find("#clear_sms_errors").attr("onClick", "clearNotifications(" + btn.attr("data-id") + ")");
        modal.modal();
    })

    $("#sms_error_modal").on('hidden.bs.modal', function () {
        let modal = $(this);
        modal.find("tbody").empty();
    })
});

function displayOption(clientId) {
    $("#option_" + clientId).show();
}

function hideOption(clientId) {
    $("#option_" + clientId).hide();
}


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

function createNewStatus() {
    let url = '/rest/status/add';
    let statusName = $('#new-status-name').val() || $('#default-status-name').val();
    if (typeof statusName === "undefined" || statusName === "") return;
    let formData = {
        statusName: statusName
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            window.location.reload();
        },
        error: function (e) {
            alert(e.responseText);
            console.log(e.responseText);
        }
    });
}

//Change status button
function changeStatusName(id) {
    let url = '/admin/rest/status/edit';
    let statusName = $("#change-status-name" + id).val();
    let trial_offset = $("#trial_offset_" + id).val();
    let next_payment_offset = $("#next_payment_offset_" + id).val();
    if (!validate_status_input(trial_offset, next_payment_offset)) {return};
    let formData = {
        statusName: statusName,
        oldStatusId: id,
        trialOffset: trial_offset,
        nextPaymentOffset: next_payment_offset
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            window.location.reload();
        },
        error: function (e) {
            alert(e.responseText);
        }
    });
}

//Status offset dates validation
function validate_status_input(trial_offset, next_payment_offset) {
    if (trial_offset > next_payment_offset) {
        alert("Отступ даты пробного периода не может быть больше отступа даты следующей оплаты!");
        return false;
    }
    return true;
}

function tilt_direction(item) {
    var left_pos = item.position().left,
        move_handler = function (e) {
            if (e.pageX >= left_pos) {
                item.addClass("right");
                item.removeClass("left");
            } else {
                item.addClass("left");
                item.removeClass("right");
            }
            left_pos = e.pageX;
        };
    $("html").bind("mousemove", move_handler);
    item.data("move_handler", move_handler);
}

function assign(id) {
    let
        url = '/rest/client/assign',
        formData = {
            clientId: id
        },
        assignBtn = $('#assign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            assignBtn.before(
                "<button " +
                "   id='unassign-client" + id + "' " +
                "   onclick='unassign(" + id + ")' " +
                "   class='btn btn-sm btn-warning remove-tag'>Отказаться от карточки</button>"
            );
            assignBtn.remove();
            $('#info-client' + id).append(
                "<p class='user-icon' id='own-" + id + "' value=" + owner.firstName + "&nbsp" + owner.lastName + ">" +
                owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</p>" +
                "<p style='display:none'>" + owner.firstName + " " + owner.lastName + "</p>"
            );
            fillFilterList()
        },
        error: function (error) {
        }
    });
}

function assignUser(id, user, principalId) {
    var
        url = '/rest/client/assign/user',
        formData = {
            clientId: id,
            userForAssign: user
        },
        assignBtn = $('#assign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            let info_client = $('#info-client' + id),
                target_btn = $("a[href='/client/clientInfo/" + id + "']"),
                unassign_btn = $('#unassign-client' + id);
            info_client.find("p[style*='display:none']").remove();
            info_client.find(".user-icon").remove();

            //If admin assigned himself
            // if(principalId === user){
            //     //If admin assigned himself second time
            //     if(unassign_btn.length === 0){
            //         target_btn.before(
            //             "<button " +
            //             "   id='unassign-client" + id +"' " +
            //             "   onclick='unassign(" + id +")' " +
            //             "   class='btn btn-sm btn-warning'>Отказаться от карточки</button>"
            //         );
            //     }
            //If admin not assign himself, he don`t have unassign button
            // }else {
            //     unassign_btn.remove();
            // }
            assignBtn.remove();

            //Add Worker icon and info for search by worker
            info_client.append(
                "<p class='user-icon' id='own-" + id + "' value=" + owner.firstName + " " + owner.lastName + ">" +
                owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</p>" +
                "<p style='display:none'>" + owner.firstName + " " + owner.lastName + "</p>"
            );
            fillFilterList()
        },
        error: function (error) {
        }
    });
}


function unassign(id) {
    let
        url = '/rest/client/unassign',
        formData = {
            clientId: id
        },
        unassignBtn = $('#unassign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            let info_client = $('#info-client' + id);
            info_client.find("p[style*='display:none']").remove();
            info_client.find(".user-icon").remove();
            if (unassignBtn.length !== 0) {
                unassignBtn.before(
                    "<button " +
                    "   id='assign-client" + id + "' " +
                    "   onclick='assign(" + id + ")' " +
                    "   class='btn btn-sm btn-info remove-tag'>Взять себе карточку</button>"
                );
                unassignBtn.remove();
            }
            fillFilterList();
        },
        error: function (error) {
        }
    });
}

function showall() {
    $('#client_filter input:checkbox').prop('checked', false);
    $('#client_filter input:checkbox').change();
}

$(document).ready(function () {
    $("#client_filter").change(function () {
        var allChecks = $('#client_filter input:checkbox');
        var data = [];
        for (var w = 0; w < allChecks.length; ++w) {
            if (allChecks[w].checked) {
                data[data.length] = allChecks[w].value;
            }
        }
        var jo = $("#status-columns").find($(".portlet"));
        if (data.length === 0) {
            jo.show();
            return;
        }
        jo.hide();
        jo.filter(function (i, v) {
            var d = $(this)[0].getElementsByClassName("user-icon");
            if (d.length === 0) {
                return false;
            }
            for (var w = 0; w < data.length; ++w) {
                if (d[0].innerText.indexOf(data[w]) !== -1) {
                    return true;
                }
            }
        }).show();
    });
});

function fillFilterList() {
    $("#client_filter").empty();
    var names = $("#status-columns").find($(".user-icon"));
    if (names.length === 0) {
        $("#client_filter_group").hide();
    } else {
        $("#client_filter_group").show();
    }
    var uniqueNames = [];
    var temp = [];
    for (var i = 0; i < names.length; ++i) {
        if (~temp.indexOf(names[i].innerText)) {
            names.slice(temp.indexOf(names[i].innerText));
        } else {
            temp.push(names[i].innerText);
            uniqueNames.push(names[i]);
        }
    }
    $.each(uniqueNames, function (i, el) {
        $("#client_filter").append("<input class='check'  type=\"checkbox\" id = checkbox_" + el.innerText + " value=" + el.innerText + " ><label for=checkbox_" + el.innerText + ">" + el.getAttribute("value") + "</label></br>");
    });
}

(function ($) {
    $(document).ready(function () {
        var $panel = $('#panel');
        if ($panel.length) {
            var $sticker = $panel.children('#panel-sticker');
            var showPanel = function () {
                $sticker.hide();
                $panel.animate({
                    right: '+=350'
                }, 200, function () {
                    $(this).addClass('visible');
                });
            };
            var hidePanel = function () {
                $panel.animate({
                    right: '-=350'
                }, 200, function () {
                    $(this).removeClass('visible');
                });
            };
            $sticker
                .children('span').click(function () {
                showPanel();
            });
            $(document.getElementById('close-panel-icon')).click(function () {
                hidePanel();
                $sticker.show();
            });
        }
    });
})(jQuery);


$(document).ready(function () {
    $("#createDefaultStatus").modal({
        backdrop: 'static',
        keyboard: false
    }, 'show');
});

$(document).ready(fillFilterList);

$(document).on('click', function () {
    var url = '/rest/user';

    var userNames = [];

    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (res) {
            for (var i = 0; i < res.length; i++) {
                userNames[i] = res[i].firstName + res[i].lastName;
            }
        },
        error: function (error) {
            console.log(error);
        }
    });

    $('#main-modal-window  .textcomplete').textcomplete([
        {
            replace: function (mention) {
                return '@' + mention + ' ';
            },
            mentions: userNames,
            match: /\B@(\w*)$/,
            search: function (term, callback) {
                callback($.map(this.mentions, function (mention) {
                    $('.textcomplete-dropdown').css('z-index', '999999');
                    return mention.indexOf(term) === 0 ? mention : null;

                }));
            },
            index: 1
        }])
});

function reAvailableUser(id) {
    let url = '/admin/rest/user/reaviable';
    let formData = {
        deleteId: id
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {
            $("#reAvailableUserModal" + id).modal("hide");
            location.reload();
        },
        error: function (e) {

        }
    });
}

function deleteUser(id) {
    let url = '/admin/rest/user/deleteUser';
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


// Отправка кастомного сообщения в вк
$(function () {
    $('.send-vk-btn').on('click', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var currentStatus = $(this).prev('.send-custom-vk-status');
        let url = '/rest/vkontakte';
        let formData = {
            clientId: clientId,
            templateId: templateId,
            body: $('#custom-VKTemplate-body').val()
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
$(function () {
    $('#customVKMessageTemplate').on('hidden.bs.modal', function () {
        var currentStatus = $(this).find('.send-custom-vk-status');
        currentStatus.empty();
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
$(function () {
    $('#customEmailMessageTemplate').on('hidden.bs.modal', function () {
        var currentStatus = $(this).find('.send-email-err-status');
        currentStatus.empty();
    });
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
    $('.open-description-btn').on('click', function (event) {
        var id = $(this).data('id');
        var infoClient = $('#info-client' + id);
        var text = infoClient.find('.client-description').text();
        var clientModal = $('#clientDescriptionModal');
        $("#save-description").attr("data-id", id);

        clientModal.find('textarea').val(text);
        clientModal.modal('show');
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

//Установка идентификаторов в модальное окно отправки сообщений с фиксированным текстом.
$(function () {
    $('.portlet-send-btn').on('click', function () {
        var clientId = $(this).closest('.common-modal').data('cardId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#sendTemplateModal');
        var btn = currentModal.find('.send-all-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});


$(function () {
    $('.test-fix-btn').on('click', function () {
        var portlet = $(this).closest('#main-modal-window');
        var clientId = portlet.data('clientId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#sendTemplateModal');
        var btn = currentModal.find('.send-all-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);

    });

});


//Отправка сообщений с фиксированнм текстом во все выбранные социальные сети, email, SMS.
$(function () {
    $('.send-all-message').on('click', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var current = $(this);
        var currentStatus = $(this).prev('.send-fixed-template');
        var formData = {clientId: clientId, templateId: templateId};
        var url = [];
        var err = [];
        $('input[type="checkbox"]:checked').each(function (el) {
            var valuecheck = $(this).val();
            switch (valuecheck) {
                case ('email'):
                    url = '/rest/sendEmail';
                    break;
                case ('vk'):
                    url = '/rest/vkontakte';
                    break;
                case ('sms'):
                    url = '/user/sms/send/now/client';
                    break;
                //TODO временный адрес заглушка пока нету facebook, чтобы не нарушать работу методаю
                case ('facebook'):
                    url = '/temporary blank';
                    break;
            }
            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                beforeSend: function () {
                    current.text("Отправка..");
                    current.attr("disabled", "true")
                },
                success: function (result) {
                    if (err.length === 0) {
                        $(".modal").modal('hide');
                        current.text("Отправить");
                        current.removeAttr("disabled");
                    }
                },
                error: function (e) {
                    err.push(valuecheck);
                    current.text("Отправить");
                    currentStatus.text("Не удалось отправить сообщение " + err);
                    current.attr("disabled", "true");
                    console.log(e)
                }
            });
        });
    });
});
$(function () {
    $('.fix-modal').on('hidden.bs.modal', function () {
        var currentForm = $(this).find('.send-fixed-template');
        currentForm.empty();
        $("input[type=checkbox]").prop('checked', false);
        $(this).find('.send-all-message').removeAttr("disabled");
        $(".soc-network-box").remove();
    });
});

//Установка идентификаторов в модальное окно отправки сообщений с кастомным текстом.
$(function () {
    $('.portlet-custom-btn').on('click', function () {
        var portlet = $(this).closest('.common-modal');
        var clientId = portlet.data('cardId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customMessageTemplate');
        var btn = currentModal.find('.send-all-custom-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

$(function () {
    $('.test-custom-btn').on('click', function () {
        var portlet = $(this).closest('#main-modal-window');
        var clientId = portlet.data('clientId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customMessageTemplate');
        var btn = currentModal.find('.send-all-custom-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);

    });

});
// Кнопка  вк
// $(function () {
//     $(function (client) {
//
//  var clientId = client.age;
//
//     $('#vk-href').attr('href', clientId);
//     });
// });

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


//Отрпавка сообщений с кастомным текстом во все выбранные социальные сети, email, SMS.
$(function () {
    $('.send-all-custom-message').on('click', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var current = $(this);
        var currentStatus = $(this).prev('.send-custom-template');
        var formData = {
            clientId: clientId, templateId: templateId,
            body: $('#custom-eTemplate-body').val()
        };
        var url = [];
        var err = [];
        $('input[type="checkbox"]:checked').each(function (el) {
            var valuecheck = $(this).val();
            switch ($(this).val()) {
                case ('email'):
                    url = '/rest/sendEmail';
                    break;
                case ('vk'):
                    url = '/rest/vkontakte';
                    break;
                case ('sms'):
                    url = '/user/sms/send/now/client';
                    break;
                //TODO временный адрес заглушка пока нету facebook, чтобы не нарушать работу методаю
                case ('facebook'):
                    url = '/temporary blank';
                    break;
            }
            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                beforeSend: function () {
                    current.text("Отправка..");
                    current.attr("disabled", "true")
                },
                success: function (result) {
                    if (err.length === 0) {
                        $(".modal").modal('hide');
                        $('#custom-eTemplate-body').val("");
                        current.text("Отправить");
                        current.removeAttr("disabled");
                    }
                },
                error: function (e) {
                    err.push(valuecheck);
                    current.text("Отправить");
                    currentStatus.text("Не удалось отправить сообщение " + err);
                    console.log(e);
                }
            });
        });
    });
});
$(function () {
    $('.custom-modal').on('hide.bs.modal', function () {
        var currentForm = $(this).find('.send-custom-template');
        currentForm.empty();
        $("input[type=checkbox]").prop('checked', false);
        $(this).find('.send-all-custom-message').removeAttr("disabled");
    });
});


function hideClient(clientId) {
    let url = 'rest/client/postpone';
    let formData = {
        clientId: clientId,
        date: $('#postponeDate' + clientId).val()
    };
    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            location.reload();
        },
        error: function (e) {
            currentStatus = $("#postponeStatus" + clientId)[0];
            currentStatus.textContent = "Произошла ошибка";
            console.log(e.responseText)
        }
    })
}

$(document).ready(function () {
    var nowDate = new Date();
    var minutes = Math.ceil((nowDate.getMinutes() + 1) / 10) * 10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes, 0, 0);
    var startDate = moment(minDate)/*.utcOffset(180)*/;
    $('input[name="postponeDate"]').daterangepicker({
        singleDatePicker: true,
        timePicker: true,
        timePickerIncrement: 10,
        timePicker24Hour: true,
        locale: {
            format: 'DD.MM.YYYY HH:mm'
        },
        minDate: startDate,
        startDate: startDate
    });
});


$(function () {
    $('.portlet-body').on('click', function (e) {
        if (e.target.className.startsWith("portlet-body") === true) {
            var clientId = $(this).parents('.common-modal').data('cardId');
            var currentModal = $('#main-modal-window');
            currentModal.data('clientId', clientId);
            currentModal.modal('show');
            markAsReadMenu($(e.target).attr('client-id'))
        }
    });
});


$(function () {
    $('.portlet-header').on('click', function (e) {
        var clientId = $(this).parents('.common-modal').data('cardId');
        var currentModal = $('#main-modal-window');
        currentModal.data('clientId', clientId);
        currentModal.modal('show');
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

$('.confirm-skype-interceptor').on('click', '.confirm-skype-btn', function (e) {
    var currentForm = $('.box-window');
    var skypeBtn = $('.skype-postpone-date');
    var skypeBtn2 = $('.confirm-skype-btn');
    var clientId = $(this).parents('#main-modal-window').data('clientId');

    skypeBtn.hide();
    skypeBtn2.remove();
    $('.skype-panel-head').text("Напомнить клиенту за час до созвона");
    currentForm.append('<button type="button" class="btn btn-success btn-xs select_all_skype_boxes" data-toggle="button">Выбрать все</button>');
    currentForm.after('<button type="button" class="btn btn-primary btn-xs send-skype-message">Подтвердить</button>');
    drawCheckbox(currentForm, clientId);
});

$('.confirm-skype-interceptor').on('click', '.send-skype-message', function (e) {
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var sel = $('input[type="checkbox"]:checked').map(function (i, el) {
        return $(el).val();
    });

    var boxList = sel.get();

    let url = 'rest/skype/assignSkype';
    let formData = {
        clientId: clientId,
        date: $('#skypePostpone' + clientId).val(),
        selectNetwork: JSON.stringify(boxList)
    };
    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            $('.skype-panel').remove();
            $('.skype-notification').hide();
            if (boxList.length === 0) {
                $('.assign-skype-call-btn').after('<h5 class="skype-text">Уведомление о напоминании не было выбрано</h5>');
            } else {
                $('.assign-skype-call-btn').after('<h5 class="skype-text">' + 'Клиент будет уведомлен за час до созвона по ' + boxList + '</h5>');
            }
        },
        error: function (e) {
            var currentStatus = $("skype-notification" + clientId)[0];
            currentStatus.text("Произошла ошибка");
            console.log(e.responseText)
        }
    })
});


$('.assign-skype-call-btn').on('click', function (e) {
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var currentBtn = $(this);
    currentBtn.attr("disabled", "true");
    var currentStatus = $('.skype-notification');
    var formData = {clientId: clientId};
    var nowDate = new Date();
    var minutes = Math.ceil((nowDate.getMinutes() + 1) / 10) * 10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes, 0, 0);
    var startDate = moment(minDate).utcOffset(180);
    $.ajax({
        type: 'GET',
        url: 'rest/client/' + clientId,
        data: formData,
        success: function (client) {
            var clientSkype = client.skype;
            if (clientSkype === null || 0 === clientSkype.length) {
                currentStatus.css('color', '#333');
                currentStatus.text("Введите Skype пользователя");
                currentStatus.after('<input class="enter-skype-login form-control"> </input>');
                $('.enter-skype-login').after('<br/>' + '<button type="button" class="btn btn-primary btn-sm confirm-skype-login">Подтвердить</button>');
            } else {
                currentBtn.attr("disabled", "true");
                currentBtn.after('<div class="panel-group skype-panel"><div class="panel panel-default"><div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                    '<div class="panel-body">' + '<input type="text" class="form-control skype-postpone-date" name="skypePostponeDate" id="skypePostpone' + client.id + '"> </input>' +
                    '<button class="btn btn-info btn-sm confirm-skype-btn">ОК</button>' + ' <form class="box-window"></form>' + '</div></div>');
                $('input[name="skypePostponeDate"]').daterangepicker({
                    singleDatePicker: true,
                    timePicker: true,
                    timePickerIncrement: 1,
                    timePicker24Hour: true,
                    locale: {
                        format: 'DD.MM.YYYY HH:mm МСК'
                    },
                    minDate: startDate,
                    startDate: startDate
                });
            }
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color', '#229922');
            currentStatus.text(error);
        }
    });
});


$('.confirm-skype-interceptor').on('click', '.confirm-skype-login', function (e) {
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var currentBtn = $('.assign-skype-call-btn');
    currentBtn.attr("disabled", "true");
    var currentStatus = $('.skype-notification');
    var nowDate = new Date();
    var minutes = Math.ceil((nowDate.getMinutes() + 1) / 10) * 10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes, 0, 0);
    var startDate = moment(minDate).utcOffset(180);
    var skypeLogin = $('.enter-skype-login').val();
    var formData = {clientId: clientId, skypeLogin: skypeLogin};
    $.ajax({
        type: 'POST',
        url: 'rest/client/setSkypeLogin',
        data: formData,
        success: function (client) {
            currentStatus.css('color', '#229922');
            currentStatus.text("Логин Skype успешно добавлен");
            $('.confirm-skype-login').remove();
            $('.enter-skype-login').remove();
            //
            currentBtn.attr("disabled", "true");
            currentBtn.after('<div class="panel-group skype-panel"><div class="panel panel-default"><div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                '<div class="panel-body">' + '<input type="text" class="form-control skype-postpone-date" name="skypePostponeDate" id="skypePostpone' + clientId + '"> </input>' +
                '<button class="btn btn-info btn-sm confirm-skype-btn">ОК</button>' + ' <form class="box-window"></form>' + '</div></div>');
            $('input[name="skypePostponeDate"]').daterangepicker({
                singleDatePicker: true,
                timePicker: true,
                timePickerIncrement: 1,
                timePicker24Hour: true,
                locale: {
                    format: 'DD.MM.YYYY HH:mm МСК'
                },
                minDate: startDate,
                startDate: startDate
            });
        },
        error: function (error) {
            currentStatus.css('color', '#229922');
            currentStatus.text("Клиент с таким логином уже существует");
        }
    });
});

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        var currentModal = $(this);
        var clientId = $(this).data('clientId');
        let formData = {clientId: clientId};
        $.ajax({
            type: 'GET',
            url: 'rest/client/' + clientId,
            data: formData,
            success: function (client) {
                $.get('rest/client/getPrincipal', function (user) {
                    if (client.ownerUser != null) {
                        var owenerName = client.ownerUser.firstName + ' ' + client.ownerUser.lastName;
                    }
                    var adminName = user.firstName + ' ' + user.lastName;
                    $('#main-modal-window').data('userId', user.id);

                    currentModal.find('.modal-title').text(client.name + ' ' + client.lastName);
                    $('#client-email').text(client.email);
                    $('#client-phone').text(client.phoneNumber);
                    if (client.canCall && user.ipTelephony) {
                        $('#client-phone').after('<td id="web-call-voximplant" class="remove-tag">' + '<button class="btn btn-default btn btn-light btn-xs call-to-client" onclick="webCallToClient(' + client.phoneNumber + ')">' + '<span class="glyphicon glyphicon-earphone call-icon">' + '</span>' + '</button>' + '</td>')
                            .after('<td id="callback-call-voximplant" class="remove-tag">' + '<button class="btn btn-default btn btn-light btn-xs callback-call" onclick="callToClient(' + user.phoneNumber + ', ' + client.phoneNumber + ')">' + '<span class="glyphicon glyphicon-phone">' + '</span>' + '</button>' + '</td>');
                    }

                    if (client.age > 0) {
                        $('#client-age').text(client.age);
                    }
                    $('#client-sex').text(client.sex);

                    if (client.email == null) {
                        $('#email-href').hide();
                    } else {
                        $('#email-href').show();
                    }
                    // здесь вставка ссылок в кнопки вк и фб


                    $('#vk-href').hide();
                    $('#fb-href').hide();

                    for (var i = 0; i < client.socialProfiles.length; i++) {
                        if (client.socialProfiles[i].socialProfileType.name == 'vk') {
                            $('#vk-href').attr('href', client.socialProfiles[i].link);
                            $('#vk-href').show();
                        }
                        if (client.socialProfiles[i].socialProfileType.name == 'facebook') {
                            $('#fb-href').attr('href', client.socialProfiles[i].link);
                            $('#fb-href').show();
                        }
                    }
                    var btnBlock = $('div#assign-unassign-btns');
                    if (client.ownerUser === null) {
                        btnBlock.append('<button class="btn btn-sm btn-info remove-tag" id="assign-client' + client.id + '"onclick="assign(' + client.id + ')"> взять себе карточку </button>');
                    }
                    if (client.ownerUser !== null && owenerName === adminName) {
                        btnBlock.prepend('<button class="btn btn-sm btn-warning remove-tag" id="unassign-client' + client.id + '" onclick="unassign(' + client.id + ')"> отказаться от карточки </button>');
                    }
                    btnBlock.prepend('<a href="/client/clientInfo/' + client.id + '">' +
                        '<button class="btn btn-info btn-sm" id="client-info"  rel="clientInfo" "> расширенная информация </button>' + '</a');
                });

                $('.send-all-custom-message').attr('clientId', clientId);
                $('.send-all-message').attr('clientId', clientId);
                $('#hideClientCollapse').attr('id', 'hideClientCollapse' + client.id);
                $('#postponeDate').attr('id', 'postponeDate' + client.id);
                $('#postpone-accordion').append('<h4 class="panel-title remove-element">' + '<a href="#hideClientCollapse' + client.id + '" сlass="font-size" data-toggle="collapse" data-parent="#hideAccordion" > Скрыть карточку  </a>' + '</h4>');
                $('#postpone-div').append('<button class="btn btn-md btn-info remove-element" onclick="hideClient(' + client.id + ')"> OK </button>');
                $('.postponeStatus').attr('id', 'postponeStatus' + client.id);
                $('.textcomplete').attr('id', 'new-text-for-client' + client.id);
                $('.comment-div').append('<button class="btn btn-sm btn-success comment-button remove-element" id="assign-client' + client.id + '"  onclick="sendComment(' + client.id + ', \'test_message\')"> Сохранить </button>');
                $('.main-modal-comment').attr('id', 'client-' + client.id + 'comments');
                $('.upload-history').attr('data-id', client.id).attr('href', '#collapse' + client.id);
                $('.client-collapse').attr('id', 'collapse' + client.id);
                $('.history-line').attr('id', 'client-' + client.id + 'history');
                $('.upload-more-history').attr('data-clientid', client.id);
            }
        });
    });
});

$(function () {
    $('#main-modal-window').on('hidden.bs.modal', function () {
        $('.assign-skype-call-btn').removeAttr("disabled");
        $('div#assign-unassign-btns').empty();
        $('.skype-notification').empty();
        $('.confirm-skype-login').remove();
        $('.enter-skype-login').remove();
        $('.skype-panel').remove();
        $('.skype-text').empty();
        $('.remove-element').remove();
        $('.hide-client-collapse').attr('id', 'hideClientCollapse');
        $('.postpone-date').attr('id', 'postponeDate');
        $('.textcomplete').removeAttr('id');
        $('.main-modal-comment').removeAttr('id');
        $('.remove-tag').remove();
        $('.history-line').find("tbody").empty();
        $('#sendEmailTemplateStatus').empty();
        $('#sendSocialTemplateStatus').empty();
        $('.client-collapse').collapse('hide');
        $('.remove-history').remove();
        $('.upload-more-history').removeAttr('data-clientid');
        $('.upload-more-history').attr("data-page", 1);
    });
});

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        var clean = $('.history-line').find("tbody");
        clean.empty();
    });
});

function callToClient(userPhone, clientPhone) {
    console.log("TRY TO CALL");
    var url = "/user/rest/call/voximplant";
    var formData = {
        from: userPhone,
        to: clientPhone
    };
    let icon = $(".callback-call");
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function () {
            console.log("PROCESS CALL");
            icon.css("background", "green");
            icon.css("color", "white");
            icon.attr("disabled", "true");
        },
        error: function (error) {
            console.log("ERROR CALL");
            console.log(error);
        }
    });
}

//web call to client by using Voximplant SDK
function webCallToClient(clientPhone) {
    console.log("Trying to call by using Voximplant SDK");
    const sdk = VoxImplant.getInstance();
    var voxLogin;
    var voxPassword;
    var callerId;
    var url = "/user/rest/call/sendData";
    var credentialsUrl = "/user/rest/call/voximplantCredentials";
    var formData = {
        to: clientPhone
    };
    let icon = $(".call-to-client");

    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function (callRecordId) {
            console.log("PROCESS WEBCALL");
            callerId = callRecordId.id;
            icon.css("background", "green");
            icon.css("color", "white");
            icon.attr("disabled", "true");
        },
        error: function (error) {
            console.log("ERROR WEBCALL");
            console.log(error);
        }
    });

    $.ajax({
        type: 'get',
        url: credentialsUrl,
        success: function (credensials) {
            var arr = credensials.split(",");
            voxLogin = arr[0];
            voxPassword = arr[1];
        },
        error: function (error) {
            console.log(error);
        }
    });

    sdk.init()
        .then(() => {
            console.log('This code is executed after SDK successfully initializes');
            // connecting to the Voximplant Cloud;
            // "false" argument disables checking of UDP connection (for fastest connect)
            return sdk.connect(false);
        })
        .then(() => {
            console.log('This code is executed after SDK is successfully connected to Voximplant');
            //return sdk.login(voxLogin, voxPassword);
            sdk.requestOneTimeLoginKey(voxLogin);

            sdk.addEventListener(VoxImplant.Events.AuthResult, e => {
                console.log('AuthResult: ' + e.result);
                if (e.result) {
                    console.log('This code is executed on successfull login');

                    const call = sdk.call({number: clientPhone, customData: callerId});
                    call.on(VoxImplant.CallEvents.Connected, () => console.log('You can hear audio from the cloud'));
                    call.on(VoxImplant.CallEvents.Failed, (e) => console.log(`Call failed with the ${e.code} error`));
                    call.on(VoxImplant.CallEvents.Disconnected, () => console.log('The call has ended'));
                } else {
                    if (e.code == 302) {
                        $.post('/user/rest/call/calcKey', {
                            key: e.key
                        }, token => {
                            sdk.loginWithOneTimeKey(voxLogin, token);
                        }, 'text');
                    }
                }
            })
            return VoxImplant.AuthResult.result;
        })
        .catch((e) => {
            console.log(e);
        });
}

//авторизация Вконтакте
function vk_popup(options) {
    var
        screenX = typeof window.screenX != 'undefined' ? window.screenX : window.screenLeft,
        screenY = typeof window.screenY != 'undefined' ? window.screenY : window.screenTop,
        outerWidth = typeof window.outerWidth != 'undefined' ? window.outerWidth : document.body.clientWidth,
        outerHeight = typeof window.outerHeight != 'undefined' ? window.outerHeight : (document.body.clientHeight - 22),
        width = options.width,
        height = options.height,
        left = parseInt(screenX + ((outerWidth - width) / 2), 10),
        top = parseInt(screenY + ((outerHeight - height) / 2.5), 10),
        features = (
            'width=' + width +
            ',height=' + height +
            ',left=' + left +
            ',top=' + top
        );
    return window.open(options.url, 'vk_oauth', features);
}

function doLogin() {
    var win;
    var redirect_uri = 'https://oauth.vk.com/blank.html';
    var uri_regex = new RegExp(redirect_uri);
    var url = '/vk-auth';
    win = vk_popup({
        width: 620,
        height: 370,
        url: url
    });
    var watch_timer = setInterval(function () {
        try {
            if (uri_regex.test(win.location)) {
                clearInterval(watch_timer);
                setTimeout(function () {
                    win.close();
                    document.location.reload();
                }, 500);
            }
        } catch (e) {
        }
    }, 100);
}

$(".change-status-position").on('click', function () {
    let destinationId = $(this).attr("value");
    let sourceId = $(this).parents(".column").attr("value");
    let url = "/rest/status/position/change";
    let formData = {
        sourceId: sourceId,
        destinationId: destinationId
    };
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function () {
            location.reload();
        },
        error: function (error) {

        }
    });
});

function changeUrl(id) {
    var state = {'page_id': id, 'user_id': id};
    var title = '';
    var url = '/client?id=' + id;

    history.replaceState(state, title, url);
}

function backUrl() {
    var state = {};
    var title = '';
    var url = '/client';

    history.replaceState(state, title, url);
}

function getAllUrlParams(url) {
    // извлекаем строку из URL или объекта window
    var queryString = url ? url.split('?')[1] : window.location.search.slice(1);
    // объект для хранения параметров
    var obj = {};
    // если есть строка запроса
    if (queryString) {
        // данные после знака # будут опущены
        queryString = queryString.split('#')[0];
        // разделяем параметры
        var arr = queryString.split('&');

        for (var i = 0; i < arr.length; i++) {
            // разделяем параметр на ключ => значение
            var a = arr[i].split('=');
            // обработка данных вида: list[]=thing1&list[]=thing2
            var paramNum = undefined;
            var paramName = a[0].replace(/\[\d*\]/, function (v) {
                paramNum = v.slice(1, -1);
                return '';
            });

            // передача значения параметра ('true' если значение не задано)
            var paramValue = typeof(a[1]) === 'undefined' ? true : a[1];

            // преобразование регистра
            paramName = paramName.toLowerCase();
            paramValue = paramValue.toLowerCase();
            // если ключ параметра уже задан
            if (obj[paramName]) {
                // преобразуем текущее значение в массив
                if (typeof obj[paramName] === 'string') {
                    obj[paramName] = [obj[paramName]];
                }
                // если не задан индекс...
                if (typeof paramNum === 'undefined') {
                    // помещаем значение в конец массива
                    obj[paramName].push(paramValue);
                }
                // если индекс задан...
                else {
                    // размещаем элемент по заданному индексу
                    obj[paramName][paramNum] = paramValue;
                }
            }
            // если параметр не задан, делаем это вручную
            else {
                obj[paramName] = paramValue;
            }
        }
    }
    return obj;
}

$(function () {
    $(document).ready(function () {

        if (window.location.href.indexOf('client?id=') != -1) {
            var clientId = getAllUrlParams(window.location.href).id;
            var currentModal = $('#main-modal-window');
            currentModal.data('clientId', clientId);
            currentModal.modal('show');
        }

    });
});


function deleteNewUser(deleteId) {
    let url = '/admin/rest/user/delete';
    let data = {
        deleteId: deleteId
    };

    $.ajax({
        type: "POST",
        url: url,
        data: data,
        success: function () {
            location.reload();
        },
        error: function () {
            alert("Пользователь не был удален")
        }
    });
}