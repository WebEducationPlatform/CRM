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
  setUserRoutesTypes();
})

function setUserRoutesTypes() {
  let routeType = $("#clientroutes-list-type").val();
  $.ajax({
    async: true,
    type: 'GET',
    dataType: "JSON",
    url: '/rest/hr/getuserroutesbytype/' + routeType,
    success: function (hrList, textStatus, XMLHttpRequest) {
      console.log(textStatus);
      if (hrList.length > 0) {
        let result = "";
        $("#routes-table-body").html("");
        for (var i = 0; i < hrList.length; i++) {
          result += "<tr>" +
              "<td>" + hrList[i].id + "</td>" +
              "<td>" + hrList[i].firstName + "</td>" +
              "<td>" + hrList[i].lastName + "</td>" +
              "<td><input name='hr_" + hrList[i].id + "' ";
          if (hrList[i].userRoutes && hrList[i].userRoutes.length > 0) {
            if(hrList[i].userRoutes[0].userRouteType == routeType){
              result += " value=" + hrList[i].userRoutes[0].weight + " /></td>" +
                  "</tr>";
            }
            else{
              result += " value=0 /></td></tr>";
            }
          }
          else{
            result += " value=0 /></td></tr>";
          }
        }
        $("#routes-table-body").html(result);

      } else {
        alert("Данных нет");
      }
    },

    error: function (error) {
      console.log(error);
    }
  });
}
