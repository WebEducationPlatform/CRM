$(document).ready(function () {
    $(document.getElementsByTagName("option")).each(function () {
        var val = $(this).closest('select').attr('value');
        var inText = $(this).text();
        if (val === inText) {
            $(this).attr("selected", "selected");
        }
    });
});

function changeUser(id) {
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
        },
        error: function (e) {
            setErrorMessage();
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
        setErrorMessage("фотографии. Файл слишком велик");
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
            setErrorMessage(data.msg)
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
    var current;
    if(typeof message === 'undefined'){
        current = document.getElementById("message");
        current.textContent = "Ошибка сохранения";
        current.style.color = "red";
    }else {
        current = document.getElementById("message");
        current.textContent = "Ошибка сохранения " + message;
        current.style.color = "red";
    }
}