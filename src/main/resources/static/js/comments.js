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
                '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteComment(' + comment.id + ')"></span>' +
                '<span class="edit-comment glyphicon glyphicon-pencil comment-functional"></span>' +
                '<span class="hide-show glyphicon glyphicon-comment comment-functional"></span>' +
                '<span  class="comment-functional">'+ comment.dateFormat +'</span>' +
                '<p class="comment-text">' + comment.content + '</p>' +
                '<div class="form-answer">' +
                '<div class="form-group">' +
                '<textarea class="form-control textcomplete" id="new-answer-for-comment' + comment.id + '" placeholder="Напишите ответ"></textarea>' +
                '<button class="btn btn-md btn-success comment-button" onclick="sendAnswer(' + comment.id + ', \'test_message\')"> Сохранить </button>' +
                '</div>' +
                '</div>' +
                '<div class="form-edit">' +
                '<div class="form-group">' +
                '<textarea class="form-control edit-textarea"' +
                ' id="edit-comment' + comment.id + '" placeholder="Редактор"></textarea>' +
                '<button class="btn btn-md btn-success comment-button" onclick="editComment(' + comment.id + ')"> Отредактировать </button>' +
                '</div>' +
                '</div>' +
                '<ul class="answer-list comment-item" id="comment-' + comment.id + 'answers">' +
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

$(function () {
    $('#main-modal-window').on('shown.bs.modal', function (event) {
        var client_id = $(this).data('clientId');
        let user_id;
        $.get('rest/client/getPrincipal', function (user) {
            user_id = user.id + '';
        });
    let url = '/rest/comment/getComments/' + client_id;
    let ulComments = $('#client-' + client_id + 'comments');
    let removeComment = "";
    let editComment = "";
    let removeAnswer = "";
    let editAnswer = "";
    let dateCommit = "";
    let html = "";
    ulComments.empty();
    $.ajax({
        type: 'get',
        contentType: "application/json",
        dataType: 'json',
        url: url,
        success: function (list) {
            for (let i = list.length-1; i >=0; i--) {
                // if (list[i].mainComment == null) {
                    if (user_id === list[i].user.id + '') {
                        removeComment = '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteComment(' + list[i].id + ')"></span>'
                        editComment = '<span class="edit-comment glyphicon glyphicon-pencil comment-functional"></span>'

                    } else {
                        removeComment = '';
                        editComment = '';

                    }
                    html +=
                        '<li class="list-group-item comment-item">' +
                        '<div id="comment' + list[i].id + '" class="comment">' +
                        '<span class="comment-name">' + list[i].user.lastName + ' ' + list[i].user.firstName + '</span>' +
                        removeComment +
                        editComment +
                        '<span class="hide-show glyphicon glyphicon-comment comment-functional"></span>' +
                        '<span  class="comment-functional">'+ list[i].dateFormat +'</span>' +
                        '<p class="comment-text" ">' + list[i].content + '</p>' +
                        '<div class="form-answer">' +
                        '<div class="form-group">' +
                        '<textarea class="form-control textcomplete" id="new-answer-for-comment' + list[i].id + '" placeholder="Напишите ответ"></textarea>' +
                        '<button class="btn btn-md btn-success comment-button" onclick="sendAnswer(' + list[i].id + ', \'test_message\')"> Сохранить </button>' +
                        '</div>' +
                        '</div>' +
                        '<div class="form-edit">' +
                        '<div class="form-group">' +
                        '<textarea class="form-control edit-textarea textcomplete"' +
                        ' id="edit-comment' + list[i].id + '" placeholder="Редактор"></textarea>' +
                        '<button class="btn btn-md btn-success comment-button" onclick="editComment(' + list[i].id + ')"> Отредактировать </button>' +
                        '</div>' +
                        '</div>' +
                        '<ul class="answer-list comment-item" id="comment-' + list[i].id + 'answers">';
                    let answers = list[i].commentAnswers;
                    for (let i = 0; i < answers.length; i++) {
                        if (user_id === answers[i].user.id + '') {
                            removeAnswer = '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteCommentAnswer(' + answers[i].id + ')"></span>'
                            editAnswer = '<span class="edit-answer glyphicon glyphicon-pencil comment-functional"></span>'
                        } else {
                            removeAnswer = '';
                            editAnswer = '';
                        }
                        html +=
                            '<li>\n' +
                            //comment
                            '<div id="answer' + answers[i].id + '" class="answer">\n' +
                            //comment-name
                          '<span class="comment-name">' + answers[i].user.lastName + ' ' + answers[i].user.firstName + '</span>' +
                            removeAnswer +
                            editAnswer +
                            '<span  class="comment-functional">'+ answers[i].dateFormat +'</span>' +
                            //comment-text
                           '<p class="comment-text" ">' + answers[i].content + '</p>' +
                            '<div class="form-edit">' +
                            '<div class="form-group">' +
                            '<textarea class="form-control edit-textarea textcomplete" ' +
                            //edit-comment
                            ' id="edit-answer' + answers[i].id + '" placeholder="Редактор"></textarea>' +
                            '<button class="btn btn-md btn-success comment-button" onclick="editCommentAnswer(' + answers[i].id + ')"> Отредактировать </button>' +
                            '                                </div>\n' +
                            '                            </div>\n' +
                            '                        </div>\n' +
                            '                    </li>\n';
                    }

                    html += '</ul>' +
                            '</div>' +
                            '</li>';

               // }
            }
            ulComments.append(html);
        },
        error: function (error) {
            console.log(error);
        }
    });
    });
});

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
        success: function (answer) {
            $('#new-answer-for-comment' + id).val("");
            $('#comment-' + id + 'answers').prepend(
                //comment
                "<div id='answer" + answer.id + "' class='answer'>" +
                "<li class='comment-item'>" +
                "   <h4><span>" + answer.user.firstName + " " + answer.user.lastName + "</span></h4>" +
                "   <span class='glyphicon glyphicon-remove comment-functional' onclick='deleteCommentAnswer(" + answer.id + ")'></span>" +
                //edit-comment
                "   <span class='edit-answer glyphicon glyphicon-pencil comment-functional'></span>" +
                '   <span  class="comment-functional">'+ answer.dateFormat +'</span>' +
                //comment-text
                "   <p class='comment-text '>" + answer.content + "</p>" +
                "   <div class='form-edit'>" +
                "   <div class='form-group'>" +
                "   <textarea class='form-control edit-textarea textcomplete'" +
                //edit-comment
                "   id='edit-answer" + answer.id + "' placeholder='Редактор'></textarea>" +
                "   <button class='btn btn-md btn-success comment-button' onclick='editCommentAnswer(" + answer.id + ")'> Отредактировать </button>" +
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
            id: id
        },
        success: function () {
            $('#comment' + id).detach();
        },
        error: function (error) {
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
            id: id,
            content: content
        },
        success: function () {
            $('.form-edit').hide();
            $('#comment' + id + ' .comment-text:first').text(content).show();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function deleteCommentAnswer(id) {
    var url = "/rest/comment/delete/answer";

    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id: id
        },
        success: function () {
            $('#answer' + id).detach();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function editCommentAnswer(id) {
    var url = "/rest/comment/edit/answer";
    var content = $('#edit-answer' + id).val();
    if (content === "") {
        return;
    }
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: url,
        data: {
            id: id,
            content: content
        },
        success: function () {
            $('.form-edit').hide();
            //comment
            $('#answer' + id + ' .comment-text:first').text(content).show();
        },
        error: function (error) {
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

$(document).on('click', '.edit-answer', function () {
    $(document).find('.form-answer').hide();
    e = $(this).closest('.answer').find('.comment-text:first');
    i = $(this).closest('.answer').find('.form-edit:first');
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

