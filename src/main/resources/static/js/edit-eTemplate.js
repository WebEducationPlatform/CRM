var current;
$(document).ready(function () {
    current = document.getElementById("message");
});

function saveTemplate(templateId) {
    let url = '/admin/editEmailTemplate';

    let wrap = {
        templateId: templateId,
        templateText: CKEDITOR.instances['body'].getData()
    };
    var current = document.getElementById("message");
    $.ajax({
        type: "POST",
        url: url,
        data: wrap,
        beforeSend: function(){
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function (result) {
            current.style.color = "limegreen";
            current.textContent = "Сохранено";
        },
        error: function (e) {
            setErrorMessage(e.responseText);
        }
    });
}
var file;

function sendImg() {

    file = $("#imgBtn")[0].files[0];
     if(file.size >  $("#imgBtn").attr("max")){
         setErrorMessage("Ошибка добавления фотографии. Файл слишком велик");
         return;
     }
    $("#imgBtn").val("");
    var dataValue = new FormData();
    dataValue.append("0", file);
    $.ajax({
        url: '/admin/savePicture',
        type: 'POST',
        data: dataValue,
        cache: false,
        dataType: 'json',
        enctype: "multipart/form-data",
        processData: false,
        contentType: false,
        success: function (userId) {
            insertNewPicture(userId);
        },
        error: function (data) {
            if(typeof data.responseJSON === 'undefined') {
                setErrorMessage();
            }
           setErrorMessage(data.responseJSON.message);
        }

    });
}

function setErrorMessage(message) {
    if(typeof message === 'undefined'){
        current.textContent = "Ошибка сохранения";
        current.style.color = "red";
    }else {
        current.textContent = message;
        current.style.color = "red";
    }
    console.log(message);
}

function insertNewPicture(userId) {
    filename = file.name.replace(/\.[^.]+$/, "");
    text = CKEDITOR.dom.element.createFromHtml("<img data-th-src=\"|cid:" + userId + '_' +filename + "|\" src=\"/admin/image/" + userId + '_' + filename + ".png\"/>");
    CKEDITOR.instances.body.insertElement(text);
}

$(document).ready(function () {
    editor = CKEDITOR.replace('body', {
        allowedContent: true,
        height: '600px'
    });

    editor.addCommand("infoCommend", {
        exec: function(edt) {
            $("#infoModal").modal('show');
        }
    });
    editor.ui.addButton('SuperButton', {
        label: "Info",
        command: 'infoCommend',
        toolbar: 'styles',
        icon: 'info.png'
    });
});
