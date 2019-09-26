
$(document).ready(function () {
    editor = CKEDITOR.replace('body', {
        allowedContent: true,
        height: '250px'
    });

    editor.addCommand("infoCommand", {
        exec: function (edt) {
            $("#infoModal").modal('show');
        }
    });
    editor.ui.addButton('SuperButton', {
        label: "Краткая информация о редакторе",
        command: 'infoCommand',
        toolbar: 'styles',
        icon: 'info.png'
        //icon: 'https://img.icons8.com/ios/26/000000/info.png'
    });
});