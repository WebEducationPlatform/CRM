let type = 'email';
let text;
function switchMailingType() {
    var selected = $('#socNetworkChoose').val();
    if (selected === 'email') {
        type = 'email';
        $('#field').show();
        $('#vkArea').hide();
        $('#smsArea').hide();
    } else if (selected === 'vk') {
        type = 'vk';
        $('#field').hide();
        $('#vkArea').show();
        $('#smsArea').hide();
        text = document.getElementById("vkArea1");
        dropArea = document.getElementById("vkArea");
        ondropText();
    }
    else if (selected === 'sms') {
        type = 'sms';
        $('#field').hide();
        $('#vkArea').hide();
        $('#smsArea').show();
        text = document.getElementById("smsArea1");
        dropArea = document.getElementById("smsArea");
        ondropText();
    }
}

$(document).ready(function () {
    editor = CKEDITOR.replace('body1', {
        allowedContent: true,
        height: '600px',
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
        icon: 'info.png'
    });
});


function mail(sendnow) {
    let date = $('#mailingDate').val();
    let templateText = CKEDITOR.instances['body1'].getData();
    let x;
    if (type != "email") {
        x = text.value;
    } else {
        x = "";
    }
    let wrap = {
        sendnow: sendnow,
        type: type,
        templateText: templateText,
        text: x,
        date: date
    };
    $.ajax({
        type: "POST",
        url: "/client/mailing/send",
        data: wrap,
        success: function (result) {
            console.log("success " + result);
        },
        error: function (e) {
            console.log("неверный формат записи, добавте clientData перед данными\n" + e);
        }
    });
}

$(document).ready(function () {
    var nowDate = new Date();
    var minutes = Math.ceil((nowDate.getMinutes() + 1) / 10) * 10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes, 0, 0);
    var startDate = moment(minDate).utcOffset(180);
    $('input[name="mailingDate"]').daterangepicker({
        singleDatePicker: true,
        timePicker: true,
        timePickerIncrement: 10,
        timePicker24Hour: true,
        locale: {
            format: 'DD.MM.YYYY H:mm МСК'
        },
        minDate: startDate,
        startDate: startDate
    });
});

$(document).ready(function () {
    editor.on('drop', function (e) {
        var files = e.data.dataTransfer.getFile(0);
        var reader = new FileReader();
        reader.onload = function () {
            if (CKEDITOR.instances["body1"].getData().includes("clientData")) {
                editor.insertText("\n\n" + this.result);
            } else {
                editor.insertText("\nclientData\n" + this.result);
            }
        };
        reader.readAsBinaryString(files);
    });
});

function ondropText() {
    var dropZone = text;
    dropZone.addEventListener('drop', function (e) {
        event.preventDefault();
        var files = e.dataTransfer.files[0];
        var reader = new FileReader();
        reader.onload = function () {
            if (text.value.includes("clientData")) {
                text.value += "\n\n" + this.result;
            } else {
                text.value += "\nclientData\n" + this.result;
            }
        };
        reader.readAsBinaryString(files);
    }, false);
}
