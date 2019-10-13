var listUsersForCall;
let fileterBtn = $('#filtration-calls');

$(document).ready(function () {
    $('.web-call-mic-off').hide();
    $('.web-call-off').hide();
    $('.upload-calls-history').attr('href', '#calls-collapse');

    let current = $(document.getElementsByClassName("all-calls panel-collapse collapse"));
    current.collapse('show');
    loadHistory();

    $('.upload-more-calls-history').on("click", function uploadMoreCallsHistory() {
        let current = $(this);
        let page = current.attr("data-page");
        let url = "/user/rest/call/records/all";
        let params = {
            page: page
        };
        let history_table = $('.calls-history-line > tBody');

        if (fileterBtn.hasClass("filtered-calls")) {
            loadFilteredHistoryCalls(history_table, current, page);
        } else {
            $.get(url, params, function takeHistoryList(list) {
                if (list.length < 10) {
                    current.hide();
                }
                drawClientCallsHistory(list, history_table);
            }).fail(function () {
                current.hide();
            });

            let data_page = +current.attr("data-page");
            data_page = data_page + 1;
            current.attr("data-page", data_page);
        }
    });
});

function loadFilteredHistoryCalls(history_table, upload_more_btn, page) {
    let selectedUserId = $('#calls-select-user').val();
    let dateFrom = new Date($('#callsDateFrom').val()).toLocaleDateString();
    let dateTo = new Date($('#callsDateTo').val()).toLocaleDateString();

    if (dateFrom == "") {
        dateFrom = new Date(2000, 1, 1).toLocaleString();
    } else {
        dateFrom = dateFrom + ', 00:00:00';
    }
    if (dateTo == "") {
        dateTo = new Date(2100, 1, 1).toLocaleString();
    } else {
        dateTo = dateTo + ', 00:00:00';
    }

    let url = "/user/rest/call/records/filter";
    let params = {
        page: page,
        userId: selectedUserId,
        from: dateFrom,
        to:dateTo
    };
    if (page < 1) {
        history_table.empty();
    }
    $.get(url, params, function get(list) {
    }).done(function (list) {
        if (list.length < 10) {
            upload_more_btn.hide();
        } else {
            upload_more_btn.show();
        }
        drawClientCallsHistory(list, history_table);
    }).fail(function () {
        upload_more_btn.hide();
    });
    if (page > 1) {
        let data_page = +upload_more_btn.attr("data-page");
        data_page = data_page + 1;
        upload_more_btn.attr("data-page", data_page);
    }
}

function loadHistory() {
    let current = $(document.getElementsByClassName("upload-calls-history"));
    let url = "/user/rest/call/records/all";
    let history_table = $('.calls-history-line > tBody');
    let upload_more_btn = current.parents("div.panel.panel-default").find(".upload-more-calls-history");
    let params = {
        page: "0"
    };
    if (fileterBtn.hasClass("filtered-calls")) {
        upload_more_btn.attr("data-page", 1);
        let page = 0;
        loadFilteredHistoryCalls(history_table, upload_more_btn, page);
    } else {
        $.get(url, params, function get(list) {
        }).done(function (list) {
            console.log(list);
            if (list.length < 10) {
                upload_more_btn.hide();
            } else {
                upload_more_btn.show();
            }
            drawClientCallsHistory(list, history_table);
        }).fail(function () {
            upload_more_btn.hide();
        })
    }
}

function drawClientCallsHistory(list, history_table) {
    for (let i = 0; i < list.length; i++) {
        let comment;
        let date;
        let tdLink = "";
        let tdClient = "";
        let clientId;
        let d;

        if (list[i].comment !== null) {
            comment = list[i].comment;
        } else if (list[i].clientHistory.title !== null) {
            comment = list[i].clientHistory.title;
        }
        if (list[i].date !== null) {
            d = new Date(list[i].date);
        } else if (list[i].clientHistory.date !== null) {
            d = new Date(list[i].clientHistory.date);
        }
        date = ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." +
            d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);

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
        } else {
            tdLink = "<td class='col-sm-1'></td>"
        }

        if (list[i].client !== null) {
            let client = list[i].client;
            clientId = client.id;
            tdClient = "<td class='col-sm-2'><a id='tdClient_" + clientId + "' href='/calls?id+" + clientId + "' onclick='clientModalInCall(" + clientId + ");return false;'>" + client.lastName + ' ' + client.name + "</a></td>"
        } else {
            tdClient = "<td class='col-sm-2'></td>"
        }

        history_table.append(
            "<tr class='remove-history-calls' style='white-space: normal;'>" +
            "<td class='col-sm-3'>" + comment + "</td>" +
            tdClient +
            "<td class='client-history-date-calls col-sm-1' style='width: 14%;'>" + date + "</td>" +
            tdLink +
            "</tr>"
        );
    }
}

function clientModalInCall(clientId) {
    changeUrl('/calls', clientId);
    let currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}

function makeCall() {
    let phoneNumber = document.getElementById('number-to-call').value;
    commonWebCall(phoneNumber);
}

function filterForCalls() {
    fileterBtn.addClass("filtered-calls");
    loadHistory();
}

function setPhoneInPathAndCall(phoneNumber){
    $('#number-to-call').val(phoneNumber);
    commonWebCall(phoneNumber);

}

//Получаем список всех верифицированных пользователей
function getVerifiedUsers() {
    let url = "/rest/users";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            listUsersForCall = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

    $(document).ready(function () {
        getVerifiedUsers();
        $('#filterForListUsers').keyup(function(event){
            let serchStr = this.value.toLowerCase();
            if (event.keyCode == 27){

                $("#listUserForCall tr").each(function(){
                    $(this).show();
                });
                $('#filterForListUsers').val('');
            }
            if (serchStr!=="") {
                for (var i = 0; i < listUsersForCall.length; i++) {
                    let currstr = listUsersForCall[i].firstName.toLowerCase() + ' ' + listUsersForCall[i].lastName.toLowerCase();
                    if(listUsersForCall[i].firstName.toLowerCase().includes(serchStr)
                        | listUsersForCall[i].lastName.toLowerCase().includes(serchStr) | currstr.includes(serchStr)) {
                        $('#listUserItem-' + listUsersForCall[i].id).show();
                    }else{
                        $('#listUserItem-' + listUsersForCall[i].id).hide();
                    }
                }
            } else {
                $("#listUserForCall tr").each(function(){
                    $(this).show();
                });
            }
        });


    });

$(document).ready(function () {
    let input = $('#number-to-call');
    input.focus();
    input.val("7 ");
    setCursorPosition(2, input);
    $('#number-to-call').on('input', function () {
        var matrix = $(this).attr("placeholder"),// .defaultValue
            i = 0,
            def = matrix.replace(/\D/g, ""),
            val = $(this).val().replace(/\D/g, "");
        def.length >= val.length && (val = def);
        matrix = matrix.replace(/[X\d]/g, function (a) {
            return val.charAt(i++) || "X"
        });
        $(this).val(matrix);
        i = matrix.lastIndexOf(val.substr(-1));
        i < matrix.length && matrix != $(this).attr("placeholder") ? i++ : i = matrix.indexOf("X");
        setCursorPosition(i, $(this));
        let getPhone = $(this).val().replace(/\s|X/g, '');
        if (getPhone.length > 4) {
            let urlToGetClientsWithoutPagination = "../rest/client/filtrationWithoutPagination";
            data = {};
            data['phoneNumber'] = getPhone;


            $.ajax({
                type: 'POST',
                contentType: "application/json",
                dataType: 'json',
                url: urlToGetClientsWithoutPagination,
                data: JSON.stringify(data),
                success: function (res) {
                    alert(res);
                }
            });
        }

    });
});


function setCursorPosition(pos, e) {
    e.focus();
    if (e.get(0).setSelectionRange){
        e.get(0).setSelectionRange(pos, pos)
    } else if (e.get(0).createTextRange) {
        var range = e.get(0).createTextRange();
        range.collapse(true);
        range.moveEnd("character", pos);
        range.moveStart("character", pos);
        range.select()
    }
}
