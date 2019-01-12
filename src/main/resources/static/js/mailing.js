const BUTTON_INFO_CLASS = "btn-info";
const BUTTON_SECONDARY_CLASS = "btn-secondary";
const DROP_ZONE_IS_DRAGOVER_CLASS = "drop-zone-is-dragover";
const BADGE_SUCCESS_CLASS = "badge-success";
const BADGE_WARNING_CLASS = "badge-warning";
const EDITOR = "editor";
const URL_POST_DATA = "/client/mailing/send";
const SEND_EMAILS = "Укажите список email получателей (каждый с новой строки):";
const SEND_SMSS = "Укажите список телефонов получателей (каждый с новой строки):";
const SEND_TO_VK = "Укажите список id или ссылок профилей ВК получателей (не более 20 человек в день, которые не в друзьях и каждый с новой строки):";

var messageType = 'email';
var vkPage;
var listMailing;

function sendMessages(sendnow) {
    let date = $('#messageSendingTime').val();
    let text = CKEDITOR.instances.editor.getData();
    let recipients = $('#addresses-area').val();
    console.warn(recipients);
    if (recipients === '') {alert("Введите получателей!"); return}
    let x;
    if (messageType !== "email") {
        x = CKEDITOR.instances.editor.document.getBody().getText();
    } else {
        x = "";
    }

    let mesfeedback;
    if (sendnow==1) {
        mesfeedback = "Сообщение отправлено";
    } else {
        mesfeedback = "Отправка запланирована на " + date;
    }

    let wrap = {
        sendnow: sendnow,
        type: messageType,
        templateText: text,
        text: x,
        date: date,
        recipients: recipients,
        vkType: vkPage = $("#vkTokenSelect").val(),
        listMailing: listMailing

    };

    let label = $("#message");
    label.prop('innerHTML', "Идет отправка соощения")
    label.css('color', 'blue');

    $.ajax({
        type: "POST",
        url: URL_POST_DATA,
        data: wrap,
        success: function (data, textStatus, xhr) {
            if (xhr.status === 204) {
                setErrorMessage("Ошибка отправки сообщения! Файл вложения не загружен на сервер.", 'red');
            } else {
                setErrorMessage(mesfeedback, 'green')
            }
        },
        error: function (xhr) {
            if (xhr.status === 500) {
                setErrorMessage("Что-то пошло не так, необходимо повторить отправку сообщений", "red");
            }
        }
    });
}

/**
 * Функция, переключающая состояние кнопок режима рассылки и перенастраивающая интерфейс редактора.
 * Для функции отправки сообщений посредством СМС, в CKEditor отключаются все плагины форматирования
 * текста, так же, как и для отправки сообщений в Вк. Плюс меняется тип сообщения, messageType.
 */
$(document).ready(function () {
    $("#vkTokenSelect").hide()
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $("#message-type-button-group > button").click(function () {
        if (messageType === $(this).attr("id")) {
            return;
        }
        messageType = $(this).attr("id");

        if (messageType !== 'email') {
            ckeditorRemoveAllToolbars();
            if (messageType === 'sms') {
                $("#addresses-label").text(SEND_SMSS);
            } else {
                $("#addresses-label").text(SEND_TO_VK);
            }
        } else {
            ckeditorAddAllToolbars();
            $("#addresses-label").text(SEND_EMAILS);
        }

        $("#message-type-button-group > button").each(function (index, element) {
            if ($(element).hasClass(BUTTON_INFO_CLASS)) {
                $(element).removeClass(BUTTON_INFO_CLASS);
                $(element).addClass(BUTTON_SECONDARY_CLASS);
            }
        });
        $(this).addClass(BUTTON_INFO_CLASS);
    });

    $.ajax({
        url: '/get/sender',
        contentType: "application/json",
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                if (data[i].vkToken!=null) {
                    $('<option value="' + data[i].vkToken + '">' + data[i].firstName + '</option>').appendTo($('#vkTokenSelect'))
                }
            }
        }
    });

    $("#historyMailingTable").on('click', 'button[id="getRecipient"]', function(e) {
        var id = $(this).closest('tr').children('td:first').text();
        $.ajax({
            type: "POST",
            url: "/get/client-data",
            data: {
                mailId: id
            },
            success: function (data) {
                for (var j = 0; j < data.length; j++) {
                    $("#recipientBodyMailing").append("<tr> \
                            <td>" + data[j].info + "</td> \
                        </tr>");
                }
            }
        });
    })

    $("#historyMailingTable").on('click', 'button[id="getNoSend"]', function(e) {
        var id = $(this).closest('tr').children('td:first').text();
        $.ajax({
            type: "POST",
            url: "/get/message/id",
            data: {
                messageId: id
            },
            success: function (data) {
                for(var i = 0; i < data.notSendId.length; i++) {
                    $("#noSend-area").each(function() {
                        $(this).val(data.notSendId.join("\n"));
                    });
                }
            }
        });
    })
});

/**
 * Функция, настраивающая datarangepicker
 */
$(document).ready(function () {
    $("#vkTokenSelect").hide()
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    let startDate = moment(new Date()).utcOffset(180); //устанавливаем минимальную дату и время по МСК (UTC + 3 часа )
    $('#messageSendingTime').daterangepicker({
        "singleDatePicker": true, //отключаем выбор диапазона дат (range)
        "showWeekNumbers": false,
        "timePicker": true,
        "timePicker24Hour": true,
        "timePickerIncrement": 10,
        "locale": {
            "format": "DD.MM.YYYY HH:mm МСК",
            "separator": " - ",
            "applyLabel": "Apply",
            "cancelLabel": "Cancel",
            "fromLabel": "From",
            "toLabel": "To",
            "customRangeLabel": "Custom",
            "weekLabel": "W",
            "daysOfWeek": ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"],
            "monthNames": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            "firstDay": 0
        },
        "linkedCalendars": false,
        "startDate": startDate,
    }, function (start, end, label) {
        console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' +
            end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
    });
});

//TODO Не срабатывает. Предназначен для установки текущей даты в стартовую и мин даты при открытии календаря
$("#messageSendingTime").on('show.daterangepicker', function (event, picker) {
    let minDate = moment(new Date()).utcOffset(180); //устанавливаем минимальную дату и время по МСК (UTC + 3 часа)
    picker.minDate = minDate;
    picker.startDate = minDate;
});

/**
 * Заполнение блока адресов
 */
$(document).ready(function () {
    $("#vkTokenSelect").hide()
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $("#addresses-area").on("drop", function (event) {
        event.preventDefault();
        event.stopPropagation();
        $("#addresses-area").removeClass("drop-zone-is-dragover");
        let reader = new FileReader();
        let file = event.originalEvent.dataTransfer.files[0];
        $("#file-info").text("Файл: " + file.name + ", Размер файла: " + file.size + " б");
        reader.onload = function (event) {
            let content = event.target.result;
            $("#addresses-area").val(content.toLowerCase());
        };
        reader.onerror = function (event) {
            setErrorMessage("The file could not be read!" + event.target.error.code, 'red')
            let fileInfo = $("#file-info");
            $(fileInfo).text("The file could not be read!" + event.target.error.code);
            $(fileInfo).removeClass(BADGE_SUCCESS_CLASS);
            $(fileInfo).addClass(BADGE_WARNING_CLASS);
        };
        reader.readAsText(file);
    })
});

/**
 * Начальная настройка CKEditor
 */

$(document).ready(function () {
    $("#vkTokenSelect").hide();
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    CKEDITOR.addCss('.cke_editable p { margin: 0 !important; }');
    let rep = CKEDITOR.replace(EDITOR, {
        customConfig: '/ckeditor/add-all-toolbars.js',
        filebrowserImageUploadUrl: '/image/upload',

    });
    CKEDITOR.config.extraPlugins = 'uploadimage';
    CKEDITOR.config.imageUploadUrl = '/image/upload';

    // CKEDITOR.ajax.load(rep.);

    CKEDITOR.on('dialogDefinition', function (e) {
        var dialogName = e.data.name;
        var dialogDefinition = e.data.definition;
        if (dialogName === 'image') {
            // dialogDefinition.removeContents('info');
            dialogDefinition.removeContents('Link');
            dialogDefinition.removeContents('advanced');
        }
    });


});


function ckeditorAddAllToolbars() {
    CKEDITOR.instances[EDITOR].destroy(true);
    CKEDITOR.replace(EDITOR, {
        customConfig: '/ckeditor/add-all-toolbars.js'


    });
    $("#imgSelectBtn").show()
    $("#vkTokenSelect").hide()
}

function ckeditorRemoveAllToolbars() {
    CKEDITOR.instances[EDITOR].destroy(true);
    CKEDITOR.replace(EDITOR, {
        customConfig: '/ckeditor/remove-all-toolbars.js'
    });
    $("#imgSelectBtn").hide()
    if(messageType === "vk") {
        $("#vkTokenSelect").show()
    } else {
        $("#vkTokenSelect").hide()
    }

}

/**
 * Визуализация событий dragover, dragleave, dragend, drop поля адресов
 */
$(document).ready(function () {
    $("#vkTokenSelect").hide()
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $("#addresses-area")
        .on("dragover", function (event) {
            $(this).addClass(DROP_ZONE_IS_DRAGOVER_CLASS);
        })
        .on("dragleave dragend drop", function () {
            $(this).removeClass(DROP_ZONE_IS_DRAGOVER_CLASS);
        })
});

var file;

function sendImg(input) {
    let templateID = 0;
    file = $("#imgBtn")[0].files[0];

    if (file.size > $("#imgBtn").attr("max")) {
        setErrorMessage("Ошибка добавления фотографии. Файл слишком велик", 'red');
        return;
    }

    var dataValue = new FormData();
    dataValue.append("0", file);
    let url = '/admin/savePicture?templateID='+templateID;
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
            insertNewPicture(userId,templateID,input);
        },
        error: function (data) {client_social_network
            if (typeof data.responseJSON === 'undefined') {
                setErrorMessage('undefined', 'red');
            }
            setErrorMessage(data.responseJSON.message, 'red');
        }
    });
}

function setErrorMessage(message, color) {
    let label = $("#message");
    label.prop('innerHTML', message)
    label.css('color', color);


    $.ajax({
        type: "GET",
        url: "/get/no/send",
        success: function (data) {
            var i = data.length - 1;
                if (data[i].notSendId.length > 0 && messageType == "vk") {
                    $("#noSendButton").show()
                        $("#noSend-area").each(function() {
                            $(this).val(data[i].notSendId.join("\n"));
                        });
                }
            }
    });
}

function insertNewPicture(userID, templateID, input) {
    if (input.files && input.files[0]) {
        let reader = new FileReader();
        reader.onload = function (e) {
            filename = file.name.replace(/\.[^.]+$/, "");
            let path = "images/templateID_" + templateID + '/' + filename +".png";
            let text = CKEDITOR.dom.element.createFromHtml("<img data-th-src=\"|cid:" + path + "|\" src='" + e.target.result + "'/>");
            CKEDITOR.instances.editor.insertElement(text);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function showHistory() {

    let startFromDate = moment(new Date()).utcOffset(180); //устанавливаем минимальную дату и время по МСК (UTC + 3 часа )
    $('#historyFromTime').daterangepicker({
        "singleDatePicker": true, //отключаем выбор диапазона дат (range)
        "showWeekNumbers": false,
        "timePicker": true,
        "timePicker24Hour": true,
        "timePickerIncrement": 10,
        "locale": {
            "format": "DD.MM.YYYY",
            "separator": " - ",
            "applyLabel": "Apply",
            "cancelLabel": "Cancel",
            "fromLabel": "From",
            "toLabel": "To",
            "customRangeLabel": "Custom",
            "weekLabel": "W",
            "daysOfWeek": ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"],
            "monthNames": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            "firstDay": 0
        },
        "linkedCalendars": false,
        "startDate": startFromDate,
        //"minDate": startFromDate //стартовая дата будет совпадать с минимальной
    }, function (start, end, label) {
        console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' +
            end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
    });

    let startToDate = moment(new Date()).utcOffset(180); //устанавливаем минимальную дату и время по МСК (UTC + 3 часа )
    $('#historyToTime').daterangepicker({
        "singleDatePicker": true, //отключаем выбор диапазона дат (range)
        "showWeekNumbers": false,
        "timePicker": true,
        "timePicker24Hour": true,
        "timePickerIncrement": 10,
        "locale": {
            "format": "DD.MM.YYYY",
            "separator": " - ",
            "applyLabel": "Apply",
            "cancelLabel": "Cancel",
            "fromLabel": "From",
            "toLabel": "To",
            "customRangeLabel": "Custom",
            "weekLabel": "W",
            "daysOfWeek": ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"],
            "monthNames": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            "firstDay": 0
        },
        "linkedCalendars": false,
        "startDate": startToDate,
    }, function (start, end, label) {
        console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' +
            end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
    });

    $.ajax({
        url: '/mailing/history',
        contentType: "application/json",
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                date = new Date(data[i].date);
                year = date.getFullYear();
                month = date.getMonth() + 1;
                dt = date.getDate();
                hour = date.getHours();
                minutes = date.getMinutes();
                seconds = date.getSeconds();


                if (dt < 10) {
                    dt = '0' + dt;
                }
                if (month < 10) {
                    month = '0' + month;
                }

                if (hour < 10) {
                    hour = '0' + hour
                }

                if (minutes < 10) {
                    minutes = '0' + minutes
                }
                if (data[i].type === "vk" && data[i].notSendId.length > 0) {
                    $("#historyBodyMailing").append("<tr> \
                            <td>" + data[i].id + " </td> \
                            <td>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td>" + data[i].text + "</td> \
                            <td>" + data[i].type + "</td> \
                            <td><button id ='getRecipient' data-toggle='modal' data-target='#recipientModal' class='btn btn-success'>Показать всех получателей</button> \
                            <br/> \
                            <button id ='getNoSend' data-toggle='modal' data-target='#noSendModal' class='btn btn-danger'>Недоставлено</button></td> \
                        </tr>");
                } else {
                    $("#historyBodyMailing").append("<tr> \
                            <td>" + data[i].id + " </td> \
                            <td>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td>" + data[i].text + "</td> \
                            <td>" + data[i].type + "</td> \
                            <td><button id ='getRecipient' data-toggle='modal' data-target='#recipientModal' class='btn btn-success'>Показать всех получателей</button></td> \
                        </tr>");
                }

            }

        }
    });
}

function removeHistory() {
    $('#managerSelect').val('');
    $('#historyBodyMailing').empty();
    $('#timeSelect').empty();
    $('#recipientBodyMailing').empty();
};




function addToListMailing() {
    let recipientsEmail = $('#listEmail').val();
    let recipientsSms = $('#listSms').val();
    let recipientsVk = $('#listVk').val();
    let listName = $('#listName').val();

    if(listName != '') {
        let wrap = {
            recipientsEmail: recipientsEmail,
            recipientsSms: recipientsSms,
            recipientsVk: recipientsVk,
            listName: listName
        }

        $.ajax({
            type: "POST",
            url: '/list-mailing',
            data: wrap,
            success: function () {
                location.reload();
            }
        });
    } else {
       $('#errorListName').html('<p style="color: red">Введите название списка</p>')
    }
}

function showManagerHistory() {

    var mangerId = $("#managerSelect").val();
    var managerFromTime = $("#historyFromTime").val();
    var managerToTime = $("#historyToTime").val();

    $.ajax({
        url: '/mailing/manager/history',
        type: 'POST',
        data: {
            managerId: mangerId,
            managerFromTime: managerFromTime,
            managerToTime: managerToTime
        },
        success: function (data) {
            $('#historyBodyMailing').empty();
            for (var i = 0; i < data.length; i++) {
                date = new Date(data[i].date);
                year = date.getFullYear();
                month = date.getMonth() + 1;
                dt = date.getDate();
                hour = date.getHours();
                minutes = date.getMinutes();
                seconds = date.getSeconds();

                if (dt < 10) {
                    dt = '0' + dt;
                }

                if (month < 10) {
                    month = '0' + month;
                }

                if (hour < 10) {
                    hour = '0' + hour
                }

                if (minutes < 10) {
                    minutes = '0' + minutes
                }

                if (data[i].type === "vk" && data[i].notSendId.length > 0) {
                    $("#historyBodyMailing").append("<tr> \
                            <td>" + data[i].id + " </td> \
                            <td>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td>" + data[i].text + "</td> \
                            <td>" + data[i].type + "</td> \
                            <td><button id ='getRecipient' data-toggle='modal' data-target='#recipientModal' class='btn btn-success'>Показать всех получателей</button> \
                            <br/> \
                            <button id ='getNoSend' data-toggle='modal' data-target='#noSendModal' class='btn btn-danger'>Недоставлено</button></td> \
                        </tr>");
                } else {
                    $("#historyBodyMailing").append("<tr> \
                            <td>" + data[i].id + " </td> \
                            <td>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td>" + data[i].text + "</td> \
                            <td>" + data[i].type + "</td> \
                            <td><button id ='getRecipient' data-toggle='modal' data-target='#recipientModal' class='btn btn-success'>Показать всех получателей</button></td> \
                        </tr>");
                }

                }
            }
    });

}
function showListMailing() {
    if (messageType !== "email") {
        x = CKEDITOR.instances.editor.document.getBody().getText();
    } else {
        x = "";
    }
    listGroupName = $('#listMailingSelect').val()
        $.ajax({
            url: '/get/listMailing',
            type: 'POST',
            data: {
                listGroupId: listGroupName
            },
            success: function (data) {

                    if (messageType == "email") {
                        for(var i = 0; i < data.recipientsEmail.length; i++) {
                            $("#addresses-area").each(function() {
                                $(this).val(data.recipientsEmail.join("\n"));
                            });
                        }
                    } else if (messageType == 'sms') {
                        for(var i = 0; i < data.recipientsSms.length; i++) {
                            $("#addresses-area").each(function() {
                                $(this).val(data.recipientsSms.join("\n"));
                            });
                        }
                    } else if (messageType == "vk") {
                        for(var i = 0; i < data.recipientsVk.length; i++) {
                            $("#addresses-area").each(function() {
                                $(this).val(data.recipientsVk.join("\n"));
                            });
                        }
                    }
                }


        });
}


function openEditShowListMailing() {

    listGroupName = $('#listMailingSelect').val()

    if(listGroupName != "null") {
        $("#deleteListMaling").removeAttr("disabled");
        $("#editButton").removeAttr("disabled");
    }

    $.ajax({
        url: '/get/listMailing',
        type: 'POST',
        data: { listGroupId: listGroupName
        },
        success: function (data) {

            $("#editListName").val(data.listName)

            $("#editListEmail").each(function () {
                $(this).val(data.recipientsEmail.join("\n"));
            });

            $("#editListSms").each(function () {
                $(this).val(data.recipientsSms.join("\n"));
            });

            $("#editListVk").each(function () {
                $(this).val(data.recipientsVk.join("\n"));
            });
        }

    });
}


function editListMailing() {

    var listName = $("#listMailingSelect").val()

    var editListName = $("#editListName").val()

    var editListEmail = $("#editListEmail").val()

    var editListSms = $("#editListSms").val()

    var editListVk = $("#editListVk").val()

    $.ajax({
        url: '/edit/list-mailing',
        type: 'POST',
        data: { listId: listName,
                editListName: editListName,
                editRecipientsEmail: editListEmail,
                editRecipientsSms: editListSms,
                editRecipientsVk: editListVk
        }, success: function () {
            location.reload();
        }

    });
}

function deleteListMailing() {

    var listName = $("#listMailingSelect").val();

    $.ajax({
        url: '/remove/list-mailing',
        type: 'POST',
        data: { listId: listName
        }, success: function () {
            location.reload();
        }

    });

}

function turnDisable() {
    $("#deleteListMaling").attr("disabled", "disabled");
    $("#editButton").attr("disabled", "disabled");
}

function removeRecipient() {
    $('#recipientBodyMailing').empty();
}

function hideNoSend() {
    $("#noSendButton").hide();
    $("#noSend-area").val("");
}


