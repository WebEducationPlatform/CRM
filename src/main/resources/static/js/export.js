window.onload = function () {
    getStatuses();
};

function exportForBitrix24(formatFile) {
    let stauses = [];

    $('input:checkbox:checked').each(function () {
        stauses.push($(this).val());
    });


    $.ajax({
        async: false,
        url: 'rest/client/export/' + formatFile,
        contentType: "application/json",
        method: 'POST',
        data: JSON.stringify(stauses),
        success: function (strDir) {
            window.location.replace("/rest/client/getClientsData");
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Функция получения списка статусов
function getStatuses() {
    let statuses;
    $.ajax({
        url: '/rest/status/dto/for-mailing',
        type: 'GET',
        async: true,
        success: function (data) {
            statuses = data;
            drawTable(statuses);
        }
    });
}

//Функция отрисовки таблицы
function drawTable(statuses) {
    let table = document.getElementById('statusTableExport');
    table.style.visibility = '';
    let tableBody = document.getElementById('statusTableExportBody');
    tableBody.innerHTML = "";

    for (let i=0; i<statuses.length; i++) {
        // table row
        let row = document.createElement("tr");

        for (let j = 0; j < 2; j++) {
            let cell = document.createElement("td");
            if (j==0){cell.innerText = statuses[i].name;}
            if (j==1){cell.innerHTML = '<input type="checkbox" name="status" value="'+statuses[i].id+'">'}
            row.appendChild(cell);
        }
        tableBody.appendChild(row);
    }
    table.appendChild(tableBody);
}

