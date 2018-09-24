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

//Get student and send update
$('#update-student').click(function () {
    if (!validate_prices()) {return}
    let student_id = $("#student-id").val();
    let url = "/rest/student/" + student_id + "/client";
    $.ajax({
        type: 'GET',
        url: url,
        success: function (client) {
            let email_selector = '#' + student_id + '_notify_email';
            let sms_selector = '#' + student_id + '_notify_sms';
            let vk_selector = '#' + student_id + '_notify_vk';
            let email_notify = $(email_selector).attr('checked') == 'checked' ? true : false;
            let sms_notify = $(sms_selector).attr('checked') == 'checked' ? true : false;
            let vk_notify = $(vk_selector).attr('checked') == 'checked' ? true : false;
            let data = {
                id : $("#student-id").val(),
                client : {id : client.id},
                trialEndDate : $("#trial-end-date").val() + "T00:00:00",
                nextPaymentDate : $("#next-payment-date").val() + "T00:00:00",
                price : $("#month-price").val(),
                paymentAmount : $("#payment").val(),
                payLater : $("#later-payment").val(),
                status : {id : $("#student-status").val(),status : $("#student-status option:selected").text()},
                notes : $("#notes").val(),
                notifyEmail: email_notify,
                notifySMS: sms_notify,
                notifyVK: vk_notify
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

//Check prices consistency
function validate_prices() {
    let price = parseInt($("#month-price").val());
    let payment = parseInt($("#payment").val());
    let pay_later = parseInt($("#later-payment").val());
    if (price != payment + pay_later) {
        alert("Сумма платежа и последующей оплаты не равны цене!\r\n"
            + payment + " + " + pay_later + " != " + price);
        return false;
    }
    return true;
}

//Fill values on student edit modal show up.
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

//All available notification checkbox id patterns
const notifications = ['_notify_email','_notify_sms','_notify_vk'];

//Check/uncheck all notifications
$('.notifier_all').click(function() {
    let id = this.value;
    let checked = this.checked;
    for (let prefix of notifications) {
        let selector = '#' + id + prefix;
        if($(selector).attr('disabled') == undefined) {
            // $(selector).prop('checked', checked);
            update_notification(selector.substr(1), checked);
        }
    }
    // $(this.id).prop('checked', checked);
    location.reload();
});

//Notification checkbox change action
//Page needs to be reloaded to update Select All checkbox?
$('.notifier').change(function() {
    let id = this.id;
    let checked = this.checked;
    update_notification(id, checked);
    // if(checked) {
        location.reload();
    // } else {
    //     let selector_all = '#' + this.value + '_notify_all';
    //     $(selector_all).prop('checked', false);
    // }
});

//Notification change
//Async request not working in loop?
function update_notification(checkbox_id, checked) {
    let url = "/rest/student/" + checkbox_id.replace(new RegExp('_', 'g'),'/');
    $.ajax({
        type: 'POST',
        url: url,
        encoding: "UTF-8",
        async: false,
        data: {status: checked}
    })
}