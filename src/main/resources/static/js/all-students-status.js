$(document).ready(RenderTableOfStudentStatuses());

function RenderTableOfStudentStatuses() {
    var trHTML = '';
    let element = $('#table-body');
    $.ajax({
        url: '/rest/student/status/',
        type: 'GET',
        async: true,
        success: function (data) {
            for (let i = 0; i < data.length; i++) {
                trHTML += "<tr>" +
                    "<td>" + data[i].id + "</td>" +
                    "<td>" + data[i].status + "</td>" +
                    "<td class='fit'>" +
                        "<button class='button_edit_status btn btn-info glyphicon glyphicon-pencil' value='" + data[i].id +
                            "' ' title='Изменить статус'></button>" +
                        "<button class='button_delete_status btn btn-danger glyphicon glyphicon-remove' value='" + data[i].id +
                            "' title='Удалить статус'></button>" +
                    "</td>" +
                    "</tr>";
            }
            element.empty();
            element.append(trHTML);
        },
        error: function (jqXHR) {
            if (jqXHR.status == "404") {
                trHTML +=
                    "<tr>" +
                        "<td></td>" +
                        "<td>В базе нет ни одного шаблона!</td>" +
                        "<td class='fit'></td>" +
                    "</tr>";
                element.empty;
                element.append(trHTML);
            }
        }
    });
}

//Edit Student status button action
$("#table-body").on('click', '.button_edit_status', function () {
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
    let pattern = /^(?!\s*$).+/;
    if (pattern.test(status)) {
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
                RenderTableOfStudentStatuses();
                $('#student-status-edit-modal').modal('hide');
            }
        })
    } else {
        alert('Имя статуса не может быть пустым!');
    }
});

//Create Student status button action
$('#create_status').click(function () {
    $('#student-status-create-modal').modal('show');
});

//Create Student status button submit changes
$('#create_student_status').click(function () {
    let status = $("#status-name").val();
    let data = {status : status};
    let pattern = /^(?!\s*$).+/;
    if (pattern.test(status)) {
        $.ajax({
            type: 'POST',
            url: '/rest/student/status/create',
            contentType : "application/json; charset=UTF-8",
            encoding: "UTF-8",
            dataType: "JSON",
            data: JSON.stringify(data),
            success: function () {
                RenderTableOfStudentStatuses();
                $('#student-status-create-modal').modal('hide');
            }
        })
    } else {
        alert('Имя статуса не может быть пустым!');
    }

});

//Delete Student status button action
$("#table-body").on('click', '.button_delete_status', function () {
    if(!confirm("Вы уверены, что хотите удалить запись?")) {return}
    let status_id = this.value;
    $.ajax({
        type: 'GET',
        url: '/rest/student/status/delete/' + status_id,
        success: function (response) {
            if (response === "CONFLICT") {
                alert("Статус занят студентами.\r\nНе могу удалить статус!");
            }
            RenderTableOfStudentStatuses();
        }
    })
});