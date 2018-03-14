function sendComment(id) {
    var url = '/admin/rest/comment/add';
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
                '<span class="comment-name">' + comment.user.lastName + ' ' + comment.user.firstName + '</span>' +
                '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteComment('+ comment.id + ')"></span>' +
                '<span class="edit-comment glyphicon glyphicon-pencil comment-functional"></span>' +
                '<span class="hide-show glyphicon glyphicon-comment comment-functional"></span>' +
                '<p class="comment-text" ">' + comment.content + '</p>' +
                    '<div class="form-answer">' +
                        '<div class="form-group">' +
                            '<textarea class="form-control" id="new-answer-for-comment' + comment.id + '" placeholder="Напишите ответ"></textarea>' +
                            '<button class="btn btn-md btn-success" onclick="sendAnswer(' + comment.id + ', \'test_message\')"> сохранить </button>' +
                        '</div>' +
                    '</div>' +
                    '<div class="form-edit">' +
                        '<div class="form-group">' +
                            '<textarea class="form-control edit-textarea"' +
                            'id="edit-comment' + comment.id + '" placeholder="Редактор"></textarea>' +
                            '<button class="btn btn-md btn-success" onclick="editComment(' + comment.id + ')"> Отредактировать </button>' +
                        '</div>' +
                    '</div>' +
                '<ul class="answer-list comment-item" id="comment-'+ comment.id + 'answers">' +
                '</ul>' +
            '</li>'
            );
        },
        error: function (error) {
            console.log(error);
        }
    });

}

function sendAnswer(id) {
    var url = '/admin/rest/comment/addAnswer';
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
                "<li class='comment-item'>" +
                "   <h4><span>" + comment.user.firstName + " " + comment.user.lastName +"</span></h4>" +
                "   <p class='comment-text '>" + comment.content + "</p>" +
                "</li>"
            );
            $('.form-answer').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function deleteComment(commentId) {
    var url = "/admin/rest/comment/deleteComment";

    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id : commentId
        },
        success: function () {
            $('#comment' + commentId).detach();
        },
        error : function (error) {
            console.log(error);
        }
    })
}

function deleteAnswer(answerId, commentId) {
    var url = "/admin/rest/comment/deleteAnswer";

    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            answerId: answerId,
            commentId: commentId
        },
        success: function () {
            $('#comment' + answerId).detach();
        },
        error : function (error) {
            console.log(error);
        }
    })
}

function editComment(id) {
    var url = "/admin/rest/comment/editComment";
    var content = $('#edit-comment' + id).val();

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
            $('#comment' + id + ' .comment-text').text(content).show();
        },
        error : function (error) {
            console.log(error);
        }
    })
}

$(document).on('click', '.edit-comment', function () {
    $(document).find('.form-edit').hide();
    $(document).find('.comment-text').show();

    e = $(this).closest('.list-group-item').find('.comment-text:first');
    i = $(this).closest('.list-group-item').find('.form-edit:first');
    var content = e.text();
    if (e.is(':visible')) {
        e.hide();
        i.show();
        $('textarea.edit-textarea').text(content);
    } else {
        i.hide();
        e.show();
    }
});

$(document).on('click', '.hide-show', function () {
    e = $(this).closest('.list-group-item').find('.form-answer');
    if (e.is(':visible')) {
        e.hide();
    } else {
        e.show();
    }
});