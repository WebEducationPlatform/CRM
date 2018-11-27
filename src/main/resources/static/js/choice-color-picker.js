//Загружаем цвет фона
$(document).ready(function () {

    $('#InterfaceSettings').on('shown.bs.modal', function GetColor() {
        $.ajax({
            type: "GET",
            url: "/user/ColorBackground",
            success: function(data){
                if (data.toLowerCase().indexOf("<html>") >= 0) {
                    window.location.reload();
                } else {
                    $("#wrap-selected-color").colorpicker('setValue', '#ffffff');
                    $("#wrap-selected-color").colorpicker('setValue', data);
                    $("#selected-color").val(data);
                }
            },
            error: function(error){
                alert(error);
            }
        })
    });
});

//Выбираем и сохраняем цвет для фона
$('#update-interface').click(function () {

    let selcolor = $("#selected-color").val();
    let wrap = {
        color : selcolor
    };

    $.ajax({
        type: "POST",
        url: "/user/ColorBackground",
        data: wrap,
        success: function(data){
            document.body.style.backgroundColor = selcolor;
            let newcolorbar = "-webkit-gradient(linear,left top,left bottom,from(" + selcolor + "),to(" + selcolor + "))";
            let navbar = document.getElementsByClassName('navbar-fixed-top')[0];
            navbar.style.backgroundImage = newcolorbar;
            navbar.style.borderColor = selcolor;
        },
        error: function(error){
            alert("Цвет не присвоен - " + error);
        }
    });
});
