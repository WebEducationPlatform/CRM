$(document).ready(function () {
    $('.web-call-mic-off').hide();
    $('.web-call-off').hide();
    $('.upload-history').attr('href', '#collapse');

    let current = $(document.getElementsByClassName("panel-collapse client-collapse collapse"));
    current.collapse('show');
    loadHistory();

    $('.upload-more-history').on("click", function uploadMoreHistory() {
        let current = $(this);
        let page = current.attr("data-page");
        let url = "/user/rest/call/records/all";
        let params = {
            page: page
        };
        let history_table = $('.history-line > tBody');
        $.get(url, params, function takeHistoryList(list) {
            if (list.length < 10) {
                current.hide();
            }
            drawClientHistory(list, history_table);
        }).fail(function () {
            current.hide();
        });

        let data_page = +current.attr("data-page");
        data_page = data_page + 1;
        current.attr("data-page", data_page);
    });
});

function loadHistory() {
    let current = $(document.getElementsByClassName("upload-history"));
    let url = "/user/rest/call/records/all";
    let history_table = $('.history-line > tBody');
    let upload_more_btn = current.parents("div.panel.panel-default").find(".upload-more-history");
    let params = {
        page: "0"
    };
    $.get(url, params, function get(list) {
    }).done(function (list) {
        console.log(list);
        if (list.length < 10) {
            upload_more_btn.hide();
        } else {
            upload_more_btn.show();
        }
        drawClientHistory(list, history_table);
    }).fail(function () {
        upload_more_btn.hide();
    })
}

function drawClientHistory(list, history_table) {
    for (let i = 0; i < list.length; i++) {
        let comment;
        let date;
        let tdLink = "";

        if (list[i].comment !== null) {
            comment = list[i].comment;
        }
        if (list[i].date !== null) {
            date = list[i].date;
        }
        if (list[i].link !== null) {
            tdLink = "<td style='width: 10%'>" +
                "<div class=\"dropdown\">\n" +
                "<button class=\"btn btn-secondary dropdown-toggle glyphicon glyphicon-play\" type=\"button\" id=\"dropdownMenuCallRecord\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
                "</button>" +
                "<div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"dropdownMenuCallRecord\">" +
                "<audio controls>" +
                "<source type=\"audio/wav\" src=\"" + list[i].link + "\">" +
                "</audio>" +
                "</div>" +
                "</div>" +
                "</td>"
        }

        history_table.append(
            "<tr class='remove-history'>" +
            "<td>" + comment + "</td>" +
            "<td class=\"client-history-date\">" + date + "</td>" +
            tdLink +
            "</tr>"
        );
    }
}

function makeCall() {
    let phoneNumber = document.getElementById('number-to-call').value;
    commonWebCall(phoneNumber);
}