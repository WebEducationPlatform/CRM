$('.checkbox').click(function() {
    var table, rows, i, status;
    table = document.getElementById("students-table");
    rows = table.rows;
    for (i = 1; i < rows.length; i++) {
        status = rows[i].getElementsByTagName("TD")[0];
        if (this.id == status.innerHTML) {
            rows[i].style.display = this.checked ? '' : 'none';
        }
    }
});

function sort_table(n, type) {
    var table, rows, switching, i, x, y, x_val, y_val, temp_x, temp_y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("students-table");
    switching = true;
    dir = "asc";
    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            x = rows[i].getElementsByTagName("TD")[n];
            y = rows[i + 1].getElementsByTagName("TD")[n];
            if(type == "href") {
                x_val = x.innerText.toLowerCase();
                y_val = y.innerText.toLowerCase();
            } else if(type == "date") {
                temp_x = x.innerHTML.toLowerCase().split(".");
                temp_y = y.innerHTML.toLowerCase().split(".");
                x_val = new Date(temp_x[2], temp_x[1] - 1, temp_x[0]);
                y_val = new Date(temp_y[2], temp_y[1] - 1, temp_y[0]);
            } else {
                x_val = x.innerHTML.toLowerCase();
                y_val = y.innerHTML.toLowerCase();
            }
            if (dir == "asc") {
                if (x_val > y_val) {
                    shouldSwitch= true;
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
            switchcount ++;
        } else {
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

$('.button_edit').click(function () {
    let currentModal = $('#student-edit-modal');
    currentModal.data('student_id', this.value);
    currentModal.modal('show');
});

$('#update-student').click(function () {
    let student_id = $("#student-id").val();
    let url = "/rest/student/" + student_id + "/client";
    $.ajax({
        type: 'GET',
        url: url,
        success: function (client) {
            let data = {
                id : $("#student-id").val(),
                // client : client,
                client : {id : client.id},
                trialEndDate : $("#trial-end-date").val(),
                nextPaymentDate : $("#next-payment-date").val(),
                price : $("#month-price").val(),
                paymentAmount : $("#payment").val(),
                payLater : $("#later-payment").val(),
                status : {id : $("#student-status").val(),status : $("#student-status option:selected").text()},
                notes : $("#notes").val()
            };

            $.ajax({
                type: 'POST',
                url: '/rest/student/update',
                contentType : "application/json; charset=UTF-8",
                encoding: "UTF-8",
                dataType: "JSON",
                data: JSON.stringify(data),
                success: function () {
                    location.reload();
                }
            })
        }
    });
});

//--------------------------------------------------------------------------------------

$(function () {
    $('#student-edit-modal').on('show.bs.modal', function () {
        var student_id = $(this).data('student_id');
        $.ajax({
            type: 'GET',
            url: '/rest/student/' + student_id,
            success: function (response) {
                $("#modal-header").empty().append(response.client.name + " " + response.client.lastName);
                $("#student-id").val(student_id);
                $("#trial-end-date").val(response.trialEndDate.substring(0,10));
                $("#next-payment-date").val(response.nextPaymentDate.substring(0,10));
                $("#month-price").val(response.price);
                $("#payment").val(response.paymentAmount);
                $("#later-payment").val(response.payLater);
                $("#student-status").empty();
                $.ajax({
                    type: 'GET',
                    url: "/rest/student/status",
                    success: function (statuses) {
                        for (var i in statuses) {
                            var selected = statuses[i].status == response.status.status ? "selected" : "";
                            $("#student-status").append("<option value='" + statuses[i].id + "' " + selected + ">" + statuses[i].status + "</option>");
                        }
                    }
                });
                $("#notes").val(response.notes);
            }
        })
    });
});

// $(function () {
//     $('#main-modal-window').on('hidden.bs.modal', function () {
//         $('.assign-skype-call-btn').removeAttr("disabled");
//         $('div#assign-unassign-btns').empty();
//         $('.skype-notification').empty();
//         $('.confirm-skype-login').remove();
//         $('.enter-skype-login').remove();
//         $('.skype-panel').remove();
//         $('.skype-text').empty();
//         $('.remove-element').remove();
//         $('.hide-client-collapse').attr('id', 'hideClientCollapse');
//         $('.postpone-date').attr('id', 'postponeDate');
//         $('.textcomplete').removeAttr('id');
//         $('.main-modal-comment').removeAttr('id');
//         $('.remove-tag').remove();
//         $('.history-line').find("tbody").empty();
//         $('#sendEmailTemplateStatus').empty();
//         $('#sendSocialTemplateStatus').empty();
//         $('.client-collapse').collapse('hide');
//         $('.remove-history').remove();
//         $('.upload-more-history').removeAttr('data-clientid');
//         $('.upload-more-history').attr("data-page", 1);
//     });
// });

