// Предполагаем что статус не установлен, при открытии страницы.
$(function () {
    if (sessionStorage.getItem('campaigns_status') == undefined) {
        sessionStorage.setItem('campaigns_status', 'false');
    }
});
// Запрос к базе о состоянии статуса происходит 1 раз при каждом новом открытии страницы (новой сессии),
// 2 раза при своевременной установке статуса админом после первого запуска и напоминания,
// n-раз до установки статуса, попап будет так же всплывать n-раз
$(function getStatus() {
    if ((sessionStorage.getItem('campaigns_status') === 'false')) {
        var url = '/rest/properties';
        $.ajax({
            type: 'GET',
            url: url,
            success: function (response) {
                if (true) {
                    var delay_popup = 2000;
                    var msg_campaigns = document.getElementById('msg_campaigns');
                    setTimeout(function () {
                        msg_campaigns.style.display = 'block';
                        msg_campaigns.className += 'fadeIn';
                    }, delay_popup);
                } else {
                    sessionStorage.setItem('student_status', 'true');
                    msg_campaigns.style.display = 'none';
                }
            }
        });
    }
});
