var isAutorizedUserOwner = false;
var isAutorizedUserAdmin = false;
var isUpdatedUserOwner = false;
var isUpdatedUserMentor = false;
var myRows = [];
let botDomain = $("#slackBotDomain").val();
let botPort = $("#slackbotPort").val();
$(document).ready(function () {
    $.each(updatedUserRoles, function (i, role) {
        if (role.roleName === 'OWNER') {
            isUpdatedUserOwner = true;
        }else if (role.roleName === 'MENTOR'){
            isUpdatedUserMentor = true;
        }
    });
    $.when($.get('/rest/client/getPrincipal')).done(function (user) {
        let autorizedUser = user;
        $.each(autorizedUser.role, function (i, role) {
            if (role.roleName === 'OWNER') {
                isAutorizedUserOwner = true;
            }
            if (role.roleName === 'ADMIN') {
                isAutorizedUserAdmin = true;
            }
        });
        //закрываем возможность не OWNER'y создавать пользователей со статусом OWNER
        //хардкод на Owner
        $('.checkbox').each(function () {
            if ($(this).text().trim() === 'OWNER') {
                var ownerCheckbox = $(this).hide();
                if (isAutorizedUserOwner) {
                    ownerCheckbox.show();
                }
            }
        });
    });
});


$(document).ready(function () {
    $(document.getElementsByTagName("option")).each(function () {
        var val = $(this).closest('select').attr('value');
        var inText = $(this).text();
        if (val === inText) {
            $(this).attr("selected", "selected");
        }
    });
});

function changeUser(id, authId) {
    if ($("#saveChanges")[0].className === "btn btn-primary disabled") {
        return;
    }
    if ($("input[name='roleCheckBx']:checked").length === 0) {
        var current = document.getElementById("message");
        current.textContent = "Необходимо указать минимум одну роль!";
        current.style.color = "red";
        return false;
    }

    var $sel = $('#edit-user-roles').find("input[type=checkbox]:checked");
    let url = '/rest/admin/user/update';

    $sel.each(function (index, sel) {
        var obj = {};
        obj["id"] = sel.value;
        let labelFor = "checkbox-user-" + sel.value;
        obj["roleName"] = $("label[for='" + labelFor  + "']").text();
        myRows.push(obj);
    });
    let wrap = {
        id: id,
        firstName: $('#edit-user-first-name').val(),
        lastName: $('#edit-user-last-name').val(),
        birthDate: $('#edit-user-birth-date').val(),
        phoneNumber: $('#edit-user-phone-number').val(),
        ipTelephony: $("#ipTel").is(":checked") ? true : false,
        email: $('#edit-user-email').val(),
        sex: $('#edit-user-sex').find('option:selected').text(),
        country: $('#edit-user-country').val(),
        city: $('#edit-user-city').val(),
        vk: $('#edit-user-VKid').val(),
        password: $('#edit-user-password').val(),
        isVerified: true,
        enabled: true,
        role: myRows,
        enableSmsNotifications: $("#checkbox-user-sms-notify").is(":checked") ? true : false,
        enableMailNotifications: $("#checkbox-user-email-notify").is(":checked") ? true : false,
        enableAsignMentorMailNotifications: !!$("#checkbox-user-asign-mentor-email-notify").is(":checked")
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        beforeSend: function () {
            var current = document.getElementById("message");
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function (result) {
            for (let i = 0; i < myRows.length; i++) {
                if (myRows[i].roleName === "MENTOR") {
                    sendPostToSlackBotAboutNewMentor(wrap);
                    break;
                }
            }
            sendPhoto(id, authId);
            updateQuantityStudents();
            window.location.replace("/client")
        },
        error: function (e) {
            setErrorMessage(e.responseJSON.message);
            console.log(e.responseText);
        }
    });

}

$(document).on('click', '#editUser', function editUserBtn() {
    $('#column1').find('input').each(function () {
        $(this)[0].disabled = $(this)[0].disabled !== true;
    });

    $('#column1').find('select').each(function () {
        $(this)[0].disabled = $(this)[0].disabled !== true;
    });

    if ($("#photoSelectBtn")[0].hasAttribute("disabled")) {
        $("#photoSelectBtn")[0].removeAttribute("disabled");
        $("#photoBtn")[0].removeAttribute("disabled");
        $('#editUser').attr("class", "btn btn-secondary")[0].innerText = 'Заблокировать';
        //блокируем не ОВНЕРУ возможность изменять пароль ОВНЕРУ
        if (!isAutorizedUserOwner) {
            $("#edit-user-password").prop("disabled", true);
            if (isAutorizedUserAdmin && !isUpdatedUserOwner) {
                $("#edit-user-password").prop("disabled", false);
            }
        }
    } else {
        $("#photoBtn")[0].setAttribute("disabled", "disabled");
        $("#photoSelectBtn")[0].setAttribute("disabled", "disabled");
        $('#editUser').attr("class", "btn btn-primary")[0].innerText = 'Редактировать';
        $("#edit-user-password").prop("disabled", true);
    }
});

$(document).ready(function () {
    $('#user-form').validator();
});

$(document).ready(function () {
    var spans = $('#current-user-roles').find("span");
    var currRoles = [];
    spans.each(function () {
        currRoles.push($(this)[0].getAttribute("value"))
    });
    $('#edit-user-roles').find('input').each(function () {
        if (currRoles.indexOf($(this)[0].value) !== -1) {
            $(this).attr("checked", "checked");
        }
    })
});

var file;

function selectPhoto() {
    file = $("#photoBtn")[0].files[0];
    readURL(this);
}

function sendPhoto(id, authId) {

    if (typeof file === 'undefined') {
        var current = document.getElementById("message");
        current.style.color = "limegreen";
        current.textContent = "Сохранено";
        if (authId === id) {
            window.location.replace("/logout")
        }
        return;
    }

    if (file.size > $("#photoBtn").attr("max")) {
        setErrorMessage("Ошибка сохранения фотографии. Файл слишком велик");
        return;
    }

    var dataValue = new FormData();
    dataValue.append("0", file);
    dataValue.append("id", id);
    $.ajax({
        url: '/rest/admin/user/update/photo',
        type: 'POST',
        data: dataValue,
        cache: false,
        dataType: 'json',
        enctype: "multipart/form-data",
        processData: false,
        contentType: false,
        success: function (data) {
            var current = document.getElementById("message");
            current.style.color = "limegreen";
            current.textContent = data.msg;
            if (authId === id) {
                window.location.replace("/logout")
            }
        },
        error: function (data) {
            setErrorMessage(data.message)
        }
    });
}

function readURL(input) {
    if (input.file) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#userPhoto').attr('src', e.target.result);
        };
        reader.readAsDataURL(input.file);
    }
}

function setErrorMessage(message) {
    var current = document.getElementById("message");
    if (typeof message === 'undefined') {
        current.textContent = "Ошибка сохранения";
        current.style.color = "red";
    } else {
        current.textContent = message;
        current.style.color = "red";
    }
}

function addUser() {
    if ($("#saveChanges")[0].className === "btn btn-primary disabled") {
        return;
    }
    if ($("input[name='roleCheckBx']:checked").length === 0) {
        var current = document.getElementById("message");
        current.textContent = "Необходимо указать минимум одну роль!";
        current.style.color = "red";
        return false;
    }

    var $sel = $('#add-user-roles').find("input[type=checkbox]:checked");
    let url = '/rest/admin/user/add';

    $sel.each(function (index, sel) {
        var obj = {};
        obj["id"] = sel.value;
        let labelFor = "checkbox-user-" + sel.value;
        obj["roleName"] = $("label[for='" + labelFor  + "']").text();
        myRows.push(obj);
    });
    let wrap = {
        firstName: $('#add-user-first-name').val(),
        lastName: $('#add-user-last-name').val(),
        birthDate: $('#add-user-birth-date').val(),
        phoneNumber: $('#add-user-phone-number').val(),
        ipTelephony: $("#ipTel").is(":checked") ? "true" : "false",
        email: $('#add-user-email').val(),
        sex: $('#add-user-sex').find('option:selected').text(),
        country: $('#add-user-country').val(),
        city: $('#add-user-city').val(),
        vk: $('#add-user-VKid').val(),
        password: $('#add-user-password').val(),
        isVerified: true,
        iaEnabled: true,
        role: myRows
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        beforeSend: function () {
            var current = document.getElementById("message");
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";
        },
        success: function (result) {
            for (let i = 0; i < myRows.length; i++) {
                if (myRows[i].roleName === "MENTOR") {
                    sendPostToSlackBotAboutNewMentor(wrap);
                    break;
                }
            }
            sendPhoto(result.id);
            window.location.replace("/client")
        },
        error: function (e) {
            setErrorMessage(e);
            var current = document.getElementById("message");
            current.textContent = "Пользователь с таким e-mail уже существует";
        }
    });
}

function registerUser() {
    if ($("#saveChanges")[0].className === "btn btn-primary disabled") {
        return;
    }
    if ($("input[name='roleCheckBx']:checked").length === 0) {
        var current = document.getElementById("message");
        current.textContent = "Необходимо указать минимум одну роль!";
        current.style.color = "red";
        return false;
    }
    let url = '/user/register';
    var obj = {};
    obj["id"] = 2;
    obj["roleName"] = 'USER';
    myRows.push(obj);
    let wrap = {
        firstName: $('#add-user-first-name').val(),
        lastName: $('#add-user-last-name').val(),
        birthDate: $('#add-user-birth-date').val(),
        phoneNumber: $('#add-user-phone-number').val(),
        ipTelephony: $("#ipTel").is(":checked") ? "true" : "false",
        email: $('#add-user-email').val(),
        sex: $('#add-user-sex').find('option:selected').text(),
        country: $('#add-user-country').val(),
        city: $('#add-user-city').val(),
        vk: $('#add-user-VKid').val(),
        password: $('#add-user-password').val(),
        isVerified: false,
        isEnabled: false,
        role: myRows
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        beforeSend: function () {
            var current = document.getElementById("message");
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";
        },
        success: function (result) {
            window.location.replace("/login")
        },
        error: function (e) {
            alert('Пользователь не был зарегистрирован');
        }
    });
}

function disableInputE() {
    var disMas = [69, 187, 189, 109];
    if (disMas.indexOf(event.keyCode) !== -1) {
        event.preventDefault()
    }
}

function sendPostToSlackBotAboutNewMentor(wrap) {
    let data = {
        name: wrap.firstName + " " + wrap.lastName,
        email: wrap.email
    };
    let url = "https://" + botDomain +  "/crm/new/mentor";
    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json; charset=UTF-8',
        data: JSON.stringify(data),
    });



}

$(document).ready(function () {
    let studentQuantity = $("#quantity-students");
    let studentsQuantityDiv = $("#students-quantity");
    let email = $('#edit-user-email').val();
    if (isUpdatedUserMentor) {
        studentsQuantityDiv.show();
        let url = "/admin/rest/mentor/student/quantity/"+ email;
        $.ajax({
                url: url,
                type: 'GET',
                contentType: 'application/json; charset=UTF-8',
                complete: function (result) {
                    studentQuantity.val(result.responseText);
                },
                error: function (data) {
                    console.log('Something went wrong, could not get quantity students')
                }
            }
        )
    }else {
        studentsQuantityDiv.hide();
    }

});
function updateQuantityStudents() {
    if (isUpdatedUserMentor){
        let mentorUrl = '/mentor/rest/user/update';
        let quantity = $("#quantity-students").val();

        console.log(quantity);
        $.ajax({
            url: mentorUrl,
            type: 'POST',
            dataType: 'json',
            data: {
                email:$('#edit-user-email').val(),
                quantityStudents: quantity,
                success: function (resp) {
                    console.log(quantity);
                }

            }

        })
    }
}