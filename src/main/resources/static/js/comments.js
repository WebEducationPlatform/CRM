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
            '<li class="list-group-item">' +
                '<span class="comment-name">' + comment.user.lastName + ' ' + comment.user.firstName + '</span>' +
                '<span class="hideshow">Ответить</span>' +
                '<p class="comment-text" ">' + comment.content + '</p>' +
                    '<div class="form-answer">' +
                        '<div class="form-group">' +
                            '<textarea class="form-control" id="new-answer-for-comment' + comment.id + '" placeholder="Напишите ответ"></textarea>' +
                            '<button class="btn btn-md btn-success" onclick="sendAnswer(' + comment.id + ', \'test_message\')"> сохранить </button>' +
                        '</div>' +
                    '</div>' +
                '<ul class="answer-list" id="\'comment-'+ comment.id + 'answers\'">' +
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
                "<li>" +
                "   <h4><span>" + comment.user.firstName + " " + comment.user.lastName +"</span></h4>" +
                "   <p class='comment-text '>" + comment.content + "</p>" +
                "</li>"
            );
        },
        error: function (error) {
            console.log(error);
        }
    });
}




