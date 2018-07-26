var selectedImage;
var file;
var current;


function switchTemplate() {
    var selected = $('#socNetworkChoose').val();
    if (selected === 'email') {
        $('#field').show();
        $('#show-area').hide();
    } else if (selected === 'vk') {
        $('#field').hide();
        $('#show-area').show();
    }
}


$(document).ready(function () {
    $("#input").cleditor();
    current = document.getElementById("message");
    let iframe = document.getElementsByTagName('iframe')[0];
    let idoc = iframe.contentDocument || iframe.contentWindow.document;

    $(idoc.body).click(function (event) {
        if (event == null) {
            return;
        }
        let img = event.target;
        if (img.tagName == 'IMG') {
            if (selectedImage !== img && selectedImage != null) {
                selectedImage.style.opacity = "1";
                selectedImage = null;
                img.style.opacity = "0.5";
            }
            if (selectedImage == null) {
                selectedImage = img;
                img.style.opacity = "0.5";
            } else {
                selectedImage = null;
                img.style.opacity = "1";
            }
        }
    });

    $(".cleditorMain iframe").contents().find('body').keyup(function () {
        let tId = $('#templateId').val();
        saveTemplate(tId);
    });
});

function saveTemplate(templateId) {
    let url = '/admin/editMessageTemplate';
    let text = $('#textTemplateArea').val();
    let body = $(".cleditorMain iframe").contents().find('body').html();
    let wrap = {
        templateId: templateId,
        templateText: body,
        otherTemplateText: text
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
        success: function (result) {
            current.style.color = "limegreen";
            current.textContent = "Сохранено";
        },
        error: function (e) {
            setErrorMessage(e.responseText);
        }
    });
}

function sendImg() {
    file = $("#imgBtn")[0].files[0];
    if (file.size > $("#imgBtn").attr("max")) {
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
    console.log(message);
}

function insertNewPicture(userId) {
    filename = file.name.replace(/\.[^.]+$/, "");
    let img = $(".cleditorMain iframe").contents().find('body');
    img.append("<img src=\"/admin/image/" + userId + '_' + filename + ".png\"/>");
    let tId = $('#templateId').val();
    saveTemplate(tId);
}

function editImage() {
    let h = $('#heightImage').val();
    let w = $('#widthImage').val();
    let img = selectedImage;

    img.height == 0 ? img.setAttribute('height', img.height) : img.setAttribute('height', h);
    img.width == 0 ? img.setAttribute('width', img.width) : img.setAttribute('width', w);
    selectedImage.style.opacity = "1";
    selectedImage = null;
    $('#heightImage').val("");
    $('#widthImage').val("");
    let tId = $('#templateId').val();
    saveTemplate(tId);
};