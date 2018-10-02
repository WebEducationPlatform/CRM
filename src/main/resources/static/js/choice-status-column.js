//Список статусов возможных для клиентов-студентов
$(function () {
    $('#choice-status-column-modal').on('show.bs.modal', function () {
        $("#status-column").empty();
        $.ajax({
            type: 'POST',
            url: '/slack/get/students/statuses',
            dataType: 'json',
            success: function (json) {
                $.each(json, function (index, element) {
                    $('#status-column')
                        .append($('<option value=' + element.id + '>')
                            .append(element.name)
                            .append('</option>'));
                })
            }
        });
    });
});
//Выбираем и сохраняем дефолтный статус
$('#update-status').click(function () {
    let selectedId = $("select#status-column").val();
    $.ajax({
        type: 'GET',
        url: '/slack/set/default/' + selectedId,
    });
});
