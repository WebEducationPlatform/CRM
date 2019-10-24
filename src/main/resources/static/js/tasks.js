var tmpElements ;
var currentEditElementId;
var currentUserId;
var currentUserFullName;

$(document).ready(function () {
    currentUserId = $('input[name=authorId]').val();
    currentUserFullName = $('input[name=author]').val();
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
    closeActiveForm();
    $('input[type=date][name=date]').val(today);
    $('input[type=date][name=date]').prop('disabled', true);
    $('#newUserTask').show();
    $('input[name=authorId]').val(currentUserId);
    $('input[name=author]').val(currentUserFullName);
}

function saveNewTasks() {
    let data = {};
    data.task = $('input[name=task]').val();
    data.date = $('input[name=date]').val();
    data.expiry_date = $('input[name=expiry_date]').val();
    data.authorId = $('input[name=authorId]').val();
    data.managerId = $('select[name=managerId]').val();
    data.executorId = $('select[name=executorId]').val();
    data.clientId = $('input[name=clientId]').val();
    data.authorFullName = $('input[name=author]').val();
    data.managerFullName = $('select[name=managerId] option:selected').text();
    data.executorFullName = $('select[name=executorId] option:selected').text();
    data.clientFullName = $('input[name=client]').val();
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
    closeActiveForm();
    currentEditElementId = id;
    tmpElements = el.html();
    setEditFormData(id);
    el.html('');
    el.attr('id','currentEditElement');
    el.attr('onclick','');
    el.off('onclick');
    $('input[name=task_id]').val(id);
    $('#newUserTask>td').appendTo(el);

    // el.html($('#newUserTask').html());
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
    $('input[name=clientId]').val(id);
    $('#listClientsFromSearch').hide();
}

function closeActiveForm() {
    if (tmpElements != null) {
        $('#currentEditElement').attr('onclick','editTaskClick(this,' + currentEditElementId + ')');
        $('#currentEditElement>td').appendTo($('#newUserTask'));
        $('#currentEditElement').html(tmpElements);
        $('#currentEditElement').attr('id', '');
        tmpElements = null;
        currentEditElementId = null;
        $('input[name=task_id]').val('');
        $('select option').prop('selected', false);
        $('input').val(null);

    }
}

$(document).keydown(function(e) {
    if( e.keyCode === 27 ) {
        $('#newUserTask').hide();
        closeActiveForm();
        return false;
    }
});

function setEditFormData(id){
    let url = "/rest/usertask/" + id;
    $.ajax({
        type: 'GET',
        contentType: "application/json",
        url: url,
        success: function (res) {
            // pullAllClientsTable(res);
            $('input[name=client]').val(res.clientFullName);
            $('input[name=clientId]').val(res.cientid);
            $('#executorId option[value=' + res.executorId + ']').prop('selected', true)
            $('#managerId option[value=' + res.managerId + ']').prop('selected', true)
            $('input[name=author]').val(res.authorFullName);
            $('input[name=authorId]').val(res.authorId);
            $('input[name=date]').val(res.date);
            $('input[name=expiry_date]').val(res.expiry_date);
            $('input[name=task]').val(res.task);
            $('input[name=task_id]').val(id);

        },
        error: function (error) {
            console.log(error);
        }
    });

}




