function sendComment(id) {
    var url = '/rest/comment/add';
    var text = $('#new-text-for-client' + id).val();
    if (text === "") {
        return;
    }
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: url,
        data: {
            clientId: id,
            content: text
        },
        success: function (comment) {
            $('#new-text-for-client' + id).val("");
            $('#client-' + id + 'comments').prepend(
            '<li class="list-group-item comment-item">' +
                '<div id="comment' + comment.id + '" class="comment">' +
                '<span class="comment-name">' + comment.user.lastName + ' ' + comment.user.firstName + '</span>' +
                '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteComment('+ comment.id + ')"></span>' +
                '<span class="edit-comment glyphicon glyphicon-pencil comment-functional"></span>' +
                '<span class="hide-show glyphicon glyphicon-comment comment-functional"></span>' +
                '<p class="comment-text" ">' + comment.content + '</p>' +
                    '<div class="form-answer">' +
                        '<div class="form-group">' +
                            '<textarea class="form-control textcomplete" id="new-answer-for-comment' + comment.id + '" placeholder="Напишите ответ"></textarea>' +
                            '<button class="btn btn-md btn-success comment-button" onclick="sendAnswer(' + comment.id + ', \'test_message\')"> сохранить </button>' +
                        '</div>' +
                    '</div>' +
                    '<div class="form-edit">' +
                        '<div class="form-group">' +
                            '<textarea class="form-control edit-textarea"' +
                            'id="edit-comment' + comment.id + '" placeholder="Редактор"></textarea>' +
                            '<button class="btn btn-md btn-success comment-button" onclick="editComment(' + comment.id + ')"> Отредактировать </button>' +
                        '</div>' +
                    '</div>' +
                '<ul class="answer-list comment-item" id="comment-'+ comment.id + 'answers">' +
                '</ul>' +
                '</div>' +
            '</li>'
            );
        },
        error: function (error) {
            console.log(error);
        }
    });

}

function sendAnswer(id) {
    var url = '/rest/comment/add/answer';
    var text = $('#new-answer-for-comment' + id).val();
    if (text === "") {
        return;
    }
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: url,
        data: {
            commentId: id,
            content: text
        },
        success: function (comment) {
            $('#new-answer-for-comment' + id).val("");
            $('#comment-' + id + 'answers').prepend(
                "<div id='comment" + comment.id + "' class='comment'>" +
                "<li class='comment-item'>" +
                "   <h4><span>" + comment.user.firstName + " " + comment.user.lastName +"</span></h4>" +
                "   <span class='glyphicon glyphicon-remove comment-functional' onclick='deleteComment(" + comment.id + ")'></span>" +
                "   <span class='edit-comment glyphicon glyphicon-pencil comment-functional'></span>" +
                "   <p class='comment-text '>" + comment.content + "</p>" +
                "   <div class='form-edit'>" +
                "   <div class='form-group'>" +
                "   <textarea class='form-control edit-textarea textcomplete'" +
                "   id='edit-comment" + comment.id + "' placeholder='Редактор'></textarea>" +
                "   <button class='btn btn-md btn-success comment-button' onclick='editComment(" + comment.id + ")'> Отредактировать </button>" +
                "   </div>" +
                "   </div>" +
                "</li>"
            );
            $('.form-answer').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function deleteComment(id) {
    var url = "/rest/comment/delete";

    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id : id
        },
        success: function () {
            $('#comment' + id).detach();
        },
        error : function (error) {
            console.log(error);
        }
    })
}

function editComment(id) {
    var url = "/rest/comment/edit";
    var content = $('#edit-comment' + id).val();
    if (content === "") {
        return;
    }
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id : id,
            content : content
        },
        success: function () {
            $('.form-edit').hide();
            $('#comment' + id + ' .comment-text:first').text(content).show();
        },
        error : function (error) {
            console.log(error);
        }
    })
}

$(document).on('click', '.edit-comment', function () {
    $(document).find('.form-answer').hide();
    e = $(this).closest('.comment').find('.comment-text:first');
    i = $(this).closest('.comment').find('.form-edit:first');
    if (e.is(':visible')) {
        $(document).find('.form-edit').hide();
        $(document).find('.comment-text').show();
        e.hide();
        i.show();
        $('textarea.edit-textarea').text(e.text());
    } else {
        $(document).find('.form-edit').hide();
        i.hide();
        e.show();
    }
});

$(document).on('click', '.hide-show', function () {
    $(document).find('.form-edit').hide();
    $(document).find('.comment-text').show();

    e = $(this).closest('.list-group-item').find('.form-answer');
    if (e.is(':visible')) {
        e.hide();
    } else {
        e.show();
    }
});

function markAsRead(clientId) {
    var url = "/rest/comment/markAsRead";
    if ($('#info-client' + clientId).find("#notification").length !== 0) {
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: url,
            data: {
                id: clientId
            },
            success: function () {
                $('#info-client' + clientId).find(".notification").remove();
                $('.menu' + clientId).remove();

            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}


function markAsReadMenu(clientId) {
    var url = "/rest/comment/markAsRead";
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id: clientId
        },
        success: function () {
            $('#info-client' + clientId).find(".notification").remove();
            $('.menu' + clientId).remove();
        },
        error : function (error) {
            console.log(error);
        }
    })
}