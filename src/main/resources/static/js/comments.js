function sendComment(id) {
    var url = '/admin/rest/comment/add';
    var text = $('#new-text-for-client' + id).val();
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
                "<li>" +
                "   <span>" + comment.user.firstName + " " + comment.user.lastName +"</span>" +
                "   <p>" + comment.content + "</p>" +
                "</li>"
            );
        },
        error: function (error) {
            console.log(error);
        }
    });

}