//Список статусов возможных для клиентов-студентов
$(function () {
    $('#choice-status-column-modal').on('show.bs.modal', function () {
        var properties;
        $("#status-column").empty();
        $("#new-client-status").empty();
        $("#repeated-client-status").empty();
        $("#reject-student-status-column").empty();

        $.ajax({
            type: 'GET',
            url: '/rest/properties',
            dataType: 'json',
            success: function (response) {
                properties = response;
            }
        });

        $.ajax({
            type: 'GET',
            url: '/rest/status',
            dataType: 'json',
            success: function (json) {
                $.each(json, function (index, element) {
                    $("#reject-student-status-column").append($('<option value=' + element.id + '>').append(element.name).append('</option>'));
                    $("#new-client-status").append("<option id = default_status_" + element.id + " value=" + element.id + ">" + element.name + "</option>");
                    $("#repeated-client-status").append("<option id = repeated_default_status_" + element.id + " value=" + element.id + ">" + element.name + "</option>");
                    if (element.createStudent == true) {
                        $('#status-column').append($('<option value=' + element.id + '>').append(element.name).append('</option>'));
                    }
                });
                if (properties !== null) {
                    $("#reject-student-status-column").val(properties.clientRejectStudentStatus);
                    $("#new-client-status").val(properties.newClientStatus);
                    $("#repeated-client-status").val(properties.repeatedDefaultStatusId);
                    $('#status-column').val(properties.defaultStatusId);
                }
            }
        });

    });
});

//Выбираем и сохраняем дефолтный статус
$('#update-status').click(function () {
    var status_id = $("select#status-column").val();
    var reject_status_id = $("select#reject-student-status-column").val();
    var new_client_status = $("#new-client-status").val();
    var repeated_client_status = $("#repeated-client-status").val();

    $.ajax({
        type: 'POST',
        url: '/rest/properties/client-default-properties',
        data: {
            repeatedStatus: repeated_client_status,
            newClientStatus: new_client_status,
            id: status_id,
            rejectId: reject_status_id
        }
    })

});
