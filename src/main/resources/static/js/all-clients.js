$(document).ready(
    $("#searchInput").keyup(function () {
        //split the current value of searchInput
        var data = this.value.split(" ");
        //create a jquery object of the rows
        var jo = $("#table-body").find("tr");
        if (this.value == "") {
            jo.show();
            return;
        }
        jo.hide();

        jo.filter(function (i, v) {
            let $validCount = 0;
            var $t = $(this);
            for (var d = 0; d < data.length; ++d) {
                if ($t.is(":contains('" + data[d] + "')")) {
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
    if ($('#state').val() !== "") {
        data['state'] = $('#state').val();
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
                for(var j  = 0; j < res[i].socialNetworks.length; j++) {
                    socLink += res[i].socialNetworks[j].link + '\n';
                }
                $("#table-body").append(
                    '    <tr>' +
                    '        <td>' + res[i].id + '</td>' +
                    '        <td>' + res[i].name + '</td>' +
                    '        <td>' + res[i].lastName + '</td>' +
                    '        <td>' + res[i].phoneNumber + '</td>' +
                    '        <td>' + res[i].email + '</td>' +
                    '        <td>' + socLink + '</td>' +
                    '        <td>' + res[i].age + ' </td>' +
                    '        <td>' + res[i].sex + ' </td>' +
                    '        <td>' + res[i].city + ' </td>' +
                    '        <td>' + res[i].country + ' </td>' +
                    '        <td>' + res[i].state + ' </td>' +
                    '        <td>' + res[i].dateOfRegistration + ' </td>' +
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
    var urlFiltration = "../rest/client/createFileFiltr";
    if (jQuery.isEmptyObject(data)) {
        $.ajax({
            type: 'POST',
            url: url,
            data: {selected: $("#selectType").val()},
            success: function () {
                window.location.replace("http://localhost:9090/rest/client/getClientsData")
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
                window.location.replace("http://localhost:9090/rest/client/getClientsData")
            }
        })
    }
});
