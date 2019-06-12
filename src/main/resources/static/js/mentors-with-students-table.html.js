let botIp = $("#slackbotIp").val();
let botPort = $("#slackbotPort").val();

$(document).ready(function () {
    console.log(students);
    // $("#mentors-row").children().remove();
    // $.each(mentors, function (i, mentor) {
    //     $.get("http://" + botIp + ":" + botPort + "/mentor/students?email=" + mentor.email)
    //         .done(function (response) {
    //             drawMentorTable(mentor, response);
    //         })
    //
    // });

});

// function drawMentorTable(mentor, studentsEmails) {
//     $('<div></div>', {
//         class: 'column ui-sortable',
//         id: 'status-column' + user.id,
//         text: mentor.firstName
//     }).appendTo('#mentors-row');
// }


