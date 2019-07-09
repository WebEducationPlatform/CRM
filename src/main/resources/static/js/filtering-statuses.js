//Фильтр клиентов в статусах

function setFilterByMentor(mentorId,statusId) {
    $.post("/rest/client/findByMentor", {mentorId: mentorId, statusId: statusId})
        .done(function () {
            location.reload();
        });
}