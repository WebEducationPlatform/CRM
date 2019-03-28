// Предполагаем что статус не установлен, при открытии страницы.
$(function () {
    if(sessionStorage.getItem('student_status') == undefined) {
        sessionStorage.setItem('student_status', "false");
    }
    if(sessionStorage.getItem('student_default_status') == undefined) {
        sessionStorage.setItem('student_default_status', "false");
    }
});
// Запрос к базе о состоянии статуса происходит 1 раз при каждом новом открытии страницы (новой сессии),
// 2 раза при своевременной установке статуса админом после первого запуска и напоминания,
// n-раз до установки статуса, попап будет так же всплывать n-раз
$(function getStatus() {
    if ((sessionStorage.getItem('student_status') == 'false') || (sessionStorage.getItem('student_default_status') == 'false')) {
        var url = '/rest/properties';
        $.ajax({
            type: 'GET',
            url: url,
            success: function (response) {
                if (response.defaultStatusId === -1) {
                    var delay_popup = 2000;
                    var msg_pop = document.getElementById('msg_pop');
                    setTimeout("document.getElementById('msg_pop').style.display='block';document.getElementById('msg_pop').className += 'fadeIn';", delay_popup);
                } else {
                    sessionStorage.setItem('student_status', 'true');
                    document.getElementById('msg_pop').style.display='none';
                }
                if (response.defaultStudentStatus === null) {
                    let delay_popup2 = 2000;
                    let msg_pop2 = document.getElementById('msg_pop2');
                    setTimeout("document.getElementById('msg_pop2').style.display='block';document.getElementById('msg_pop2').className += 'fadeIn';", delay_popup2);
                } else {
                    sessionStorage.setItem('student_default_status', 'true');
                    document.getElementById('msg_pop2').style.display='none';
                }
            }
        });
    }
});
