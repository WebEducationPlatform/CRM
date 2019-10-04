$(function () {
    $('.back-btn').click(function () {
        history.back();
        return false;
    });
});

$(document).ready(function () {
    selectOptions($("#edit-client-state"));
    selectOptions($("#edit-client-sex"));

    $('#edit-client-country').ready(function () {
        // получаем список стран из вк-апи
        var countryNameInput = $('#edit-client-country').val();
        var url = '/rest/vkontakte/vk-countries';
        var countries;
        var countryArray = [];
        $.ajax({
            type: 'get',
            url: url,
            dataType: 'json',
            success: function (result) {
                countries = result;
                countryArray = $(countries).attr('response');
                // проверяем, есть ли страна в списке стран, если да - то берем id страны для получения спика городов этой страны
                $.each(countryArray, function () {
                    if (countryNameInput == $(this).attr('value')) {
                        var array = document.getElementById('edit-client-country');
                        var countId = $(this).attr('cid');
                        array.setAttribute('cid', countId);
                        // получаем список городов по id страны и запускаем автодополнение в поле "Город"
                        doCities(countId);
                    }
                });

// автодоплнение из списка всех стран в поле "Страна"
                $('#edit-client-country').autocomplete({
                    source: countryArray,
                    select: function( event , ui ) {
                        console.debug( 'Selected country: ' + ui.item.label + ' id: ' + + ui.item.cid );
                        var countId = ui.item.cid;
                        // как только страна выбрана, получаем список городов по id этой страны и делаем автодополнение этими городами поля "Город"
                        doCities(countId);
                    }
                });
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });
});


function doCities(cid) {
    $('#edit-client-city').autocomplete({
        source: function (request, response) {
            $.ajax({
                type: 'get',
                url: "/rest/vkontakte/vk-cities",
                dataType: "json",
                data: {
                    q: request.term,
                    country: cid
                },
                success: function (data) {
                    response($(data).attr('response'));
                },
                error: function (error) {
                    console.log(error.responseText);
                }
            });
        },
        minLength: 2,
        delay: 500,
        select: function (event, ui) {
            console.debug('Selected city: ' + ui.item.label + ' id: ' + +ui.item.cid);
        }
    });
}

function GetStringListEmails(Emails, selectItem) {
    if (selectItem.length > 0) {
        let obj = {};
        selectItem.each(function (i, item) {
            obj = $(item).val();
            Emails.push(obj);
        })
    }
}

function GetStringList(Phones, selectItem) {
    if (selectItem.length > 0) {
        let obj = {};
        selectItem.each(function (i, item) {
            obj = $(item).val();
            Phones.push(obj);
        })
    }
}

function changeClient(id) {
    if ($("#saveChanges")[0].className === "btn btn-primary disabled") {
        return;
    }
    var SN = [];
    var $th = $('#SocialNetworks').find('th');
    try {
        $('#SocialNetworks').find('tbody tr').each(function (i, tr) {
            var obj = {}, $tds = $(tr).find('td');
            $th.each(function (index, th) {
                if ($(th)[0].innerText !== "id" && $tds.eq(index).text() === "") {
                    var current = document.getElementById("message");
                    current.textContent = "Заполните пустые поля в таблице 'Cоциальные сети'";
                    current.style.color = "red";
                    throw new Error("Пустые поля в таблице 'Cоциальные сети'");
                }
                if ($(th).attr('abbr') !== "") {
                    if (typeof $tds.eq(index).children().val() === 'undefined') {
                        var obj1 = {};
                        if ($tds.eq(index)[0].id === "edit-client-SN_type") {
                            obj1["name"] = $tds.eq(index)[0].innerText;
                            obj[$(th).attr('abbr')] = obj1;
                        } else {
                            obj[$(th).attr('abbr')] = $tds.eq(index).text();
                        }
                    } else {
                        obj[$(th).attr('abbr')] = $tds.eq(index).children().val();
                    }
                }
            });
            SN.push(obj);
        });
    } catch (e) {
        return;
    }
    var Job = [];
    $th = $('#Job').find('th');
    try {
        $('#Job').find('tbody tr').each(function (i, tr) {
            var obj = {}, $tds = $(tr).find('td');
            $th.each(function (index, th) {
                if ($(th)[0].innerText !== "id" && $tds.eq(index).text() === "") {
                    var current = document.getElementById("message");
                    current.textContent = "Заполните пустые поля в таблице 'Работа'";
                    current.style.color = "red";
                    throw new Error("Пустые поля в таблице 'Работа'")
                }
                if ($(th).attr('abbr') !== "") {
                    obj[$(th).attr('abbr')] = $tds.eq(index).text();
                }
            });
            Job.push(obj);
        });
    } catch (e) {
        return;
    }

    var Emails = [];
    GetStringListEmails(Emails,$('#userEmailList option'));


    var Phones = [];
    GetStringList(Phones,$('#userPhoneList option'));


    let url = '/rest/admin/client/update';
    let wrap = {
        id: id,
        name: $('#edit-client-first-name').val(),
        lastName: $('#edit-client-last-name').val(),
        middleName: $('#edit-client-middle-name').val(),
        birthDate: $('#edit-client-birthday').val(),
        sex: $('#edit-client-sex').find('option:selected').text(),
        state: $('#edit-client-state').val(),
        country: $('#edit-client-country').val(),
        city: $('#edit-client-city').val(),
        skype: $('#edit-client-skype').val(),
        socialProfiles: SN,
        status: {},
        jobs: Job,
        clientEmails: Emails,
        clientPhones: Phones
    };
    var current = document.getElementById("message");
    let data = JSON.stringify(wrap);
    console.log('wrap = ' + data);
    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: data,
        beforeSend: function () {
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function () {
            current.textContent = "Сохранено";
            current.style.color = "limegreen";
        },
        error: function (e) {
            console.log(e.responseText);
            current.textContent = "Ошибка сохранения. " + e.responseText;
            current.style.color = "red";
        }
    });
}

function deleteClient(clientId) {
    const url = '/rest/admin/client/remove';
    const infoMsg = document.getElementById("message");
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: {clientId: clientId},
        beforeSend: function () {
            infoMsg.style.color = "darkorange";
            infoMsg.textContent = "Загрузка...";

        },
        success: function () {
            infoMsg.textContent = "Удалено";
            infoMsg.style.color = "limegreen";
            document.location.href = '/client';
        },
        error: function (e) {
            console.log(e.responseText);
            infoMsg.textContent = "Ошибка сохранения. " + e.responseText;
            infoMsg.style.color = "red";
        }
    });
}

function disableInputE() {
    var disMas = [69, 187, 189, 109];
    if (disMas.indexOf(event.keyCode) !== -1) {
        event.preventDefault()
    }
}

$(document).on('click', 'td', (function (e) {
    if (e.target.localName !== "td" || e.target.firstElementChild !== null || (e.target.offsetParent.id !== "SocialNetworks" && e.target.offsetParent.id !== "Job" && e.target.offsetParent.id !== "AdditionalEmails" && e.target.offsetParent.id !== "AdditionalPhones") || $('#edit-client-first-name')[0].disabled) {
        return;
    }
    var t = e.target || e.srcElement;
    var elm_name = t.tagName.toLowerCase();
    if (elm_name === 'input') {
        return false;
    }
    var val = $(t).html();
    var code;
    if (e.target.cellIndex === 2 && e.target.offsetParent.id === "SocialNetworks") {
        code = '<select id="edit" value = "' + val + '" class=\"form-control\">' + SNs + '</select>'
    } else {
        code = '<input type="text" id="edit" value="' + val + '" />';
    }
    $(t).empty().append(code);
    var newEditElement = $("#edit");
    selectOptions(newEditElement);
    newEditElement.focus();
    newEditElement.blur(function () {
        var val = $(this).val();
        $(this).parent().empty().html(val);
    });
}));

$(window).keydown(function (event) {
    if (event.target.id === "edit") {
        if (event.keyCode === 13) {
            $('#edit').blur();
        }
    }
});

function deleteSocial(element) {
    $(element).parent().parent().remove();
}

function deleteJob(element) {
    $(element).parent().parent().remove();
}

function deleteEmail(element) {
    $(element).parent().parent().remove();
}

function deletePhone(element) {
    $(element).parent().parent().remove();
}


var SNs = "";

function addNewSN() {
    if (SNs.length === 0) {
        console.log("Массив socialNetworkTypes пуст!");
        return;
    }
    var size = ($("#SN-table-body")[0]).rows.length;
    $("#SN-table-body").append("<tr><td hidden=\"hidden\"></td><td></td><td></td><td><button type=\"button\" onclick=\"deleteSocial(this)\" class=\"glyphicon glyphicon-remove\"></button></td></tr>")
}

function addNewJob() {
    var size = ($("#job-table-body")[0]).rows.length;
    $("#job-table-body").append("<tr><td hidden=\"hidden\"></td><td></td><td></td><td><button type=\"button\" onclick=\"deleteJob(this)\" class=\"glyphicon glyphicon-remove\"></button></td></tr>")
}

//Добавляем Email
function addNewEmailExtra() {
    if ($('#newEmail').val().length > 0) {
        let addOpt = '<option value="' + $('#newEmail').val() + '"> ' + $('#newEmail').val() + '</option>';
        $('#userEmailList').append(addOpt);
        $('#newEmail').val("");
    }
}

function addNewPhoneExtra() {
    if ($('#newPhoneNumber').val().length > 0) {
        let addOpt = '<option value="' + $('#newPhoneNumber').val()  + '"> ' + $('#newPhoneNumber').val() + '</option>';
        $('#userPhoneList').append(addOpt);
        $('#newPhoneNumber').val("");
    }
}

function removeSelectPhones(){
    $('#userPhoneList option:selected').remove();}

//Удаляем Email
function removeSelectEmails(){
    $('#userEmailList option:selected').remove();}

function setDefSelPhoneExtra(){
    let phoneList = $('#userPhoneList option:selected');
    if ( phoneList.length == 1) {
        let defPhone = phoneList.val();
        $('#defaultPhone').val(defPhone);
        //первый в списке будет по-умолчанию  - основным номером
        let listPhones = [];
        GetStringList(listPhones,$('#userPhoneList option'));
        let index_item = listPhones.indexOf(defPhone);
        if ( index_item !== -1){
            listPhones[index_item] = listPhones[0];
            listPhones[0] = defPhone;
        }
        $('#userPhoneList').empty();
        $.each(listPhones, function(key, value) {
            $('#userPhoneList').append('<option value="' + value + '">' + value + '</option>');
        });
    }
}

//Устанавливаем Email по умолчанию - для отправки писем
    function setDefEmailExtra(){
        let emailList = $('#userEmailList option:selected');
        if ( emailList.length == 1) {
            let defEmail = emailList.val();
            $('#defaultEmail').val(defEmail);
            //первый в списке будет по-умолчанию  - основным номером
            let listEmails = [];
            GetStringList(listEmails,$('#userEmailList option'));
            let index_item = listEmails.indexOf(defEmail);
            if ( index_item !== -1){
                listEmails[index_item] = listEmails[0];
                listEmails[0] = defEmail;
            }
            $('#userEmailList').empty();
            $.each(listEmails, function(key, value) {
                $('#userEmailList').append('<option value="' + value + '">' + value + '</option>');
            });
        }
    }

function revertUnable() {
    var column1 = $('#column1');
    column1.find('input').each(function () {
        if ($(this)[0].disabled === true) {
            $(this)[0].disabled = false;
            $('#editClientBtn').attr("class", "btn btn-secondary")[0].innerText = 'Заблокировать';
        } else {
            $(this)[0].disabled = true;
            $('#editClientBtn').attr("class", "btn btn-primary")[0].innerText = 'Редактировать';
        }
    });
    column1.find('select').each(function () {
        $(this)[0].disabled = $(this)[0].disabled !== true;
    });

    column1.find('button').each(function () {
        if ($(this).parent()[0].localName === 'td') {
            $(this)[0].disabled = $(this)[0].disabled !== true;
        }
    });
    $("#addNewSN")[0].disabled = $("#addNewSN")[0].disabled !== true;
    $("#addNewJob")[0].disabled = $("#addNewJob")[0].disabled !== true;
    $("#addNewEmail")[0].disabled = $("#addNewEmail")[0].disabled !== true;
    $("#addNewPhone")[0].disabled = $("#addNewPhone")[0].disabled !== true;
    $("#removePhones")[0].disabled = $("#removePhones")[0].disabled !== true;
    $("#setDefSelPhone")[0].disabled = $("#setDefSelPhone")[0].disabled !== true;
    $("#removeEmails")[0].disabled = $("#removeEmails")[0].disabled !== true;
    $("#setDefEmail")[0].disabled = $("#setDefEmail")[0].disabled !== true;
}

$(function () {
    $('#client-from').validator({
        disable: true
    })
});

var socialNetworkTypes = [];

$(document).ready(function () {

    var url = '/user/socialNetworkTypes';
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (res) {
            socialNetworkTypes = res;
            $.each(socialNetworkTypes, function (index, type) {
                SNs = SNs + "<option>" + type + "</option>"
            });
        },
        error: function (error) {
            console.log(error);
        }
    });
});

function selectOptions(element) {
    element.find("option").each(function () {
        var val = $(this).closest('select').attr('value');
        var inText = $(this).text();
        if (val === inText) {
            $(this).attr("selected", "selected");
        }
    });
}

$(function () {
    $("#edit-client-age").on('keyup', function (e) {
        var reg = new RegExp("^$|^[0-9]$|^[1-9][0-9]$|^1[0-1][1-9]$|^12[1-7]$");
        if (!reg.test($("#edit-client-age").val())) {
            $("#edit-client-age").siblings("div[class='help-block with-error']")[0].innerText = "Диапазон от 0 до 127";
            $("#saveChanges")[0].setAttribute("disabled", "disabled");
        } else {
            $("#edit-client-age").siblings("div[class='help-block with-error']")[0].innerText = "";
            $("#saveChanges")[0].removeAttribute("disabled");
        }
    });
});
