$(document).ready(function() {

    var check_box = $("#customCheck1");

    check_box.click(function() {
        if ($(this).is(":checked")) {
            $("#go-button").removeAttr("disabled");
        } else {
            $("#go-button").attr("disabled", "disabled");
        }
    });
});

$('#go-button').on("click", function(){
    $('#contract-loading').show();
});

$('#inputDateOfIssue').mask('00.00.0000');
$('#inputBirthday').mask('00.00.0000');