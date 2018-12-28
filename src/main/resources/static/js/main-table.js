var initialized = false; // SDK загружено

const sdk = VoxImplant.getInstance();
sdk.on(VoxImplant.Events.SDKReady, onSdkReady);
sdk.on(VoxImplant.Events.ConnectionEstablished, onConnectionEstablished);
sdk.on(VoxImplant.Events.AuthResult, onAuthResult);

var currentCall;
var voxLogin;
var voxPassword;
var callerId;
var callToPhone;
var micMuted;

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

//Search clients in main
function clientsSearch() {
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
}

let logged_in_profiles;

function get_us() {
    $.ajax({
        type: "GET",
        url: "/rest/conversation/us",
        success: function (response) {
            logged_in_profiles = response;
        }
    })
}

$(document).ready(function () {
    get_us();
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

    clientsSearch();

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
    let currentStatus = document.getElementById("sendSocialTemplateStatus");

    if (typeof statusName === "undefined" || statusName === "") return;
    let formData = {
        statusName: statusName
    };

    if(statusName.length < 25) {
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
    else {
        currentStatus.style.color = "red";
        currentStatus.textContent = "Название уменьши ка, будь человеком";
    }
}

//Change status button
function changeStatusName(id) {
    let url = '/admin/rest/status/edit';
    let statusName = $("#change-status-name" + id).val();
    let trial_offset = parseInt($("#trial_offset_" + id).val());
    let next_payment_offset = trial_offset +  parseInt($("#next_payment_offset_" + id).val());
    if (!validate_status_input(trial_offset, next_payment_offset)) {
        return
    }
    ;
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

//добавляем упоминания юзеров в полях комментариев карточки клиента
document.querySelector('.modal-comments').onclick = (e) => {
    const target = e.target;
    const area = target.getAttribute('id');
    if (area.indexOf("new-text-for-client") === 0 || area.indexOf("new-answer-for-comment") === 0) {
        mentionUser();
    }
};
//функция упоминания юзера
function mentionUser() {
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

    $('#main-modal-window .textcomplete').textcomplete([
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
}

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
            if (url.length > 0) {
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
            }
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
        if ($('#custom-eTemplate-body').val().length===0){
            return currentStatus.text("Введите текст сообщения!");
        }
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
            if (url.length > 0) {
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
            }
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
    let flag = document.querySelector(".isPostponeFlag").checked;
    let commentUrl = '/rest/comment/add';
    let comment = document.querySelector(".postponeComment").value;
    let formData = {
        clientId: clientId,
        date: $('#postponeDate' + clientId).val(),
        isPostponeFlag: flag,
        postponeComment: comment,
    };
    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {
            $.ajax({
                type: 'POST',
                dataType: 'json',
                url: commentUrl,
                data: {
                    clientId: clientId,
                    content: comment
                },
                success: function () {
                    let currentStatus = document.getElementById("postpone-status");
                    currentStatus.style.color = "limegreen";
                    currentStatus.textContent = "Клиент успешно скрыт";
                },
            });
        },
        error: function (e) {
            currentStatus = $("#postponeStatus" + clientId)[0];
            currentStatus.textContent = "Произошла ошибка";
            console.log(e.responseText)
        }
    });
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

var idMentor;
var skypeCallDateNew;
var skypeCallDateOld;

function assignSkype(id) {
    var clientId = id;
    var btnBlockTask = $('div.confirm-skype-interceptor .assign-skype-call-btn');
    var currentBtn = $(document).find('.assign-skype-call-btn');
    currentBtn.attr("disabled", "true");
    var currentStatus = $('.skype-notification');
    var formData = {clientId: clientId};
    var nowDate = new Date();
    var minutes =  Math.ceil((nowDate.getMinutes() +1 )/10)*10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes , 0, 0);
    var startDate = minDate;
    $.ajax({
        type: 'GET',
        url: 'rest/client/' + clientId,
        data: formData,
        success: function (client) {
            btnBlockTask.attr('id', 'assign-skype' + clientId);
            var clientSkype = client.skype;
            if(clientSkype === null || 0 === clientSkype.length) {
                currentStatus.css('color', '#333');
                currentStatus.text("Введите Skype пользователя");
                currentStatus.after('<input class="enter-skype-login form-control"> </input>');
                $('.enter-skype-login').after('<br/>' + '<button onclick="confirmSkype(' + id + ')" type="button" class="btn btn-primary btn-sm confirm-skype-login">Подтвердить</button>');
            } else {
                // Get the list of mentors
                $.ajax({
                    type: 'GET',
                    url: 'rest/skype/allMentors',
                    dataType: 'json',
                    success: function (mentors) {
                        currentStatus.css('color', '#333');
                        currentStatus.text("Выбирете ментора из списка");
                        currentStatus.after(
                            '<p><select id="mentor" class="remove-element enter-mentor-list form-control"></select></p>' +
                            '<button class="btn btn-info btn-sm confirm-skype-btn">Подтвердить</button>');
                        if (mentors === null || mentors.length === 0) {
                            currentStatus.css('color', '#229922');
                            currentStatus.text("Менторы не найдены");
                        } else {
                            $.each(mentors, function(key, value) {
                                $('.enter-mentor-list')
                                    .append($("<option></option>")
                                        .attr("value",value.id)
                                        .text(value.firstName + " " + value.lastName));
                            });
                        }
                    },

                    error: function (error) {
                        console.log(error);
                        currentStatus.css('color','#229922');
                        currentStatus.text(error);
                    }

                });


                currentBtn.attr("disabled", "true");
                currentBtn.after(
                    '<div class="panel-group skype-panel"><div class="panel panel-default"><div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                    '<div class="panel-body">' + '<input readonly="false" type="text" class="form-control skype-postpone-date" name="skypePostponeDateOld" id="skypePostpone' + client.id +'"> </input>' +
                    ' <form class="box-window"></form>' +'</div></div>');
                $('.skype-panel').after(
                    '<div class="panel-group skype-panel">' +
                    '<div class="panel panel-default"><div class="panel-heading skype-panel-head">Напомнить клиенту за час до созвона</div>' +
                    '<div class="panel-body">' +
                    '<form class="add-box-window">' +
                    // '<button type="button" class="btn btn-success btn-xs select_all_skype_boxes" data-toggle="button">Выбрать все</button>' +
                    '</form>' +
                    '</div>' +
                    '</div>' +
                    '</div>');
                $(drawCheckbox($(".add-box-window"), clientId));
                // drawCheckbox($(".add-box-window"), clientId);
                $('input[name="skypePostponeDateOld"]').daterangepicker({
                    singleDatePicker: true,
                    timePicker: true,
                    timePickerIncrement: 10,
                    timePicker24Hour: true,
                    locale: {
                        format: 'DD.MM.YYYY HH:mm МСК'
                    },
                    minDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' })),
                    startDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }))
                });
            }
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#229922');
            currentStatus.text(error);
        }
    });
};

function confirmSkype(id) {
    var currentBtn = $(document).find('.assign-skype-call-btn');
    var clientId = id;
    var currentStatus = $('.skype-notification');
    var skypeLogin = $('.enter-skype-login').val();
    var formData = {clientId: clientId, skypeLogin: skypeLogin};
    var nowDate = new Date();
    var minutes =  Math.ceil((nowDate.getMinutes() +1)/10)*10;
    var startDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes , 0, 0);
    $.ajax({
        type: 'POST',
        url: 'rest/client/setSkypeLogin',
        data: formData,
        success: function (client) {
            currentStatus.css('color', '#229922');
            currentStatus.text("Логин Skype успешно добавлен");
            $('.confirm-skype-login').remove();
            $('.enter-skype-login').remove();

            // Get the list of mentors
            $.ajax({
                type: 'GET',
                url: 'rest/skype/allMentors',
                dataType: 'json',
                success: function (mentors) {
                    currentStatus.css('color', '#333');
                    currentStatus.text("Выбирете ментора из списка");
                    currentStatus.after(
                        '<p><select id="mentor" class="remove-element enter-mentor-list form-control"></select></p>' +
                        '<button class="btn btn-info btn-sm confirm-skype-btn">Подтвердить</button>');
                    if (mentors === null || mentors.length === 0) {
                        currentStatus.css('color', '#333');
                        currentStatus.text("Менторы не найдены");
                    } else {
                        $.each(mentors, function(key, value) {
                            $('.enter-mentor-list')
                                .append($("<option></option>")
                                    .attr("value",value.id)
                                    .text(value.firstName + " " + value.lastName));
                        });
                        idMentor = document.getElementsByTagName("option")[document.getElementById("mentor").selectedIndex].value;
                    }
                },
                error: function (error) {
                    console.log(error);
                    currentStatus.css('color','#229922');
                    currentStatus.text(error);
                }
            });

            currentBtn.attr("disabled", "true");
            currentBtn.after(
                '<div class="panel-group skype-panel"><div class="panel panel-default"><div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                '<div class="panel-body">' + '<input readonly="false" type="text" class="form-control skype-postpone-date" name="skypePostponeDateOld" id="skypePostpone' + clientId +'"> </input>' +
                ' <form class="box-window"></form>' +'</div></div>');
            $('.skype-panel').after(
                '<div class="panel-group skype-panel">' +
                '<div class="panel panel-default"><div class="panel-heading skype-panel-head">Напомнить клиенту за час до созвона</div>' +
                '<div class="panel-body">' +
                '<form class="add-box-window">' +
                // '<button type="button" class="btn btn-success btn-xs select_all_skype_boxes" data-toggle="button">Выбрать все</button>' +
                '</form>' +
                '</div>' +
                '</div>' +
                '</div>');
            $(drawCheckbox($(".add-box-window"), clientId));
            $('input[name="skypePostponeDateOld"]').daterangepicker({
                singleDatePicker: true,
                timePicker: true,
                timePickerIncrement: 10,
                timePicker24Hour: true,
                locale: {
                    format: 'DD.MM.YYYY HH:mm МСК'
                },
                minDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' })),
                startDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }))
            });
        },
        error: function () {
            currentStatus.css('color','#229922');
            currentStatus.text("Клиент с таким логином уже существует");
        }
    });
};

$(document).on('click','.confirm-skype-btn', function (e) {
    skypeCallDateOld = $('input[name="skypePostponeDateOld"]').data('daterangepicker').startDate._d;
    idMentor = document.getElementsByTagName("option")[document.getElementById("mentor").selectedIndex].value;
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var currentBtn = $(document).find('.assign-skype-call-btn');
    var currentStatus = $('.skype-notification');
    var editDate = $('#assign-skype' + clientId);
    var sel = $('.soc-network-box input:checkbox:checked').map(function (i, el) {
        return $(el).val();
    });
    var boxList = sel.get();

    let checkCallSkype = {
        idMentor: idMentor,
        startDate: Date.UTC(skypeCallDateOld.getFullYear(), skypeCallDateOld.getMonth(), skypeCallDateOld.getDate(), skypeCallDateOld.getHours(), skypeCallDateOld.getMinutes() , 0, 0),
        clientId: clientId
    };

    let addCallSkype = {
        idMentor: idMentor,
        startDate: Date.UTC(skypeCallDateOld.getFullYear(), skypeCallDateOld.getMonth(), skypeCallDateOld.getDate(), skypeCallDateOld.getHours(), skypeCallDateOld.getMinutes() , 0, 0),
        clientId: clientId,
        selectNetwork: JSON.stringify(boxList)
    };

    // Check free date
    $.ajax({
        type: 'GET',
        url: 'rest/skype/checkFreeDateAndCorrectEmail',
        data: checkCallSkype,
        dataType: 'json',
        statusCode: {
            200: function () {
                // Add Event in calendar mentor
                $.ajax({
                    type: 'POST',
                    url: 'rest/skype/addSkypeCallAndNotification',
                    data: addCallSkype,
                    success: function () {
                        $('.assign-skype-call-btn').hide();
                        $('#freeDate, .skype-panel, .skype-notification, .enter-mentor-list, .confirm-skype-btn').remove();
                        editDate.after(
                            '<div class="remove-tag confirm-skype-interceptor">' +
                            '<div class="update btn-group">' +
                            '<button id="assign-skype' + clientId + '" type="button" onclick="updateCallDate(' + clientId + ')" class="btn btn-default update-date-btn btn-sm"><span class="glyphicon glyphicon-pencil"></span> Изменить время беседы</button>' +
                            '<button type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <span class="glyphicon glyphicon-remove"></span></button>'  +
                            '<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="deleteDate">\n' +
                            '<li><a onclick="deleteCallDate(' + clientId + ')" href="#">Удалить беседу</a></li>\n' +
                            '<li><a href="#">Отмена</a></li>\n' +
                            '</ul>' +
                            '</div>' +
                            '<div class="skype-notification" style="color:#229922">Время беседы назначено.</div>' +
                            '</div>');
                    },
                    error: function (error) {
                        console.log(error);
                        currentStatus.css('color','#d01717');
                        currentStatus.text(error.responseText);
                    }
                });
            },
            400: function (error) {
                currentStatus.css('color','#515151');
                currentStatus.text('Выбирете ментора из списка');
                $('#freeDate').remove();
                if(error.responseText.indexOf('@gmail.com)') >= 0){
                    currentStatus.css('color','#d01717');
                    currentStatus.text(error.responseText);
                }
                if(error.responseText.indexOf('Календарь ментора не привязан к календарю администратора.') >= 0){
                    currentStatus.css('color','#d01717');
                    currentStatus.text(error.responseText);
                }
                if (error.responseText.indexOf('Текущая дата уже занята') >= 0) {
                    currentBtn.after('<div id="freeDate"><span style="color:#d01717">Текущая дата уже занята, выберите другую.</span></div>');
                }
                // if (!document.getElementById('freeDate')) {
                //     currentBtn.after('<span id="freeDate" style="color:#d01717">Текущая дата уже занята, выберите другую.</span>');
                // }
            },
            401: function () {
                $('.skype-panel').hide();
                $('.skype-notification').hide();
                $('.enter-mentor-list').hide();
                $('.confirm-skype-btn').hide();
                $('.assign-skype-call-btn').after(
                    '<p> ' +
                    '<div class="skype-notification" style="color:#d01717">Авторизируйтесь в Google аккаунте.</div>' +
                    '<div class="inline">' +
                    '    <form class="form" method="get" action="/login/google">' +
                    '        <input type="submit" class="btn btn btn-success pul" value="Авторизация Google">' +
                    '    </form>' +
                    '</div>' +
                    '</p>');
                console.log("Авторизируйтесь в Google аккаунте");
            }
        },
    });
});

function updateCallDate(id) {
    var clientId = id;
    var btnBlockTask = $('div.confirm-skype-interceptor .assign-skype-call-btn');
    var currentBtn = $(document).find('.update', '.btn-group');
    var currentStatus = $('.skype-notification');
    var formData = {clientId: clientId};
    $(document).find('.update-date-btn').attr("disabled", "true");
    $.ajax({
        type: 'GET',
        url: 'rest/skype/' + clientId,
        data: formData,
        dataType: 'json',
        success: function (assignSkypeCall) {
            var oldDate = new Date(new Date(assignSkypeCall.skypeCallDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }));
            var date = new Date();
            var minutes =  Math.ceil((date.getMinutes() +1)/10)*10;
            var startDate = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), minutes , 0, 0);
            btnBlockTask.attr('id', 'assign-skype' + clientId);
            var socialNetworks = assignSkypeCall.selectNetworkForNotifications;
            // Get the list of mentors
            $.ajax({
                type: 'GET',
                url: 'rest/skype/allMentors',
                dataType: 'json',
                success: function (mentors) {
                    currentStatus.show();
                    currentStatus.css('color', '#333');
                    currentStatus.text("Выбирете ментора из списка");
                    currentStatus.after(
                        '<p><select id="mentor" class="remove-element enter-mentor-list form-control"></select></p>' +
                        '<button class="btn btn-info btn-sm update-skype-call">Подтвердить</button>');
                    if (document.getElementById('freeDate')) {
                        document.getElementById('freeDate').remove();
                    }
                    if (mentors === null || mentors.length === 0) {
                        currentStatus.css('color', '#229922');
                        currentStatus.text("Менторы не найдены");
                    } else {
                        $.each(mentors, function (key, value) {
                            if (assignSkypeCall.fromAssignSkypeCall.id === value.id) {
                                $('.enter-mentor-list')
                                    .append($("<option></option>")
                                        .prop('selected', true)
                                        .attr("value", value.id)
                                        .text(value.firstName + " " + value.lastName + " (Текущий ментор)"));
                            } else {
                                $('.enter-mentor-list')
                                    .append($("<option></option>")
                                        .attr("value", value.id)
                                        .text(value.firstName + " " + value.lastName));
                            }
                        });
                    }
                },

                error: function (error) {
                    console.log(error);
                    currentStatus.css('color','#d01717');
                    currentStatus.text(error);
                }

            });

            currentBtn.attr("disabled", "true");
            currentBtn.after(
                '<div class="panel-group skype-panel">' +
                '<div class="panel panel-default">' +
                '<div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                '<div class="panel-body">' + '<input readonly="false" type="text" class="form-control skype-postpone-date" name="skypePostponeDateNew" id="skypePostpone' + clientId +'"> </input>' +
                ' <form class="box-window"></form>' +'</div></div></div>');
            $('.skype-panel').after(
                '<div class="panel-group skype-panel">' +
                '<div class="panel panel-default"><div class="panel-heading skype-panel-head">Напомнить клиенту за час до созвона</div>' +
                '<div class="panel-body">' +
                '<form class="add-box-window">' +
                // '<button type="button" class="btn btn-success btn-xs select_all_skype_boxes" data-toggle="button">Выбрать все</button>' +
                '</form>' +
                '</div>' +
                '</div>' +
                '</div>');
            $(drawCheckbox($(".add-box-window"), clientId)).ready(function(){
                setTimeout(function() {
                    if (socialNetworks.indexOf('email') > 0) {
                        $(".my-checkbox-soc:checkbox[value='email']").prop("checked", "true")
                    }
                    if (socialNetworks.indexOf('vk') > 0) {
                        $(".my-checkbox-soc:checkbox[value='vk']").prop("checked", "true")
                    }
                    if (socialNetworks.indexOf('sms') > 0) {
                        $(".my-checkbox-soc:checkbox[value='sms']").prop("checked", "true")
                    }
                    if (socialNetworks.indexOf('facebook') > 0) {
                        $(".my-checkbox-soc:checkbox[value='facebook']").prop("checked", "true")
                    }
                }, 500);
            });
            // drawCheckbox($(".add-box-window"), clientId);
            $('input[name="skypePostponeDateNew"]').daterangepicker({
                singleDatePicker: true,
                timePicker: true,
                timePickerIncrement: 10,
                timePicker24Hour: true,
                locale: {
                    format: 'DD.MM.YYYY HH:mm МСК'
                },
                minDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' })),
                startDate: oldDate
            });
            skypeCallDateOld = oldDate;
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#d01717');
            currentStatus.text(error);
        }
    });
};

$(document).on('click','.update-skype-call', function (e) {
    skypeCallDateNew = $('input[name="skypePostponeDateNew"]').data('daterangepicker').startDate._d;
    idMentor = document.getElementsByTagName("option")[document.getElementById("mentor").selectedIndex].value;
    var skypeBtn2 = $('.update-skype-call, #mentor');
    var skypeBtn = $('.skype-postpone-date');
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var currentBtn = $(document).find('.update', '.btn-group');
    var currentStatus = $('.skype-notification');
    var sel = $('.soc-network-box input:checkbox:checked').map(function (i, el) {
        return $(el).val();
    });
    var boxList = sel.get();


    let checkFreeDate = {
        clientId: clientId,
        idMentor: idMentor,
        startDate: Date.UTC(skypeCallDateNew.getFullYear(), skypeCallDateNew.getMonth(), skypeCallDateNew.getDate(), skypeCallDateNew.getHours(), skypeCallDateNew.getMinutes() , 0, 0),
    };

    let updateEvent = {
        clientId: clientId,
        idMentor: idMentor,
        selectNetwork: JSON.stringify(boxList),
        skypeCallDateNew: Date.UTC(skypeCallDateNew.getFullYear(), skypeCallDateNew.getMonth(), skypeCallDateNew.getDate(), skypeCallDateNew.getHours(), skypeCallDateNew.getMinutes() , 0, 0),
        skypeCallDateOld: Date.UTC(skypeCallDateOld.getFullYear(), skypeCallDateOld.getMonth(), skypeCallDateOld.getDate(), skypeCallDateOld.getHours(), skypeCallDateOld.getMinutes() , 0, 0)
    };

    // Check free date
    $.ajax({
        type: 'GET',
        url: 'rest/skype/checkFreeDateAndCorrectEmail',
        data: checkFreeDate,
        dataType: 'json',
        statusCode: {
            200: function() {
                if (document.getElementById('freeDate')) {
                    document.getElementById('freeDate').remove();
                }
                // Update Event in calendar mentor
                $.ajax({
                    type: 'POST',
                    url: 'rest/mentor/updateEvent',
                    data: updateEvent,
                    success: function (e) {
                        if (!document.getElementById('freeDate')) {
                            currentBtn.after('<div id="freeDate"><span style="color:#229922">Новая дата назначена.</span></div>');
                            $(document).find('.update-date-btn').removeAttr("disabled");
                            skypeBtn2.remove();
                            skypeBtn.hide();
                            $('.skype-notification').hide();
                            $('.skype-panel').remove();
                            skypeCallDateOld = skypeCallDateNew;
                        }
                    },

                    error: function (error) {
                        console.log(error);
                        currentStatus.css('color','#d01717');
                        currentStatus.text(error.responseText);
                    }
                });
            },
            400: function (error) {
                currentStatus.css('color','#515151');
                currentStatus.text('Выбирете ментора из списка');
                $('#freeDate').remove();
                if(error.responseText.indexOf('@gmail.com)') >= 0){
                    currentStatus.css('color','#d01717');
                    currentStatus.text(error.responseText);
                }
                if(error.responseText.indexOf('Календарь ментора не привязан к календарю администратора.') >= 0){
                    currentStatus.css('color','#d01717');
                    currentStatus.text(error.responseText);
                }
                if (error.responseText.indexOf('Текущая дата уже занята') >= 0) {
                    currentBtn.after('<div id="freeDate"><span style="color:#d01717">Текущая дата уже занята, выберите другую.</span></div>');
                }
            },
            401: function () {
                $('.skype-panel').hide();
                $('.skype-notification').hide();
                $('.enter-mentor-list').hide();
                $('.confirm-skype-btn').hide();
                $('.assign-skype-call-btn').after(
                    '<p> ' +
                    '<div class="skype-notification" style="color:#d01717">Авторизируйтесь в Google аккаунте.</div>' +
                    '<div class="inline">' +
                    '    <form class="form" method="get" action="/login/google">' +
                    '        <input type="submit" class="btn btn btn-success pul" value="Авторизация Google">' +
                    '    </form>' +
                    '</div>' +
                    '</p>');
                console.log("Авторизируйтесь в Google аккаунте");
            }
        }
    });
});

function deleteCallDate(id) {
    var clientId = id;
    var skypeBtn2 = $('.confirm-skype-btn, #mentor');
    var skypeBtn = $('.skype-postpone-date');
    var formDataId = {clientId: clientId};
    var currentBtn = $(document).find('.update .btn-group');
    var currentStatus = $('.skype-notification');
    var btnBlockShow = $('div#assign-unassign-btns');

    $.ajax({
        type: 'GET',
        url: 'rest/skype/' + clientId,
        data: formDataId,
        dataType: 'json',
        success: function (assignSkypeCall) {
            // Delete date
            var date = new Date(new Date(assignSkypeCall.skypeCallDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }));
            var startDateOld = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes() , 0, 0);

            let deleteEvent = {
                clientId: clientId,
                idMentor: assignSkypeCall.fromAssignSkypeCall.id,
                skypeCallDateOld: startDateOld
            };

            $.ajax({
                type: 'POST',
                url: 'rest/mentor/deleteEvent',
                data: deleteEvent,
                success: function() {
                    currentBtn.removeAttr("disabled");
                    skypeBtn2.remove();
                    skypeBtn.hide();
                    $('.skype-panel').remove();
                    $('.skype-text').remove();
                    $('.enter-mentor-list').remove();
                    $('.update.btn-group').hide();
                    $('.skype-notification').show();

                    if (btnBlockShow.length > 0)
                    {
                        $('.confirm-skype-interceptor').remove();
                        btnBlockShow.after('<div class="remove-tag confirm-skype-interceptor"><button id="assign-skype' + clientId + '" onclick="assignSkype(' + clientId + ')" class="btn btn-primary center-block assign-skype-call-btn btn-sm">Назначить беседу в Skype</button>\n' +
                            '<div class="skype-notification"></div>' +
                            '</div>')
                    } else {
                        $('.assign-skype-call-btn').show().removeAttr("disabled");
                    }
                },
                401: function () {
                    $('.skype-panel').hide();
                    $('.skype-notification').hide();
                    $('.enter-mentor-list').hide();
                    $('.confirm-skype-btn').hide();
                    $('.assign-skype-call-btn').after(
                        '<p> ' +
                        '<div class="skype-notification" style="color:#d01717">Авторизируйтесь в Google аккаунте.</div>' +
                        '<div class="inline">' +
                        '    <form class="form" method="get" action="/login/google">' +
                        '        <input type="submit" class="btn btn btn-success pul" value="Авторизация Google">' +
                        '    </form>' +
                        '</div>' +
                        '</p>');
                    console.log("Авторизируйтесь в Google аккаунте");
                },
                error: function (error) {
                    console.log(error);
                    currentStatus.css('color','#229922');
                    currentStatus.text(error);
                }
            });
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#229922');
            currentStatus.text(error);
        }
    });
};

let interlocutor_profiles;

function get_interlocutors(clientId) {
    $.ajax({
        type: "GET",
        url: "/rest/conversation/interlocutors",
        data: {id: clientId},
        success: function (response) {
            interlocutor_profiles = response;
        }
    })
}

let conversations = $("#conversations-body");

function start_chats(clientId) {
    $.ajax({
        type: "GET",
        url: "/rest/conversation/all",
        data: {id: clientId},
        success: function (response) {
            $("#chat-messages").empty();
            for (let i in response) {
                let message_id = response[i].id;
                let send_date = new Date(response[i].time);
                let text = response[i].text;
                let is_outgoing = response[i].outgoing;
                let is_read = response[i].read;
                let sn_type = response[i].chatType;
                append_all_chats_message(message_id, send_date, text, is_outgoing, is_read, sn_type);
            }
            $("#send-selector").prop('value', response[response.length - 1].chatType);
            setTimeout(update_chat, 2000);
            setTimeout(scroll_down, 1000);
        }
    })
}

function set_send_selector(clientId) {
    let selector = $("#send-selector");
    selector.empty();
    $.ajax({
        type: "GET",
        url: "/rest/client/" + clientId,
        success: function (client) {
            for (let i = 0; i < client.socialProfiles.length; i++) {
                if (client.socialProfiles[i].socialProfileType.name === 'vk') {
                    selector.append("<option id='send-vk' value='vk'>Отправить в ВК</option>");
                }
                if (client.socialProfiles[i].socialProfileType.name === 'telegram') {
                    selector.append("<option id='send-telegram' value='telegram'>Отправить в Telegram</option>");
                }
                if (client.socialProfiles[i].socialProfileType.name === 'whatsapp') {
                    selector.append("<option id='send-whatsapp' value='whatsapp'>Отправить в WhatsApp</option>");
                }
            }
        }
    })

}

$('#conversations-modal').on('show.bs.modal', function () {
    let clientId = $("#main-modal-window").data('clientId');
    set_send_selector(clientId);
    start_chats(clientId);
});

$('#conversations-modal').on('hidden.bs.modal', function () {
    let clientId = $("#main-modal-window").data('clientId');
    $("#chat-messages").empty();
    $.ajax({
        type: 'GET',
        url: '/rest/telegram/messages/chat/close',
        data: {clientId: clientId}
    })
});

function client_has_telegram(client) {
    let has_telegram = false;
    for (let i = 0; i < client.socialProfiles.length; i++) {
        if (client.socialProfiles[i].socialProfileType.name === 'telegram') {
            has_telegram = true;
            break;
        }
    }
    return has_telegram;
}

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        var currentModal = $(this);
        var clientId = $(this).data('clientId');
        let formData = {clientId: clientId};
        $.ajax({
            async: false,
            type: 'GET',
            url: 'rest/client/' + clientId,
            data: formData,
            success: function (client) {
                if (!client_has_telegram(client) && client.phoneNumber !== '') {
                    set_telegram_id_by_phone(client.phoneNumber);
                }
                $("#conversations-title").prop('innerHTML', 'Чат с ' + client.name + ' ' + client.lastName);
                $.get('rest/client/getPrincipal', function (user) {
                }).done(function (user) {
                    if (client.ownerUser != null) {
                        var owenerName = client.ownerUser.firstName + ' ' + client.ownerUser.lastName;
                    }
                    var adminName = user.firstName + ' ' + user.lastName;
                    $('#main-modal-window').data('userId', user.id);

                    currentModal.find('.modal-title').text(client.name + ' ' + client.lastName);
                    $('#client-email').text(client.email);
                    $('#client-phone').text(client.phoneNumber);
                    if (client.canCall && user.ipTelephony) {
                        $('#client-phone')
                            .after('<td id="web-call-voximplant" class="remove-tag" style="white-space: nowrap;">' + '<button class="btn btn-default btn btn-light btn-xs call-to-client" onclick="webCallToClient(' + client.phoneNumber + ')">' + '<span class="glyphicon glyphicon-earphone call-icon">' + '</span>' + '</button>' + '</td>')
                            .after('<td id="callback-call-voximplant" class="remove-tag">' + '<button class="btn btn-default btn btn-light btn-xs callback-call" onclick="callToClient(' + user.phoneNumber + ', ' + client.phoneNumber + ')">' + '<span class="glyphicon glyphicon-phone">' + '</span>' + '</button>' + '</td>');
                        $(".call-to-client").after('<button id="btn-call-off" class="btn btn-default btn btn-light btn-xs web-call-off">' + '<span class="glyphicon glyphicon-phone-alt call-icon">' + '</span>' + '</button>' + '</td>');
                        $('.call-to-client').after('<button id="btn-mic-off" class="btn btn-default btn btn-light btn-xs web-call-mic-off">' + '<span class="glyphicon glyphicon-ice-lolly">' + '</span>' + '</button>' + '</td>');
                        $('#btn-call-off').css("background", "red");
                        $('#btn-call-off').css("color", "white");
                        $('#btn-mic-off').hide();
                        $('#btn-call-off').hide();
                    }

                    if (client.age > 0) {
                        $('#client-age').text(client.age);
                    } else {
                        $('#client-age').text('');
                    }
                    $('#client-sex').text(client.sex);
                    if(client.clientDescriptionComment != null && client.clientDescriptionComment.length > 0) {
                        $('#client-label').text(client.clientDescriptionComment);
                    } else {
                        $('#client-label').text('');
                    }
                    if (client.email == null) {
                        $('#email-href').hide();
                    } else {
                        $('#email-href').show();
                    }

                    // здесь вставка ссылок в кнопки вк, фб и слак
                    $('#vk-href').hide();
                    $('#vk-im-button').hide();

                    $('#fb-href').hide();
                    $('#slack-href').hide();

                    $('#slack-invite-href').attr('onclick', 'slackInvite(' + '\"' + client.email + '\"' + ')');

                    for (var i = 0; i < client.socialProfiles.length; i++) {
                        if (client.socialProfiles[i].socialProfileType.name == 'vk') {
                            $('#vk-href').attr('href', client.socialProfiles[i].link);
                            $('#vk-href').show();


                            $('#chat-button').attr("clientID", client.id);
                            $('#chat-im-count').text($('#chat-notification'+clientId).text());
                            $('#chat-button').show();
                        }
                        if (client.socialProfiles[i].socialProfileType.name == 'facebook') {
                            $('#fb-href').attr('href', client.socialProfiles[i].link);
                            $('#fb-href').show();
                        }
                        get_interlocutors(clientId);
                    }

                    if (client.slackProfile != undefined) {
                        $('#slack-href').attr('href', "https://javamentor.slack.com/messages/C2AEE8T9B/team/" + client.slackProfile.hashName);
                        $('#slack-href').show();
                    }

                    var btnBlock = $('div#assign-unassign-btns');

                    if (client.liveSkypeCall) {
                        btnBlock.after('<div class="remove-tag confirm-skype-interceptor"><div class="update btn-group"><button id="assign-skype' + client.id + '" type="button" onclick="updateCallDate(' + client.id + ')" class="btn btn-default update-date-btn btn-sm"><span class="glyphicon glyphicon-pencil"></span> Изменить время беседы</button>\n' +
                            '<button id="deleteDate" type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <span class="glyphicon glyphicon-remove"></span></button>' +
                            '<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="deleteDate">\n' +
                            '    <li><a onclick="deleteCallDate(' + client.id + ')" href="#">Удалить беседу</a></li>\n' +
                            '    <li><a href="#">Отмена</a></li>\n' +
                            '    </ul>' +
                            '</div>' +
                            '<div class="skype-notification"></div>' +
                            '</div>')
                    } else {
                        btnBlock.after(
                            '<div class="remove-tag confirm-skype-interceptor">' +
                            '<button id="assign-skype' + client.id + '" onclick="assignSkype(' + client.id + ')" class="btn btn-primary center-block assign-skype-call-btn btn-sm">Назначить беседу в Skype</button>' +
                            '<div class="skype-notification"></div>' +
                            '</div>')
                    }

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
                $('#postpone-accordion').append('<h4 class="panel-title remove-element">' + '<a href="#hideClientCollapse' + client.id + '" сlass="font-size" data-toggle="collapse" data-parent="#hideAccordion" > Добавить напоминание  </a>' + '</h4>');
                $('#postpone-div').append('<button class="btn btn-md btn-info remove-element" onclick="hideClient(' + client.id + ')"> OK </button>');
                $('.postponeStatus').attr('id', 'postponeStatus' + client.id);
                $('.textcomplete').attr('id', 'new-text-for-client' + client.id);
                $('.comment-div').append('<button class="btn btn-sm btn-success comment-button remove-element" id="assign-client' + client.id + '"  onclick="sendComment(' + client.id + ', \'test_message\')"> Сохранить </button>');
                $('.main-modal-comment').attr('id', 'client-' + client.id + 'comments');
                $('.upload-history').attr('data-id', client.id).attr('href', '#collapse' + client.id);
                $('.client-collapse').attr('id', 'collapse' + client.id);
                $('.history-line').attr('id', 'client-' + client.id + 'history');
                $('.upload-more-history').attr('data-clientid', client.id);
                $('#repeated-client-info').hide();

                if (client.repeated) {

                    $('#repeated-client-info').show();

                }
            }
        });
    })
});

function dropRepeatedFlag(clientId, repeated) {
    var url = '/rest/client/setRepeated';
    var formData = {
        clientId: clientId,
        isRepeated: repeated
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {

        },
        error: function (e) {
            console.log(e);
        }
    });

    $('#repeated-client-info').hide();

}
$(function () {
    $('#main-modal-window').on('hidden.bs.modal', function () {
        var clientId = $(this).data('clientId');
        dropRepeatedFlag(clientId, false);
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
        backUrl();
        clientsSearch();
    });
});

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        var clean = $('.history-line').find("tbody");
        let clientId = $(this).data('clientId');
        let formData = {
            clientId: clientId
        };
        clean.empty();

        $.ajax({
            type: "POST",
            url: "/user/notification/postpone/getAll",
            data: formData,

            success: function (result) {
                if(result.length > 0) {
                    $.ajax({
                        type: "POST",
                        url: "rest/client/postpone/getComment",
                        data: formData,

                        success: function (result) {
                            let currentModal = $('#postponeCommentModal');
                            currentModal.modal('show');
                            let div = document.querySelector(".colorChoose");
                            div.innerHTML = "";
                            var node = document.createElement('div');
                            node.innerHTML = '<p> ' + result;
                            div.appendChild(node);
                        },
                        error: function (e) {
                            console.log(e)
                        }
                    });
                }

            },
            error: function (e) {
                console.log(e)
            }
        });

    });
});

$('#postponeCommentModal').on('hidden.bs.modal', function () {
    let currentModal = $('#main-modal-window');
    currentModal.css("overflow-y","auto");
})

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
    var url = "/user/rest/call/sendData";
    var credentialsUrl = "/user/rest/call/voximplantCredentials";
    var formData = {
        to: clientPhone
    };
    // var incr = 0;
    var icon = $(".call-to-client");
    micMuted = false;
    callToPhone = clientPhone;

    $.ajax({
        async: false,
        type: 'post',
        url: url,
        data: formData,
        success: function(callRecordId) {
            console.log("PROCESS WEBCALL");
            callerId = callRecordId.id;
            icon.css("background", "green");
            icon.css("color", "white");
            icon.attr("disabled", "true");

            callToolControl('showWebCallClientTools');
        },
        error: function (error) {
            console.log("ERROR WEBCALL");
            console.log(error);
        }
    });

    $.ajax({
        async: false,
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

    if (!initialized) {
        sdk.init({
            micRequired: true,
            videoSupport: false,
            progressTone: true
        });
    } else {
        startCall();
    };
}

function callToolControl(operation) {
    let iconCallBackCall = $(".callback-call");
    let iconWebCallToClint = $(".call-to-client");

    if (operation == 'showWebCallClientTools') {
        $('#callback-call-voximplant').hide();

        $('#btn-call-off').show();
        $('#btn-mic-off').show();
    } else if (operation == 'default') {
        $('#callback-call-voximplant').show();

        $('#btn-call-off').hide();
        $('#btn-mic-off').hide();

        $(".call-to-client").css("background", "");
        $(".call-to-client").css("color", "");
        $(".call-to-client").removeAttr("disabled");
    }
}

//voximplant handlers
function onSdkReady() {
    sdk.connect(false);
    console.log('sdk is initialized');
}


function onConnectionEstablished() {
    console.log('It is connected to vox cloud');
    if (sdk.getClientState() !== "LOGGED_SUCCESS") {
        // sdk.login(voxLogin, voxPassword);
        sdk.requestOneTimeLoginKey(voxLogin)
        console.log('request for authentication');
    } else {
        console.log('already authorize.');
        startCall();
    }
}

function onAuthResult(eAuth) {
    if (eAuth.result) {
        initialized = true;
        console.log('Athentication successfull');
        startCall();// авторизовались - теперь можно звонить или принимать звонки
    } else {
        if (eAuth.code == 302) {
            console.log("generating token ... ");
            $.post('/user/rest/call/calcKey', {
                key: eAuth.key
            }, token => {
                sdk.loginWithOneTimeKey(voxLogin, token);
            }, 'text');
        } else {
            console.log("Athentication is unsuccessful with code " + eAuth.code);
        }
    }
}

function startCall() {
    console.log('Starting call .... ');
    currentCall = sdk.call({number: callToPhone, customData: callerId});
    currentCall.on(VoxImplant.CallEvents.Connected, () => {
        // console.log('You can hear audio from the cloud')
    });
    currentCall.on(VoxImplant.CallEvents.Failed, (e) => {
        console.log(`Call failed with the ${e.code} error`);
        callToolControl('default');
    });
    currentCall.on(VoxImplant.CallEvents.Disconnected, () => {
        console.log('The call has ended');
        callToolControl('default');
    } );
}

//call hangup voximplant
$(document).on('click','#btn-call-off',function(){
    console.log("Client-state: " + sdk.getClientState());
    if (currentCall && currentCall.state() != "ENDED") {
        currentCall.hangup();
    }
    callToolControl('default');
});

//mute microphone voximplant
$(document).on('click','#btn-mic-off',function(){
    if (currentCall && currentCall.active()) {
        let elBtnMic = $('#btn-mic-off');
        if (micMuted) {
            currentCall.unmuteMicrophone();
            elBtnMic.css("background", "");
            elBtnMic.css("color", "");
            // console.log("mic is unmuted");
        } else {
            currentCall.muteMicrophone();
            elBtnMic.css("background", "red");
            elBtnMic.css("color", "white");
            // console.log("mic is muted");
        };
        micMuted = !micMuted;
    };
});

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

function slackInvite(email) {
    $.ajax({
        type: "GET",
        url: "/slack/" + email,
        dataType: "json",

        success: function (data) {
            var ok = data.ok;
            if (ok) {
                alert('User is invited');
            } else alert('already_invited');
        },
        error: function (e) {
            alert('The user is not invited')
        }
    })
}
