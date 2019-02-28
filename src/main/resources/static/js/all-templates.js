//Open creaate new template modal
$("#button_create_template").click(function () {
    $('#template-create-modal').modal('show');
});

//Create new template
$("#create_template").click(function () {
    let name = $("#template-name").val();
    let pattern = /^(?!\s*$).+/;
    if (pattern.test(name)) {
        $.ajax({
            type: 'POST',
            url: '/rest/message-template',
            dataType: "JSON",
            data: {name: name},
            success: function () {
                window.location = "/template/create/" + name;
            },
            error: function () {
                alert("Шаблон с таким именем " + name + " уже существует!");

            }
        });
    } else {
        $("#template-create-modal-err").html("Имя шаблона не может быть пустым!");
    }
});

//Edit template page redirect
$(".button_edit_template").click( function () {
    let id = this.value;
    window.location = "/template/edit/" + id;
});

//Delete template modal button
$(".button_delete_template").click( function () {
    if(!confirm("Вы уверены, что хотите удалить запись?")) {return}
    let id = this.value;
    $.ajax({
        type: 'POST',
        url: '/rest/message-template/delete',
        data: {id: id},
        success: function (response) {
            if (response === "CONFLICT") {
                alert("Шаблон используется для оповещения!");
            } else {
                location.reload();
            }
        }
    });
});

//Clearing text inside #template-create-modal-err after closing modal
$('#template-create-modal').on('hidden.bs.modal', function () {
    $("#template-create-modal-err").empty();
});