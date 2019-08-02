$(document).ready(function () {
    $('#main-modal-window').on('show.bs.modal', function ShowContent() {
        let current = $(document.getElementsByClassName("panel-collapse client-collapse collapse history"));
        current.collapse('show');
    });

    $('.upload-more-history').on("click", function uploadMoreHistory() {
        let current = $(this);
        let clientId = current.attr("data-clientid");
        let page = current.attr("data-page");
        let url = '/client/history/rest/getHistory/' + clientId;

        //Set sorting order and arrow direction.
        let arrow = $('#date').find('i');
        let isAsc = false;
        if (arrow.hasClass('fa-sort-up')) {
            isAsc = true;
        } else if (arrow.hasClass('fa-sort-down')) {
            isAsc = false;
        }

        let params = {
            page: page,
            isAsc: isAsc
        };
        let history_table = $('#client-' + clientId + 'history').find("tbody");
        $.get(url, params, function takeHistoryList(list) {
            if (list.length < 10) {
                current.hide();
            }
            //draw client history
            drawClientHistory(list, history_table);
        }).fail(function () {
            current.hide();
        });

        let data_page = +current.attr("data-page");
        data_page = data_page + 1;
        current.attr("data-page", data_page);
    });

    //Better way
    let collapseObject = $("#collapse-history");
    collapseObject.on("show.bs.collapse", function () {
        let collapse = $(this);
        let client_id = collapse.attr("data-clientid");
        let url = '/client/history/rest/getHistory/' + client_id;
        let isAsc = false;
        let data = {
            page: 0,
            isAsc: isAsc
        };
        $.get(url, data, function (history) {
            let tbody = collapse.find('tbody');
            if (history.length >= 10) {
                collapse.find("button.upload-more-history").show();
            }
            drawClientHistory(history, tbody);
        })
    });

    collapseObject.on("shown.bs.collapse", function () {
        $(this).height("600px");
    });

    collapseObject.on("hidden.bs.collapse", function () {
        $(this).find("tbody").empty();
        //reset arrow
        $(this).find('i').removeClass('fa-sort-up').addClass('fa-sort-down');
        $(this).find("button.upload-more-history").attr("data-page", 1);
    })
});

/*Re-sort client history by date.*/
function resortClientHistory(button) {
    let uploadHistoryButton = $('.upload-more-history');
    let clientId = uploadHistoryButton.attr("data-clientid");
    let url = '/client/history/rest/getHistory/' + clientId;

    //Set sorting order and change arrow direction.
    let arrow = $(button).find('i');
    let isAsc;
    if (arrow.hasClass('fa-sort-up')) {
        isAsc = false;
        arrow.toggleClass('fa-sort-up fa-sort-down');
    } else if (arrow.hasClass('fa-sort-down')) {
        isAsc = true;
        arrow.toggleClass('fa-sort-down fa-sort-up');
    }

    let history_table = $('#client-' + clientId + 'history').find("tbody");
    //If table is full, reverse it!
    if (uploadHistoryButton.is(":hidden")) {
        history_table.each(function(elem,index){
            var arr = $.makeArray($("tr",this).detach());
            arr.reverse();
            $(this).append(arr);
        });
    //Else recreate it with new order.
    } else {
        let params = {
            page: 0,
            isAsc: isAsc
        };
        history_table.empty();
        $.get(url, params, function takeHistoryList(list) {
            if (list.length < 10) {
                uploadHistoryButton.hide();
            }
            //draw client history
            drawClientHistory(list, history_table);
        }).fail(function () {
            uploadHistoryButton.hide();
        });
        //Reset page number
        let data_page = uploadHistoryButton.attr("data-page");
        data_page = 1;
        uploadHistoryButton.attr("data-page", data_page);
    }
}


function drawClientHistory(list, history_table) {
    var len = (list.length > 10) ? 10 : list.length;
    for (let i = 0; i < len; i++) {
        let $tdLink = "";
        if (list[i].link != null) {
            $tdLink = "" +
                "<td style='width: 10%'>" +
                "<button class=\"btn btn-default glyphicon glyphicon-paperclip open-window-btn h-link\" onclick = 'viewClientHistoryMessage(" + list[i].link + ")'>" +
                "</button>" +
                "<div id=\"modalClientHistoryMessageHolder\">" +
                "</div>" +
                "</td>";
        }
        if (list[i].recordLink != null && list[i].recordLink !== "http://www.google.com") {
            $tdLink = "<td style='width: 10%'>" +
                "<div class=\"dropdown\">\n" +
                "<button class=\"btn btn-secondary dropdown-toggle glyphicon glyphicon-play\" type=\"button\" id=\"dropdownMenuCallRecord\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
                "</button>" +
                "<div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"dropdownMenuCallRecord\">" +
                "<audio controls preload='auto'>" +
                "<source type=\"audio/wav\" src=\"" + list[i].recordLink + "\">" +
                "</audio>" +
                "</div>" +
                "</div>" +
                "</td>";
        } else {
            if (list[i].recordLink != null) {
                $tdLink = "<td style='width: 10%'>" +
                    "</td>";
            }
        }
        let d = new Date(list[i].date);
        let date = ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." +
            d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
        let title = list[i].title;
        if (title.includes("Новая заявка")) {
            // glyphicon glyphicon-question-sign
            title =
                'Новая заявка'
                +'<button class="glyphicon glyphicon-question-sign" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">'
                + '</button>'
                + '<div class="collapse" id="collapseExample">'
                + '<div class="well">'
                + list[i].title
                + '</div>'
                + '</div>';
        }
        if (title.includes("Повторная заявка")) {
            title =
                'Повторная заявка'
                +'<button class="glyphicon glyphicon-question-sign" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">'
                + '</button>'
                + '<div class="collapse" id="collapseExample">'
                + '<div class="well">'
                + list[i].title
                + '</div>'
                + '</div>';

        }
        history_table.append(
            "<tr class='remove-history'>" +
            "   <td>" + title + "</td>" +
            "   <td class=\"client-history-date\">" + date + "</td>" +
            $tdLink +
            "</tr>"
        );
    }
}

function open_new_window(elem) {
    let url = $(elem).attr("href");
    window.open(url, "", "width=700,height=500,location=0,menubar=0,titlebar=0");
}

//Open modal with client history message.
function viewClientHistoryMessage(id) {
    let currentModal = $('#modalClientHistoryMessage');
    currentModal.data('message_id', id);
    currentModal.modal('show');
}

$('#modalClientHistoryMessage').on('hidden.bs.modal', function () {
    $('#main-modal-window').css('overflow-y', 'auto');
});

//Fill values on client history message modal show up.
$(function () {
    $('#modalClientHistoryMessage').on('show.bs.modal', function () {
        var message_id = $(this).data('message_id');
        $.ajax({
            type: 'GET',
            url: "/rest/client/message/info/" + message_id,
            success: function (response) {
                $("#message_content").empty().append(response.content);
            }
        })
    });
});
