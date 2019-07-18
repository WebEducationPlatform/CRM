$(document).ready(function () {
    let i = 1;
    while ($("#column-number" + i).length) {
        const statusId = $("#column-number" + i).attr("value");
        const url = "/status/" + statusId;
        $("#clients-for-status" + statusId).load(url);
        i++;
    }
});

//Сортировка клиентов в статусах
$(".change-client-order").on('click', function () {
    const newOrder = $(this).attr("id");
    const statusId = $(this).parents(".column").attr("value");
    console.log(statusId + " " + newOrder);
    $.post("/rest/client/order", {newOrder: newOrder, statusId: statusId})
        .done(function () {

            // location.reload();
        });
});


function displayOption(clientId) {
    $("#option_" + clientId).show();
}