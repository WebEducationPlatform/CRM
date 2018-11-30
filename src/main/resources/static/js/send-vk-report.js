
$(document).ready(function () {
    $.ajax({
        url: "/rest/direct",
        type: "POST",
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
        }
    })
});