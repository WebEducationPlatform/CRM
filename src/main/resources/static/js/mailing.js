const BUTTON_INFO_CLASS = "btn-info";
const BUTTON_SECONDARY_CLASS = "btn-secondary";
const DROP_ZONE_IS_DRAGOVER_CLASS = "drop-zone-is-dragover";
const BADGE_SUCCESS_CLASS = "badge-success";
const BADGE_WARNING_CLASS = "badge-warning";
const EDITOR = "editor";
const URL_POST_DATA = "/client/mailing/send";
const URL_EMAILS_FROM_STATUSES = "/rest/client/emails/statuses";
const URL_PHONES_FROM_STATUSES = "/rest/client/phones/statuses";
const SEND_EMAILS = "Укажите список email получателей (каждый с новой строки):";
const SEND_SMSS = "Укажите список телефонов получателей (каждый с новой строки):";
const SEND_TO_VK = "Укажите список id или ссылок профилей ВК получателей (не более 20 человек в день, которые не в друзьях и каждый с новой строки):";
const SEND_TO_SLACK = "Укажите список id Slack получателей (каждый с новой строки):";

var messageType = 'email';
var vkPage;
var listMailing;

function sendMessages(sendnow) {
    let date;
    let msgFeedBack;
    let recipients = $('#addresses-area').val();
    if (recipients === '') {alert("Введите получателей!"); return}
    let text;
    if (messageType !== "email") {
        text = CKEDITOR.instances.editor.document.getBody().getText();
    } else {
        text = CKEDITOR.instances.editor.getData();
    }

    let selectAppTokenNumber = $("#selectAppToken").val();

    if (sendnow===1) {
        date = $.date(new Date(), 'format', 'd.m.Y H:i МСК');
        msgFeedBack = "Сообщение отправлено";
    } else {
        date = $('#messageSendingTime').val();
        msgFeedBack = "Отправка запланирована на " + date;
    }

    let wrap = {
        type: messageType,
        text: text,
        date: date,
        recipients: recipients,
        vkType: vkPage = $("#vkTokenSelect").val(),
        selectValueAppNumberToken: selectAppTokenNumber,
        listMailing: listMailing
    };

    let label = $("#message");
    label.prop('innerHTML', "Идет отправка сообщения")
    label.css('color', 'blue');

    $.ajax({
        type: "POST",
        url: URL_POST_DATA,
        data: wrap,
        success: function (data, textStatus, xhr) {
            if (xhr.status === 204) {
                setErrorMessage("Ошибка отправки сообщения! Файл вложения не загружен на сервер.", 'red');
            } else {
                setErrorMessage(msgFeedBack, 'green')
            }
        },
        error: function (xhr) {
            if (xhr.status === 500) {
                setErrorMessage("Не удалось записать текст сообщения в БД:\n " + xhr.responseText, "red");
            }
            if (xhr.status === 401) {
                setErrorMessage("Для отправки сообщения необходимо авторизоваться в системе (перезайти в систему)\n " + xhr.responseText, "red");
            }
        }
    });
}

function setMailingLists() {
    let selector = $('#listMailingSelect');
    $.ajax({
        url: '/get/listMailing/' + messageType,
        type: 'GET',
        async: true,
        success: function (data) {
            selector.empty().append('<option value="null">Выберите список рассылки</option>');
            for (var i = 0; i < data.length; i++) {
                selector.append('<option value="' + data[i].id+'">'+data[i].listName+'</option>');
            }
        }
    });
}

function fillStatuses() {
    let updateSelector = $('#update-list-statuses');
    let createSelector = $('#list-statuses');
    $.ajax({
        url: '/rest/status/dto/for-mailing',
        type: 'GET',
        async: true,
        success: function (data) {
            updateSelector.empty();
            createSelector.empty();
            for (var i = 0; i < data.length; i++) {
                createSelector.append('<label class="checkbox-inline"><input class="status-checkboxes" ' +
                    'type="checkbox" id="status_checkbox_' + data[i].id + '" value="' + data[i].id + '"/>' +
                    data[i].name + '</label>');
                updateSelector.append('<label class="checkbox-inline"><input class="update-status-checkboxes" ' +
                    'type="checkbox" id="update_status_checkbox_' + data[i].id + '" value="' + data[i].id + '"/>' +
                    data[i].name + '</label>');
            }
        }
    });
}

/**
 * Функция, переключающая состояние кнопок режима рассылки и перенастраивающая интерфейс редактора.
 * Для функции отправки сообщений посредством СМС, в CKEditor отключаются все плагины форматирования
 * текста, так же, как и для отправки сообщений в ВК или Slack. Плюс меняется тип сообщения, messageType.
 */
$(document).ready(function () {
    fillStatuses();
    setMailingLists();
    $("#vkTokenSelect").hide();
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $('#selectAppToken').hide();
    $("#message-type-button-group > button").click(function () {
        if (messageType === $(this).attr("id")) {
            return;
        }
        messageType = $(this).attr("id");

        setMailingLists();

        switch (messageType) {
            case 'sms':
                $("#addresses-label").text(SEND_SMSS);
                $("#mailing-list-type > option[value='4']").prop('selected', true);
                showHideButtonsOnAddMailingList(messageType);
                showHideButtonsOnEditMailingList(messageType);
                break;
            case 'vk':
                $("#addresses-label").text(SEND_TO_VK);
                $("#mailing-list-type > option[value='2']").prop('selected', true);
                showHideButtonsOnAddMailingList(messageType);
                showHideButtonsOnEditMailingList(messageType);
                break;
            case 'slack':
                $("#addresses-label").text(SEND_TO_SLACK);
                $("#mailing-list-type > option[value='3']").prop('selected', true);
                showHideButtonsOnAddMailingList(messageType);
                showHideButtonsOnEditMailingList(messageType);
                break;
            case 'email':
                $("#addresses-label").text(SEND_EMAILS);
                $("#mailing-list-type > option[value='1']").prop('selected', true);
                showHideButtonsOnAddMailingList(messageType);
                showHideButtonsOnEditMailingList(messageType);
                break;
        }

        if (messageType !== 'email') {
            ckeditorRemoveAllToolbars();
        } else {
            ckeditorAddAllToolbars();
        }

        $("#message-type-button-group > button").each(function (index, element) {
            if ($(element).hasClass(BUTTON_INFO_CLASS)) {
                $(element).removeClass(BUTTON_INFO_CLASS);
                $(element).addClass(BUTTON_SECONDARY_CLASS);
            }
        });
        $(this).addClass(BUTTON_INFO_CLASS);
    });

    (function( $ ){
        $.fn.fillWithEmails = function() {
            var field = this;
            $.ajax({
                url: '/slack/get/emails',
                contentType: "text/plain;charset=UTF-8",
                type: 'GET',
                dataType: 'text',
                async: true,
                success: function (data) {
                    field.val(data);
                }
            });
        };
    })( jQuery );

    (function( $ ){
        $.fn.fillWithIds = function() {
            var field = this;
            $.ajax({
                url: '/slack/get/ids/all',
                contentType: "text/plain;charset=UTF-8",
                type: 'GET',
                dataType: 'text',
                async: true,
                success: function (data) {
                    field.val(data);
                }
            });
        };
    })( jQuery );

    (function( $ ){
        $.fn.fillWithStudentsIds = function(selector) {
            var field = this;
            let selectedStatuses = [];
            $('.' + selector + ':checked').each(function(){
                selectedStatuses.push($(this).val());
            });
            let wrap = {
                "statuses" : selectedStatuses
            };
            $.ajax({
                url: '/slack/get/ids/students',
                contentType: "text/plain;charset=UTF-8",
                type: 'GET',
                dataType: 'text',
                data: wrap,
                traditional: true,
                async: true,
                success: function (data) {
                    field.val(data);
                }
            });
        };
    })( jQuery );

    (function( $ ){
        $.fn.fillRecipientListFromStatuses = function(selector, url) {
            var field = this;
            let selectedStatuses = [];
            $('.' + selector + ':checked').each(function(){
                selectedStatuses.push($(this).val());
            });
            let wrap = {
                'statuses' : selectedStatuses
            };
            $.ajax({
                url: url,
                type: 'POST',
                data: wrap,
                traditional: true,
                async: true,
                success: function (data) {
                    field.val(data.join('\n'));
                }
            });
        };
    })( jQuery );

    $("#slackImportButton").click(function () {
        $("#listRecipients").fillWithEmails();
    });

    $("#slackIdAllImportButton").click(function () {
        $("#listRecipients").fillWithIds();
    });

    $("#slackIdStudentsImportButton").click(function () {
        $("#listRecipients").fillWithStudentsIds('status-checkboxes');
    });

    $("#slackUpdateImportButton").click(function () {
        $("#editListRecipients").fillWithEmails();
    });

    $("#slackUpdateIdAllImportButton").click(function () {
        $("#editListRecipients").fillWithIds();
    });

    $("#slackUpdateIdStudentsImportButton").click(function () {
        $("#editListRecipients").fillWithStudentsIds('update-status-checkboxes');
    });

    $("#fromStatusesImportButton").click(function () {
        let messageType = $("#mailing-list-type option:selected").text();
        switch (messageType) {
            case 'sms':
                $("#listRecipients").fillRecipientListFromStatuses('status-checkboxes', URL_PHONES_FROM_STATUSES);
                break;
            case 'vk':
                break;
            case 'slack':
                break;
            case 'email':
                $("#listRecipients").fillRecipientListFromStatuses('status-checkboxes', URL_EMAILS_FROM_STATUSES);
                break;
        }
    });

    $("#fromFiltersImportButton").click(function () {

        if (!$("#filter-mailing-list-countries").val() && !$("#filter-mailing-list-cities").val()
            && !$("#filter-mailing-list-age-min").val() && !$("#filter-mailing-list-age-max").val()
        && !($("#filter-mailing-list-male").is(':checked') ^ $("#filter-mailing-list-female").is(':checked'))){
            alert("Для фильтра необходимо указать условие или несколько! \n Уточните параметры фильтра");
        }
        else {
            var field =  $("#listRecipients");
            let country = $("#filter-mailing-list-countries").val();
            let city = $("#filter-mailing-list-cities").val();

            let age_min = $("#filter-mailing-list-age-min").val() ? $("#filter-mailing-list-age-min").val() : '-1';
            let age_max = $("#filter-mailing-list-age-max").val() ? $("#filter-mailing-list-age-max").val() : '-1';
            let sex = 'ANY';

            if ($("#filter-mailing-list-male").is(':checked') ^ $("#filter-mailing-list-female").is(':checked')) {
                if ($("#filter-mailing-list-male").is(':checked')) {
                    sex = 'MALE';
                } else {
                    sex = 'FEMALE';
                }
            }

            var request = $.ajax({
                url: "/rest/client/emails/filters",
                type: "POST",
                data: {
                    country: country,
                    city: city,
                    age_min: age_min,
                    age_max: age_max,
                    sex: sex
                },
                traditional: true,
                async: true
            });
            request.done(function (data) {
                if (data.length === 0 ) {
                    alert("Данных не найдено! \n Уточните параметры фильтра");
                }
                else{
                    let listEmails = field.val();
                    field.val('');
                    $.each(data, function (i, item) {
                        field.val(field.val() + item  + '\n');
                    });
                }
            });
            request.fail(function (jqXHR, textStatus) {
                alert("при запросе данных произошла ошибка\n Уточните данные запроса ");
            });
        }


    });

    $("#updateFromStatusesImportButton").click(function () {
        let messageType = $("#edit-mailing-list-type option:selected").text();
        switch (messageType) {
            case 'sms':
                $("#editListRecipients").fillRecipientListFromStatuses('update-status-checkboxes', URL_PHONES_FROM_STATUSES);
                break;
            case 'vk':
                break;
            case 'slack':
                break;
            case 'email':
                $("#editListRecipients").fillRecipientListFromStatuses('update-status-checkboxes', URL_EMAILS_FROM_STATUSES);
                break;
        }
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

    $('#historyMailingTable').delegate('.recipient-modal', 'click', function() {
        $('#recipientModal').modal('show');
        var id = $(this).closest('tr').children('td:first').text();
        $.ajax({
            type: "POST",
            url: "/get/client-data",
            data: {
                mailId: id
            },
            success: function (data) {
                $("#recipientBodyMailing").empty();
                for (var j = 0; j < data.length; j++) {
                    $("#recipientBodyMailing").append("<tr> \
                            <td>" + data[j].info + "</td> \
                        </tr>");
                }
            }
        });
    });

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
    $("#vkTokenSelect").hide();
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $('#selectAppToken').hide();
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
        "startDate": startDate
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
    $("#vkTokenSelect").hide();
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $('#selectAppToken').hide();
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
    $('#selectAppToken').hide();
    CKEDITOR.addCss('.cke_editable p { margin: 0 !important; }');
    let rep = CKEDITOR.replace(EDITOR, {
        customConfig: '/ckeditor/add-all-toolbars.js',
        filebrowserImageUploadUrl: '/image/upload'

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
    $("#imgSelectBtn").show();
    $("#vkTokenSelect").hide();
    $('#selectAppToken').hide();
}

function ckeditorRemoveAllToolbars() {
    CKEDITOR.instances[EDITOR].destroy(true);
    CKEDITOR.replace(EDITOR, {
        customConfig: '/ckeditor/remove-all-toolbars.js'
    });
    $("#imgSelectBtn").hide();
    if(messageType === "vk") {
        $("#vkTokenSelect").show();
    } else {
        $("#vkTokenSelect").hide();
    }

    if(messageType === "slack") {
        $("#selectAppToken").show();
    } else {
        $("#selectAppToken").hide();
    }
}

/**
 * Визуализация событий dragover, dragleave, dragend, drop поля адресов
 */
$(document).ready(function () {
    $("#vkTokenSelect").hide();
    $("#falseHistory").hide();
    $("#noSendButton").hide();
    $("#selectAppToken").hide();
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
    let url = '/rest/admin/savePicture?templateID='+templateID;
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
        error: function (data) {
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
                if (data[i].notSendId.length > 0 && messageType === "vk") {
                    $("#noSendButton").show();
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
                            <td class='history-table-td-id'>" + data[i].id + " </td> \
                            <td class='history-table-td-date'>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td class='history-table-td-text'>" + data[i].text + "</td> \
                            <td class='history-table-td-type'>" + data[i].type + "</td> \
                            <td class='history-table-td-buttons'><button class='btn btn-success recipient-modal'>Получатели</button> \
                            <br/> \
                            <button id ='getNoSend' data-toggle='modal' data-target='#noSendModal' class='btn btn-danger'>Не доставл.</button></td> \
                        </tr>");
                } else {
                    $("#historyBodyMailing").append("<tr> \
                            <td class='history-table-td-id'>" + data[i].id + " </td> \
                            <td class='history-table-td-date'>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td class='history-table-td-text'>" + data[i].text + "</td> \
                            <td class='history-table-td-type'>" + data[i].type + "</td> \
                            <td class='history-table-td-buttons'><button class='btn btn-success recipient-modal'>Получатели</button></td> \
                        </tr>");
                }

            }

        }
    });
}

$('#noSendModal').on('hidden.bs.modal', function () {
    $('#historyModal').css('overflow-y', 'auto');
});

$('#recipientModal').on('hidden.bs.modal', function () {
    $('#historyModal').css('overflow-y', 'auto');
});

function removeHistory() {
    $('#managerSelect').val('');
    $('#historyBodyMailing').empty();
    $('#timeSelect').empty();
    $('#recipientBodyMailing').empty();
};

function addToListMailing() {
    let recipients = $('#listRecipients').val();
    let listName = $('#listName').val();
    let typeId = $('#mailing-list-type').val();
    if (listName !== '' && typeId !== null && typeId !== '') {
        let wrap = {
            recipients: recipients,
            typeId: typeId,
            listName: listName
        };
        $.ajax({
            type: "POST",
            url: '/list-mailing',
            data: wrap,
            success: function () {
                setMailingLists();
                $("#listMailingModal").modal('hide');
                $('#listRecipients').val("");
                $('#listName').val("");
                $('#list-statuses .status-checkboxes:checked').each(function(){
                    $(this).prop('checked', false);
                });
            }
        });
    } else {
       $('#errorListName').html('<p style="color: red">Введите название списка</p>');
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
                            <td><button class='btn btn-success recipient-modal'>Показать всех получателей</button> \
                            <br/> \
                            <button id ='getNoSend' data-toggle='modal' data-target='#noSendModal' class='btn btn-danger'>Недоставлено</button></td> \
                        </tr>");
                } else {
                    $("#historyBodyMailing").append("<tr> \
                            <td>" + data[i].id + " </td> \
                            <td>" + dt + '.' + month + '.' + year + " <br/> " + hour + ':' + minutes + " </td> \
                            <td>" + data[i].text + "</td> \
                            <td>" + data[i].type + "</td> \
                            <td><button class='btn btn-success recipient-modal'>Показать всех получателей</button></td> \
                        </tr>");
                }

                }
            }
    });

}
function showListMailing() {
    // Странный кусок кода - предназначение не понятно!
    /*if (messageType !== "email") {
        x = CKEDITOR.instances.editor.document.getBody().getText();
    } else {
        x = "";
    }*/
    let listGroupName = $('#listMailingSelect').val();
    if(listGroupName === "null") {
        alert("Выберите список рассылки!");
        return;
    } else {
        $.ajax({
            url: '/get/listMailing',
            type: 'POST',
            data: {
                listGroupId: listGroupName
            },
            success: function (data) {
                for (var i = 0; i < data.recipients.length; i++) {
                    $("#addresses-area").each(function () {
                        $(this).val(data.recipients.join("\n"));
                    });
                }
            }
        });
    }
}

function clearListRecipients(selector) {
    $("#" + selector).val('');
}

function openEditShowListMailing() {

    listGroupName = $('#listMailingSelect').val();

    if(listGroupName === "null") {
        alert("Выберите список рассылки!");
        return;
    } else {
        $.ajax({
            url: '/get/listMailing',
            type: 'POST',
            data: {
                listGroupId: listGroupName
            },
            success: function (data) {
                $("#editListName").val(data.listName);
                $("#editListRecipients").each(function () {
                    $(this).val(data.recipients.join("\n"));
                });
                $("#edit-mailing-list-type").val(data.type.id);
            }
        });
        $("#editListMailingModal").modal('show');
    }
}

function editListMailing() {
    let listName = $("#listMailingSelect").val();
    let editListName = $("#editListName").val();
    let editListRecipients = $("#editListRecipients").val();
    let editTypeId = $("#edit-mailing-list-type").val();
    $.ajax({
        url: '/edit/list-mailing',
        type: 'POST',
        data: {
            listId: listName,
            editListName: editListName,
            editRecipients: editListRecipients,
            typeId: editTypeId
        },
        success: function () {
            setMailingLists();
            $("#editListMailingModal").modal('hide');
    }
    });
}

function deleteListMailing() {

    var listName = $("#listMailingSelect").val();

    $.ajax({
        url: '/remove/list-mailing',
        type: 'POST',
        data: {
            listId: listName
        },
        success: function () {
            setMailingLists();
            $("#editListMailingModal").modal('hide');
        }
    });

}

function removeRecipient() {
    $('#recipientBodyMailing').empty();
}

function hideNoSend() {
    $("#noSendButton").hide();
    $("#noSend-area").val("");
}

$("#mailing-list-type").on("change", function () {
    let messageType = $("#mailing-list-type option:selected").text();
    showHideButtonsOnAddMailingList(messageType);
});

$("#edit-mailing-list-type").on("change", function () {
    let messageType = $("#edit-mailing-list-type option:selected").text();
    showHideButtonsOnEditMailingList(messageType);
});

// Фукнкции показывают / скрывают кнопки в модальном окне согласно типу списка рассылки
function showHideButtonsOnAddMailingList(messageType) {
    switch (messageType) {
        case 'sms':
            $("#slackImportButton").hide();
            $("#slackIdAllImportButton").hide();
            $("#slackIdStudentsImportButton").hide();
            $("#fromStatusesImportButton").show();
            break;
        case 'vk':
            $("#slackImportButton").hide();
            $("#slackIdAllImportButton").hide();
            $("#slackIdStudentsImportButton").hide();
            $("#fromStatusesImportButton").show();
            break;
        case 'slack':
            $("#slackImportButton").hide();
            $("#slackIdAllImportButton").show();
            $("#slackIdStudentsImportButton").show();
            $("#fromStatusesImportButton").hide();
            break;
        case 'email':
            $("#slackImportButton").show();
            $("#slackIdAllImportButton").hide();
            $("#slackIdStudentsImportButton").hide();
            $("#fromStatusesImportButton").show();
            break;
    }
}

function showHideButtonsOnEditMailingList(messageType) {
    switch (messageType) {
        case 'sms':
            $("#slackUpdateImportButton").hide();
            $("#slackUpdateIdAllImportButton").hide();
            $("#slackUpdateIdStudentsImportButton").hide();
            $("#updateFromStatusesImportButton").show();
            break;
        case 'vk':
            $("#slackUpdateImportButton").hide();
            $("#slackUpdateIdAllImportButton").hide();
            $("#slackUpdateIdStudentsImportButton").hide();
            $("#updateFromStatusesImportButton").show();
            break;
        case 'slack':
            $("#slackUpdateImportButton").hide();
            $("#slackUpdateIdAllImportButton").show();
            $("#slackUpdateIdStudentsImportButton").show();
            $("#updateFromStatusesImportButton").hide();
            break;
        case 'email':
            $("#slackUpdateImportButton").show();
            $("#slackUpdateIdAllImportButton").hide();
            $("#slackUpdateIdStudentsImportButton").hide();
            $("#updateFromStatusesImportButton").show();
            break;
    }
}