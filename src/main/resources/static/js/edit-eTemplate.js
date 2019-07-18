function switchTemplate() {
    let selected = $('#socNetworkChoose').val();
    if (selected === 'email') {
        $('#field').show();
        $('#show-area').hide();
    } else if (selected === 'vk') {
        $('#field').hide();
        $('#show-area').show();
    }
}

var current;
var exit;
var defaltText;
var count = 0;
$(document).ready(function () {
    current = document.getElementById("message");
});

var timerId = setInterval(function () {
    if (count == 0) {
        defaltText = CKEDITOR.instances['body'].getData();
        count++;
        exit = true;
    }
    if (defaltText != CKEDITOR.instances['body'].getData()) {
        exit = false;
    }
}, 100);

window.onbeforeunload = function () {
    if (!exit) {
        return "Данные не сохранены. Точно перейти?";
    }
};

function saveTemplate(templateName) {
    let url = '/admin/editMessageTemplate';
    let text = $('#textTemplateArea').val();
    let themeTemplate = $('#template-theme-rename').val();
    let wrap = {
        templateName: templateName,
        templateText: CKEDITOR.instances['body'].getData(),
        otherTemplateText: text,
        theme: themeTemplate
    };
    var current = document.getElementById("message");
    $.ajax({
        type: "POST",
        url: url,
        data: wrap,
        beforeSend: function () {
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";
        },
        success: function () {
            current.style.color = "limegreen";
            current.textContent = "Сохранено";
            exit = true;
            defaltText = CKEDITOR.instances['body'].getData();
            window.location = "/template/all";
        },
        error: function (e) {
            setErrorMessage(e.responseText);
        }
    });
}

var file;

function sendImg(templateName, input) {

    file = $("#imgBtn")[0].files[0];

    if (file.size > $("#imgBtn").attr("max")) {
        setErrorMessage("Ошибка добавления фотографии. Файл слишком велик");
        return;
    }

    var dataValue = new FormData();
    dataValue.append("0", file);
    let url = '/admin/savePicture?templateName='+templateName;
    $.ajax({
        url: url,
        type: 'POST',
        data: dataValue,
        cache: false,
        dataType: 'json',
        enctype: "multipart/form-data",
        processData: false,
        contentType: false,
        success: function (userId) {
            insertNewPicture(userId,templateName, input);
        },
        error: function (data) {
            if (typeof data.responseJSON === 'undefined') {
                setErrorMessage();
            }
            setErrorMessage(data.responseJSON.message);
        }
    });
}

function setErrorMessage(message) {
    if (typeof message === 'undefined') {
        current.textContent = "Ошибка сохранения";
        current.style.color = "red";
    } else {
        current.textContent = message;
        current.style.color = "red";
    }
}

function insertNewPicture(userID,templateName, input) {
    if (input.files && input.files[0]) {
        let reader = new FileReader();
        reader.onload = function (e) {
            filename = file.name.replace(/\.[^.]+$/, "");
            let path = "images/templateID_" + templateName + '/' + filename +".png";
            let text = CKEDITOR.dom.element.createFromHtml("<img data-th-src=\"|cid:" + path + "|\" src='" + e.target.result + "'/>");
            CKEDITOR.instances.body.insertElement(text);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

$(document).ready(function () {
    editor = CKEDITOR.replace('body', {
        allowedContent: true,
        height: '600px',
        filebrowserImageUploadUrl: '/image/upload'

    });

    CKEDITOR.config.extraPlugins = 'uploadimage';
    CKEDITOR.config.imageUploadUrl = '/image/upload';
    CKEDITOR.on('dialogDefinition', function (e) {
        var dialogName = e.data.name;
        var dialogDefinition = e.data.definition;
        if (dialogName === 'image') {
            // dialogDefinition.removeContents('info');
            dialogDefinition.removeContents('Link');
            dialogDefinition.removeContents('advanced');
        }
    });
    editor.addCommand("infoCommend", {
        exec: function (edt) {
            $("#infoModal").modal('show');
        }
    });
    editor.ui.addButton('SuperButton', {
        label: "Info",
        command: 'infoCommend',
        toolbar: 'styles',
        icon: 'https://img.icons8.com/ios/26/000000/info.png'
    });
});
