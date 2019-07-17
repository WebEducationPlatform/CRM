//Сортировка клиентов в статусах
$(".change-client-order").on('click', function () {
    let newOrder = $(this).attr("id");
    let statusId = $(this).parents(".column").attr("value");
    console.log(statusId + " " + newOrder);
    $.post("/rest/client/order", {newOrder: newOrder, statusId: statusId})
        .done(function () {

            // location.reload();
        });
});


function displayOption(clientId) {
    $("#option_" + clientId).show();
}