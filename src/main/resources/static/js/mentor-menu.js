function setBooleanShowOnlyMyClients() {
    let showOnlyMyClients;
    if ($('#showOnlyMyClients').is(':checked')) {
        showOnlyMyClients = {
            showOnlyMyClients: true
        }
    } else {
        showOnlyMyClients = {
            showOnlyMyClients: false
        }
    }

    let url =
        $.ajax({
            type: "POST",
            url: "/mentor/showOnlyMyClients",
            data: showOnlyMyClients,
            success: function (data) {
                window.location.reload();
            },
            error: function (error) {
            }
        });
}

$(document).ready(function () {
    let user;
    $.ajax({
        type: "GET",
        url: "rest/client/getPrincipal",
        async: false,
        success: function (userFromSession) {
            user = userFromSession;
        },
        error: function (e) {
        }
    })

    let id = user["id"];
    console.log(id);
    let showAllClients;

    $.ajax({
        type: "GET",
        url: "/mentor/showOnlyMyClient/" + id,
        async: false,
        success: function (showAll) {
            showAllClients = showAll;
        },
        error: function (e) {
        }
    });

    console.log(showAllClients);

    if (showAllClients) {

        let firstLastLetter;

        $.ajax({
            type: "GET",
            url: "/mentor/firstLetterFromNameAndSurname/" + id,
            async: false,
            success: function (letterFirstLastLetters) {
                firstLastLetter = letterFirstLastLetters;
            },
            error: function (e) {
            }
        });

        console.log(firstLastLetter);
        var jo = $("#status-columns").find($(".portlet"));
        jo.hide();
        jo.filter(function (i, v) {
            var d = $(this)[0].getElementsByClassName("mentor-icon_card");
            if (d.length === 0) {
                return false;
            }

            if (d[0].innerText.indexOf(firstLastLetter) !== -1) {
                return true;
            }

        }).show();
    }
})

$(document).ready(function () {
    let user;
    $.ajax({
        type: "GET",
        url: "rest/client/getPrincipal",
        async: false,
        success: function (userFromSession) {
            user = userFromSession;
        },
        error: function (e) {
        }
    })

    let id = user["id"];
    let showAllClients;

    $.ajax({
        type: "GET",
        url: "/mentor/showOnlyMyClient/" + id,
        async: false,
        success: function (showAll) {
            showAllClients = showAll;
        },
        error: function (e) {
        }
    });
    if (showAllClients) {
        $("#showOnlyMyClientsDiv").append("Показывать только своих клиентов ");
        $("#showOnlyMyClientsDiv").append($('<input>', {
            class: "form-check-input",
            id: "showOnlyMyClients",
            type: "checkbox",
            checked: "checked",
            onclick: "setBooleanShowOnlyMyClients()"
        }));
    } else {
        $("#showOnlyMyClientsDiv").append("Показывать только своих клиентов ");
        $("#showOnlyMyClientsDiv").append($('<input>', {
            class: "form-check-input",
            id: "showOnlyMyClients",
            type: "checkbox",
            onclick: "setBooleanShowOnlyMyClients()"
        }));
    }
})
