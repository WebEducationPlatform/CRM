var tmpElements ;

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
    data.manager_id = $('select[name=manager_id]').val();
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

            $('#newUserTask').hide();
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function editTaskClick(elem,id){
    let el = jQuery(elem);
    $('#newUserTask').hide();
    if (tmpElements != null){
        $('#currentEditElement').html(tmpElements);
        $('#currentEditElement').attr('id', '');
        tmpElements = null;
    }
    tmpElements = el.html();
    el.attr('id','currentEditElement');
    $('input[name=task_id]').val(id);
    el.html($('#newUserTask').html());

    //$('#newUserTask').show();

}

$(document).ready(function () {
    $('input[name=client]').on('input', function () {
        let input = $('input[name=client]');

        if (input.val().length > 2) {
            let url = "/rest/client/name?name=" + $('input[name=client]').val();
            $.ajax({
                type: 'GET',
                contentType: "application/json",
                url: url,
                success: function (res) {
                    pullAllClientsTable(res);
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


function pullAllClientsTable(data) {
    let resultStr = '<ul class="list-group list-group-flush">';
    for (var i = 0; i < data.length; i++) {
        resultStr += ' <a href="#" class="list-group-item " onclick="setClientOnTask(' + data[i].id + ',\'' + data[i].name + ' ' + data[i].lastName + '\')" >';
        resultStr += data[i].name + ' ' + data[i].lastName;
        resultStr += '</br>(' + data[i].phoneNumber + ')</a> ';
    }
    resultStr += '</ul>';
    $('#listClientsFromSearch>.card-body').empty();
    $('#listClientsFromSearch>.card-body').append(resultStr);
}

function setClientOnTask(id, fname) {
    $('input[name=client]').val(fname);
    $('input[name=client_id]').val(id);
    $('#listClientsFromSearch').hide();
}

$(document).keydown(function(e) {
    if( e.keyCode === 27 ) {

        $('#newUserTask').hide();
        if (tmpElements != null){
            $('#currentEditElement').html(tmpElements);
            $('#currentEditElement').attr('id', '');
            tmpElements = null;
            $('input[name=task_id]').val('');

        }

        return false;
    }
});




