$(document).ready(
    $("#searchInput").keyup(function () {
        //split the current value of searchInput
        let data = this.value.toLowerCase().split(" ");
        //create a jquery object of the rows
        let jo = $("#table-body").find("tr");
        if (this.value.trim() === "") {
            jo.show();
            return;
        }
        jo.hide();

        jo.filter(function () {
            let $validCount = 0;
            let $t = $(this);
            let $temp = $t.clone();
            $temp.text($temp.text().toLowerCase());
            for (let d = 0; d < data.length; ++d) {
                if ($temp.is(":contains('" + data[d] + "')")) {
                    $validCount++;
                }
            }
            return $validCount === data.length;
        }).show();
    }).focus(function () {
        this.value = "";
        $(this).css({
            "color": "black"
        });
        $(this).unbind('focus');
    }).css({
        "color": "#C0C0C0"
    })
);

var data = {};
$('#filtration').click(function (){
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
            $("#table-body").remove();
            $("#thead-table-clients").after(
                '<tbody id="table-body">' +
                '    </tbody>'
            );

            for (var i = 0; i < res.length; i++) {
                var socLink = '';
                for(var j  = 0; j < res[i].socialProfiles.length; j++) {
                    socLink += res[i].socialProfiles[j].link + '<br>';
                }

                //Вывод даты регистрации всех клиентов по московскому времени в таблице всех клиентов
                var d = new Date(new Date(res[i].dateOfRegistration).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }));
                var dateOfRegistration = ("0" + d.getDate()).slice(-2) + "." + ("0"+(d.getMonth()+1)).slice(-2) + "." +
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
                            ' <button type="button" class="btn btn-default" data-toggle="dropdown" data-client="'+ res[i].id +'">Вернуть</button>' +
                            '<ul class="dropdown-menu statuses-content"></ul>' +
                            '</div>'
                    }

                    if (res[i].postponeDate != undefined) {
                        returnBtn =
                            '<div class="button-return-from-postpone">' +
                            '<button type="button" id="return-from-postpone" class="btn btn-default from-postpone" data-client="'+ res[i].id +'"> Вернуть </button>' +
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
    $.each(user.role, function (i,v) {
        if (v.roleName === 'ADMIN' || v.roleName === 'OWNER' ) {
            isAdmin = true;
        }
    })
});

$(document).ready(function () {
    var win = $(window);
    let page = 1;


    win.scroll(function () {
        if ($(document).height() - win.height() === win.scrollTop()) {

            $.get('/rest/client/pagination/get', {page : page}, function upload(clients) {
                let table = $("#clients-table").find("tbody");
                drawClients(table, clients, page);
                page++;
            })
        }
    });


    function drawClients(table, res) {
        for (let i = 0; i < res.length; i++) {
            let socLink = '';
            for(let j  = 0; j < res[i].socialProfiles.length; j++) {
                socLink += res[i].socialProfiles[j].link + '<br>';
            }

            //Вывод даты регистрации всех клиентов по московскому времени в таблице всех клиентов
            var d = new Date(new Date(res[i].dateOfRegistration).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }));
            var dateOfRegistration = ("0" + d.getDate()).slice(-2) + "." + ("0"+(d.getMonth()+1)).slice(-2) + "." +
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
                        '<button type="button" class="btn btn-default" data-toggle="dropdown" data-client="'+ res[i].id +'"> Вернуть </button>' +
                        '<ul class="dropdown-menu statuses-content"></ul>' +
                        '</div>'
                }

                if (res[i].postponeDate != undefined) {
                    returnBtn =
                        '<div class="button-return-from-postpone">' +
                        '<button type="button" id="return-from-postpone" class="btn btn-default from-postpone" data-client="'+ res[i].id +'"> Вернуть </button>' +
                        '</div>'
                }
            }

            table.append(
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
                '        <td>' + res[i].status.name + ' </td>' +
                '        <td>' + dateOfRegistration + ' МСК' + ' </td>' +
                '        <td class="no-fix">' + returnBtn + ' </td>' +
                '    </tr>'
            )
        }
    }
});
