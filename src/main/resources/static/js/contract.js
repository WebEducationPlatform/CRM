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

const isValidName = document.getElementById('inputFirstName').checkValidity();
const isValidLastName = document.getElementById('inputLastName').checkValidity();
const isValidBirthday = document.getElementById('inputBirthday').checkValidity();
const isValidPassportIssued = document.getElementById('inputPassportIssued').checkValidity();
const isValidRegistrationAddress = document.getElementById('inputRegistrationAddress').checkValidity();
const isValidEmail = document.getElementById('inputEmail').checkValidity();
const isValidPhoneNumber = document.getElementById('inputPhoneNumber').checkValidity();
const isValidPassportSeries = document.getElementById('inputPassportSeries').checkValidity();
const isValidPassportNumber = document.getElementById('inputPassportNumber').checkValidity();
const isValidDateOfIssue = document.getElementById('inputDateOfIssue').checkValidity();

$('#go-button').on("click", function(){
    if (isValidName && isValidLastName && isValidBirthday && isValidPassportIssued && isValidRegistrationAddress &&
        isValidEmail && isValidPhoneNumber && isValidPassportSeries && isValidPassportNumber && isValidDateOfIssue) {
        $('#contract-loading').show();
    }
});
