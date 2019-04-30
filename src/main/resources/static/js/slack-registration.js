const SUCCESS_MESSAGE = 'Успешно! Вам на почту придет письмо с подтверджением регистрации. Перейдите по ссылке, чтобы задать пароль и получить доступ к Slack.';
const ERROR_MESSAGE = 'Ошибка! Попробуйте позже или обратитесь к администратору.';
const CRM_URL = 'https://crm.java-mentor.com';

function getHash() {
    let urlParams = window.location.href.split("?");
    if (urlParams.length > 1) {
        return urlParams[1];
    }
    return null;
}

$('#reg-button').on('click', function () {
    let name = $('#name');
    let lastName = $('#last-name');
    let email = $('#email');
    let message = $('#message');

    $.ajax({
        url: CRM_URL + '/slack/registration',
        async: true,
        type: 'POST',
        data: {
            'hash': getHash(),
            'name' : name.val(),
            'lastName' : lastName.val(),
            'email': email.val()},
        success: function () {
            message.text(SUCCESS_MESSAGE);
        },
        error: function () {
            message.text(ERROR_MESSAGE);
        }
    });

});