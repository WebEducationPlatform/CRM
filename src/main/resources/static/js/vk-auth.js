//авторизация Вконтакте
function vk_popup(options) {
    var
        screenX = typeof window.screenX != 'undefined' ? window.screenX : window.screenLeft,
        screenY = typeof window.screenY != 'undefined' ? window.screenY : window.screenTop,
        outerWidth = typeof window.outerWidth != 'undefined' ? window.outerWidth : document.body.clientWidth,
        outerHeight = typeof window.outerHeight != 'undefined' ? window.outerHeight : (document.body.clientHeight - 22),
        width = options.width,
        height = options.height,
        left = parseInt(screenX + ((outerWidth - width) / 2), 10),
        top = parseInt(screenY + ((outerHeight - height) / 2.5), 10),
        features = (
            'width=' + width +
            ',height=' + height +
            ',left=' + left +
            ',top=' + top
        );
    return window.open(options.url, 'vk_oauth', features);
}

function doLogin() {
    var win;
    var redirect_uri = 'https://oauth.vk.com/blank.html';
    var uri_regex = new RegExp(redirect_uri);
    var url = '/vk-auth';
    win = vk_popup({
        width: 620,
        height: 370,
        url: url
    });
    var watch_timer = setInterval(function () {
        try {
            if (uri_regex.test(win.location)) {
                clearInterval(watch_timer);
                setTimeout(function () {
                    win.close();
                    document.location.reload();
                }, 500);
            }
        } catch (e) {
        }
    }, 100);
}