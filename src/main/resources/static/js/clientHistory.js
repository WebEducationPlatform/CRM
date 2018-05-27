$(document).ready(function () {
    $('.open-window-btn').on("click", function openWindow(event) {
        let url = $(this).attr("href");
        window.open(url, "", "width=700,height=500,location=0,menubar=0,titlebar=0");
        return false;
        });

    $('.upload-history').on("click", function openClientHistory() {
        let current = $(this);
        let isHistory = current.attr("class").includes('collapse');
        let client_id = current.attr("data-id");
        let url = '/rest/client/getHistory/' + client_id;
        let history_table =  $('#client-' + client_id + 'history').find("tbody");
        if (!isHistory) {
            history_table.empty();
            current.removeClass("history-clean");
        } else {
            $.get(url, function takeHistoryList(list) {
                for (let i = 0; i < list.length ; i++) {
                    let $tdLink = "";
                    if (list[i].link !== null) {
                        $tdLink = "<td><a class=\"btn btn-default glyphicon glyphicon-paperclip open-window-btn h-link\" href=\""+ list[i].link +"\"></a></td>"
                    }
                    history_table.prepend(
                        "<tr>" +
                        "   <td>" + list[i].title + "</td>" +
                        "   <td class=\"client-history-date\">" + list[i].date + "</td>" +
                        $tdLink +
                        "</tr>"
                    );
                }
                //Without this method, appended <a> don't work
                $('.open-window-btn').on("click", function openWindow(event) {
                    let url = $(this).attr("href");
                    window.open(url, "", "width=700,height=500,location=0,menubar=0,titlebar=0");
                    return false;
                });
            })
        }
    });

});