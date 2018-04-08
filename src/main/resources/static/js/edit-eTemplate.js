function saveTemplate(templateId) {
    let url = '/admin/editEmailTemplate';

    let wrap = {
        templateId: templateId,
        templateText: CKEDITOR.instances['body'].getData()
    };
    var current = document.getElementById("message");
    $.ajax({
        type: "POST",
        url: url,
        data: wrap,
        beforeSend: function(){
            current.style.color = "darkorange";
            current.textContent = "Загрузка...";

        },
        success: function (result) {
            current.style.color = "limegreen";
            current.textContent = "Сохранено";
        },
        error: function (e) {
            current.textContent = "Ошибка сохранения";
            current.style.color = "red";
            console.log(e.responseText);
        }
    });
}
