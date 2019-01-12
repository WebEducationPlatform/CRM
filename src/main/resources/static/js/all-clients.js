var data = {};
//Current clients page for pagination
let page = 1;
//объект статус-цвет, массив его ключей
let statuscol, arrKeys;
//массив статусов
let statuses = [];

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
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        url: url,
        data: JSON.stringify(data),
        success: function (res) {
            clearClientsTable();
            for (var i = 0; i < res.length; i++) {
                var socLink = '';
                for (var j = 0; j < res[i].socialProfiles.length; j++) {
                    socLink += res[i].socialProfiles[j].link + '<br>';
                }

                //Вывод даты регистрации всех клиентов по московскому времени в таблице всех клиентов
                var d = new Date(new Date(res[i].dateOfRegistration).toLocaleString('en-US', {timeZone: 'Europe/Moscow'}));
                var dateOfRegistration = ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." +
                    d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);

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
                            ' <button type="button" class="btn btn-default" data-toggle="dropdown" data-client="' + res[i].id + '">Вернуть</button>' +
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

                $("#table-body").append(
                    '    <tr>' +
                    '        <td>' + res[i].id + '</td>' +
                    '        <td class="line-decoration"><a href="/client/clientInfo/' + res[i].id + '">' + res[i].name + '</a></td>' +
                    '        <td>' + res[i].lastName + '</td>' +
                    '        <td>' + phoneNumber + '</td>' +
                    '        <td>' + email + '</td>' +
                    '        <td>' + socLink + '</td>' +
                    '        <td>' + res[i].age + ' </td>' +
                    '        <td>' + sex + ' </td>' +
                    '        <td>' + city + ' </td>' +
                    '        <td>' + country + ' </td>' +
                    '        <td>' + res[i].status.name + ' </td>' +
                    '        <td>' + dateOfRegistration + ' МСК' + ' </td>' +
                    '        <td>' + returnBtn + ' </td>' +
                    '    </tr>'
                )
            }
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
    if (jQuery.isEmptyObject(data)) {
        $.ajax({
            type: 'POST',
            url: url,
            data: {selected: $("#selectType").val()},
            success: function () {
                window.location.replace("/rest/client/getClientsData")
            }
        });
    }
    if (!(jQuery.isEmptyObject(data))) {
        data['selected'] = $("#selectType").val();
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
        if (v.roleName === 'ADMIN' || v.roleName === 'OWNER') {
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

//при закрытии фильтра отображаем дефолтный вывод таблицы
$("#open-filter").click(function () {
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
            socLink += res[i].socialProfiles[j].link + '<br>';
        }

        //Вывод даты регистрации всех клиентов по московскому времени в таблице всех клиентов
        var d = new Date(new Date(res[i].dateOfRegistration).toLocaleString('en-US', {timeZone: 'Europe/Moscow'}));
        var dateOfRegistration = ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." +
            d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);

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

        $("#table-body").append(
            '    <tr>' +
            '        <td>' + res[i].id + '</td>' +
            '        <td class="line-decoration"><a href="/client/clientInfo/' + res[i].id +'">' + res[i].name + '</a></td>' +
            '        <td>' + res[i].lastName + '</td>' +
            '        <td>' + phoneNumber + '</td>' +
            '        <td>' + email + '</td>' +
            '        <td>' + socLink + '</td>' +
            '        <td>' + res[i].age + ' </td>' +
            '        <td>' + sex + ' </td>' +
            '        <td>' + city + ' </td>' +
            '        <td>' + country + ' </td>' +
            '        <td class="colorTd" id="td_'+res[i].id+'">' + res[i].status.name + '</td>' +
            '        <td class="dateOfRegistration">' + dateOfRegistration + ' МСК' + ' </td>' +
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
    if (e.keyCode === 13) {
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
    let win = $(window);
    let body = $("#table-body");
    win.scroll(function () {
        if (($(document).height() - win.height() === Math.ceil(win.scrollTop())) && ($("#searchInput").val() === "")) {
            //пагинация при фильтрации
            if ($("#filter").hasClass('in')) {
                data['pageNumber']++;
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
                //пагинация при обычном просмотре страницы
            } else {
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