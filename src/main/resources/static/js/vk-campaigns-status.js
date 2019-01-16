// Предполагаем что статус не установлен, при открытии страницы.
$(function () {
    if (sessionStorage.getItem('campaigns_status') == undefined) {
        sessionStorage.setItem('campaigns_status', 'unknown');
    }
});

$(function getStatus() {
    if ((sessionStorage.getItem('campaigns_status') === 'unknown')) {
        let url = '/rest/vk-campaigns/havingproblems';
        $.ajax({
            type: 'GET',
            url: url,
            success: function (response) {
                let msg_campaigns = document.getElementById('msg_campaigns');
                if (response) {
                    let delay_popup = 2000;
                    setTimeout(function () {
                        msg_campaigns.style.display = 'block';
                        msg_campaigns.className += 'fadeIn';
                    }, delay_popup);
                } else {
                    sessionStorage.setItem('campaigns_status', 'ok');
                    msg_campaigns.style.display = 'none';
                }
            }
        });
    }
});
