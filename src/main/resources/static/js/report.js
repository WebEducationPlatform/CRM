let clients;
let data = [];

function createChart() {
    $("form#formCreate :input").each(function () {
        let input = $(this);
        data.push(input.val());
    });

    let wrap = JSON.stringify(
        {
            days: data[0],
            learning: data[1],
            finish: data[2],
            out: data[3]
        }
    );

    /*var data = JSON.stringify(
        {
            "id": 0,
            "login": formArr[0].value,
            "password": formArr[1].value,
            "roles":formArr[2].value
        }
    );*/

    $.ajax({
        url: "/last-days",
        type: "POST",
        data: wrap,
        Accept: "application/json",
        contentType: "application/json",
        dataType: "json",
        success: function (response) {
            alert('success ' + response);
            //window.reload();
        },
        error: function (response) {
            alert('error ' + response);
        }
    });


    /* $.get('/last-days', x, function upload(clientsList) {
         clients = clientsList;
         alert("succes");
     })*/
}


/*$.ajax({
            type: "GET",
        url: "/last-days",
        data: wrap,
        success: function (result) {
            alert("bad" + "\n" + result);
            //clients = result;
        },
        error: function (e) {
            alert("bad" + "\n" + e.responseText);
        }
    });*/