//Open creaate new campaign modal
$("#button_create_campaign").click(function () {
    $('#vk-campaign-create-modal').modal('show');
});

//Create new campaign
$("#create_campaign").click(function () {
    $(this).prop("disabled", true);

    let name = $("#campaign-name").val();
    let appid = $("#campaign-app-id").val();
    let text = $("#campaign-add-text").val();

    $.ajax({
        async: true,
        type: 'GET',
        url: '/rest/vk-campaigns/isnameexists',
        dataType: "JSON",
        data: { name : name },
        success: function (exists) {
            if(!exists) {
                window.location = "/vk/campaigns/create/"
                    + "?name=" + name
                    + "&appid=" + appid
                    + "&text=" + text;
            } else {
                $("#create_campaign").prop("disabled", false);
                alert("Кампания с названием " + name + " уже существует!");
            }
        }
    });
});

//Edit campaign page redirect button
$(".button_edit_campaign").click( function () {
    let id = this.value;
    window.location = "/vk/campaigns/edit/" + id;
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
