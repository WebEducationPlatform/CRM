
function assign(id, userId) {
    $.ajax({
        type: 'POST',
        url: '/rest/client/assign/mentor',
        data: { clientId: id, userForAssign: userId},
        success: function () {
            getTable();
        },
        error: function (error) {}
    });
}

function formatDate(date) {
    var mDate = new Date(new Date(date).toLocaleString("en-US", {timeZone: "Europe/Moscow"}));
    var dd = mDate.getDate();
    if (dd < 10) dd = '0' + dd;

    var mm = mDate.getMonth() + 1;
    if (mm < 10) mm = '0' + mm;

    var yy = mDate.getFullYear();

    var hh = mDate.getHours();
    if (hh < 10) hh = '0' + hh;

    var m = mDate.getMinutes();
    if (m < 10) m = '0' + m;
    return dd + '.' + mm + '.' + yy + ' ' + hh + ':' + m + ' MCK';
}

async function getTable() {

    function fillTable(clients) {
        var ntbody = document.createElement('tbody'), row, rowData, i;
        ntbody.setAttribute('id', 'clients-table-body');

        for (i = 0; i < clients.calls.length; i++) {
            row = document.createElement('tr');

            // Client id
            rowData = document.createElement('td');
            rowData.textContent = clients.calls[i].toAssignSkypeCall.id;
            row.appendChild(rowData);

            // First call date
            rowData = document.createElement('td');
            rowData.textContent = formatDate(clients.calls[i].skypeCallDate);
            row.appendChild(rowData);

            // Client name
            rowData = document.createElement('td');
            rowData.textContent = clients.calls[i].toAssignSkypeCall.name;
            row.appendChild(rowData);

            // Client email
            rowData = document.createElement('td');
            rowData.textContent = clients.calls[i].toAssignSkypeCall.email;
            row.appendChild(rowData);

            // Client phone
            rowData = document.createElement('td');
            rowData.textContent = clients.calls[i].toAssignSkypeCall.phoneNumber;
            row.appendChild(rowData);

            // Client skype
            rowData = document.createElement('td');
            rowData.textContent = clients.calls[i].toAssignSkypeCall.skype;
            row.appendChild(rowData);

            // Mentor actions
            if (clients.needActions) {
                rowData = document.createElement('td');
                var getButton = document.createElement('button');
                getButton.setAttribute('type', 'button');
                getButton.setAttribute('onclick', 'assign(' + clients.calls[i].toAssignSkypeCall.id + ',' + clients.userId + ')');
                getButton.classList.add('btn');
                getButton.classList.add('btn-default');
                getButton.textContent = 'Взять';
                rowData.appendChild(getButton);
                row.appendChild(rowData);
            }

            ntbody.appendChild(row);
        }

        document.getElementById('clients-table').replaceChild(ntbody, document.getElementById('clients-table-body'));
    }

    $.ajax({
        type: 'GET',
        url: '/firstCallClients/get',
        success: function (data) {
            fillTable(data);
        },
        error: function (error) {}
    });

    document.removeEventListener('DOMContentLoaded', getTable);
}

document.addEventListener('DOMContentLoaded', getTable);