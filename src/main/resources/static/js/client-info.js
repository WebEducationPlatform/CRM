$(document).ready(function () {
    selectOptions($("#edit-client-state"));
    selectOptions($("#edit-client-sex"));
});

function changeClient(id) {
    if($("#saveChanges")[0].className ==="btn btn-primary disabled"){
        return;
    }
    var SN = [];
    var $th = $('#SocialNetworks').find('th');
    try {
        $('#SocialNetworks').find('tbody tr').each(function (i, tr) {
            var obj = {}, $tds = $(tr).find('td');
            $th.each(function (index, th) {
                if ($(th)[0].innerText !== "id" && $tds.eq(index).text() === "") {
                    var current = document.getElementById("message");
                    current.textContent = "Заполните пустые поля в таблице 'Cоциальные сети'";
                    current.style.color = "red";
                    throw new Error("Пустые поля в таблице 'Cоциальные сети'");
                }
                if ($(th).attr('abbr') !== "") {
                    if(typeof $tds.eq(index).children().val()==='undefined') {
                        obj[$(th).attr('abbr')] = $tds.eq(index).text();
                    }else{
                        obj[$(th).attr('abbr')] = $tds.eq(index).children().val();
                    }
                }
            });
            SN.push(obj);
        });
    } catch (e) {
        return;
    }
    var Job = [];
    $th = $('#Job').find('th');
    try {
        $('#Job').find('tbody tr').each(function (i, tr) {
            var obj = {}, $tds = $(tr).find('td');
            $th.each(function (index, th) {
                if ($(th)[0].innerText !== "id" && $tds.eq(index).text() === "") {
                    var current = document.getElementById("message");
                    current.textContent = "Заполните пустые поля в таблице 'Работа'";
                    current.style.color = "red";
                    throw new Error("Пустые поля в таблице 'Работа'")
                }
                if ($(th).attr('abbr') !== "") {
                    obj[$(th).attr('abbr')] = $tds.eq(index).text();
                }
            });
            Job.push(obj);
        });
    } catch (e) {
        return;
    }
    let url = '/admin/rest/client/update';
    let wrap = {
        id: id,
        name: $('#edit-client-first-name').val(),
        lastName: $('#edit-client-last-name').val(),
        phoneNumber: $('#edit-client-phone-number').val(),
        email: $('#edit-client-email').val(),
        age: $('#edit-client-age').val(),
        sex: $('#edit-client-sex').find('option:selected').text(),
        state:  $('#edit-client-state').val(),
        country: $('#edit-client-country').val(),
        city: $('#edit-client-city').val(),
        socialNetworks: SN,
        jobs: Job
    };
    var current = document.getElementById("message");
    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(wrap),
        beforeSend: function(){
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function () {
            current.textContent = "Сохранено";
            current.style.color = "limegreen";
        },
        error: function (e) {
            console.log(e.responseText);
            current.textContent = "Ошибка сохранения. " + e.responseText;
            current.style.color = "red";
        }
    });
}

function disableInputE() {
    return event.keyCode !== 69;
}

$(document).on('click', 'td', (function (e) {
    if (e.target.localName !== "td" || e.target.firstElementChild !== null || (e.target.offsetParent.id !== "SocialNetworks" && e.target.offsetParent.id !== "Job") || $('#edit-client-first-name')[0].disabled) {
        return;
    }
    var t = e.target || e.srcElement;
    var elm_name = t.tagName.toLowerCase();
    if (elm_name === 'input') {
        return false;
    }
    var val = $(t).html();
    var code;
    if(e.target.cellIndex === 2 && e.target.offsetParent.id === "SocialNetworks"){
        code = '<select id="edit" value = "'+ val + '" class=\"form-control\">' + SNs + '</select>'
    }else {
        code = '<input type="text" id="edit" value="' + val + '" />';
    }
    $(t).empty().append(code);
    var newEditElement = $("#edit");
    selectOptions(newEditElement);
    newEditElement.focus();
    newEditElement.blur(function () {
        var val = $(this).val();
        $(this).parent().empty().html(val);
    });
}));

$(window).keydown(function (event) {
    if (event.target.id === "edit") {
        if (event.keyCode === 13) {
            $('#edit').blur();
        }
    }
});

function deleteSocial(id) {
    $('#deleteSocial' + id).parent().parent().remove();
}

function deleteJob(id) {
    $('#deleteJob' + id).parent().parent().remove();
}

var SNs="";
function addNewSN(snid) {
    if(SNs.length===0){
        console.log("Массив SocialMarkers пуст!");
        return;
    }
    $("#SN-table-body").append("<tr><td hidden=\"hidden\" id=\"edit-client-SN_" + snid + "_id\"></td><td id=\"edit-client-SN_" + snid + "_link\"></td><td id=\"edit-client-SN_" + snid + "_type\"></td><td><button type=\"button\" id=\"deleteSocial" + snid + "\" onclick=\"deleteSocial('" + snid + "')\" class=\"glyphicon glyphicon-remove\"></button></td></tr>")
}

function addNewJob(jobid) {
    $("#job-table-body").append("<tr><td hidden=\"hidden\" id=\"edit-client-job_" + jobid + "_id\"></td><td id=\"edit-client-job_" + jobid + "_organization\"></td><td id=\"edit-client-job_" + jobid + "_position\"></td><td><button type=\"button\" id=\"deleteJob" + jobid + "\" onclick=\"deleteJob('" + jobid + "')\" class=\"glyphicon glyphicon-remove\"></button></td></tr>")
}

function revertUnable() {
    var column1 =  $('#column1');
    column1.find('input').each(function () {
        if ($(this)[0].disabled === true) {
            $(this)[0].disabled = false;
            $('#editClientBtn').attr("class", "btn btn-secondary")[0].innerText = 'Заблокировать';
        } else {
            $(this)[0].disabled = true;
            $('#editClientBtn').attr("class", "btn btn-primary")[0].innerText = 'Редактировать';
        }
    });
    column1.find('select').each(function () {
        $(this)[0].disabled = $(this)[0].disabled !== true;
    });

    column1.find('button').each(function () {
        if ($(this).parent()[0].localName === 'td') {
            $(this)[0].disabled = $(this)[0].disabled !== true;
        }
    });
    $("#addNewSN")[0].disabled = $("#addNewSN")[0].disabled !== true;
    $("#addNewJob")[0].disabled = $("#addNewJob")[0].disabled !== true;
}

$(function () {
    $('#client-from').validator({
        disable: true
    })
});

var socialMarkers = [];

$(document).ready(function () {

    var url = '/user/socialMarkers';
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (res) {
            socialMarkers=res;
            $.each(socialMarkers,function (index, marker) {
                SNs = SNs + "<option>"+ marker + "</option>"
            });
        },
        error: function (error) {
            console.log(error);
        }
    });
});

function selectOptions (element) {
    element.find("option").each(function () {
        var val = $(this).closest('select').attr('value');
        var inText = $(this).text();
        if (val === inText) {
            $(this).attr("selected", "selected");
        }
    });
}