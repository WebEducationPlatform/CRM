// Модуль для логики залипания заголовков статусов на странице "Доска"

var clientsContent = $("#status-columns");
var navbar = $('.navbar-default')[0];
var columnHeaders = $(".column-header");

// Добавляем врапер ко всем элементам column-header
$(document).ready(function () {
    var columnHeaderTop = columnHeaders[0].getBoundingClientRect().top + window.pageYOffset;
    // Если есть колонки, то добавляем логику
    if (clientsContent) {
        // Обработка на скрол по документу
        document.addEventListener('scroll', function(e) {
            // Проверяем текущее отображение статусов - выполняем только для горизонтального
            if ((clientsContent.css('flex-direction') !== 'column')
                && (window.pageYOffset + navbar.offsetHeight > columnHeaderTop)) {
                if (!clientsContent.hasClass('fix-status-header')) {
                    clientsContent.addClass('fix-status-header');
                    // Получаем ширину навбара и выставляем заголовок, чтобы прилипли
                    $('.column-header-wrapper').css({
                        top: navbar.offsetHeight + 'px'
                    });
                }
            } else {
                if (clientsContent.hasClass('fix-status-header')) {
                    // Удаляем класс липучку
                    clientsContent.removeClass('fix-status-header');
                    // И смещение
                    $('.column-header-wrapper').css({transform: ''});
                }
            }

            // Если прилипли уже, то смещаем и по Х на скролл
            if (clientsContent.hasClass('fix-status-header')) {
                $('.column-header-wrapper').each(function (index, value) {
                    $(value).css({
                        transform: `translateX(${- window.pageXOffset}px)`,
                    });
                });
            }
        });

        async function preparingSticky() {
            $('.column-header').each(function (index, value) {
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
    }
});