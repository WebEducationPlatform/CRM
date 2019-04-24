//Сортировка клиентов в статусах
$(".change-client-order").on('click', function () {
    let newOrder = $(this).attr("id");
    let statusId = $(this).parents(".column").attr("value");
    console.log(statusId + " " + newOrder);
    $.post("/rest/client/order", {newOrder: newOrder, statusId: statusId})
        .done(function () {
            location.reload();
        });
});

//Показать пропавших студентов.
$('a#FIND_MISSING').on('click', function () {
    let statuses;
    $.get("/rest/status")
        .done(function (statusesFromServer) {
            statuses = statusesFromServer;
            $(".portlet.common-modal.panel.panel-default").remove();
            $.each(statuses, function (i, status) {
                if (status.createStudent === true) {
                    drawMissingStudents(status)
                }
            })
        })
});

function drawMissingStudents(status) {
    let students = null;
    $.get("/rest/status/" + status.id)
        .done(function (studentsInStatus) {
            students = studentsInStatus;
            $.each(students, function (i, student) {
                $.get("http://localhost:8080/student/lost?email=" + student.email)
                    .done(function (isLost) {
                        if (isLost === true) {
                            drawClientsPortlet(student, status);
                        }
                    })
            })
        });
}

function drawClientsPortlet(student, status) {

}