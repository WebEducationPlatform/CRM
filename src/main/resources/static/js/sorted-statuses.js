$(document).ready(function () {
    let i = 1;
    while ($("#column-number" + i).length) {
        const statusId = $("#column-number" + i).attr("value");
        $.get("/rest/client/order", {statusId: statusId})
            .done(function (order) {
                $("#" + order + statusId).addClass("active");
            });
        const url = "/status/" + statusId;
        $("#clients-for-status" + statusId).load(url);
        i++;
    }
});

//Сортировка клиентов в статусах
$(".change-client-order").on('click', function () {
    const newOrder = $(this).attr("id");
    const statusId = $(this).parents(".column").attr("value");
    $.post("/rest/client/order", {newOrder: newOrder, statusId: statusId})
        .done(function () {
            const url = "/status/" + statusId;
            $("#clients-for-status" + statusId).load(url);
        });
    clearActiveClientOrder(statusId);
    $("#" + newOrder + statusId).addClass("active");
});

function clearActiveClientOrder(statusId) {
    $("#NEW_FIRST" + statusId).removeClass("active");
    $("#OLD_FIRST" + statusId).removeClass("active");
    $("#NEW_CHANGES_FIRST" + statusId).removeClass("active");
    $("#OLD_CHANGES_FIRST" + statusId).removeClass("active");
}

function showActiveClientDetails(clientId) {
    const currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}

function displayOption(clientId) {
    $("#option_" + clientId).show();
}
