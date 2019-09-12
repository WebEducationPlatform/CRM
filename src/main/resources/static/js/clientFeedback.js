function getFeedback(user_id) {
    var url = '/rest/client/feedback/' + user_id;
    loadTable(user_id);
    $('#modal-client-feedback').modal('show');
}

function loadTable(user_id) {
    var tableFooter = $('#client-feedback-table-footer');
    tableFooter.empty();
    tableFooter.append(
        "<button type='button' class='btn btn-primary' onclick='addFeedback(" + user_id + ")'>Добавить отзыв</button>"
    );
    $.ajax({
        url: '/rest/client/feedback/' + user_id,
        type: 'GET',
        success: function (list) {
            $('#client-feedback-table').empty();
            var i = 0;
            Array.prototype.forEach.call(list, function (feedback) {
                i++;
                $('#client-feedback-table').append(
                    "<tr id='tr" + feedback.id + "'>" +
                    "<td ondblclick='edit(this)'><textarea class='feedback-social-url' disabled onblur='disable(this)'>" + feedback.socialUrl + "</textarea></td>" +
                    "<td ondblclick='edit(this)'><textarea class='feedback-text' disabled onblur='disable(this)'>" + feedback.text + "</textarea></td>" +
                    "<td ondblclick='edit(this)'><textarea class='feedback-video-url' disabled onblur='disable(this)'>" + feedback.videoUrl + "</textarea></td>" +
                    "<td><a onclick='updateFeedback(" + feedback.id + ")' class='btn btn-success'>&#10003;</a></td>" +
                    "<td><a onclick='deleteFeedbackAndReload(" + feedback.id + "," + user_id + ")' class='btn btn-danger'>X</a></td>" +
                    "</tr>"
                )
            });
        },
        error: function (e) {
            console.log("Отзывов не найдено" + e.responseText);
        }
    })
}

function closeTable() {
    $('#modal-client-feedback').modal('hide');
    $('#client-feedback-table').empty();
}

function edit(el) {
    el.childNodes[0].removeAttribute("disabled");
    el.childNodes[0].focus();
    window.getSelection().removeAllRanges();
}

function disable(el) {
    el.setAttribute("disabled", "");
}

function deleteFeedback(feedback_id) {
    $.ajax({
        url: '/rest/client/feedback/' + feedback_id,
        type: 'DELETE',
        error: function (e) {
            console.log("Ошибка удаления отзыва" + e.responseText);
        }
    })
}

function deleteFeedbackAllTable(feedback_id) {
    deleteFeedback(feedback_id);
    $('#tbody-all-feedback-table #tr' + feedback_id).remove();
}

function deleteFeedbackAndReload(feedback_id, user_id) {
    deleteFeedback(feedback_id);
    $('#client-feedback-table #tr' + feedback_id).remove();
    loadTable(user_id);
}

function deleteNewFeedback() {
    $('#new-feedback-tr').remove();
}

function addFeedback(user_id) {
    $('#client-feedback-table').append(
        "<tr id='new-feedback-tr'>" +
        "<td ondblclick='edit(this)'><textarea id='new-feedback-social-url' disabled onblur='disable(this)'></textarea></td>" +
        "<td ondblclick='edit(this)'><textarea id='new-feedback-text' disabled onblur='disable(this)'></textarea></td>" +
        "<td ondblclick='edit(this)'><textarea id='new-feedback-video-url' disabled onblur='disable(this)'></textarea></td>" +
        "<td><a onclick='saveNewFeedback(" + user_id + ")' class='btn btn-success'>&#10003;</a></td>" +
        "<td><a onclick='deleteNewFeedback()' class='btn btn-danger'>X</a></td>" +
        "</tr>"
    )
}

function updateFeedback(feedback_id) {
    var feedback = {
        id: feedback_id,
        socialUrl: $('#tr' + feedback_id + ' .feedback-social-url').val(),
        text: $('#tr' + feedback_id + ' .feedback-text').val(),
        videoUrl: $('#tr' + feedback_id + ' .feedback-video-url').val()
    };
    $.ajax({
        url: '/rest/client/feedback/update',
        type: 'POST',
        data: JSON.stringify(feedback),
        contentType: 'application/json',
        error: function (e) {
            console.log("Ошибка обновления отзыва" + e.responseText);
        }
    })
}

function saveNewFeedback(user_id) {
    var feedback = {
        socialUrl: $('#new-feedback-social-url').val(),
        text: $('#new-feedback-text').val(),
        videoUrl: $('#new-feedback-video-url').val()
    };
    $.ajax({
        url: '/rest/client/feedback/add/' + user_id,
        type: 'POST',
        data: JSON.stringify(feedback),
        contentType: 'application/json',
        error: function (e) {
            console.log("Ошибка сохранения отзыва" + e.responseText);
        }
    });
}

/*Поиск в таблице*/
$(document).ready(function () {
    $("#searchInput").keyup(function () {
        _this = this;

        $.each($("#tbody-all-feedback-table tr"), function () {
            if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    });
});

function sort_table(n) {
    var table, rows, switching, i, x, y, x_val, y_val, shouldSwitch, dir, switchCount = 0;
    table = document.getElementById("all-feedback-table");
    switching = true;
    dir = "asc";
    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            if (n == 0) {
                x = rows[i].getElementsByTagName("A")[n];
                y = rows[i + 1].getElementsByTagName("A")[n];
            } else {
                x = rows[i].getElementsByTagName("TEXTAREA")[n - 1];
                y = rows[i + 1].getElementsByTagName("TEXTAREA")[n - 1];
            }
            x_val = x.innerHTML.toLowerCase();
            y_val = y.innerHTML.toLowerCase();
            if (dir == "asc") {
                if (x_val > y_val) {
                    shouldSwitch = true;
                    break;
                }
            } else if (dir == "desc") {
                if (x_val < y_val) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchCount++;
        } else {
            if (switchCount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

function checkboxClick() {
    var checkBox = document.getElementById("no-feedback-checkbox");
    var row = document.getElementsByClassName("client-no-feedback");
    if (checkBox.checked == true) {
        for (var i = 0; i < row.length; i++) {
            row[i].style.display = 'table-row';
        }
    } else {
        for (var i = 0; i < row.length; i++) {
            row[i].style.display = 'none';
        }
    }
}