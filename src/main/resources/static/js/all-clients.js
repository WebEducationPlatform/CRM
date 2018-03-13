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
            var $t = $(this);
            for (var d = 0; d < data.length; ++d) {
                if ($t.is(":contains('" + data[d] + "')")) {
                    return true;
                }
            }
            return false;
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

$('#filtration').click(function () {
    var data = {};
    var url = "../admin/rest/client/filtration";

    data['sex'] = $('#sex').val();
    data['ageTo'] = $('#ageTo').val();
    data['ageFrom'] = $('#ageFrom').val();
    data['city'] = $('#city').val();
    data['country'] = $('#country').val();
    data['dateFrom'] = $('#dateFrom').val();
    data['dateTo'] = $('#dateTo').val();
    data['isFinished'] = $('#isFinished').val();
    data['isRefused'] = $('#isRefused').val();

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
                $("#table-body").append(
                    '    <tr>' +
                    '        <td>' + res[i].id + '</td>' +
                    '        <td>' + res[i].name + '</td>' +
                    '        <td>' + res[i].lastName + '</td>' +
                    '        <td>' + res[i].phoneNumber + '</td>' +
                    '        <td>' + res[i].email + '</td>' +
                    '        <td>' + res[i].age + ' </td>' +
                    '        <td>' + res[i].sex + ' </td>' +
                    '    </tr>'
                )
            }
        },
        error: function (error) {
            console.log(error);
        }
    })
})
