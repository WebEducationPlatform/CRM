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
      $(".alert").alert ('close');
      gHrList = hrList;
        $("#routes-table-body  input").val("0");
        // $("#hr-" + hrList[i].id ).val(hrList[i].userRoutes[y].weight);
      if (hrList.length > 0) {
        let result = "";
        // $("#routes-table-body").html("");
        // for (var i = 0; i < hrList.length; i++) {
        //   result += "<tr>" +
        //       "<td>" + hrList[i].user_id + "</td>" +
        //       "<td>" + hrList[i].first_name + "</td>" +
        //       "<td>" + hrList[i].last_name + "</td>" +
        //       "<td><input id='hr-" + hrList[i].user_id + "' name='hr-" + hrList[i].user_id +
        //       "' type='number' min='0' max='100' value='" + hrList[i].weight + "'/></td></tr>";
        // }
        // $("#routes-table-body").html(result);
        for (var i = 0; i < hrList.length; i++) {
                $("#hr-" + hrList[i].user_id ).val(hrList[i].weight);
        }
      }
    },

    error: function (error) {
      console.log(error);
    }
  });
}

function saveUserRoutes() {
  $(".alert").alert ('close');
  let hr_routes = [];
  let routeType = $("#clientroutes-list-type").val();

  if ( checkSummPercents( $("input[id^='hr-']")) != 100 ){
    $('<div class="alert alert-warning" role="alert">Сумма всех полей должна == 100 </div>').prependTo($(".modal-footer"));
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
      $('<div class="alert alert-success" role="alert">Данные сохранены</div>').prependTo($(".modal-footer"));
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
