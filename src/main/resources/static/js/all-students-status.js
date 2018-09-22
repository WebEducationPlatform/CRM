//Edit Student status button action
$('.button_edit_status').click(function () {
    let currentModal = $('#student-status-edit-modal');
    currentModal.data('student_status_id', this.value);
    currentModal.modal('show');
});

//Edit Student status modal filled
$(function () {
    $('#student-status-edit-modal').on('show.bs.modal', function () {
        let status_id = $(this).data('student_status_id');
        $.ajax({
            type: 'GET',
            url: '/rest/student/status/' + status_id,
            success: function (response) {
                $("#edit-student-status-id").val(response.id);
                $("#edit-status-name").val(response.status);
            }
        })
    });
});

//Edit Student status button submit changes
$("#edit_student_status").click(function () {
    let id = $("#edit-student-status-id").val();
    let status = $("#edit-status-name").val();
    let data = {
        id : id,
        status : status
    };
    $.ajax({
        type: 'POST',
        url: '/rest/student/status/update',
        contentType : "application/json; charset=UTF-8",
        encoding: "UTF-8",
        dataType: "JSON",
        data: JSON.stringify(data),
        success: function () {
            location.reload();
        }
    })
});

//Create Student status button action
$('#create_status').click(function () {
    let currentModal = $('#student-status-create-modal');
    currentModal.modal('show');
});

//Create Student status button submit changes
$('#create_student_status').click(function () {
    let status = $("#status-name").val();
    let data = {status : status};
    $.ajax({
        type: 'POST',
        url: '/rest/student/status/create',
        contentType : "application/json; charset=UTF-8",
        encoding: "UTF-8",
        dataType: "JSON",
        data: JSON.stringify(data),
        success: function () {
            location.reload();
        }
    })
});

//Delete Student status button action
$('.button_delete_status').click(function () {
    let status_id = this.value;
    $.ajax({
        type: 'GET',
        url: '/rest/student/status/delete/' + status_id,
        success: function (response) {
            if (response == "CONFLICT") {
                alert("Статус занят студентами.\r\nНе могу удалить статус!");
            }
            location.reload();
        }
    })
});