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
      gHrList = hrList;
      console.log(textStatus);
      if (hrList.length > 0) {
        let result = "";
        $("#routes-table-body").html("");
        for (var i = 0; i < hrList.length; i++) {
          result += "<tr>" +
              "<td>" + hrList[i].id + "</td>" +
              "<td>" + hrList[i].firstName + "</td>" +
              "<td>" + hrList[i].lastName + "</td>" +
              "<td><input id='hr-" + hrList[i].id + "' name='hr-" + hrList[i].id + "' type='number' min='0' max='100' value=0 /></td></tr>";
        }
        $("#routes-table-body").html(result);
        for (var i = 0; i < hrList.length; i++) {
          if (hrList[i].userRoutes && hrList[i].userRoutes.length > 0) {
            for (var y = 0; y < hrList[i].userRoutes.length; y++) {
              if (hrList[i].userRoutes[y].userRouteType == routeType) {
                $("#hr-" + hrList[i].id ).val(hrList[i].userRoutes[y].weight);
              }
            }
          }
        }
      } else {
        alert("Данных нет");
      }
    },

    error: function (error) {
      console.log(error);
    }
  });
}

function saveUserRoutes() {
  let hr_routes = [];
  let routeType = $("#clientroutes-list-type").val();

  if ( checkSummPercents( $("input[id^='hr-']")) > 100 ){
    alert ("Сумма всех полей больше 100");
    return;
  }
  $("#routes-table-body  :input").each(function (indx, element) {
    hr_routes.push({
      id:null,
      user_id: parseInt($(element).attr('name').replace(/hr-/g, '')),
      weight: parseInt($(element).val()),
      userRouteType: routeType
    });
  });
  let json = JSON.stringify(hr_routes);
  let url = "/rest/hr/saveroutes";
  $.ajax({
    url: url,
    contentType: "application/json",
    type: 'POST',
    dataType: 'JSON',
    data: json,
    success: function () {
      alert("Сохранено");
      // $("#routes-modal-window").modal('hide');
    },
    error: function (jqxhr, status, errorMsg) {
      alert("При выполнении запроса произошла ошибка\n" +
          "Статус: " + status + " Ошибка: " + errorMsg + "\n"+
          jqxhr);
    }
  });
}

function checkSummPercents( inputLists){
  let sum=0;
  $.each(inputLists,function(){sum+=parseFloat($( this ).val()) || 0;});
  return sum;
}
