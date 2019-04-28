// Предполагаем что статус не установлен, при открытии страницы.
$(function () {
    if(sessionStorage.getItem('student_default_status') == undefined) {
        sessionStorage.setItem('student_default_status', "false");
    }
    if(sessionStorage.getItem('birth_day_message_template') == undefined) {
        sessionStorage.setItem('birth_day_message_template', "false");
    }
});
// Запрос к базе о состоянии статуса происходит 1 раз при каждом новом открытии страницы (новой сессии),
// 2 раза при своевременной установке статуса админом после первого запуска и напоминания,
// n-раз до установки статуса, попап будет так же всплывать n-раз
$(function getStatus() {
    if ((sessionStorage.getItem('student_default_status') == 'false')
        || (sessionStorage.getItem('birth_day_message_template') == 'false')) {
        var url = '/rest/properties';
        $.ajax({
            type: 'GET',
            url: url,
            success: function (response) {
                if (response.defaultStudentStatus === null) {
                    let delay_status_new_clients = 2000;
                    setTimeout("document.getElementById('msg_status_to_new_clients').style.display='block';document.getElementById('msg_status_to_new_clients').className += 'fadeIn';", delay_status_new_clients);
                } else {
                    sessionStorage.setItem('student_default_status', 'true');
                    document.getElementById('msg_status_to_new_clients').style.display='none';
                }
                if (response.birthDayMessageTemplate === null) {
                    let delay_birthday = 2000;
                    setTimeout("document.getElementById('birthday-template-status').style.display='block';document.getElementById('birthday-template-status').className += 'fadeIn';", delay_birthday);
                } else {
                    sessionStorage.setItem('birth_day_message_template', 'true');
                    document.getElementById('birthday-template-status').style.display='none';
                }
            }
        });
    }
});
