var count = 0;
function getInfo() {
    if (count === 0) {
        var resultTable = "<table class='table table-striped'><thead><tr><th scope='col'>E-mail</th><th scope='col'>Студент</th>" +
            "<th scope='col'>Учится/не учится</th></th></tr></thead><tbody></tbody></table>";
        $('#public-information').append(resultTable);
        count++;
    }
    var email = document.getElementById('request').value;
    var url = "/public/info/" + email;
    $.ajax({
        url: url,
        contentType: "application/json",
        type: 'GET',
        dataType: 'JSON',
        success: function (obj) {
            var resultRow = "<tr><td>" + email + "</td><td>" + obj.name + " " + obj.lastName + "</td>" +
                "<td>Учится</td></tr>";
            $('tbody').append(resultRow);
        },
        error: function (error) {
            var resultRow = "<tr><td>" + email + "</td><td>---</td><td>Не учится</td></tr>";
            $('tbody').append(resultRow);
        }
    });
}