const SUCCESS_MESSAGE = 'Успешно! Вам на почту придет письмо с подтверджением регистрации. Перейдите по ссылке, чтобы задать пароль и получить доступ к Slack.';
const ERROR_MESSAGE = 'Ошибка! Попробуйте позже или обратитесь к администратору.';

$('#reg-button').on('click', function () {
    let name = $('#name');
    let lastName = $('#last-name');
    let email = $('#email');
    let message = $('#message');

    $.ajax({
        url: '/slack/registration',
        async: true,
        type: 'POST',
        data: {
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