//Open creaate new template modal
$("#button_create_autoanswer").click(function () {
    $('#autoanswer-create-modal').modal('show');
});
//Create new autoanswer
$("#create_autoanswer").click(function () {

    if ($('#autoanswer-subject').val().length != 0 ) {
        $.ajax({
            type: 'POST',
            url: '/rest/autoanswers/add',
            data: {
                subject: $('#autoanswer-subject').val(),
                messageTemplate:Number($('#autoanswer-template').val()),
                status: Number($('#autoanswer-status').val())
            },
            success: function (response) {
                location.reload();
            },
            error: function () {
                alert("Шаблон с таким именем " + name + " уже существует!");

            }
        });
    } else {
        $("#autoanswer-create-modal-err").html("Тема доолжна быть указана!");
    }
    // $('#autoanswer-create-modal').modal('show');
});

//Edit autoanswer by id modal button
$(".button_update_autoanswer").click( function () {
    let id = this.value;
    $('<input>', { value: id, text: id, type: 'hidden'}).appendTo('#wrapper');
    $('#autoanswer-create-modal').modal('show');
    $.ajax({
        type: 'POST',
        url: '/rest/autoanswers/delete',
        data: {autoanswer_id: id},
        success: function (response) {
            if (response === "CONFLICT") {
                alert("Шаблон используется для оповещения!");
            } else {
                location.reload();
            }
        }
    });
});
//Delete autoanswer by id modal button
$(".button_delete_autoanswer").click( function () {
    if(!confirm("Вы уверены, что хотите удалить запись?")) {return}
    let id = this.value;
    $.ajax({
        type: 'POST',
        url: '/rest/autoanswers/delete',
        data: {autoanswer_id: id},
        success: function (response) {
            if (response === "CONFLICT") {
                alert("Шаблон используется для оповещения!");
            } else {
                location.reload();
            }
        }
    });
});