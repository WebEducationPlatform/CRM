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

                var d = new Date(res[i].dateOfRegistration);
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
                    '        <td>' + dateOfRegistration + ' </td>' +
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

let table = $("#clients-table").find("tbody");

//Draw clients first page to the table
function drawDefaultClients() {
    $.get('/rest/client/pagination/get', {page : 0}, function upload(clients) {
        table.empty();
        drawClients(table, clients);
    })
}

//Draw clients list to the table
function drawClients(table, res) {
    for (let i = 0; i < res.length; i++) {
        let socLink = '';
        for(let j  = 0; j < res[i].socialProfiles.length; j++) {
            socLink += res[i].socialProfiles[j].link + '<br>';
        }

        let d = new Date(res[i].dateOfRegistration);
        let dateOfRegistration = ("0" + d.getDate()).slice(-2) + "." + ("0"+(d.getMonth()+1)).slice(-2) + "." +
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
            '        <td>' + dateOfRegistration + ' </td>' +
            '        <td class="no-fix">' + returnBtn + ' </td>' +
            '    </tr>'
        )
    }
}

//Search by keyword
$("#searchInput").keyup(function (e) {
    if (e.keyCode === 13) {
        let search = this.value.toLowerCase();
        table.empty();
        if (search === "") {
            drawDefaultClients();
        } else {
            $.ajax({
                type: 'GET',
                url: "/rest/client/search",
                data: {search: search},
                success: function (response) {
                    drawClients(table, response);
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
    let page = 1;

    win.scroll(function () {
        if (($(document).height() - win.height() === Math.ceil(win.scrollTop())) && ($("#searchInput").val() === "")) {
            $.get('/rest/client/pagination/get', {page : page}, function upload(clients) {
                drawClients(table, clients, page);
                page++;
            })
        }
    });
});

//Clearable search functions
function tog(v) {
    return v?'addClass':'removeClass';
}

//Clearable search functions
$(document).on('input', '.clearable', function(){
    $(this)[tog(this.value)]('x');
}).on('mousemove', '.x', function( e ){
    $(this)[tog(this.offsetWidth-18 < e.clientX-this.getBoundingClientRect().left)]('onX');
}).on('touchstart click', '.onX', function( ev ){
    ev.preventDefault();
    $(this).removeClass('x onX').val('').change();
    drawDefaultClients();
});