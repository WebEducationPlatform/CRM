$('.checkbox').click(function() {
    var table, rows, i, status, json;
    table = document.getElementById("students-table");
    rows = table.rows;
    for (i = 1; i < rows.length; i++) {
        status = rows[i].getElementsByTagName("TD")[0];
        if (this.id == status.innerHTML) {
            rows[i].style.display = this.checked ? '' : 'none';
        }
    }
    json = '{';
    $.each($('#filter')[0]['children'][0]['children'], function (k, v) {
        let input = v.getElementsByTagName("INPUT")[0];
        if (input.checked) {
            json += "'" + input.id + "':'true',";
        }
    });
    json = json.replace(new RegExp(',$'), '}');
    $.ajax({
        async: true,
        type: 'POST',
        url: '/admin/rest/user/filters',
        data: {'filters' : json}
    });
    calc_info_values();
});

$(document).ready(function() {
    renderStudentsTable();
    calc_info_values();
});

function renderStudentsTable() {
    var table, rows, i, status;
    table = document.getElementById("students-table");
    rows = table.rows;
    for (i = 1; i < rows.length; i++) {
        status = rows[i].getElementsByTagName("TD")[0];
        rows[i].style.display = 'yes';
    }
    $.each($('#filter')[0]['children'][0]['children'], function (k, v) {
        let input = v.getElementsByTagName("INPUT")[0];
        if (input.checked) {
            for (i = 1; i < rows.length; i++) {
                status = rows[i].getElementsByTagName("TD")[0];
                if (input.id == status.innerHTML) {
                    rows[i].style.display = '';
                }
            }
        }
    });
}

function calc_info_values() {
    try {
        table = document.getElementById("students-table");
        var sum = 0.0;
        var count = 0;
        $.each(table.rows, function (k, v) {
            if (v.style.display !== 'none' && v.getElementsByClassName('price-for-student')[0] != null) {
                sum += parseFloat(v.getElementsByClassName('price-for-student')[0].innerHTML);
                count++;
            }
        });
        $('#students-price-info').text(sum.toFixed(2));
        $('#students-count').text(count);
    } catch (e) {
        console.log('Probably you are not OWNER, so this function is not for you :)');
    }
}

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
                temp_x = x.getElementsByTagName("SPAN")[0].innerHTML.toLowerCase().split(".");
                temp_y = y.getElementsByTagName("SPAN")[0].innerHTML.toLowerCase().split(".");
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

var clickedStatus = [];
var clickedEndDate = [];
var clickedPaymentDate = [];
var lastClickedId = -1;

$('body').on('focus', '[contenteditable]', function() {
    if (lastClickedId != -1) {
        $('#trial-end-date_' + lastClickedId).addClass('hidden');
        $('#next-payment-date_' + lastClickedId).addClass('hidden');
        $('#student-status_' + lastClickedId).addClass('hidden');
        $('#statusValue_' + lastClickedId).show();
        $('#trialEndDateValue_' + lastClickedId).show();
        $('#nextPaymentDateValue_' + lastClickedId).show();
    }
    const $this = $(this);
    $this.data('before', $this.html());
}).on('focusout', '[id*="table-body"]', function () {
    if (lastClickedId != -1) {
        $('#trial-end-date_' + lastClickedId).addClass('hidden');
        $('#next-payment-date_' + lastClickedId).addClass('hidden');
        $('#student-status_' + lastClickedId).addClass('hidden');
        $('#statusValue_' + lastClickedId).show();
        $('#trialEndDateValue_' + lastClickedId).show();
        $('#nextPaymentDateValue_' + lastClickedId).show();
        updateStudent(lastClickedId);
        lastClickedId = -1;
    }
    const $this = $(this);
    $this.data('before', $this.html());
}).on('blur paste', '[contenteditable]', function() {
    const $this = $(this);
    if ($this.data('before') !== $this.html()) {
        updateStudent(lastClickedId);
        lastClickedId = -1;
    }
}).on('keydown', '[contenteditable]', function(e) {
    if (e.keyCode === 13) {
        updateStudent(lastClickedId);
        return false;
    }
}).on('keydown', 'input[type*="date"]', function (e) {
    if (e.keyCode === 13) {
        $('#trial-end-date_' + lastClickedId).addClass('hidden');
        $('#next-payment-date_' + lastClickedId).addClass('hidden');
        $('#student-status_' + lastClickedId).addClass('hidden');
        $('#statusValue_' + lastClickedId).show();
        $('#trialEndDateValue_' + lastClickedId).show();
        $('#nextPaymentDateValue_' + lastClickedId).show();
        updateStudent(lastClickedId);
        return false;
    }
});

function changeDateValue(field, id, valueLink, inputLink) {
    let dateArr;
    switch (field) {
        case 'add_weeks_button':
            dateArr = $("#" + valueLink + id).text().split('.');
            var d = new Date(dateArr[2], dateArr[1], dateArr[0]);
            var date = d.getDate() + 15;
            var newDate = new Date(d.getFullYear(), d.getMonth(), date);
            var month = newDate.getMonth();
            if (month == 0) {
                newDate.setFullYear(newDate.getFullYear() - 1);
                month = 12;
            }
            $('#' + valueLink + id).text((newDate.getDate() < 10 ? '0' : '') + newDate.getDate() + '.' + (month < 10 ? '0' : '') + month + '.' + newDate.getFullYear());
            $('#' + inputLink + id).val(newDate.getFullYear() + '-' + (month < 10 ? '0' : '') + month + '-' + (newDate.getDate() < 10 ? '0' : '') + newDate.getDate());
            break;
        case 'add_month_button':
            dateArr = $("#" + valueLink + id).text().split('.');
            var d = new Date(dateArr[2], dateArr[1], dateArr[0]);
            var month = d.getMonth() + 1;
            $('#' + valueLink + id).text((d.getDate() < 10 ? '0' : '') + d.getDate() + '.' + (month < 10 ? '0' : '') + month + '.' + d.getFullYear());
            $('#' + inputLink + id).val(d.getFullYear() + '-' + (month < 10 ? '0' : '') + month + '-' + (d.getDate() < 10 ? '0' : '') + d.getDate());
            break;
    }
}

$('.trial-date-btn').on('click', function () {
    if (!this.id) {
        return;
    }
    var field = this.id.substring(0, this.id.lastIndexOf("_"));
    var id = this.id.substring(this.id.lastIndexOf("_") + 1, this.id.length);
    changeDateValue(field, id, 'trialEndDateValue_', 'trial-end-date_');
    updateStudent(id);
    return false;
});

$('#additional-data-clickable-zone').on('click', function () {
    $('#additional-data').addClass('hidden');
    $('#show-additional-info-button').removeClass('hidden');
});

$('#show-additional-info-button').on('click', function () {
    calc_info_values();
    $('#additional-data').removeClass('hidden');
    $('#show-additional-info-button').addClass('hidden');
});

$('.payment-date-btn').on('click', function () {
    if (!this.id) {
        return;
    }
    var field = this.id.substring(0, this.id.lastIndexOf("_"));
    var id = this.id.substring(this.id.lastIndexOf("_") + 1, this.id.length);
    changeDateValue(field, id, 'nextPaymentDateValue_', 'next-payment-date_');
    updateStudent(id);
    return false;
});

$('.button_color').on('click', function () {
    $(this).colorpicker({format: 'hex'});
});

$('.button_color').colorpicker().on('changeColor', function (e) {
    let id = $(this)[0].value;
    let color = e.color.toHex();
    $('#row_' + id).css({'background-color' : color});
}).on('hidePicker', function (e) {
    let id = $(this)[0].value;
    let color = e.color.toHex();
    $.ajax({
        async: false,
        type: 'POST',
        url: '/rest/student/color/set/' + id,
        data: {'color' : color},
        success: function () {
            $('#row_' + id).css({'background-color' : color});
        }
    });
});

$('#reset-all-colors-btn').on('click', function () {
    $.ajax({
        async: false,
        type: 'POST',
        url: '/rest/student/color/reset/all',
        success: function () {
            $('tr').each(function () {
                $(this).css({'background-color' : ''});
            });
        }
    });
});

$('.button_color_reset').on('click', function () {
    let id = $(this).val();
    $.ajax({
        async: false,
        type: 'POST',
        url: '/rest/student/color/reset/' + id,
        success: function () {
            $('#row_' + id).css({'background-color' : ''});
        }
    });
});

//enable student editing when clicking on his fields in table
$('td').click(function () {
    if (!this.id) {
        return;
    }
    var i;
    var field = this.id.substring(0, this.id.lastIndexOf("_"));
    var id = this.id.substring(this.id.lastIndexOf("_") + 1, this.id.length);
    if (lastClickedId != -1 && lastClickedId != id) {
        $('#trial-end-date_' + lastClickedId).addClass('hidden');
        $('#next-payment-date_' + lastClickedId).addClass('hidden');
        $('#student-status_' + lastClickedId).addClass('hidden');
        $('#statusValue_' + lastClickedId).show();
        $('#trialEndDateValue_' + lastClickedId).show();
        $('#nextPaymentDateValue_' + lastClickedId).show();
    }
    lastClickedId = id;
    $('#editBtn' + id).removeAttr('disabled');
    switch (field) {
        case 'trialEndDate':
            $('#trialEndDateValue_' + id).hide();
            $('#trial-end-date_' + id).removeClass('hidden');
            dateArr = $("#trialEndDateValue_" + id).text().split('.');
            $('#trial-end-date_' + id).val(dateArr[2] + '-' + dateArr[1] + '-' + dateArr[0]);
            break;
        case 'nextPaymentDate':
            $('#nextPaymentDateValue_' + id).hide();
            $('#next-payment-date_' + id).removeClass('hidden');
            dateArr = $("#nextPaymentDateValue_" + id).text().split('.');
            $('#next-payment-date_' + id).val(dateArr[2] + '-' + dateArr[1] + '-' + dateArr[0]);
            break;
        case 'status':
            for(i=0; i<clickedStatus.length; i++) {
                if (clickedStatus[i] == id) {
                    clickedStatus[i] = '';
                    return;
                }
            }
            clickedStatus.push(id);
            if ($('#student-status_' + id + ' option').length == 0) {
                $.ajax({
                    type: 'GET',
                    url: "/rest/student/status",
                    success: function (statuses) {
                        for (var i in statuses) {
                            var selected = statuses[i].status == $('#statusValue_' + id).text() ? "selected" : "";
                            $('#student-status_' + id).append("<option value='" + statuses[i].id + "' " + selected + ">" + statuses[i].status + "</option>");
                        }
                    }
                });
            }
            $('#student-status_' + id).removeClass('hidden');
            $('#statusValue_' + id).hide();
            $('#' + this.id).attr('contenteditable', 'true');
            break;
        default:
            $('#' + this.id).attr('contenteditable', 'true');
    }
});

//change dates in table fields
$("input[type='date']").on('change', function() {
    var field = this.id.substring(0, this.id.lastIndexOf("_"));
    var id = this.id.substring(this.id.lastIndexOf("_") + 1, this.id.length);
    switch (field) {
        case 'trial-end-date':
            var arr = $(this).val().split('-');
            var date = arr[2] + '.' + arr[1] + '.' + arr[0];
            $('#trialEndDateValue_' + id).text(date);
            break;
        case 'next-payment-date':
            var arr = $(this).val().split('-');
            var date = arr[2] + '.' + arr[1] + '.' + arr[0];
            $('#nextPaymentDateValue_' + id).text(date);
            break;
    }
});

//change status in table field from select input
$('select').on('change', function () {
    var optionSelected = $("option:selected", this).text();
    var id = this.id.substring(this.id.lastIndexOf("_") + 1, this.id.length);
    $('#statusValue_' + id).text(optionSelected);
    $('#statusValue_' + id).show();
    $('#student-status_' + id).addClass('hidden');
    clickedStatus.push(id);
    updateStudent(id);
});

function updateStudent(id) {
    if (!validate_prices(id)) {return}
    let url = "/rest/student/" + id + "/client";
    $.ajax({
        type: 'GET',
        url: url,
        success: function (client) {
            let trialDateArr = $("#trialEndDateValue_"+id).text().split('.');
            let nextPaymentDateArr = $("#nextPaymentDateValue_"+id).text().split('.');
            let trialDate = trialDateArr[2] + '-' + trialDateArr[1] + '-' + trialDateArr[0];
            let nextPayDate = nextPaymentDateArr[2] + '-' + nextPaymentDateArr[1] + '-' + nextPaymentDateArr[0];
            let email_selector = '#' + id + '_notify_email';
            let sms_selector = '#' + id + '_notify_sms';
            let vk_selector = '#' + id + '_notify_vk';
            let email_notify = $(email_selector).prop('checked');
            let sms_notify = $(sms_selector).prop('checked');
            let vk_notify = $(vk_selector).prop('checked');
            let data = {
                id : id,
                client : {id : client.id, name : $("#name_"+id).text(), lastName : $("#lastName_"+id).text(), email : $("#email_"+id).text()},
                trialEndDate : trialDate + "T00:00:00",
                nextPaymentDate : nextPayDate + "T00:00:00",
                price : $("#price_"+id).text(),
                paymentAmount : $("#paymentAmount_"+id).text(),
                payLater : $("#payLater_"+id).text(),
                status : {status : $("#statusValue_"+id).text()},
                notes : $("#notes_"+id).text(),
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
                    $('#editBtn' + id).attr('disabled', 'disabled');
                }
            })
        }
    });
}

//go to student info page
$('.button_info').click(function () {
    var clientId = this.value;
    changeUrl('/student/all', clientId);
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
});

//delete student by id
$('.button_delete').click(function () {
    let currentModal = $('#student-reject-modal');
    currentModal.data('reject-student-id', this.value);
    $('#reject-reason').val('');
    currentModal.modal('show');
});

$('#reject_student').on('click', function () {
    var id = $('#student-reject-modal').data('reject-student-id');
    var message = $('#reject-reason').val();
    var defaultStatus = $('#defaultStatusForRejectedStudent').text();
    $.ajax({
        type: 'POST',
        url: '/rest/student/reject/' + id,
        encoding: "UTF-8",
        data: {
            "message" : message
        },
        success: function (response) {
            $('#student-reject-modal').modal('hide');
            if (response !== 'CONFLICT') {
                $('#status_' + id).text(defaultStatus);
                renderStudentsTable();
                calc_info_values();
            } else {
                alert('Probably default statuses is not set')
            }
        }
    });
});

//Check prices consistency
function validate_prices(id) {
    let price = parseInt($("#price_"+id).text());
    let payment = parseInt($("#paymentAmount_"+id).text());
    let pay_later = parseInt($("#payLater_"+id).text());
    if (isNaN(price) || isNaN(payment) || isNaN(pay_later)) {
        alert('Введите цену!');
        return false;
    }
    // if (price != payment + pay_later) {
    //     alert("Сумма платежа и последующей оплаты не равна цене!\r\n"
    //         + payment + " + " + pay_later + " ≠ " + price);
    //     return false;
    // }
    return true;
}

//All available notification checkbox id patterns
const notifications = ['_notify_email','_notify_sms','_notify_vk'];

//Check/uncheck all notifications
$('.notifier_all').click(function() {
    let id = this.value;
    let checked = this.checked;
    for (let prefix of notifications) {
        let selector = '#' + id + prefix;
        if(($(selector).prop('disabled') == false) && ($(selector).prop('checked') != checked)) {
            $(selector).prop('checked', checked);
            update_notification(selector.substr(1), checked);
        }
    }
    $(this.id).prop('checked', checked);
});

//Notification checkbox change action
//Page needs to be reloaded to update Select All checkbox?
$('.notifier').change(function() {
    let id = this.id;
    let checked = this.checked;
    let selector_all = '#' + this.value + '_notify_all';
    update_notification(id, checked);
    if(checked) {
        let result = true;
        for (let prefix of notifications) {
            let selector = '#' + this.value + prefix;
            if(!$(selector).prop('disabled')) {
                result = $(selector).prop('checked') && result;
            }
        }
        $(selector_all).prop('checked', result);
    } else {
        $(selector_all).prop('checked', false);
    }
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

//Search on page
$("#searchInput").keyup(function () {
    let data = this.value.toLowerCase().split(" ");
    let jo = $("#table-body").find("tr");
    if (this.value.trim() === "") {
        jo.show();
        return;
    }
    jo.hide();

    jo.filter(function () {
        let $validCount = 0;
        let $t = $(this);
        let $temp = $t.clone();
        $temp.text($temp.text().toLowerCase());
        for (let d = 0; d < data.length; ++d) {
            if ($temp.is(":contains('" + data[d] + "')")) {
                $validCount++;
            }
        }
        return $validCount === data.length;
    }).show();
}).focus(function () {
    this.value = "";
    $(this).css({
        "color": "black"
    });
    $(this).unbind('focus');
}).css({
    "color": "#C0C0C0"
});
