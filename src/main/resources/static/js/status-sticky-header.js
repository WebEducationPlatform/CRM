// Модуль для логики залипания заголовков в статусах в разделе "Доска"

var clientsContent = $("#status-columns");
var navbar = $('.navbar-default')[0];
var columHeaderTop = $(".column-header")[0].getBoundingClientRect().top;

// Если есть колонки то добавляем логику
if (clientsContent) {
    // Обработка на скрол
    document.addEventListener("scroll", function(e) {
        if (window.pageYOffset + navbar.offsetHeight > columHeaderTop) {
            if (!clientsContent.hasClass('fix-status-header')) {
                clientsContent.addClass("fix-status-header");
                // Получаем ширину навбара и выставляем заголовок чтобы прили
                $('.column-header-wrapper').css({top: navbar.offsetHeight + 'px'});
            }
        } else {
            clientsContent.removeClass("fix-status-header");
        }
    });

    // Добавляем врапер ко всем элементам column-header
    $(document).ready(function () {
        async function preparingSticky() {
            $(".column-header").each(function (index, value) {
                var parent = value.parentNode;
                var wrapper = document.createElement('div');
                wrapper.classList.add('column-header-wrapper');
                var wrapperDummy = document.createElement('div');
                wrapperDummy.classList.add('column-header-wrapper-dummy');

                parent.replaceChild(wrapperDummy, value);
                wrapper.appendChild(value);
                parent.insertBefore(wrapper, wrapperDummy);
            });
        }
        preparingSticky();
    });
}

