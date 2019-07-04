//Список статусов возможных для клиентов-студентов
$(function () {
    $('#choice-status-column-modal').on('show.bs.modal', function () {
        var properties;
        $("#new-client-status").empty();
        $("#repeated-client-status").empty();
        $("#reject-student-status-column").empty();
        $("#first-pay-status-column").empty();
        $("#first-skype-call-after-status").empty();

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
                        $('#first-pay-status-column').append($('<option value=' + element.id + '>').append(element.name).append('</option>'));
                    }
                    $("#first-skype-call-after-status").append("<option id = first_skype_call_after_status_" + element.id + " value=" + element.id + ">" + element.name + "</option>");

                });
                if (properties !== null) {
                    $("#reject-student-status-column").val(properties.clientRejectStudentStatus);
                    $("#new-client-status").val(properties.newClientStatus);
                    $("#repeated-client-status").val(properties.repeatedDefaultStatusId);
                    $('#first-pay-status-column').val(properties.clientFirstPayStatus);
                    $('#first-skype-call-after-status').val(properties.firstSkypeCallAfterStatus);
                }
            }
        });

    });
});

//Выбираем и сохраняем дефолтный статус
$('#update-status').click(function () {
    let reject_status_id = $("select#reject-student-status-column").val();
    let new_client_status = $("#new-client-status").val();
    let repeated_client_status = $("#repeated-client-status").val();
    let client_first_pay_status = $("#first-pay-status-column").val();
    let first_skype_call_after_status = $("#first-skype-call-after-status").val();

    $.ajax({
        type: 'POST',
        url: '/rest/properties/client-default-properties',
        data: {
            repeatedStatus: repeated_client_status,
            newClientStatus: new_client_status,
            rejectId: reject_status_id,
            firstPayStatus: client_first_pay_status,
            firstSkypeCallAfterStatus: first_skype_call_after_status
        }
    })

});
