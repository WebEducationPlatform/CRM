var learningStatus;
var templates;
var index = 0;
//init variables for dropdown list

function fillDropDownLists(){
    getStatuses();
    getMessageTemplates();
    addRowWithTemplate();
}


// auto-answer-modal
$('#auto-answer-modal').on('shown.bs.modal', function (e) {

    // create and fill template table
    $.ajax({
        type: 'GET',
        url: '/rest/subject-template',
        success: function (response) {
            document.getElementById("templatesBody").innerHTML = response;
            console.log(response);
        }
    });
});

function getStatuses(){
    // console.log("get statuses");
    $.ajax({
        type: 'GET',
        url: '/rest/status',
        dataType: 'json',
        success: function (json) {
            var idOfDropDownListOfStatuses = "dropDownStatus-" + index;

            if (learningStatus == "") {
                learningStatus = "<select class='study-status' id="+idOfDropDownListOfStatuses+">";
                learningStatus += "<option selected='selected'>Не добавлять в статус</option>"
                $.each(json, function (index, element) {
                    learningStatus += '<option value=' + element.name + '>' + element.name + '</option>';
                });
                learningStatus += "</select>";
            }
        }
    });
    // console.log("learning status: "+learningStatus);

}

function getMessageTemplates(){
    var idOfDropDownListOfTemplates = "dropDownTemplates-" + index;
    $.ajax({
        type: 'GET',
        url: '/rest/message-template',
        dataType: 'json',
        success: function (response) {
            templates = "<select class='message-template' id=" + idOfDropDownListOfTemplates + ">";
            // templates += "<option selected='selected'>Не выбрано</option>";
            $.each(response, function (i, item) {
                templates += "<option value="+item.name+">" + item.name + "</option>";
            });
            templates += "</select>";
        }
    });
    // console.log("templates: "+templates);
}

$("#templatesTable").on("click", ".remove-row-button", function(data) {
    $(this).closest("tr").remove();
    var templateTitle = $(this).closest("tr").children()[0].childNodes[0].value;
    var dropDownTemplate = $(this).closest("tr").children().eq(1).find(":selected").text();
    var dropDownStatus = $(this).closest("tr").children().eq(2).find(":selected").text();

    var jsonObject = JSON.stringify({"title":templateTitle, "template":dropDownTemplate, "status":dropDownStatus});

    removeData(jsonObject);
});

function removeData(data){
    $.ajax({
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        url: "/rest/subject-template/delete",
        data: JSON.stringify(data), // Note it is important
        success: function (result) {
            // do what ever you want with data
        }
    });
}

$("#templatesTable").on("click", ".save-row-button", function() {
    var templateTitle = $(this).closest("tr").children()[0].childNodes[0].value;
    var dropDownTemplate = $(this).closest("tr").children().eq(1).find(":selected").text();
    var dropDownStatus = $(this).closest("tr").children().eq(2).find(":selected").text();

    var jsonObject = JSON.stringify({"title":templateTitle, "template":dropDownTemplate, "status":dropDownStatus});
    postData(jsonObject);
});



// sending data to service for writing into database
function postData(data){

    $.ajax({
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        url: "/rest/subject-template",
        data: JSON.stringify(data), // Note it is important
        success: function (result) {
            // do what ever you want with data
        }
    });
}

$(document).ready(function () {
    getStatuses();
    getMessageTemplates();

    learningStatus = '';
    editor = CKEDITOR.replace('body', {
        allowedContent: true,
        height: '250px'
    });

    editor.addCommand("infoCommend", {
        exec: function (edt) {
            $("#infoModal").modal('show');
        }
    });

    editor.ui.addButton('SuperButton', {
        label: "Info",
        command: 'infoCommend',
        toolbar: 'styles',
        icon: 'info.png'
    });


});

function addRowWithTemplate(data) {
    var idOfMessageSubject = "messageSubject-" + index;
 // init table with assortments of templates
    var bodyOfTable = document.getElementById('templatesBody');
    var newRow = bodyOfTable.insertRow(-1);
   var newCell0 = newRow.insertCell(0);
   if(index == 0){
       newCell0.innerHTML = "<input type='text' name='template' class='template' id=" + idOfMessageSubject + " value='Не известный' readonly required> ";
   }else{
       newCell0.innerHTML = "<input type='text' name='template' class='template' id=" + idOfMessageSubject + " required>";
   }

    var newCell1 = newRow.insertCell(1);
    newCell1.innerHTML = templates;
    var newCell2 = newRow.insertCell(2);
    newCell2.innerHTML = learningStatus;
    var newCell3 = newRow.insertCell(3);
    var newCell4 = newRow.insertCell(4);
    if(index > 0){
        newCell3.innerHTML = "<button class=\"remove-row-button\"><i class=\"far fa-trash-alt\"></i></button>";
        newCell4.innerHTML = "<button class=\"save-row-button\"><i class=\"far fa-save\"></i></button>";
    }else{
        newCell3.innerHTML = "";
        newCell4.innerHTML = "<button class=\"save-row-button\"><i class=\"far fa-save\"></i></button>";
    }

    index++;
}
