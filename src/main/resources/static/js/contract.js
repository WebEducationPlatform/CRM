$(document).ready(function () {
    $("#customCheck1").click(function () {
        if ($(this).is(":checked")) {
            $("#go-button").removeAttr("disabled");
        } else {
            $("#go-button").attr("disabled", "disabled");
        }
    });
});

$('#go-button').on("click", function () {
    $('#contract-loading').show();
});
