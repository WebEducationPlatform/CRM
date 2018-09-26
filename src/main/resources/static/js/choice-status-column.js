$(function () {
    $('#choice-status-column-modal').on('show.bs.modal', function () {
        $.ajax({
            type: 'GET',
            url: '/slack/get/students/statuses',
            success: function (response) {
                $.each(response, function (index, el) {
                    alert("element at " + index + ": " + el.toString());
                })
            }
        });
    });
});
