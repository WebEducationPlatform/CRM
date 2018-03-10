$(document).ready(function () {
    $(".column").sortable({
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

    $("#create-new-status-span").click(function () {
        $(this).hide();
        $("#new-status-form").show();
        document.getElementById("new-status-name").focus();
    });

   /* $("#new-status-form").focusout(
        function () {
            $(this).hide();
            $("#create-new-status-span").show();
        });*/
});

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
};

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
    let url = '/admin/rest/status/add';
    let statusName = $('#new-status-name').val();
    if (statusName===""){
        return;
    }
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
        url = '/admin/rest/status/change',
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
                url = '/admin/rest/client/' + clientId;
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
        url = '/admin/rest/client/assign',
        formData = {
            clientId: id,
        };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            $('#assign-client' + id).remove();
            $('#info-client' + id).append(
                "<p class='user-icon'>" +
                    owner.firstName.substring(0,1) + owner.lastName.substring(0,1) +
                "</p>"
            );
        },
        error: function (error) {
        }
    });
}

$(document).ready(function() {
    $("#client_filter").change(function(){
        var data = ($(this).val());
        var jo = $("#status-columns").find($(".portlet"));
        if (this.value === "") {
            jo.show();
            return;
        }
        jo.hide();
        jo.filter(function (i, v) {
            var d = $(this)[0].getElementsByClassName("user-icon");
            if(d.length===0){
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

$(document).ready(function() {
    var names = $("#status-columns").find($(".user-icon"));
    if (names.length===0){
        $("#client_filter_group").remove();
    }
    var uniqueNames = [];
    var temp = [];
    for (var i = 0; i < names.length; ++i) {
        if( ~temp.indexOf(names[i].innerText) ) {
            names.slice(temp.indexOf(names[i].innerText));
        } else {
            temp.push(names[i].innerText);
            uniqueNames.push(names[i]);
        }}
    $.each(uniqueNames, function(i, el){
        $("#client_filter").append("<option value = "+el.innerText+">" + el.getAttribute("value") + "</option>");
    });
});

$(document).ready(function () {
    var url = '/admin/rest/user';

    var userNames = [];

    $.ajax({
        type: 'get',
        url: url,
        dataType : 'json',
        success: function (res) {
            for (var i = 0; i < res.length; i++) {
                userNames[i] = res[i].firstName + res[i].lastName;
            };
        },
        error : function (error) {
            console.log(error);
        }
    })

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