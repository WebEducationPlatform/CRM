function deleteVkReqestById(id) {
    var url = "/vk/request/delete/" + id;
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        complete: function () {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function createVkRequestField() {
    var table = document.getElementById("vk-request-table");
    var rowCount = table.rows.length;
    if (rowCount === 0) {
        rowCount = 1;
    }
    var indexForNameRequest = document.getElementById("app-name-create").options.selectedIndex;
    var nameRequest = document.getElementById("app-name-create").options[indexForNameRequest].text;
    if (nameRequest === "") {
        return;
    }

    for (var i = 1; i < table.rows.length; i++) {
        var row = table.rows[i];
        var name = row.cells[1].textContent;
        if (nameRequest === name) {
            return;
        }
    }

    var url = "/vk/request/create";
    var mass = {
        numberVkField: rowCount,
        nameVkField: nameRequest,
        typeVkField: "Обязательное"
    };
    var emp = JSON.stringify(mass);
    $.ajax({
        url: url,
        data: emp,
        contentType: "application/json",
        type: 'POST',
        dataType: 'JSON',
        complete: function () {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function createVkRequestFieldForLabel() {
    var table = document.getElementById("vk-request-table");
    var rowCount = table.rows.length;
    if (rowCount === 0) {
        rowCount = 1;
    }
    var nameRequestField = document.getElementById("app-name-create-label").value;
    if (nameRequestField === "") {
        return;
    }
    for (var i = 1; i < table.rows.length; i++) {
        var row = table.rows[i];
        var name = row.cells[1].textContent;
        if (name === nameRequestField) {
            return;
        }
    }
    var typeRequestField = "В заметки";
    var url = "/vk/request/create";
    var mass = {
        numberVkField: rowCount,
        nameVkField: nameRequestField,
        typeVkField: typeRequestField
    };
    var emp = JSON.stringify(mass);
    $.ajax({
        url: url,
        data: emp,
        contentType: "application/json",
        type: 'POST',
        dataType: 'JSON',
        success: function (returnObj) {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
    location.reload();

}

function getVkRequestById(id) {
    var url = "/vk/request/" + id;
    $.ajax({
        url: url,
        contentType: "application/json",
        type: 'GET',
        dataType: 'JSON',
        success: function (obj) {
            document.getElementById("app-id").value = id;
            document.getElementById("app-rowNumber").value = obj.numberVkField;
            var nameVk = obj.nameVkField;
            if (nameVk === "Имя" || nameVk === "Фамилия" || nameVk === "Номер телефона" || nameVk === "Email" || nameVk === "Skype"
                || nameVk === "Возраст" || nameVk === "Пол" || nameVk === "Страна" || nameVk === "Город") {
                $("#app-type").prop("disabled", false);
                $("#app-name").prop("disabled", false);
                document.getElementById("app-label").value = "";
                $("#app-label").prop("disabled", true);
                if (nameVk === "Имя") {
                    document.getElementById("app-name").value = '1';
                } else if (nameVk === "Фамилия") {
                    document.getElementById("app-name").value = '2';
                } else if (nameVk === "Номер телефона") {
                    document.getElementById("app-name").value = '3';
                } else if (nameVk === "Email") {
                    document.getElementById("app-name").value = '4';
                } else if (nameVk === "Skype") {
                    document.getElementById("app-name").value = '5';
                } else if (nameVk === "Возраст") {
                    document.getElementById("app-name").value = '6';
                } else if (nameVk === "Пол") {
                    document.getElementById("app-name").value = '7';
                } else if (nameVk === "Страна") {
                    document.getElementById("app-name").value = '8';
                } else if (nameVk === "Город") {
                    document.getElementById("app-name").value = '9';
                }
                if (obj.typeVkField === "Обязательное") {
                    document.getElementById("app-type").value = '1';
                } else {
                    document.getElementById("app-type").value = '2';
                }
            } else {
                document.getElementById("app-name").value = '0';
                document.getElementById("app-type").value = '0';
                $("#app-type").prop("disabled", true);
                $("#app-name").prop("disabled", true);
                $("#app-label").prop("disabled", false);
                document.getElementById("app-label").value = nameVk;
            }

        },
        error: function (error) {
            console.log(error);
        }
    });
}

function updateVkRequestField() {
    var table = document.getElementById("vk-request-table");
    var id = document.getElementById("app-id").value;
    var indexForNameRequest = document.getElementById("app-name").options.selectedIndex;
    var nameRequest = document.getElementById("app-name").options[indexForNameRequest].text;
    if (nameRequest === "") {
        return;
    }
    for (var i = 1; i < table.rows.length; i++) {
        var row = table.rows[i];
        var name = row.cells[1].textContent;
        if (nameRequest === name) {
            return;
        }
    }
    var index = document.getElementById("app-type").options.selectedIndex;
    var typeRequest = document.getElementById("app-type").options[index].text;
    var rowNumber = document.getElementById("app-rowNumber").value;
    var url = "/vk/request/update/" + id;
    var mass = {
        numberVkField: rowNumber,
        nameVkField: nameRequest,
        typeVkField: typeRequest
    };
    var emp = JSON.stringify(mass);
    $.ajax({
        url: url,
        data: emp,
        contentType: "application/json",
        type: 'PUT',
        dataType: 'JSON',
        success: function (returnObj) {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
    location.reload();
}

function updateVkRequestFieldForLabel() {
    var id = document.getElementById("app-id").value;
    var nameRequestField = document.getElementById("app-label").value;
    var rowNumber = document.getElementById("app-rowNumber").value;
    if ("" === nameRequestField) {
        alert("Нельзя создать пустое поле!");
        return;
    }
    var typeRequestField = "В заметки";
    var url = "/vk/request/update/" + id;
    var mass = {
        numberVkField: rowNumber,
        nameVkField: nameRequestField,
        typeVkField: typeRequestField
    };
    var emp = JSON.stringify(mass);
    $.ajax({
        url: url,
        data: emp,
        contentType: "application/json",
        type: 'PUT',
        dataType: 'JSON',
        success: function (returnObj) {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
    location.reload();
}

function updateVkRequest() {
    var name = document.getElementById("app-label").value;
    if (name.length === 0) {
        updateVkRequestField();
    } else {
        updateVkRequestFieldForLabel();
    }
}


$(function () {
    $("#vk-request-body").sortable({
        connectWith: ".connectedSortable",
        stop: function () {
            var table = document.getElementById("vk-request-table");
            for (var i = 1; i < table.rows.length; i++) {
                var row = table.rows[i];
                var id = row.cells[0].textContent;
                var name = row.cells[1].textContent;
                var type = row.cells[2].textContent;
                updateVkRequestAfterSort(id, name, type, i);
            }
        }
    }).disableSelection();

});

function updateVkRequestAfterSort(id, name, type, rowNumber) {
    var url = "/vk/request/update/" + id;
    var mass = {
        numberVkField: rowNumber,
        nameVkField: name,
        typeVkField: type
    };
    var emp = JSON.stringify(mass);
    $.ajax({
        url: url,
        data: emp,
        contentType: "application/json",
        type: 'PUT',
        dataType: 'JSON',
        success: function (returnObj) {
        },
        error: function (error) {
            console.log(error);
        }
    });
}




