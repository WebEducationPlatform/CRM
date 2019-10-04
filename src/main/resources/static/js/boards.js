$.ajax({
    dataType : "json",
    url : '/rest/board/boards',
    headers : {
        'Accept' : 'application/json',
        'Content-Type' : 'application/json'
    },
    type : 'GET',
    success : function(data) {
        for (let i = 0; i < data.length; i++) {
            $(".board-links-ul").append(
                '<li><a class="dropdown-item" href="/'+ data[i].id +'">' + data[i].name + '</a></li>'
            );
        }
    }
});

$(document).ready(function() {

    $(".board-links li")
        .hover(function() {
            $('.board-links-ul').show();

        },function(){
            $('.board-links-ul').hide();
        });
});