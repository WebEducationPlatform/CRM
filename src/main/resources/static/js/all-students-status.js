$('.button_edit_status').click(function () {
    // let currentModal = $('#student-edit-modal');
    console.log("edit " + this.value);
    // currentModal.data('student_id', this.value);
    // currentModal.modal('show');
});

$('.button_delete_status').click(function () {
    console.log("delete " + this.value);
    // currentModal.data('student_id', this.value);
    // currentModal.modal('show');
});

$('#create_status').click(function () {
    let currentModal = $('#student-status-create-modal');
    // currentModal.data('student_id', this.value);
    currentModal.modal('show');
});

$('#create_student_status').click(function () {
    let status = $("#status-name").val();
    let data = {status : status};
    console.log(data);
    $.ajax({
        type: 'POST',
        url: '/rest/student/status/create',
        contentType : "application/json; charset=UTF-8",
        encoding: "UTF-8",
        dataType: "JSON",
        data: JSON.stringify(data),
        success: function () {
            // location.reload();
        }
    })

});

// $(function () {
//     $('#student-status-create-modal').on('show.bs.modal', function () {
//
//     });
// });
