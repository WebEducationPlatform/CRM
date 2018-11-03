function deleteBidById(id) {
    var url = "/bid/delete/" + id;
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        success: function () {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
    location.reload();
}

function createBid() {
    var val = document.getElementById("app-number-create").value;
    var name = document.getElementById("app-name-create").value;
    var n = document.getElementById("app-type-create").options.selectedIndex;
    var type = document.getElementById("app-type-create").options[n].text;
    var url = "/bid/create";
    var mass = {
        number: val,
        name: name,
        type: type
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

function getBidById(id) {
    var url = "/bid/getBid/" + id;
    $.ajax({
        url: url,
        contentType: "application/json",
        type: 'GET',
        dataType: 'JSON',
        success: function (obj) {
            document.getElementById("app-id").value = id;
            document.getElementById("app-number").value = obj.number;
            document.getElementById("app-name").value = obj.name;
            if (obj.type === "Обязательное") {
                document.getElementById("app-type").value = '1';
            } else {
                document.getElementById("app-type").value = '2';
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function updateBid() {
    var id = document.getElementById("app-id").value;
    var val = document.getElementById("app-number").value;
    var name = document.getElementById("app-name").value;
    var n = document.getElementById("app-type").options.selectedIndex;
    var type = document.getElementById("app-type").options[n].text;
    var url = "/bid/update/" + id;
    var mass = {
        number: val,
        name: name,
        type: type
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