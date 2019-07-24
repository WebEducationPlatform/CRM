//Фильтр клиентов в статусах

function setFilterByMentor(mentorId,statusId) {
    $.post("/rest/client/filter", {newFilter: "BY_MENTOR", filterId: mentorId, statusId: statusId})
        .done(function () {
            const url = "/status/" + statusId;
            $("#clients-for-status" + statusId).load(url);
        });
}
function clearFilterByMentor(statusId) {
    $.post("/rest/client/filter/clear", { statusId: statusId})
        .done(function () {
            const url = "/status/" + statusId;
            $("#clients-for-status" + statusId).load(url);
        });
}
