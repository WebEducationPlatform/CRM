//Search by keyword
$(document).ready(function () {
  $("#searchInput").keyup(function () {
    const value = $(this).val().toLowerCase();
    $("#table-body").find("tr").filter(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });
});


$('#routes-modal-window').on('shown.bs.modal', function () {
  alert($("#clientroutes-list-type").val());
})

function setUserRoutesTypes(){
  alert($(this).text());
}
