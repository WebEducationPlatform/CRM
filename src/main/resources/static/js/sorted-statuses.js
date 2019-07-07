//Сортировка клиентов в статусах
$(".change-client-order").on('click', function () {
    const newOrder = $(this).attr("id");
    const statusId = $(this).parents(".column").attr("value");
    console.log(statusId + " " + newOrder);
    $.post("/rest/client/order", {newOrder: newOrder, statusId: statusId})
        .done(function () {
            const url = "/status/" + statusId;
            $('#statusClientSortedBlockInitial'+statusId).hide();
            $('#statusClientSortedBlock'+statusId).load(url);
        });
});

function displayOption(clientId) {
    $("#option_" + clientId).show();
}
