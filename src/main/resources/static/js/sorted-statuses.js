$(document).ready(function () {
  let i = 1;
  while ($("#statusSortedMenu"+i).length){
    const statusId = $("#statusSortedMenu"+i).attr("value");
    $.get("/rest/client/order", {statusId: statusId})
      .done(function (order) {
        $("#"+order+statusId).addClass("active");
      });
    const url = "/status/" + statusId;
    $('#statusClientSortedBlock'+statusId).load(url);
    i++;
  }
});

//Сортировка клиентов в статусах
$(".change-client-order").on('click', function () {
    const newOrder = $(this).attr("value");
    const statusId = $(this).parents(".column").attr("value");
    console.log(statusId + " " + newOrder);
    $.post("/rest/client/order", {newOrder: newOrder, statusId: statusId})
        .done(function () {
            const url = "/status/" + statusId;
            $('#statusClientSortedBlock'+statusId).load(url);
        });
    clearActiveClientOrder(statusId);
    $("#"+newOrder+statusId).addClass("active");
});

function clearActiveClientOrder(statusId) {
  $("#NEW_FIRST"+statusId).removeClass("active");
  $("#OLD_FIRST"+statusId).removeClass("active");
  $("#NEW_CHANGES_FIRST"+statusId).removeClass("active");
  $("#OLD_CHANGES_FIRST"+statusId).removeClass("active");
}

function displayOption(clientId) {
    $("#option_" + clientId).show();
}
