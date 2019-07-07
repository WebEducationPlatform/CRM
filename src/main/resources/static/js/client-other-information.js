function deleteOtherInformationById(id) {
    var url = "/otherInformation/delete/" + id;
    $.ajax({
        type: "DELETE",
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

function getOtherInformationById(id) {
    var url = "/otherInformation/" + id;
    $.ajax({
        url: url,
        contentType: "application/json",
        type: 'GET',
        dataType: 'JSON',
        success: function (obj) {
            document.getElementById("app-id").value = id;
            document.getElementById("app-rowNumber").value = obj.numberField;
            var name = obj.nameField;
            $("#app-label").prop("disabled", false);
            document.getElementById("app-label").value = name;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function createCheckbox() {
    var table = document.getElementById("other-information-table");
    var rowCount = table.rows.length;
    if (rowCount === 0) {
        rowCount = 1;
    }
    var nameField = document.getElementById("app-name-create-checkbox").value;
    if (nameField === "") {
        return;
    }
    for (var i = 1; i < table.rows.length; i++) {
        var row = table.rows[i];
        var name = row.cells[1].textContent;
        if (name === nameField) {
            return;
        }
    }
    var typeField = "CHECKBOX";
    var url = "/otherInformation/create";
    var mass = {
        numberField: rowCount,
        nameField: nameField,
        typeField: typeField
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

function createText() {
    var table = document.getElementById("other-information-table");
    var rowCount = table.rows.length;
    if (rowCount === 0) {
        rowCount = 1;
    }
    var nameField = document.getElementById("app-name-create-text").value;
    if (nameField === "") {
        return;
    }
    for (var i = 1; i < table.rows.length; i++) {
        var row = table.rows[i];
        var name = row.cells[1].textContent;
        if (name === nameField) {
            return;
        }
    }
    var typeField = "TEXT";
    var url = "/otherInformation/create";
    var mass = {
        numberField: rowCount,
        nameField: nameField,
        typeField: typeField
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

function updateOtherInformation() {
    var id = document.getElementById("app-id").value;
    var nameField = document.getElementById("app-label").value;
    var rowNumber = document.getElementById("app-rowNumber").value;
    if ("" === nameField) {
        alert("Нельзя создать пустое поле!");
        return;
    }

    var type = document.getElementById("app-type").options.selectedIndex;
    if (type === 0) {
        type = "CHECKBOX";
    } else {
        type = "TEXT";
    }

    var url = "otherInformation/update/" + id;
    var mass = {
        numberField: rowNumber,
        nameField: nameField,
        typeField: type
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