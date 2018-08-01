$(document).ready(function () {
    $('.upload-more-history').on("click", function uploadMoreHistory() {
        let current = $(this);
        let clientId = current.attr("data-clientid");
        let page = current.attr("data-page");
        let url = '/client/history/rest/getHistory/' + clientId;
        let params = {
            page: page
        };
        let history_table =  $('#client-' + clientId + 'history').find("tbody");
        $.get(url, params, function takeHistoryList(list) {
            if(list.length < 10) {
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

    $('.upload-history').on("click", function openClientHistory() {
        let current = $(this);
        let isHistory = current.attr("class").includes('collapse');
        let client_id = current.attr("data-id");
        let url = '/client/history/rest/getHistory/' + client_id;
        let params = {
            page: "0"
        };
        let history_table =  $('#client-' + client_id + 'history').find("tbody");
        let upload_more_btn = current.parents("div.panel.panel-default").find(".upload-more-history");
        if (!isHistory) {
            history_table.empty();
            upload_more_btn.attr("data-page", 1);
            current.removeClass("history-clean");
        } else {
            $.get(url, params, function get(list) {
            }).done(function (list) {
                if(list.length < 10) {
                    upload_more_btn.hide();
                } else {
                    upload_more_btn.show();
                }
                //draw client history
                drawClientHistory(list, history_table);
            })
        }
    });

    //Better way
    let collapseObject = $("#collapse-history");
    collapseObject.on("show.bs.collapse", function () {
        let collapse = $(this);
        let client_id = collapse.attr("data-clientid");
        let url = '/client/history/rest/getHistory/' + client_id;
        let data = {
            page: 0
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
        $(this).find("button.upload-more-history").attr("data-page", 1);
    })
});



function drawClientHistory(list, history_table) {
    for (let i = 0; i < list.length; i++) {
        let $tdLink = "";
        if (list[i].link !== null) {
            $tdLink = "<td style='width: 10%'><button class=\"btn btn-default glyphicon glyphicon-paperclip open-window-btn h-link\" href=\"" + list[i].link + "\" onclick='open_new_window(this)'></button></td>"
        }
        if (list[i].recordLink != null) {
            $tdLink = "<td style='width: 10%'>" +
                "<div tabindex=\"0\" class=\"onclick-menu btn btn-secondary dropdown-toggle glyphicon glyphicon-play\">\n" +
                "<ul class=\"onclick-menu-content dropdown-menu dropdown-menu-right\" aria-labelledby=\"dropdownMenuCallRecord\">\n" +
                "<li>\n" +
                "<audio controls>" +
                "<source type=\"audio/wav\" src=\"" + list[i].recordLink + "\">" +
                "</audio>" +
                "</li>\n" +
                "</ul>\n" +
                "</div>"
        }
        history_table.append(
            "<tr class='remove-history'>" +
            "   <td>" + list[i].title + "</td>" +
            "   <td class=\"client-history-date\">" + list[i].date + "</td>" +
            $tdLink +
            "</tr>"
        );
    }
}

function open_new_window(elem) {
    let url = $(elem).attr("href");
    window.open(url, "", "width=700,height=500,location=0,menubar=0,titlebar=0");
}


