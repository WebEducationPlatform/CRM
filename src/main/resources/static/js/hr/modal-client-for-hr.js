function openModalWindowWithUsersData(clientId) {

    let keysForClientsAttr;
    let client;
    let comments;

    function getClientsInfo() {
        return $.get("/rest/client/" + clientId)
            .done(function (clientFromServer) {
                //Array for fields in ClientModalBody
                keysForClientsAttr = {
                    "Email": clientFromServer.email,
                    "Телефон": clientFromServer.phoneNumber,
                    "Дата рождения": clientFromServer.birthDate,
                    "Возраст": clientFromServer.age,
                    "Город": clientFromServer.city
                };
                client = clientFromServer;
            });
    }

    function getClientsComments() {
        return $.get("/rest/comment/getComments/" + clientId)
            .done(function (commentsFromServer) {
                comments = commentsFromServer;
            });
    }

    //waiting all ajax before open client modal window
    $.when(getClientsInfo(), getClientsComments()).done(function () {
        $("#clientModal").modal('show');
    });

    //Steps before modal window open
    $("#clientModal").on("show.bs.modal", function () {
        drawModalTitle(client);
        drawModalBody(keysForClientsAttr);
        drawModalFooterComments(comments);
    });

    function drawModalTitle(client) {
        $(".modal-title").html("");
        $('<div></div>', {
            class: "clientIdForPage",
            id: client.id
        }).appendTo('h4.modal-title');
        $('<div></div>', {
            text: "Клиент: " + client.name + " " + client.lastName
        }).appendTo('h4.modal-title');
    }

    function drawModalBody(keysForClientsAttr) {
        $(".modal-body").html("");
        $.each(keysForClientsAttr, function (i, attr) {
            let elementId = i.split(" ")[0];
            if ((attr !== "") && (attr !== null) && (attr !== 0)) {
                $('<div></div>', {
                    class: "modal-body row",
                    id: elementId
                }).appendTo('div.modal-body.container-fluid');
                let element = 'div#' + elementId + '.modal-body.row';
                $('<div></div>', {
                    class: 'col-sm left-column',
                    text: i + ":"
                }).appendTo(element);
                $('<div></div>', {
                    class: 'col-sm middle-column',
                    text: attr
                }).appendTo(element);
                $('<div></div>', {
                    class: 'col-sm right-column ' + elementId + '-column'
                }).appendTo(element);
            }
        });
        if ($(".modal-body").is("#Телефон")) {
            drawVoximplantButtons();
        }
    }

    function drawModalFooterComments(comments) {
        //clear all bottom
        $("#sendForm").html("");
        $('ul#client-comments').contents().remove();
        //new comment text area
        $('<textarea></textarea>', {
            class: "form-control",
            placeholder: "Напишите комментарий",
            id: "newCommentsTextArea"
        }).appendTo('div#sendForm');
        //button Save new comment
        $('<button></button>', {
            class: "btn btn-sm btn-success comment-button remove-element",
            id: "saveNewCommentsButton",
            onclick: "sendComment()",
            text: "Сохранить"
        }).appendTo('div#sendForm');
        $('<li></li>', {
            class: 'list-group-item comment-item',
            id: "anchor_comment",
        }).prependTo('#client-comments');

        //draw comments
        $.each(comments, function (i, comment) {
            $('<li></li>', {
                class: 'list-group-item comment-item',
                id: "commentId" + comment.id,
            }).prependTo('#client-comments');
            $('<div></div>', {
                class: 'comment-text',
                text: comment.content,
                id: "commentIdText" + comment.id,
            }).prependTo('#commentId' + comment.id)
        })
    }

    function drawVoximplantButtons() {
        let element = 'div.Телефон-column';
        $('<button></button>', {
            class: "btn btn-default btn btn-light btn-xs call-to-client",
            onclick: "webCallToClient(" + client.phoneNumber + ")",
            id: "hrCall"
        }).appendTo(element);
        $('<span></span>', {
            class: "glyphicon glyphicon-earphone call-icon"
        }).appendTo('button#hrCall');

        $('<button></button>', {
            id: "btn-mic-off",
            class: "btn btn-default btn btn-light btn-xs web-call-mic-off",
            style: "display: none;"
        }).appendTo(element);
        $('<span></span>', {
            class: "glyphicon glyphicon-ice-lolly"
        }).appendTo('button#btn-mic-off');

        $('<button></button>', {
            id: "btn-call-off",
            class: "btn btn-default btn btn-light btn-xs web-call-off",
            style: "background: red; color: rgb(255, 255, 255); display: none;"
        }).appendTo(element);
        $('<span></span>', {
            class: "glyphicon glyphicon-phone-alt call-icon"
        }).appendTo('button#btn-call-off');
    }
}

function sendComment() {
    let id = $('.clientIdForPage').attr('id');
    let text = $('#newCommentsTextArea').val();
    if (id === "" || text === "") {
        return false;
    }
    let data = {
        clientId: id,
        content: text
    };
    $.post('/rest/comment/add', data)
        .done(function () {
            $('<li></li>', {
                class: 'list-group-item comment-item',
            }).prependTo($('#client-comments').find('li:first'));
            $('<div></div>', {
                class: 'comment-text',
                text: text
            }).prependTo($('#client-comments').find('li:first'))
            $('#newCommentsTextArea').val('');
        })
}