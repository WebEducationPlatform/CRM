//Open creaate new template modal
$("#button_create_autoanswer").click(function () {
    $('#autoanswer-id').val('');
    $('#autoanswer-subject').val('');
    deselectOptions()
    $('#autoanswer-create-modal').modal('show');
});
//Create and update  autoanswer
$("#create_autoanswer").click(function () {
    let id = $('#autoanswer-id').val();
    if (id > 0){
        url = '/rest/autoanswers/update/';
    }else{
        url = '/rest/autoanswers/add';
    }

    if ($('#autoanswer-subject').val().length != 0 ) {
        $.ajax({
            type: 'POST',
            url: url,
            data: {
                id: id,
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
$(".button_edit_autoanswer").click( function () {
    let id = this.value;
    $.ajax({
        type: 'GET',
        url: '/rest/autoanswers/get/' + id,
        dataType: 'JSON',
        success: function (response) {
            $('#autoanswer-id').val(response.id);
            $('#autoanswer-subject').val(response.subject);
            deselectOptions()
            $('#autoanswer-template option[value=' + response.messageTemplate_id + ']').prop('selected', true);
            $('#autoanswer-status option[value=' + response.status_id + ']').prop('selected', true);
            $('#autoanswer-create-modal').modal('show');
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

function deselectOptions(){
    $('#autoanswer-template option:selected').each(function(){
        this.selected=false;
    });
    $('#autoanswer-status option:selected').each(function(){
        this.selected=false;
    });
}