$(document).ready(function () {

    $('input[type=date][name=date]').val(new Date());
});

function newTasksCreate() {
    let now = new Date();
    let month = (now.getMonth() + 1);
    let day = now.getDate();
    if (month < 10)
        month = "0" + month;
    if (day < 10)
        day = "0" + day;
    var today = now.getFullYear() + '-' + month + '-' + day;
    $('#newUserTask').show();
    $('input[type=date][name=date]').val(today);
    $('input[type=date][name=date]').prop('disabled', true);
}

function saveNewTasks() {
    let data = {};
    data.task = $('input[name=task]').val();
    data.date = $('input[name=date]').val();
    data.expiry_date = $('input[name=expiry_date]').val();
    data.author_id = $('input[name=author_id]').val();
    data.executor_id = $('select[name=executor_id]').val();
    data.client_id = $('input[name=client_id]').val();
    let newTask = JSON.stringify(data);
    $.ajax({
        url: "/rest/usertask",
        data: newTask,
        contentType: "application/json",
        type: 'PUT',
        dataType: 'JSON',
        success: function (returnObj) {
        },
        error: function (error) {
            console.log(error);
        }
    });
}

$(document).ready(function () {
    $('input[name=client]').on('input', function () {
        let input = $('input[name=client]');

        if (input.val().length > 4) {
            let url = "/rest/client/names?full_name=" + $('input[name=client]').val();
            $.ajax({
                type: 'GET',
                contentType: "application/json",
                url: url,
                success: function (res) {
                    // pullAllClientsTable(res);
                    $('#listClientsFromSearch').show();
                },
                error: function (error) {
                    console.log(error);
                    $('#listClientsFromSearch').hide();
                }
            });
        } else {
            $('#listClientsFromSearch').hide();
            $('#listClientsFromSearch>.card-body').empty();
        }
    });
});



