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

//Rename template
function renameTemplate(button) {
    //Get id and name
    var id = button.getAttribute('data');
    var oldName = button.value;
    //Set name field
    document.getElementById('template-rename').value = oldName;
    //Clean error row
    $("#rename-template-modal-err").empty();
    //Show modal
    $('#rename-template-modal').modal('show');
    //Save new name, remove old click listeners before
    $("#send_name").off('click').click(function () {
        //Get new name
        var newName = $('#template-rename').val();
        var pattern = /^(?!\s*$).+/;
        //Check name
        if (oldName === newName) {
            //Hide modal
            $('#rename-template-modal').modal('hide');
        } else if (pattern.test(newName)) {
            var data = {
                id: id,
                name: newName
            };
            $.ajax({
                type: 'POST',
                url: '/rest/message-template/rename',
                data: data,
                success: function (response) {
                    if (response === "BAD_REQUEST") {
                        alert("Такое имя уже используется!");
                    } else {
                        location.reload();
                    }
                }
            });
        } else {
            $("#rename-template-modal-err").html("Имя шаблона не может быть пустым!");
        }
    });
};