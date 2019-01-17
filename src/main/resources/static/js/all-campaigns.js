//Open creaate new campaign modal
$("#button_create_campaign").click(function () {
    $('#vk-campaign-create-modal').modal('show');
});

//Create new campaign
$("#create_campaign").click(function () {
    $(this).prop("disabled", true);

    let name = $("#campaign-name").val(),
        appid = $("#campaign-app-id").val(),
        text = $("#campaign-add-text").val(),
        duplicates = $('#duplicates').is(':checked');

    $.ajax({
        type: 'GET',
        url: '/rest/vk-campaigns/isnameexists',
        dataType: "JSON",
        data: { name : name },
        success: function (exists) {
            if(!exists) {
                window.location = "/vk/campaigns/create/"
                    + "?name=" + name
                    + "&appid=" + appid
                    + "&text=" + text
                    + "&duplicates=" + duplicates;
            } else {
                $("#create_campaign").prop("disabled", false);
                alert("Кампания с названием " + name + " уже существует!");
            }
        }
    });
});

//Edit campaign modal button
var $modaledit = $('#vk-campaign-edit-modal').modal({show: false});

$(".button_edit_campaign").click( function () {
    let id = this.value;

    $.ajax({
        type: 'GET',
        url: '/rest/vk-campaigns/' + id,
        dataType: "JSON",
        success: function (response) {
            if (response != null) {
                $('#edit-campaign-id').val(response.campaignId);
                $('#edit-campaign-name').val(response.campaignName);
                $('#edit-campaign-add-text').val(response.requestText);
                $('#edit-duplicates').prop('checked', response.allowDuplicates);
                $modaledit.modal('show');
            }
        },
        error: function (request, status, error) {
            console.log("ajax call went wrong:" + request.responseText);
        }
    });
});

//Statistics campaign modal button
var $modalstats = $('#vk-campaign-stats-modal').modal({show: false});

$(".button_stats_campaign").click( function () {
    let id = this.value;

    $.ajax({
        type: 'GET',
        url: '/rest/vk-campaigns/' + id + '/stats',
        dataType: "JSON",
        success: function (response) {
            if (response != null) {
                $('#stats_all_ids').val(response.allIds);
                $('#stats_sent_all').val(response.requestSent);
                $('#stats_already_friends').val(response.friendsAdded);
                $('#stats_response_sent').val(response.requestSent);
                $('#stats_response_approved').val(response.requestApproved);
                $('#stats_response_reattempt').val(response.requestReattempt);
                $('#stats_error_selfrequest').val(response.selfRequest);
                $('#stats_error_you_blacklisted').val(response.youBlacklisted);
                $('#stats_error_in_your_blacklist').val(response.inYourBlacklist);
                $('#stats_error_not_found').val(response.notFound);


                $modalstats.modal('show');
            }
        },
        error: function (request, status, error) {
            console.log("ajax call went wrong:" + request.responseText);
        }
    });
});

//Delete campaign button
$(".button_delete_campaign").click( function () {
    if(!confirm("Вы уверены, что хотите удалить кампанию?")) {return}
    let id = this.value;
    $.ajax({
        type: 'DELETE',
        url: '/rest/vk-campaigns/' + id,
        success: function (response) {
            if (response !== "OK") {
                alert("Ошибка удаления!");
            } else {
                location.reload();
            }
        }
    });
});

//Start campaign button
$(".button_start_campaign").click( function () {
    if(!confirm("Вы уверены, что хотите запустить кампанию?")) {return}
    let id = this.value;
    $.ajax({
        type: 'PATCH',
        url: '/rest/vk-campaigns/' + id + '/start',
        success: function (response) {
            if (response.response !== "started") {
                alert("Ошибка запуска!");
            } else {
                location.reload();
            }
        }
    });
});

//Stop campaign button
$(".button_stop_campaign").click( function () {
    if(!confirm("Вы уверены, что хотите приостановить кампанию?")) {return}
    let id = this.value;
    $.ajax({
        type: 'PATCH',
        url: '/rest/vk-campaigns/' + id + '/stop',
        success: function (response) {
            if (response.response !== "stopped") {
                alert("Ошибка остановки!");
            } else {
                location.reload();
            }
        }
    });
});


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
