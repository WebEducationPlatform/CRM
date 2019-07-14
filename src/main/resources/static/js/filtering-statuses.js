//Фильтр клиентов в статусах

function setFilterByMentor(mentorId,statusId) {
    $.post("/rest/client/filter", {newFilter: "BY_MENTOR", filterId: mentorId, statusId: statusId})
        .done(function () {
            location.reload();
        });
}