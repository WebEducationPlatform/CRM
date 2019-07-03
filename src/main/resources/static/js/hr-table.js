//Search by keyword
$(document).ready(function () {
  $("#searchInput").keyup(function () {
    const value = $(this).val().toLowerCase();
    $("#table-body").find("tr").filter(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });
});
