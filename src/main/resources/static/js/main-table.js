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

// $(function () {
//     $('.save_value').on('click', function(event) {
//         var sel = $('input[type="checkbox"]:checked').map(function(i, el) {
//             return $(el).val();
//         });
//         console.log(sel.get())
//     })
// });

// $(function () {
//     $('.select_all').click(function() {
//         var currentForm = $(this).parents('.box-modal');
//         currentForm.find('.my-checkbox').prop('checked');
//     });
// });


$(function () {
    $('.open-description-btn').on('click', function(event) {
        var id = $(this).data('id');
        var infoClient =  $('#info-client'+ id);
        var text = infoClient.find('.client-description').text();
        var testModal = $('#TestModal');

        testModal.find('textarea').val(text);
        testModal.find('button').remove();
        testModal.find('.modal-footer').append("<button type='button' class='btn btn-success btn-sm' onclick='saveDescription(" + id + ")'>Сохранить</button>");
        testModal.modal('show');
    });
});


function saveDescription(id) {
    let text =  $('#TestModal').find('textarea').val();
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
            $('#TestModal').modal('hide');
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
        let data = this.value.split(" ");
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
            let $validCount = 0;
            for (let i = 0; i < data.length; i++){
                if(portlet.is(":contains('"+ data[i] +"')")){
                    $validCount++;
                }
            }
            return $validCount === data.length;
        }).show();
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

function senReqOnChangeStatus(clientId, statusId) {
    let
        url = '/rest/status/change',
        formData = {
            clientId: clientId,
            statusId: statusId
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (data) {
            let
                url = '/rest/client/' + clientId;
            $.get(url, 
                function (data) {
                    $('#client-' + data.id + 'history').prepend(
                        "<li>" +
                        "   <span>" + data.history[0].title + "</span>" +
                        "</li>"
                    );
                });
        },
        error: function (error) {
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
                "   class='btn btn-sm btn-warning'>отказаться от карточки</button>"
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
                        "   class='btn btn-sm btn-warning'>отказаться от карточки</button>"
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
                    "   class='btn btn-sm btn-info'>взять себе карточку</button>"
                );
                unassignBtn.remove();
            }else{
                $("a[href='/admin/client/clientInfo/"+ id +"']").before(
                    "<button " +
                    "   id='assign-client" + id + "' " +
                    "   onclick='assign(" + id +")' " +
                    "   class='btn btn-md btn-info'>взять себе карточку</button>"
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
        $("#client_filter").append("<input type=\"checkbox\" id = checkbox_" + el.innerText + " value=" + el.innerText + "><label for=checkbox_" + el.innerText + ">" + el.getAttribute("value") + "</label></br>");
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
    let url = '/rest/sendVK';
    let formData = {
        clientId: clientId,
        templateId: templateId
    };
    var current = document.getElementById("sendTemplateBtn-" + templateId + "-" + clientId);
    var currentStatus = document.getElementById("sendTemplateStatus-" + templateId+ "-" + clientId);
    $.ajax({
        type: "POST",
        url: url,
        data: formData,

        beforeSend: function(){
            current.textContent ="Отправка..";
            current.setAttribute("disabled", "true")
        },
        success: function (result) {
            currentStatus.style.color = "limegreen";
            currentStatus.textContent = "Отправлено";
            current.textContent ="Да";
            current.removeAttribute("disabled");
        },
        error: function (e) {
            current.textContent ="Да";
            current.removeAttribute("disabled");
            currentStatus.style.color = "red";
            currentStatus.textContent = "Ошибка";
            console.log(e)
        }
    });
}


function sendTempate(clientId, templateId) {
    let url = '/rest/sendEmail';
    let formData = {
        clientId: clientId,
        templateId: templateId
    };
    var current = document.getElementById("sendTemplateBtn-" + templateId + "-" + clientId);
    var currentStatus = document.getElementById("sendTemplateStatus-" + templateId+ "-" + clientId);
    $.ajax({
        type: "POST",
        url: url,
        data: formData,

        beforeSend: function(){
            current.textContent ="Отправка..";
            current.setAttribute("disabled", "true")
        },
        success: function (result) {
            currentStatus.style.color = "limegreen";
            currentStatus.textContent = "Отправлено";
            current.textContent ="Да";
            current.removeAttribute("disabled");
        },
        error: function (e) {
            current.textContent ="Да";
            current.removeAttribute("disabled");
            currentStatus.style.color = "red";
            currentStatus.textContent = "Ошибка";
            console.log(e)
        }
    });
}

function sendCustomTempate(clientId) {
    let url = '/rest/sendCustomEmailTemplate';
    let formData = {
        clientId: clientId,
        body: $('#custom-eTemplate-body').val()
    };
    var current = $("#sendCustomTemplateBtn")[0];
    var currentStatus = $("#sendCustomEmailTemplateStatus")[0];
    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        beforeSend: function(){
            current.textContent ="Отправка..";
            current.setAttribute("disabled", "true")
        },
        success: function (result) {
            current.textContent ="Отправить";
            current.removeAttribute("disabled");
            currentStatus.style.color = "limegreen";
            currentStatus.textContent = "Отправлено";
        },
        error: function (e) {
            current.textContent ="Отправить";
            current.removeAttribute("disabled");
            currentStatus.style.color = "red";
            currentStatus.textContent = "Ошибка";
            console.log(e)
        }
    });
}

function hideClient(clientId) {
    let url = 'admin/rest/client/postpone';
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
