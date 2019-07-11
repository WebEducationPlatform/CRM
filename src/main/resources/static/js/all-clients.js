var data = {};
//Current clients page for pagination
let page = 1;
//объект статус-цвет, массив его ключей
let statuscol, arrKeys;
//массив статусов
let statuses = [];

var searchProcess = false;

//Получаем объект Статус - цвет, и массив ключей
$.get('/rest/properties', function getStatusesColor(projectProperties) {
    statuscol = JSON.parse(projectProperties.statusColor);
    if (statuscol) {
        arrKeys = Object.keys(JSON.parse(projectProperties.statusColor));
    }
});

//Получаем массив статусов
$.get('/rest/status', function getStatuses(studentStuses) {
    studentStuses.forEach(function (element, index, array) {
        statuses[index] = element.name;
    });

});

function clientModal (id) {
    changeUrl('/client/allClients', id);
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', id);
    currentModal.modal('show');
}


function clearClientsTable() {
    $("#table-body").remove();
    $("#thead-table-clients").after(
        '<tbody id="table-body">' +
        '    </tbody>'
    );
}

$('#filtration').click(function () {
    page = 1;
    data = {};
    var url = "../rest/client/filtration";
    let urlToGetClientsWithoutPagination = "../rest/client/filtrationWithoutPagination";

    if ($('#sex').val() !== "") {
        data['sex'] = $('#sex').val();
    }
    data['ageTo'] = $('#ageTo').val();
    data['ageFrom'] = $('#ageFrom').val();
    data['city'] = $('#city').val();
    data['country'] = $('#country').val();
    data['dateFrom'] = $('#dateFrom').val();
    data['dateTo'] = $('#dateTo').val();
    data['pageNumber'] = page;
    if ($('#status').val() !== "") {
        data['status'] = $('#status').val();
    }
    if ($('#ownerUser').val() !== "") {
        data['ownerUserId'] = $('#ownerUser').val();
    }
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        url: url,
        data: JSON.stringify(data),
        success: function (res) {
            $.ajax({
                type: 'POST',
                contentType: "application/json",
                dataType: 'json',
                url: urlToGetClientsWithoutPagination,
                data: JSON.stringify(data),
                success: function (res) {
                    drawNumberOfClients(res);
                    document.getElementById("divToFiltration").style.display = "block";
                }
            })
            var body = $("#table-body");
            clearClientsTable();
            drawClients(body, res);
            res.length;
        },
        error: function (error) {
            console.log(error);
        }
    })
});

$('#clientData').click(function (event) {
    event.preventDefault();
    var url = "../rest/client/createFile";
    var urlFiltration = "../rest/client/createFileFilter";
    let selected = [];
    $('#checkboxes input:checked').each(function () {
        selected.push($(this).attr('value'));
    });
    let filetype = $('#radiobuttons input:checked').val();
    let delimeter = $('#delimeter').val();
    let arr = {};
    arr['selected'] = selected;
    arr['filetype'] = filetype;
    arr['delimeter'] = delimeter;
    if (jQuery.isEmptyObject(data)) {
        $.ajax({
            type: 'POST',
            url: url,
            contentType: "application/json",
            data: JSON.stringify(arr),
            success: function () {
                window.location.replace("/rest/client/getClientsData")
            }
        });
    }
    if (!(jQuery.isEmptyObject(data))) {
        data['selectedCheckbox'] = selected;
        data['filetype'] = filetype;
        data['delimeter'] = delimeter;
        $.ajax({
            type: 'POST',
            url: urlFiltration,
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(data),
            success: function () {
                window.location.replace("/rest/client/getClientsData")
            }
        })
    }
});

let isAdmin;
$.get('/rest/client/getPrincipal', function (user) {
    $.each(user.role, function (i, v) {
        if (v.roleName === 'ADMIN' || v.roleName === 'OWNER' || v.roleName === 'HR') {
            isAdmin = true;
        }
    })
});

let table = $("#clients-table").find("tbody");

//Draw clients first page to the table
function drawDefaultClients() {
    $.get('/rest/client/pagination/new/first', {page: 0}, function upload(clients) {
        let body = $("#table-body");
        body.empty();
        drawClients(body, clients);
        page = 1;
    })
}

function drawNumberOfClients(count){
    var divToWrite = document.getElementById('divToFiltration'),
    textWriteTodiv='По вашему запросу найдено людей : ' + count.length;
    divToWrite.innerHTML = textWriteTodiv
}

//при закрытии фильтра отображаем дефолтный вывод таблицы
$("#open-filter").click(function () {
    document.getElementById("divToFiltration").style.display = "none";
    if ($("#filter").hasClass('in')) {
        drawDefaultClients();
    }
    document.getElementById("searchInput").value = "";
});

//Draw clients list to the table
function drawClients(table, res) {
    for (let i = 0; i < res.length; i++) {
        let socLink = '';
        for (let j = 0; j < res[i].socialProfiles.length; j++) {
            if (res[i].socialProfiles[j].socialNetworkType.name == 'vk' || res[i].socialProfiles[j].socialNetworkType.name == 'facebook') {
                if (res[i].socialProfiles[j].socialNetworkType.link == null) {
                    socLink += res[i].socialProfiles[j].socialId + '<br>';
                } else {
                    socLink += res[i].socialProfiles[j].socialNetworkType.link + res[i].socialProfiles[j].socialId + '<br>';
                }
            }
        }

        //Вывод даты регистрации всех клиентов по московскому времени в таблице всех клиентов
        var d = new Date(new Date(res[i].dateOfRegistration).toLocaleString('en-US', {timeZone: 'Europe/Moscow'}));
        var dateOfRegistration = ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." +
            d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);

        //Вывод даты последнего изменения клиентов по мск времени
        var lastHistory = new Date(res[i].history[0].date);
        var lastComment = null;
        var lastChange;

        $.ajax({
            type: 'GET',
            url: '/rest/comment/getComments/' + res[i].id,
            contentType: "application/json",
            dataType: 'json',
            async: false,
            success: function (comments) {
                if (comments.length > 0) {
                    lastComment = new Date(comments[comments.length - 1].dateFormat);
                } else {
                    lastComment = 'undefined';
                }
            }
        });

        if (lastComment != 'undefined' && lastHistory < lastComment) {
            lastChange = new Date(lastComment.toLocaleString('en-US', {timeZone: 'Europe/Moscow'}));
        } else {
            lastChange = new Date(lastHistory.toLocaleString('en-US', {timeZone: 'Europe/Moscow'}));
        }
        var dateOfLastChange = ("0" + lastChange.getDate()).slice(-2) + "." + ("0" + (lastChange.getMonth() + 1)).slice(-2) + "." +
            lastChange.getFullYear() + " " + ("0" + lastChange.getHours()).slice(-2) + ":" + ("0" + lastChange.getMinutes()).slice(-2);

        let email = res[i].email === null ? '' : res[i].email,
            phoneNumber = res[i].phoneNumber === null ? '' : res[i].phoneNumber,
            city = res[i].city === null ? '' : res[i].city,
            country = res[i].country === null ? '' : res[i].country,
            sex = res[i].sex === null ? '' : res[i].sex;

        let returnBtn = '';
        if (isAdmin) {
            if (res[i].status.invisible) {
                returnBtn =
                    '<div class="dropdown statuses-by-dropdown">' +
                    '<button type="button" class="btn btn-default" data-toggle="dropdown" data-client="' + res[i].id + '"> Вернуть </button>' +
                    '<ul class="dropdown-menu statuses-content"></ul>' +
                    '</div>'
            }

            if (res[i].hideCard) {
                returnBtn =
                    '<div class="button-return-from-postpone">' +
                    '<button type="button" id="return-from-postpone" class="btn btn-default from-postpone" data-client="' + res[i].id + '"> Вернуть </button>' +
                    '</div>'
            }
        }
        let bDate = res[i].birthDate;
        let birthDate;
        if (bDate === null) {
            birthDate = '-';
        } else {
            let bDateValues = bDate.split('-');
            birthDate = bDateValues[2] + '.' + bDateValues[1] + '.' + bDateValues[0];
        }
        let oUsers = res[i].ownerUser;
        let ownerUsers;
        if (oUsers === null) {
            ownerUsers = '';
        } else {
            ownerUsers = oUsers.fullName;
        }
        $("#table-body").append(
            '    <tr>' +
            '        <td>' + res[i].id + '</td>' +
            '        <td class="line-decoration"><a href="#" onclick="clientModal('+ res[i].id +')" >' + res[i].name + '</a></td>' +
            '        <td>' + res[i].lastName + '</td>' +
            '        <td>' + phoneNumber + '</td>' +
            '        <td>' + email + '</td>' +
            '        <td>' + socLink + '</td>' +
            '        <td>' + birthDate + ' </td>' +
            '        <td>' + sex + ' </td>' +
            '        <td>' + city + ' </td>' +
            '        <td>' + country + ' </td>' +
            '        <td>' + ownerUsers + '</td>' +
            '        <td class="colorTd" id="td_'+res[i].id+'">' + res[i].status.name + '</td>' +
            '        <td class="dateOfRegistration">' + dateOfRegistration + ' МСК' + ' </td>' +
            '        <td class="dateOfLastChange">' + dateOfLastChange + ' МСК' + ' </td>' +
            '        <td class="no-fix">' + returnBtn + ' </td>' +
            '    </tr>'
        );

        if (statuscol) {
            $('#clients-table tr:last').after(function () {
                var tds = $(this).find('td.colorTd');
                $(this).css("background-color", statuscol[tds.html().trim()]);
            })
        }
    }
}

//Search by keyword
$("#searchInput").keyup(function (e) {
    let body = $("#table-body");
    if (e.keyCode === 13 && searchProcess === false) {
        startAnimation();
        searchProcess = true;
        let search = this.value.toLowerCase();
        body.empty();
        if (search === "") {
            drawDefaultClients();
        } else {
            $.ajax({
                type: 'GET',
                url: "/rest/client/search",
                data: {search: search},
                success: function (response) {
                    stopAnimation();
                    searchProcess = false;
                    drawClients(body, response);
                }
            })
        }
    }
}).focus(function () {
    this.value = "";
    $(this).css({
        "color": "black"
    });
    $(this).unbind('focus');
}).css({
    "color": "#C0C0C0"
});

$(document).ready(function () {
    stopAnimation();
    let win = $(window);
    let body = $("#table-body");
    win.scroll(function () {
        if (($(document).height() - win.height() === Math.ceil(win.scrollTop())) && ($("#searchInput").val() === "")) {
            //пагинация при фильтрации
            if ($("#filter").hasClass('in')) {
                data['pageNumber']++;
                //если есть сортировка
                if (body.hasClass('name') ||
                    body.hasClass('lastName') ||
                    body.hasClass('phoneNumber') ||
                    body.hasClass('email') ||
                    body.hasClass('city') ||
                    body.hasClass('country') ||
                    body.hasClass('ownerUser') ||
                    body.hasClass('status') ||
                    body.hasClass('dateOfRegistration') ||
                    body.hasClass('dateOfLastChange')) {
                    var url = "/rest/client/filtration/sort?columnName=" + name + "&sortType=" + sort_type_asc;

                    $.ajax({
                        type: 'POST',
                        contentType: "application/json",
                        dataType: 'json',
                        url: url,
                        data: JSON.stringify(data),
                        success: function (clients) {
                            sort_type_asc = !sort_type_asc;
                            body.empty();
                            drawClients(body, clients);
                        },
                        error: function (error) {
                            console.log(error);
                        }
                    })
                } else {
                    var url = "../rest/client/filtration";
                    $.ajax({
                        type: 'POST',
                        contentType: "application/json",
                        dataType: 'json',
                        url: url,
                        data: JSON.stringify(data),
                        success: function (clients) {
                            drawClients(body, clients);
                        }
                    });
                }
            } // пагинация при сортировке
            else if (body.hasClass('name') ||
                body.hasClass('lastName') ||
                body.hasClass('phoneNumber') ||
                body.hasClass('email') ||
                body.hasClass('city') ||
                body.hasClass('country') ||
                body.hasClass('ownerUser') ||
                body.hasClass('status') ||
                body.hasClass('dateOfRegistration') ||
                body.hasClass('dateOfLastChange')) {
                $.get('/rest/client/sort',
                    {page: page, columnName: document.getElementById('table-body').className, sortType: !sort_type_asc},
                    function upload(clients) {
                        drawClients(body, clients, page);
                        page++;
                    });
            }
            //пагинация при обычном просмотре страницы
            else {
                $.get('/rest/client/pagination/new/first', {page: page}, function upload(clients) {
                    drawClients(body, clients, page);
                    page++;
                });
            }
        }
    });
});

//Clearable search functions
function tog(v) {
    return v ? 'addClass' : 'removeClass';
}

//Clearable search functions
$(document).on('input', '.clearable', function () {
    $(this)[tog(this.value)]('x');
}).on('mousemove', '.x', function (e) {
    $(this)[tog(this.offsetWidth - 18 < e.clientX - this.getBoundingClientRect().left)]('onX');
}).on('touchstart click', '.onX', function (ev) {
    ev.preventDefault();
    $(this).removeClass('x onX').val('').change();
    drawDefaultClients();
});

/*
*Отрисовываем модальное окно. Инпуты со значением цвета берём из объекта statusCol , если объект еще не заполнен
*то формируем инпуты с цветами по умолчанию
*/
$('.selectColorBtn').click(function () {
    let currentModal = $('#changeColorByStatusModal');
    currentModal.data('statuses', statuses);
    currentModal.modal('show');

    let div = document.querySelector(".colorChoose");
    div.innerHTML = "";

    if (statuscol) {
        for (let i = 0; i < statuses.length; i++) {
            for (var property in statuscol) {
                if (statuses[i] === property) {
                    var node = document.createElement('div');
                    node.className = "input-group colorpicker-component";
                    node.innerHTML = '<label style="margin-right:0.5em;width:10em;float:left;text-align:left;display:block;" for="' + statuses[i] + '">' + statuses[i] + '</label>' +
                        '<span style="width: 30%" class="input-group-addon"><i></i></span>' +
                        '<input type="text" class="form-control" ' +
                        'id="' + statuses[i] + '" value = "' + statuscol[statuses[i]] +
                        '" name = "' + statuses[i] + '">';
                    div.appendChild(node);
                }
                $(function () {
                    $('.colorpicker-component').colorpicker();
                });
            }
        }
    }
    else {
        for (let i = 0; i < statuses.length; i++) {
            var node = document.createElement('div');
            node.className = "input-group colorpicker-component";
            node.innerHTML = '<label style="margin-right:0.5em;width:10em;float:left;text-align:left;display:block;" for="' + statuses[i] + '">' + statuses[i] + '</label>' +
                '<span style="width: 30%" class="input-group-addon"><i></i></span>' +
                '<input type="text" class="form-control" ' +
                'id="' + statuses[i] + '" value = "#ffff" name = "' + statuses[i] + '">';
            div.appendChild(node);
        }
        $(function () {
            $('.colorpicker-component').colorpicker();
        });
    }
});

/*
Отправка данных с выбранными цветами, формирование json
 */
$(document).on('submit','form.color_form',function(e){
    e.preventDefault();
    let url = '/rest/properties/saveColorByStatus';
    let tmpData = {};

    for(let i = 0; i < statuses.length; i++) {
        tmpData[statuses[i]] = document.getElementById(statuses[i]).value;
    }

    let data = {
        colors : JSON.stringify(tmpData)
    };

    $.ajax({
        type: "POST",
        url: url,
        data: data,
        success: function () {
            location.reload();
        },
        error: function (e) {
            console.log(e.responseText);
        }
    });
});

/*
По загрузке страницы, пробегаем по таблице, ищем td с цветом, сопоставляем
 со статусом из объекта statusCol , красим tr
 */
$(document).ready(function () {
    $.get('/rest/properties', function upload(projectProperties) {
        var statuscol = JSON.parse(projectProperties.statusColor);
        if (statuscol) {
            var arrKeys = Object.keys(JSON.parse(projectProperties.statusColor));

            $('#clients-table tr').each(function () {
                var tds = $(this).find('td.colorTd');
                $.each(arrKeys, function (key, value) {

                    if (value === tds.html()) {
                        tds.closest('tr').css("background-color", statuscol[value]);
                    }
                });
            });
        }
    })
});

function massClientInputSend() {
    let fioList = $('#listFio').val();
    let emailList = $('#listEmail').val();
    let trialDateList = $('#trialDate').val();
    let nextPaymentList = $('#nextPayment').val();
    let priceList = $('#price').val();
    let paymentSumList = $('#paymentSum').val();
    let studentStatus = $('#studentStatus').val();
    let url = "../rest/client/massInputSend";

    if(fioList&&emailList) {
        let wrap = {
            fioList: fioList,
            emailList: emailList,
            trialDateList: trialDateList,
            nextPaymentList: nextPaymentList,
            priceList: priceList,
            paymentSumList: paymentSumList,
            studentStatus: studentStatus
        };

        $.ajax({
            type: "POST",
            url: url,
            data: wrap,
            success: function () {
                location.reload();
            }
        });
    } else {
        console.log("Ошибка при сохранении пользователей");
    }
}

let sort_type_asc = true; // asc - от а до я, 10 до 0 Date последнее 2019-2000
function sort_table(name) {
    page = 1;
    var table_body = $('#table-body');
    table_body.removeClass();
    table_body.addClass(name);

    if ($("#filter").hasClass('in')) {
        data = {};
        var url = "/rest/client/filtration/sort?columnName=" + name + "&sortType=" + sort_type_asc;

        if ($('#sex').val() !== "") {
            data['sex'] = $('#sex').val();
        }
        data['ageTo'] = $('#ageTo').val();
        data['ageFrom'] = $('#ageFrom').val();
        data['city'] = $('#city').val();
        data['country'] = $('#country').val();
        data['dateFrom'] = $('#dateFrom').val();
        data['dateTo'] = $('#dateTo').val();
        data['pageNumber'] = page;
        if ($('#status').val() !== "") {
            data['status'] = $('#status').val();
        }
        if ($('#ownerUser').val() !== "") {
            data['ownerUserId'] = $('#ownerUser').val();
        }
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            dataType: 'json',
            url: url,
            data: JSON.stringify(data),
            success: function (clients) {
                sort_type_asc = !sort_type_asc;
                table_body.empty();
                drawClients(table_body, clients);
            },
            error: function (error) {
                console.log(error);
            }
        })
    } else {
        $.get('/rest/client/sort',
            {page: page, columnName: name, sortType: sort_type_asc},
            function upload(clients) {
                sort_type_asc = !sort_type_asc;
                table_body.empty();
                drawClients(table_body, clients);
                page++;
            });
    }
}