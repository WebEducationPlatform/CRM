
$(document).ready(function () {
    editor = CKEDITOR.replace('body', {
        allowedContent: true,
        height: '250px'
    });

    editor.addCommand("infoCommend", {
        exec: function (edt) {
            $("#infoModal").modal('show');
        }
    });
    editor.ui.addButton('SuperButton', {
        label: "Info",
        command: 'infoCommend',
        toolbar: 'styles',
        icon: 'info.png'
    });
});