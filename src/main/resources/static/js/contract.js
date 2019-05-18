$(document).ready(function () {
    $("#customCheck1").click(function () {
        if ($(this).is(":checked")) {
            $("#contract-send-date").removeAttr("disabled");
        } else {
            $("#contract-send-date").attr("disabled", "disabled");
        }
    });
});
var form = $('#contract-form');

$('#contract-send-date').on("click", function () {
    if (form[0].checkValidity()) {
        $('#contract-loading').show();
    } else {
        console.log("Заполнены не все поля!")
    }
});