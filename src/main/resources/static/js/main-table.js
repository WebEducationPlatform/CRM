// Отрисовка чекбоксов социальных сетей в модальном окне.
$(function () {
    $('.fix-modal').on('show.bs.modal', function () {
        var currentForm = $(this).find('.box-modal');
        var clientId = $(this).find('.send-all-message').data('clientId');
        let formData = {clientId: clientId};
        $.ajax({
            type: 'GET',
            url: 'rest/client/' + clientId,
            data: formData,
            beforeSend: function () {
                if(currentForm.find('.my-checkbox-soc').is('.my-checkbox-soc')) {
                    return false;
                }
            },
            success: function(data) {
                var soc = data.socialNetworks;
                var email = data.email;
                var phoneNumber = data.phoneNumber;

                for (let i = 0; i < soc.length; i++) {
                    currentForm.prepend("<label class='checkbox-inline'>" +
                        "<input type='checkbox'  value=" + soc[i].socialNetworkType.name + "  class='my-checkbox-soc' />" + soc[i].socialNetworkType.name +
                        "</label>");
                }
                if(email !== null) {
                    currentForm.prepend("<label class='checkbox-inline'>" +
                        "<input type='checkbox'  value=" + 'email' + "  class='my-checkbox-soc' />" + 'e-mail' +
                        "</label>");
                } if (phoneNumber !== null ) {
                    currentForm.prepend("<label class='checkbox-inline'>" +
                        "<input type='checkbox'  value=" + 'sms' + "  class='my-checkbox-soc' />" + 'sms' +
                        "</label>");
                }
            }
        });
    });
});


$(function () {
    $('.custom-modal').on('show.bs.modal', function () {
        var currentForm = $(this).find('.box-modal');
        var clientId = $(this).find('.send-all-custom-message').data('clientId');
        let formData = {clientId: clientId};
        $.ajax({
            type: 'GET',
            url: 'rest/client/' + clientId,
            data: formData,
            beforeSend: function () {
                if(currentForm.find('.my-checkbox-soc').is('.my-checkbox-soc')) {
                    return false;
                }
            },
            success: function(data) {
                var soc = data.socialNetworks;
                var email = data.email;
                var phoneNumber = data.phoneNumber;

                for (let i = 0; i < soc.length; i++) {
                    currentForm.prepend("<label class='checkbox-inline'>" +
                        "<input type='checkbox'  value=" + soc[i].socialNetworkType.name + "  class='my-checkbox-soc' />" + soc[i].socialNetworkType.name +
                        "</label>");
                }
                if(email !== null) {
                    currentForm.prepend("<label class='checkbox-inline'>" +
                        "<input type='checkbox'  value=" + 'email' + "  class='my-checkbox-soc' />" + 'e-mail' +
                        "</label>");
                } if (phoneNumber !== null ) {
                    currentForm.prepend("<label class='checkbox-inline'>" +
                        "<input type='checkbox'  value=" + 'sms' + "  class='my-checkbox-soc' />" + 'sms' +
                        "</label>");
                }
            }
        });
    });
});

$(function(){
    $(".hide-main-modal").click(function(e){
        $(".main-modal .close").click()
    });
});

// Выбрать , отключить все чекбоксы в меню отправки сообщений в email.SMS, VK,FB.
$(function () {
    $('.select_all').click(function() {
        var currentForm = $(this).parents('.box-modal');
        currentForm.find('.my-checkbox-soc').prop('checked', true);
        currentForm.find('.deselect_all').prop('checked', false);
    });
});

$(function () {
    $('.deselect_all').click(function() {
        var currentForm = $(this).parents('.box-modal');
        currentForm.find('.my-checkbox-soc').prop('checked', false);
        currentForm.find('.select_all').prop('checked', false);
    });
});


//Сохранить заметку на лицевой стороне карточки
function saveDescription(id) {
    let text =  $('#TestModal'+ id).find('textarea').val();
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
            $('#TestModal'+ id).modal('hide');
        },
        error: function (error) {
        }
    });
}




$(document).ready(function () {
    $(".column").sortable({
    delay:100,
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

    $(document).ready(function(){
        $("#new-status-name").keypress(function(e){
            if(e.keyCode===13){
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
        //split input data by space
        let data = this.value.toLowerCase().split(" ");
        //take portlet data
        let portletArr = $(".portlet");
        //if input data is empty: show all and return
        if(this.value.trim() === ''){
            portletArr.show();
            return;
        }
        portletArr.hide();
        //filtering array of portlet
        portletArr.filter(function () {
            //filtering by data in portlet body
            let portlet = $(this).find(".portlet-body");
            let temp = portlet.clone();
            temp.text(temp.text().toLowerCase());
            let $validCount = 0;
            for (let i = 0; i < data.length; i++){
                if(temp.is(":contains('"+ data[i] +"')")){
                    $validCount++;
                }
            }
            return $validCount === data.length;
        }).show();
    });

    $(".sms-error-btn").on("click", function smsInfoModalOpen() {
        let modal = $("#sms_error_modal"),
            btn = $(this),
            url = '/user/notification/sms/error/' + btn.attr("data-id");
        $.get(url, function(){}).done(function (notifications) {
            let body = modal.find("tbody");
            for (let i = 0; i < notifications.length; i++) {
                body.append(
                    "<tr><td>" + notifications[i].information + "</td></tr>"
                )
            }
        });
        modal.find("#clear_sms_errors").attr("onClick", "clearNotifications("+ btn.attr("data-id") +")");
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

function deleteStatus(id) {
    let url = '/admin/rest/status/delete';
    let formData = {
        deleteId: id
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            location.reload();
        },
        error: function (e) {

        }
    });
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
    let statusName = $('#new-status-name').val() ||  $('#default-status-name').val();
    if(typeof statusName === "undefined" || statusName === "") return;
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

function changeStatusName(id) {
    let url = '/admin/rest/status/edit';
    let statusName = $("#change-status-name" + id).val();
    let formData = {
        statusName: statusName,
        oldStatusId:id
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
                "   id='unassign-client" + id +"' " +
                "   onclick='unassign(" + id +")' " +
                "   class='btn btn-sm btn-warning'>Отказаться от карточки</button>"
            );
            assignBtn.remove();
            $('#info-client' + id).append(
                "<p class='user-icon' id='own-"+id+"' value=" + owner.firstName + "&nbsp" + owner.lastName + ">" +
                    owner.firstName.substring(0,1) + owner.lastName.substring(0,1) +
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
            userForAssign : user
        },
        assignBtn = $('#assign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            let info_client = $('#info-client' + id),
                target_btn = $("a[href='/admin/client/clientInfo/"+ id +"']"),
                unassign_btn = $('#unassign-client' + id);
            info_client.find("p[style*='display:none']").remove();
            info_client.find(".user-icon").remove();

            //If admin assigned himself
            if(principalId === user){
                //If admin assigned himself second time
                if(unassign_btn.length === 0){
                    target_btn.before(
                        "<button " +
                        "   id='unassign-client" + id +"' " +
                        "   onclick='unassign(" + id +")' " +
                        "   class='btn btn-sm btn-warning'>Отказаться от карточки</button>"
                    );
                }
                //If admin not assign himself, he don`t have unassign button
            }else {
                unassign_btn.remove();
            }
            assignBtn.remove();

            //Add Worker icon and info for search by worker
            info_client.append(
                "<p class='user-icon' id='own-"+id+"' value=" + owner.firstName + " " + owner.lastName + ">" +
                owner.firstName.substring(0,1) + owner.lastName.substring(0,1) +
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
            if(unassignBtn.length !== 0){
                unassignBtn.before(
                    "<button " +
                    "   id='assign-client" + id + "' " +
                    "   onclick='assign(" + id +")' " +
                    "   class='btn btn-sm btn-info'>Взять себе карточку</button>"
                );
                unassignBtn.remove();
            }else{
                $("a[href='/admin/client/clientInfo/"+ id +"']").before(
                    "<button " +
                    "   id='assign-client" + id + "' " +
                    "   onclick='assign(" + id +")' " +
                    "   class='btn btn-md btn-info'>Взять себе карточку</button>"
                );
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
        var data=[];
        for (var w = 0; w < allChecks.length; ++w){
            if(allChecks[w].checked){
                data[data.length]=allChecks[w].value;
            }
        }
        var jo = $("#status-columns").find($(".portlet"));
        if (data.length===0) {
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
    }else {
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
    },'show');
});

$(document).ready(fillFilterList);

$(document).ready(function () {
    var url = '/rest/user';

    var userNames = [];

    $.ajax({
        type: 'get',
        url: url,
        dataType : 'json',
        success: function (res) {
            for (var i = 0; i < res.length; i++) {
                userNames[i] = res[i].firstName + res[i].lastName;
            }
        },
        error : function (error) {
            console.log(error);
        }
    });

    $('.textcomplete').textcomplete([
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

function deleteUser(id) {
    let url = '/admin/rest/user/delete';
    let formData = {
        deleteId: id
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (result) {
            location.reload();
        },
        error: function (e) {

        }
    });
}

function sendMessageVK(clientId, templateId) {
    let url = '/rest/vkontakte';
    let formData = {
        clientId: clientId,
        templateId: templateId,
        body: $('#custom-VKTemplate-body' + clientId + templateId).val()
    };
    var currentStatus = document.getElementById("sendSocialTemplateStatus" + clientId);
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
}



// Отправка кастомного сообщения в вк
$(function () {
    $('.send-vk-btn').on('click', function(event) {
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
                currentStatus.css('color','"limegreen""');
                currentStatus.text("Отправлено");
            },
            error: function (e) {
                currentStatus.css('color','red"');
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
        var btn =  currentModal.find('.send-vk-btn');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

// Отправка кастомного сообщения в email
$(function () {
    $('.send-email-btn').on('click', function(event) {
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
                currentStatus.css('color','"limegreen""');
                currentStatus.text("Отправлено");
            },
            error: function (e) {
                currentStatus.css('color','red"');
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
        var btn =  currentModal.find('.send-email-btn');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

function sendTemplate(clientId, templateId) {
    let url = '/rest/sendEmail';
    let formData = {
        clientId: clientId,
        templateId: templateId,
        body: $('#custom-EmaileTemplate-body' + clientId + templateId).val()
    };
    var currentStatus = document.getElementById("sendEmailTemplateStatus" + clientId);
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
}

$(function () {
    $('.open-description-btn').on('click', function(event) {
        var id = $(this).data('id');
        var infoClient =  $('#info-client'+ id);
        var text = infoClient.find('.client-description').text();
        var clientModal = $('#TestModal' + id);

        clientModal.find('textarea').val(text);
        clientModal.modal('show');
    });
});

//Отправка выбранных чекбоксов на контроллер отрпавки сообщений в email.SMS, VK,FB.
$(function () {
    $('.save_value').on('click', function(event) {
        var sel = $('input[type="checkbox"]:checked').map(function (i, el) {
            return $(el).val();
        });
        var boxList =sel.get();
        console.log(sel.get());

        $.ajax({
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify(boxList),
            url:"/rest/sendSeveralMessage",
            success:function(result){
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


//Отрпавка сообщений с фиксированнм текстом во все выбранные социальные сети, email, SMS.
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
            var valuecheck = $( this ).val();
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
    $('.fix-modal').on('hide.bs.modal', function () {
        var currentForm = $(this).find('.send-fixed-template');
        currentForm.empty();
        $("input[type=checkbox]").prop('checked', false);
        $(this).find('.send-all-message').removeAttr("disabled");
    });
});

//Установка идентификаторов в модальное окно отправки сообщений с кастомным текстом.
$(function () {
    $('.portlet-custom-btn').on('click', function () {
        var portlet = $(this).closest('.common-modal');
        var clientId = portlet.data('cardId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customMessageTemplate');
        var btn =  currentModal.find('.send-all-custom-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

//Отрпавка сообщений с кастомным текстом во все выбранные социальные сети, email, SMS.
$(function () {
    $('.send-all-custom-message').on('click', function(event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var current = $(this);
        var currentStatus = $(this).prev('.send-custom-template');
        var formData = {clientId: clientId, templateId: templateId,
            body: $('#custom-eTemplate-body').val()};
        var url = [];
        var err = [];
        $('input[type="checkbox"]:checked').each(function (el) {
            var valuecheck = $(this).val();
            switch ($( this ).val()) {
                case ('email'):
                    url = '/rest/sendEmail';
                    break;
                case ('vk'):
                    url = '/rest/vkontakte';
                    break;
                case ('sms'):
                    url = '/user/sms/send/now/client';
                    break;
            }
            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                beforeSend: function(){
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
    var minutes =  Math.ceil((nowDate.getMinutes() +1)/10)*10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes , 0, 0);
    $('input[name="postponeDate"]').daterangepicker({
        singleDatePicker: true,
        timePicker: true,
        timePickerIncrement: 10,
        timePicker24Hour: true,
        locale: {
            format: 'DD.MM.YYYY H:mm'
        },
        minDate: minDate,
        startDate: minDate
    });
});


$(function () {
    $('.portlet-body').on('click', function (e) {
        if (e.target.className.startsWith("portlet-body") === true) {
            var clientId = $(this).parents('.common-modal').data('cardId');
            var currentModal =  $('#main-modal-window');
            currentModal.data('clientId', clientId);
            currentModal.modal('show');
			markAsReadMenu($(e.target).attr('client-id'))
        }
    });
});


$(function () {
    $('.portlet-header').on('click', function (e) {
        var clientId = $(this).parents('.common-modal').data('cardId');
        var currentModal =  $('#main-modal-window');
        currentModal.data('clientId', clientId);
        currentModal.modal('show');
    });
});

$(function () {
    $('.portlet-content').on('click', function (e) {
        var clientId = $(this).parents('.common-modal').data('cardId');
        var currentModal =  $('#main-modal-window');
        currentModal.data('clientId', clientId);
        currentModal.modal('show');
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
            success: function(client) {
                $.get('rest/client/getPrincipal', function (user) {
                    $(this).data('userId', user.id);
                    // $('#main-modal-window').attr("onClick", "openClientComments("+ user.phoneNumber+','+ client.phoneNumber +")");

                    currentModal.find('.modal-title').text(client.name + ' ' + client.lastName);
                    $('#client-email').text(client.email);
                    $('#client-phone').text(client.phoneNumber);
                    if(client.canCall && user.ipTelephony) {
                        $('#client-phone').after('<td class="remove-tag">' + '<a class="btn btn-default btn btn-light btn-xs call-to-client" onclick="callToClient(' + user.phoneNumber + ', '+ client.phoneNumber +')">' + '<span class="glyphicon glyphicon-earphone call-icon">'+ '</span>' + '</a>' + '</td>');
                    }

                    if (client.age > 0) {
                        $('#client-age').text(client.age);
                    }
                    $('#client-sex').text(client.sex);
                    var btnBlock = $('div#assign-unassign-btns');
                    if (client.ownerUser === null) {
                        btnBlock.append('<button class="btn btn-sm btn-info" id="assign-client' + client.id + '"onclick="assign(' + client.id + ')"> взять себе карточку </button>');
                    }
                    if (client.ownerUser !== null) {
                        btnBlock.prepend('<button class="btn btn-sm btn-warning" id="unassign-client' + client.id + '"onclick="unassign(' + client.id + ')"> отказаться от карточки </button>');
                    }
                    btnBlock.prepend('<a href="/admin/client/clientInfo/' + client.id +'">' +
                        '<button class="btn btn-info btn-sm" id="client-info"  rel="clientInfo" "> расширенная информация </button>' + '</a');
                });
                $('#hideClientCollapse').attr('id','hideClientCollapse'+ client.id );
                $('#postponeDate').attr('id','postponeDate'+ client.id);
                $('#postpone-accordion').append('<h4 class="panel-title remove-element">' + '<a href="#hideClientCollapse'+ client.id +'" сlass="font-size" data-toggle="collapse" data-parent="#hideAccordion" > Скрыть карточку  </a>' + '</h4>');
                $('#postpone-div').append('<button class="btn btn-md btn-info remove-element" onclick="hideClient(' + client.id + ')"> OK </button>');
                $('.textcomplete').attr('id','new-text-for-client'+ client.id);
                $('.comment-div').append('<button class="btn btn-sm btn-success comment-button remove-element" id="assign-client' + client.id +'"  onclick="sendComment(' + client.id + ', \'test_message\')"> Сохранить </button>');
                $('.main-modal-comment').attr('id','client-'+ client.id + 'comments');
                $('.upload-history').attr('data-id',client.id).attr('href','#collapse'+ client.id);
                $('.client-collapse').attr('id','collapse'+ client.id);
                $('.history-line').attr('id','client-'+ client.id + 'history');
                $('.upload-more-history').attr('data-clientid',client.id);
            }
        });
    });
});

$(function () {
    $('#main-modal-window').on('hidden.bs.modal', function () {
       $('div#assign-unassign-btns').html('');
        $('.remove-element').remove();
        $('.hide-client-collapse').attr('id','hideClientCollapse');
        $('.postpone-date').attr('id','postponeDate');
        $('.textcomplete').removeAttr('id');
        $('.main-modal-comment').removeAttr('id');
        $('.remove-tag').remove();
        $('.history-line').find("tbody").empty();
        // $('.upload-history').removeAttr('data-Id').removeAttr('href');
        // $('.client-collapse').removeAttr('id');
        $('.client-collapse').collapse('hide');
        $('.remove-history').remove();
        // $('.upload-more-history').removeAttr('data-clientid');
});
});

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        var clean = $('.history-line').find("tbody");
        clean.empty();;
    });
});

function callToClient(userPhone, clientPhone) {
    var url = "/user/rest/call/voximplant";
    var formData = {
        from: userPhone,
        to: clientPhone
    };
    let icon = $(".call-icon");
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        success: function() {
            icon.parent("a").css("background","green");
            icon.css("color","white");
            icon.parent("a").attr("disabled","disabled");

        },
        error: function (error) {
            console.log(error);
        }
    });
}

