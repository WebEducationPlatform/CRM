//Fill values on notification status configuration modal show up.
$('#payment-notification-modal').on('show.bs.modal', function () {
    // var student_id = $(this).data('student_id');
    $.ajax({
        type: 'GET',
        url: '/rest/properties',
        success: function (response) {
            console.log(response);
            $("#payment-notification-status").val();
            $("#payment-notification-time").val();
        }
    })
});
