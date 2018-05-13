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
    if($("#saveChanges")[0].className ==="btn btn-primary disabled"){
        return;
    }
    if($("input[name='roleCheckBx']:checked").length === 0) {
        var current = document.getElementById("message");
        current.textContent = "Необходимо указать минимум одну роль!";
        current.style.color = "red";
        return false;
    }

    var $sel = $('#edit-user-roles').find("input[type=checkbox]:checked");
    let url = '/admin/rest/user/update';
    var myRows = [];

    $sel.each(function (index, sel) {
        var obj = {};
        obj["id"] = sel.value;
        obj["roleName"] = sel.innerText;
        myRows.push(obj);
    });
    let wrap = {
        id: id,
        firstName: $('#edit-user-first-name').val(),
        lastName: $('#edit-user-last-name').val(),
        phoneNumber: $('#edit-user-phone-number').val(),
        ipTelephony: $("#ipTel").is(":checked") ? "true" : "false",
        email: $('#edit-user-email').val(),
        age: $('#edit-user-age').val(),
        sex: $('#edit-user-sex').find('option:selected').text(),
        country: $('#edit-user-country').val(),
        city: $('#edit-user-city').val(),
        salary: $('#edit-user-salary').val(),
        vk:$('#edit-user-VKid').val(),
        password:$('#edit-user-password').val(),
        vacancy: $("#edit-user-vacancy").val(),
        role:myRows
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        beforeSend: function(){
            var current = document.getElementById("message");
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function (result) {
            sendPhoto(id);
            if (authId === id) {
                window.location.replace("http://localhost:9090/logout")
            }
        },
        error: function (e) {
            setErrorMessage(e.responseJSON.message);
            console.log(e.responseText);
        }
    });
}

$(document).on('click','#editUser',function editUserBtn() {
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
    } else {
        $("#photoBtn")[0].setAttribute("disabled","disabled");
        $("#photoSelectBtn")[0].setAttribute("disabled","disabled");
        $('#editUser').attr("class", "btn btn-primary")[0].innerText = 'Редактировать';
    }
});

$(document).ready(function () {
    $('#user-form').validator()
});

$(document).ready(function () {
    var spans = $('#current-user-roles').find("span");
    var currRoles=[];
    spans.each(function () {
        currRoles.push($(this)[0].getAttribute("value"))
    });
    $('#edit-user-roles').find('input').each(function () {
        if(currRoles.indexOf($(this)[0].value)!== -1){
            $(this).attr("checked","checked");
        }
    })
});

var file;

function selectPhoto() {
    file = $("#photoBtn")[0].files[0];
    readURL(this);
}

function sendPhoto(id) {

    if (typeof file === 'undefined') {
        var current = document.getElementById("message");
        current.style.color = "limegreen";
        current.textContent = "Сохранено";
        return;
    }

    if(file.size >  $("#photoBtn").attr("max")){
        setErrorMessage("Ошибка сохранения фотографии. Файл слишком велик");
        return;
    }

    var dataValue = new FormData();
    dataValue.append("0", file);
    dataValue.append("id",id);
    $.ajax({
        url: '/admin/rest/user/update/photo',
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
        },
        error: function (data) {
            setErrorMessage(data.responseJSON.message)
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
    if(typeof message === 'undefined'){
        current.textContent = "Ошибка сохранения";
        current.style.color = "red";
    }else {
        current.textContent = message;
        current.style.color = "red";
    }
}

function addUser() {
    if($("#saveChanges")[0].className ==="btn btn-primary disabled"){
        return;
    }
    if($("input[name='roleCheckBx']:checked").length === 0) {
        var current = document.getElementById("message");
        current.textContent = "Необходимо указать минимум одну роль!";
        current.style.color = "red";
        return false;
    }

    var $sel = $('#add-user-roles').find("input[type=checkbox]:checked");
    let url = '/admin/rest/user/add';
    var myRows = [];

    $sel.each(function (index, sel) {
        var obj = {};
        obj["id"] = sel.value;
        obj["roleName"] = sel.innerText;
        myRows.push(obj);
    });
    let wrap = {
        firstName: $('#add-user-first-name').val(),
        lastName: $('#add-user-last-name').val(),
        phoneNumber: $('#add-user-phone-number').val(),
        ipTelephony: $("#ipTel").is(":checked") ? "true" : "false",
        email: $('#add-user-email').val(),
        age: $('#add-user-age').val(),
        sex: $('#add-user-sex').find('option:selected').text(),
        country: $('#add-user-country').val(),
        city: $('#add-user-city').val(),
        salary: $('#add-user-salary').val(),
        vk:$('#add-user-VKid').val(),
        password:$('#add-user-password').val(),
        vacancy: $("#add-user-vacancy").val(),
        role:myRows
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        beforeSend: function(){
            var current = document.getElementById("message");
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function (result) {
            sendPhoto(result);
        },
        error: function (e) {
            setErrorMessage(e.responseJSON.message);
            console.log(e.responseText);
        }
    });


}