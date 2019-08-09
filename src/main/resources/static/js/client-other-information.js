//Счетчик вариантов для вопроса с неск. чекбоксами (по дефолту 2 варианта + 1)
count = 3;

oimcLength = 0;

$(document).mouseup(function (e){
    var div = $("#myModal");
    if (!div.is(e.target) && div.has(e.target).length === 0) {
        removeEmptyVariants();
    }
});

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
            $("#app-label").prop("disabled", false);
            document.getElementById("app-label").value = obj.nameField;

            if (obj.typeField === "CHECKBOX") {
                document.getElementById('option0').setAttribute('selected', 'selected');
            } else if (obj.typeField === "CHECKBOXES") {
                document.getElementById('option1').setAttribute('selected', 'selected');
                oimcLength = obj.oimc.length;
                $('#app-name-edit-multiple').append("<label id='app-variant' for='app-variant-1'>Варианты чекбоксов:</label>");
                var addButton = "<button " + "id=" + "app-button-add" + " class='btn btn-info btn-sm'" + " type='button'" +
                    " onclick='addUpdateVariant()'>" + "Добавить чекбокс" + "</button>";
                $('#app-name-edit-multiple').append(addButton);
                for (var j = 1; j <= oimcLength; j++) {
                //for (var j = oimcLength; j >= 1; j--) {
                    var input = "<input " + "id=" + "app-variant-" + j + " type='text' class='form-control'/>" +
                        "<button " + "id=" + "app-button-remove-" + j + " class='btn btn-danger btn-sm'" + " type='button'" +
                        " onclick='removeCurrent(" + j + ")'>" + "Удалить чекбокс" + "</button>";
                    $('#app-name-edit-multiple').append(input);
                    document.getElementById("app-variant-" + j).value = obj.oimc[j - 1].nameField;
                }
            } else {
                document.getElementById('option2').setAttribute('selected', 'selected');
                $('#app-name-edit-multiple').append("<label for='app-field-edit'>Поле в карточке:</label>");
                input = "<select " + "class='form-control'" + " id='app-field-edit'>" + "<option" + " id='app-field-edit-0'>" + "-</option>"
                    + "<option" + " id='app-field-edit-1'>" + "Email</option>" + "<option" + " id='app-field-edit-2'>" + "Номер телефона</option>"
                    + "<option" + " id='app-field-edit-3'>" + "Skype</option>"
                    + "<option" + " id='app-field-edit-4'>" + "Дата рождения</option>" + "<option" + " id='app-field-edit-5'>" + "Возраст</option>"
                    + "<option" + " id='app-field-edit-6'>" + "Пол</option>"
                    + "<option" + " id='app-field-edit-7'>" + "Страна</option>" + "<option" + " id='app-field-edit-8'>" + "Город</option>"
                    + "<option" + " id='app-field-edit-9'>" + "Университет</option>"
                    + "</select>";
                $('#app-name-edit-multiple').append(input);
                if (obj.cardField === 'EMAIL') {
                    document.getElementById('app-field-edit-1').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'PHONE') {
                    document.getElementById('app-field-edit-2').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'SKYPE') {
                    document.getElementById('app-field-edit-3').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'BIRTHDAY') {
                    document.getElementById('app-field-edit-4').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'AGE') {
                    document.getElementById('app-field-edit-5').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'GENDER') {
                    document.getElementById('app-field-edit-6').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'COUNTRY') {
                    document.getElementById('app-field-edit-7').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'CITY') {
                    document.getElementById('app-field-edit-8').setAttribute('selected', 'selected');
                } else if (obj.cardField === 'UNIVER') {
                    document.getElementById('app-field-edit-9').setAttribute('selected', 'selected');
                } else {
                    document.getElementById('app-field-edit-0').setAttribute('selected', 'selected');
                }
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function addUpdateVariant() {
    oimcLength++;
    var input = "<input " + "id=" + "app-variant-" + oimcLength + " type='text' class='form-control'/>" +
        "<button " + "id=" + "app-button-remove-" + oimcLength + " class='btn btn-danger btn-sm'" + " type='button'" +
        " onclick='removeCurrent(" + oimcLength + ")'>" + "Удалить чекбокс" + "</button>";
    $('#app-name-edit-multiple').append(input);
}

function removeEmptyVariants() {
    $('#app-variant').remove();
    $('#app-button-add').remove();
    $('#app-field-edit').remove();
    for (var j = 1; j <= oimcLength; j++) {
        $('#app-variant-' + j).remove();
        $('#app-button-remove-' + j).remove();
    }
}

function removeCurrent(position) {
    $('#app-variant-' + position).remove();
    $('#app-button-remove-' + position).remove();
}

function createCheckboxes() {
    var table = document.getElementById("other-information-table");
    var rowCount = table.rows.length;
    if (rowCount === 0) {
        rowCount = 1;
    }
    var nameField = document.getElementById("app-name-create-several-checkboxes").value;

    var oimc = [];
    for (var j = 1; j < count; j++) {
        var value = document.getElementById("app-name-create-several-checkboxes-" + j).value;
        if (value === "") {
            return;
        }
        oimc.push(value);
    }
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
    var typeField = "CHECKBOXES";
    var url = "/otherInformation/create";

    var mass = {
        numberField: rowCount,
        nameField: nameField,
        typeField: typeField,
        oimc: oimc
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

    var type = document.getElementById("app-field").options.selectedIndex;
    if (type === 1) {
        var cardField = 'EMAIL';
    } else if (type === 2) {
        cardField = 'PHONE';
    } else if (type === 3) {
        cardField = 'SKYPE';
    } else if (type === 4) {
        cardField = 'BIRTHDAY';
    } else if (type === 5) {
        cardField = 'AGE';
    } else if (type === 6) {
        cardField = 'GENDER';
    } else if (type === 7) {
        cardField = 'COUNTRY';
    } else if (type === 8) {
        cardField = 'CITY';
    } else if (type === 9) {
        cardField = 'UNIVER';
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
        typeField: typeField,
        cardField: cardField
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

    var oimc = [];


    var type = document.getElementById("app-type").options.selectedIndex;
    if (type === 0) {
        type = "CHECKBOX";
    } else if (type === 1) {
        type = "CHECKBOXES";
        for (var j = oimcLength; j >= 1; j--) {
            var element = document.getElementById("app-variant-" + j);
            if (element) {
                oimc.push(element.value);
            }
        }
    } else {
        type = "TEXT";

        var option = document.getElementById("app-field-edit").options.selectedIndex;
        if (option === 1) {
            var cardField = 'EMAIL';
        } else if (option === 2) {
            cardField = 'PHONE';
        } else if (option === 3) {
            cardField = 'SKYPE';
        } else if (option === 4) {
            cardField = 'BIRTHDAY';
        } else if (option === 5) {
            cardField = 'AGE';
        } else if (option === 6) {
            cardField = 'GENDER';
        } else if (option === 7) {
            cardField = 'COUNTRY';
        } else if (option === 8) {
            cardField = 'CITY';
        } else if (option === 9) {
            cardField = 'UNIVER';
        }
    }

    var url = "otherInformation/update/" + id;
    var mass = {
        numberField: rowNumber,
        nameField: nameField,
        typeField: type,
        cardField: cardField,
        oimc: oimc
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

function addVariant() {
    var input = "<input " + "id=" + "app-name-create-several-checkboxes-" + count +
        " type='text' class='form-control'/>";
    $('#app-name-create-multiple').append(input);
    count += 1;
}

function removeVariant() {
    $("#app-name-create-several-checkboxes-" + (count - 1)).remove();
    count -= 1;
}