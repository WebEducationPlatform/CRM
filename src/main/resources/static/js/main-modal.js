var backFromModalUrl;
var userLoggedIn;

function backUrl(url) {
    var state = {};
    var title = '';
    history.replaceState(state, title, url);
}

// Эта функция закрывает карточку юзера и переводит на страницу | http://localhost:9999/client |
// Относится к крестику в карточке, там два события на нем: backUrl() и это! [Трелло-ид:890]
$('#main-modal-window').on( "click", function() {
    showUsersInStatuses();
});


//Запрос: на главной модалке показать или скрыть
$('#client-request-button').click( () => {
    var x = document.getElementById("client-request");
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
});

function getAllUrlParams(url) {
    // извлекаем строку из URL или объекта window
    var queryString = url ? url.split('?')[1] : window.location.search.slice(1);
    // объект для хранения параметров
    var obj = {};
    // если есть строка запроса
    if (queryString) {
        // данные после знака # будут опущены
        queryString = queryString.split('#')[0];
        // разделяем параметры
        var arr = queryString.split('&');

        for (var i = 0; i < arr.length; i++) {
            // разделяем параметр на ключ => значение
            var a = arr[i].split('=');
            // обработка данных вида: list[]=thing1&list[]=thing2
            var paramNum = undefined;
            var paramName = a[0].replace(/\[\d*\]/, function (v) {
                paramNum = v.slice(1, -1);
                return '';
            });

            // передача значения параметра ('true' если значение не задано)
            var paramValue = typeof(a[1]) === 'undefined' ? true : a[1];

            // преобразование регистра
            paramName = paramName.toLowerCase();
            paramValue = paramValue.toLowerCase();
            // если ключ параметра уже задан
            if (obj[paramName]) {
                // преобразуем текущее значение в массив
                if (typeof obj[paramName] === 'string') {
                    obj[paramName] = [obj[paramName]];
                }
                // если не задан индекс...
                if (typeof paramNum === 'undefined') {
                    // помещаем значение в конец массива
                    obj[paramName].push(paramValue);
                }
                // если индекс задан...
                else {
                    // размещаем элемент по заданному индексу
                    obj[paramName][paramNum] = paramValue;
                }
            }
            // если параметр не задан, делаем это вручную
            else {
                obj[paramName] = paramValue;
            }
        }
    }
    return obj;
}

$(function () {
    $(document).ready(function () {
        // Указываем ендпоинты которые надо проверять, на которых возможно открытие модального диалога карточки по ID
        var enpointsWithId = ['client', 'client/allClients', 'student/all'];

        for (var i = 0; i < enpointsWithId.length; i++) {
            // Если совпадение есть, то выполняем логику и выходим
            if (window.location.href.indexOf(enpointsWithId[i] + '?id=') !== -1) {
                var clientId = getAllUrlParams(window.location.href).id;
                var currentModal = $('#main-modal-window');
                currentModal.data('clientId', clientId);
                currentModal.modal('show');
                break;
            }
        }
    });
});

function changeUrl(page, id) {
    var state = {'page_id': id, 'user_id': id};
    var title = '';
    var url = page + '?id=' + id;
    backFromModalUrl = page;
    history.replaceState(state, title, url);
}

$(function () {
    $('#main-modal-window').on('hidden.bs.modal', function () {
        var clientId = $(this).data('clientId');
        dropRepeatedFlag(clientId, false);
        $('.assign-skype-call-btn').removeAttr("disabled");
        $('div#assign-unassign-btns').empty();
        $('.skype-notification').empty();
        $('.confirm-skype-login').remove();
        $('.enter-skype-login').remove();
        $('.skype-panel').remove();
        $('.skype-text').empty();
        $('.remove-element').remove();
        $('.hide-client-collapse').attr('id', 'hideClientCollapse');
        $('.postpone-date').attr('id', 'postponeDate');
        $('.textcomplete').removeAttr('id');
        $('.main-modal-comment').removeAttr('id');
        $('.remove-tag').remove();
        $('.history-line').find("tbody").empty();
        $('#sendEmailTemplateStatus').empty();
        $('#sendSocialTemplateStatus').empty();
        $('.client-collapse').collapse('hide');
        $('.remove-history').remove();
        $('.upload-more-history').removeAttr('data-clientid');
        $('.upload-more-history').attr("data-page", 1);
        backUrl(backFromModalUrl);
        // clientsSearch(); ??? нужно ли тут это
    });
});

//Напоминание
$('#postponeCommentModal').on('hidden.bs.modal', function () {
    let currentModal = $('#main-modal-window');
    currentModal.css("overflow-y","auto");
})

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {
        var clean = $('.history-line').find("tbody");
        let clientId = $(this).data('clientId');
        let formData = {
            clientId: clientId
        };
        clean.empty();

        $.ajax({
            type: "POST",
            url: "/user/notification/postpone/getAll",
            data: formData,
            success: function (result) {
                if(result.length > 0) {
                    $.ajax({
                        type: "POST",
                        url: "rest/client/postpone/getComment",
                        data: formData,
                        success: function (result) {
                            let currentModal = $('#postponeCommentModal');
                            currentModal.modal('show');
                            let div = document.querySelector(".colorChoose");
                            div.innerHTML = "";
                            var node = document.createElement('div');
                            node.innerHTML = '<p> ' + result;
                            div.appendChild(node);
                        },
                        error: function (e) {
                            console.log(e)
                        }
                    });
                }
            },
            error: function (e) {
                console.log(e)
            }
        });
    });
});

function dropRepeatedFlag(clientId, repeated) {
    var url = '/rest/client/setRepeated';
    var formData = {
        clientId: clientId,
        isRepeated: repeated
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {

        },
        error: function (e) {
            console.log(e);
        }
    });

    $('#repeated-client-info').hide();
}

$(function () {
    $('#main-modal-window').on('show.bs.modal', function () {

        var cardFieldEmail;
        var cardFieldPhone;
        var cardFieldSkype;
        var cardFieldBirthday;
        var cardFieldAge;
        var cardFieldGender;
        var cardFieldCountry;
        var cardFieldCity;
        var cardFieldUniver;

        var currentModal = $(this);
        var clientId = $(this).data('clientId');
        $('#slackLinkModal').data('clientId', clientId);

        $.ajax({
            type: 'GET',
            url: "/otherInformation/client/" + clientId,
            success: function (listInfo) {
                for (let i = 0; i < listInfo.length; i++) {
                    if (listInfo[i].cardField === "EMAIL") {
                        cardFieldEmail = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "PHONE") {
                        cardFieldPhone = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "SKYPE") {
                        cardFieldSkype = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "BIRTHDAY") {
                        cardFieldBirthday = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "AGE") {
                        cardFieldAge = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "GENDER") {
                        cardFieldGender = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "COUNTRY") {
                        cardFieldCountry = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "CITY") {
                        cardFieldCity = listInfo[i].textValue;
                    }
                    if (listInfo[i].cardField === "UNIVER") {
                        cardFieldUniver = listInfo[i].textValue;
                    }
                }
            },
            error: function (error) {
                console.log(error);
            }
        });

        $.ajax({
            async: true,
            type: 'GET',
            dataType: "JSON",
            url: '/rest/client/card/' + clientId,
            success: function (clientCard) {
                var status = clientCard.statuses;
                var client = clientCard.client;

                $('#client-status-list').empty();
                $.each(status, function (i, s) {
                    $('#client-status-list').append(
                        '<li><a onclick="changeStatus(' + clientId + ', ' + s.id + ')" href="#">' + s.name + '</a></li>'
                    );
                });

                if (!client_has_telegram(client) && client.clientPhones[0] !== '') {
                    set_telegram_id_by_phone(client.clientPhones[0]);
                }
                $("#conversations-title").prop('innerHTML', 'Чат с ' + client.name + ' ' + client.lastName);

                if (userLoggedIn === undefined) {
                    getUserLoggedIn(false);
                }

                let user = userLoggedIn;
                if (client.ownerUser.id != null) {
                    var owenerName = client.ownerUser.firstName + ' ' + client.ownerUser.lastName;

                }

                if (client.ownerMentor.id != null) {
                    var owenerMentorName = client.ownerMentor.firstName + ' ' + client.ownerMentor.lastName;

                }
                var adminName = user.firstName + ' ' + user.lastName;

                $('#main-modal-window').data('userId', user.id);

                currentModal.find('.modal-title-profile').text(client.name + ' ' + client.lastName);
                currentModal.find('#client-set-status-button').text(client.status.name);

                if (cardFieldEmail) {
                    $('#client-email').text(cardFieldEmail);
                } else {
                    $('#client-email').text(client.clientEmails[0]);
                }

                if (cardFieldPhone) {
                    $('#client-phone').text(cardFieldPhone);
                } else {
                    $('#client-phone').text(client.clientPhones[0]);
                }

                if (client.skype) {
                    $('#client-skype').text(client.skype);
                } else {
                    $('#client-skype').text(cardFieldSkype);
                }

                if (client.canCall && user.ipTelephony) {
                    $('#client-phone')
                        .after('<td id="web-call-voximplant" class="remove-tag" style="white-space: nowrap;">' + '<button class="btn btn-default btn btn-light btn-xs call-to-client main-modal" onclick="webCallToClient(' + client.clientPhones[0] + ')">' + '<span class="glyphicon glyphicon-earphone call-icon">' + '</span>' + '</button>' + '</td>')
                        .after('<td id="callback-call-voximplant" class="remove-tag">' + '<button class="btn btn-default btn btn-light btn-xs callback-call" onclick="callToClient(' + user.phoneNumber + ', ' + client.clientPhones[0] + ')">' + '<span class="glyphicon glyphicon-phone">' + '</span>' + '</button>' + '</td>');
                    $(".call-to-client.main-modal").after('<button id="btn-call-off" style="display: none !important;" class="btn btn-default btn btn-light btn-xs web-call-off">' + '<span class="glyphicon glyphicon-phone-alt call-icon">' + '</span>' + '</button>' + '</td>');
                    $('.call-to-client.main-modal').after('<button id="btn-mic-off" style="display: none !important;" class="btn btn-default btn btn-light btn-xs web-call-mic-off">' + '<span class="glyphicon glyphicon-ice-lolly">' + '</span>' + '</button>' + '</td>');
                    $('#btn-mic-off').hide();
                    $('#btn-call-off').hide();
                }


                if (cardFieldAge) {
                    $('#client-age').text(cardFieldAge);
                } else {
                    if (client.age > 0) {
                        $('#client-age').text(client.age);
                    } else {
                        $('#client-age').text('');
                    }
                }

                if (cardFieldGender) {
                    $('#client-sex').text(cardFieldGender);
                } else {
                    $('#client-sex').text(client.sex);
                }

                if (client.clientDescriptionComment != null && client.clientDescriptionComment.length > 0) {
                    $('#client-label').text(client.clientDescriptionComment);
                } else {
                    $('#client-label').text('');
                }
                if (client.email == null) {
                    $('#email-href').hide();
                } else {
                    $('#email-href').show();
                }

                if (cardFieldBirthday) {
                    $('#client-date-of-birth').text(cardFieldBirthday);
                } else if (client.birthDate) {
                    let bDate = client.birthDate.split('-');
                    $('#client-date-of-birth').text(bDate[2] + '.' + bDate[1] + '.' + bDate[0]);
                } else {
                    $('#client-date-of-birth').text('');
                }

                if (cardFieldCountry) {
                    $('#client-country').text(cardFieldCountry);
                } else {
                    $('#client-country').text(client.country);
                }

                if (cardFieldCity) {
                    $('#client-city').text(cardFieldCity);
                } else {
                    $('#client-city').text(client.city);
                }

                if (cardFieldUniver) {
                    $('#client-university').text(cardFieldUniver);
                } else {
                    $('#client-university').text(client.university);
                }

                if (client.requestFrom !== null) {
                    $('#client-request-button').show();
                    $('#client-request').text(client.requestFrom);
                } else {
                    $('#client-request-button').hide();
                    $('#client-request').empty();
                }
                // здесь вставка ссылок в кнопки вк, фб и слак
                $('#vk-href').hide();
                $('#vk-im-button').hide();
                $('#slack-href').hide();
                $('#fb-href').hide();

                document.getElementById("profilePhoto").removeAttribute("src");
                for (var i = 0; i < client.socialProfiles.length; i++) {
                    if (client.socialProfiles[i].socialNetworkType.name === 'vk') {
                        //ajax call for profile photo
                        let vkref = client.socialProfiles[i].socialNetworkType.link + client.socialProfiles[i].socialId;
                        let url = '/rest/vkontakte/getProfilePhotoById';

                        $.ajax({
                            url: url,
                            async: true,
                            type: 'GET',
                            data: {vkref: vkref},
                            dataType: 'json',
                            complete: function (data) {
                                document.getElementById("profilePhoto").setAttribute("src", data.responseText);
                            }
                        });

                        $('#vk-href').attr('href', vkref);
                        $('#vk-href').show();


                    }

                    $('#chat-button').attr("clientID", client.id);
                    $('#chat-im-count').text($('#chat-notification' + clientId).text());
                    $('#chat-button').show();

                    if (client.socialProfiles[i].socialNetworkType.name === 'facebook') {
                        $('#fb-href').attr('href', client.socialProfiles[i].socialNetworkType.link + client.socialProfiles[i].socialId);
                        $('#fb-href').show();
                    }

                    if (client.socialProfiles[i].socialNetworkType.name === 'slack') {

                        let clientSlackId = client.socialProfiles[i].socialId;

                        $.ajax({
                            url: '/slack/get/chat/by/client/' + clientSlackId,
                            async: true,
                            type: 'GET',
                            success: function (data) {
                                $('#slack-href').attr('href', slack_url + '/messages/' + data);
                            },
                            error: function () {
                                $('#slack-href').attr('href', slack_url + '/team/' + clientSlackId);
                            },
                            complete: function () {
                                $('#slack-href').show();
                            }
                        });
                    }
                    get_interlocutors(clientId);
                }
//append_all_chats_message(

                var btnBlock = $('div#assign-unassign-btns-skype');
                var btnBlock1 = $('div#assign-unassign-btns1');
                var btnBlock2 = $('div#assign-unassign-btns2');
                var btnBlock3 = $('div#assign-unassign-btns3');
                var btnBlock4 = $('div#slack-invite');
                var message = $('div#message');



                if (client.liveSkypeCall) {
                    btnBlock.after('<div class="remove-tag confirm-skype-interceptor"><div class="update btn-group"><button id="assign-skype' + client.id + '" type="button" onclick="updateCallDate(' + client.id + ')" class="btn btn-default update-date-btn btn-sm"><span class="glyphicon glyphicon-pencil"></span> Изменить время созвона</button>\n' +
                        '<button id="deleteDate" type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <span class="glyphicon glyphicon-remove"></span></button>' +
                        '<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="deleteDate">\n' +
                        '    <li><a onclick="deleteCallDate(' + client.id + ')" href="#">Удалить созвон</a></li>\n' +
                        // '    <li><a href="#">Отмена</a></li>\n' +
                        '    </ul>' +
                        '</div>' +
                        '<div class="skype-notification"></div>' +
                        '</div>')
                } else {
                    btnBlock.after(
                        '<div class="remove-tag confirm-skype-interceptor">' +
                        '<button id="assign-skype' + client.id + '" onclick="assignSkype(' + client.id + ')" class="btn btn-primary center-block assign-skype-call-btn btn-sm">Назначить первый созвон</button>' +
                        '<div class="skype-notification"></div>' +
                        '</div>')
                }

                btnBlock1.append('<button class="btn btn-info btn-sm remove-tag" id="get-slack-invite-link-button" data-toggle="modal" data-target="#slackLinkModal">Ссылка на первый урок</button>');

                var currentUserRole;
                for (var i = 0; i < userLoggedIn.role.length; i++) {
                    if(userLoggedIn.role[i].roleName === 'ADMIN' || userLoggedIn.role[i].roleName === 'OWNER'){
                        currentUserRole = 'ADMIN'
                    }
                }

                if (currentUserRole !== undefined) {

                    if (client.ownerUser.id === null) {
                        btnBlock2.append('<button class="btn btn-info btn-sm remove-tag" id="assign-client' + client.id + '"onclick="assign(' + client.id + ')"> Взять себе карточку </button>');
                    }
                    if (client.ownerUser !== null && owenerName === adminName) {
                        btnBlock2.append('<button class="btn btn-sm btn-warning remove-tag" id="unassign-client' + client.id + '" onclick="unassign(' + client.id + ')"> Отказаться от карточки </button>');
                    }
                } else {
                    if (client.ownerMentor.id === null) {
                        btnBlock2.append('<button class="btn btn-info btn-sm remove-tag" id="assign-client' + client.id + '"onclick="assignMentor(' + client.id + ' , '+ userLoggedIn.id + ')"> Взять себе карточку </button>');
                    }
                    if (client.ownerMentor !== null && owenerMentorName === adminName) {
                        btnBlock2.append('<button class="btn btn-sm btn-warning remove-tag" id="unassign-client' + client.id + '" onclick="unassignMentor(' + client.id + ')"> Отказаться от карточки </button>');
                    }

                }
                btnBlock3.append('<a href="/client/clientInfo/' + client.id + '"><button class="btn btn-info btn-sm remove-tag" id="client-info" rel="clientInfo"> Расширенная информация </button></a>');

                btnBlock4.append('<button class="btn btn-info btn-sm remove-tag" id="slack-inv" onclick="inviteSlack(' + '\'' + client.clientEmails[0] + '\'' + ')">Пригласить в Slack</button>');
                message.text("");

                $('#contract-btn').empty();

                if (client.contractLinkData != null) {
                    $('#contract-btn').empty().append('<button class="btn btn-info btn-sm" id="get-contract-button" ' +
                        'data-toggle="modal" data-target="#contract-client-link-modal" >Договор</button>');
                    $.ajax({
                        type: 'GET',
                        url: "/contract/updateLink?id=" + client.id,
                        success: function (newLink) {
                            $('#contract-client-link-modal-link').empty().val(newLink);
                        }
                    });
                } else {
                    $('#contract-btn').empty().append('<button class="btn btn-info btn-sm" id="get-contract-button" ' +
                        'data-toggle="modal" data-target="#contract-client-setting-modal" >Договор</button>');
                    $('#contract-client-setting-contract-link').empty();
                }

                $('#other-information-btn').empty();

                if (client.otherInformationLinkData != null) {
                    $('#other-information-btn').append('<button class="btn btn-info btn-sm" id="get-other-info-btn" ' +
                        'data-toggle="modal" data-target="#other-information-value-modal" >Дополнительная информация</button>');
                    $.ajax({
                        async: true,
                        type: 'GET',
                        url: "/otherInformation/client/" + client.id,
                        success: function (listInfo) {
                            let id = "client-" + client.id + "history";
                            console.log(id);
                            for (let i = 0; i < listInfo.length; i++) {
                                let name = listInfo[i].nameField;
                                if (listInfo[i].typeField === "CHECKBOX") {
                                    if (listInfo[i].checkboxValue) {
                                        $('#' + id + ' tbody').append('<tr><td>' + name + '</td><td>ДА</td></tr>')
                                    } else {
                                        $('#' + id + ' tbody').append('<tr><td>' + name + '</td><td>НЕТ</td></tr>')
                                    }
                                } else if (listInfo[i].typeField === "CHECKBOXES") {
                                    $('#' + id + ' tbody').append('<tr><td colspan="2" align="center">' + listInfo[i].nameField + '</td></tr>')
                                    for (let j = 0; j < listInfo[i].oimc.length; j++) {
                                        if (listInfo[i].oimc[j].checkboxValue) {
                                            $('#' + id + ' tbody').append('<tr><td>' + listInfo[i].oimc[j].nameField + '</td><td>ДА</td></tr>')
                                        } else {
                                            $('#' + id + ' tbody').append('<tr><td>' + listInfo[i].oimc[j].nameField + '</td><td>НЕТ</td></tr>')
                                        }
                                    }
                                } else {
                                    $('#' + id + ' tbody').append('<tr><td>' + name + '</td>' +
                                        '<td>' + listInfo[i].textValue + '</td>' + '</tr>');
                                }
                            }
                        }
                    });
                } else {
                    $('#other-information-btn').empty().append('<button class="btn btn-info btn-sm" id="get-other-info-btn" ' +
                        'data-toggle="modal" data-target="#other-information-link-modal" >Дополнительная информация</button>');
                    $('#other-information-link-input').empty();
                }

                    $('#message-history-btn').empty().append('<button class="btn btn-info btn-sm" id="get-other-info-btn" ' +
                        'data-toggle="modal" data-target="#message-history-modal" >История сообщений</button>');

                $('.send-all-custom-message').attr('clientId', clientId);
                $('.send-all-message').attr('clientId', clientId);
                $('#hideClientCollapse').attr('id', 'hideClientCollapse' + client.id);
                $('#postponeDate').attr('id', 'postponeDate' + client.id);
                $('#postpone-accordion').append('<h4 class="panel-title remove-element">' + '<a href="#hideClientCollapse' + client.id + '" сlass="font-size" data-toggle="collapse" data-parent="#hideAccordion" > Добавить напоминание  </a>' + '</h4>');
                $('#postpone-div').append('<button class="btn btn-md btn-info remove-element" onclick="hideClient(' + client.id + ')"> OK </button>');
                $('.postponeStatus').attr('id', 'postponeStatus' + client.id);
                $('.textcomplete').attr('id', 'new-text-for-client' + client.id);
                $('.comment-div').append('<button class="btn btn-sm btn-success comment-button remove-element" id="assign-client' + client.id + '"  onclick="sendComment(' + client.id + ', \'test_message\')"> Сохранить </button>');
                $('.main-modal-comment').attr('id', 'client-' + client.id + 'comments');
                $('.upload-history').attr('data-id', client.id).attr('href', '#collapse' + client.id);
                $('.client-collapse').attr('id', 'collapse' + client.id);
                $('.history-line').attr('id', 'client-' + client.id + 'history');
                $('.upload-more-history').attr('data-clientid', client.id);
                $('#repeated-client-info').hide();

                if (client.repeated) {

                    $('#repeated-client-info').show();

                }

                /*Client card's comments panel, see comments.js*/
                var list = client.comments;

                let user_id = user.id;

                let ulComments = $('#client-' + client.id + 'comments');
                let removeComment = "";
                let editComment = "";
                let removeAnswer = "";
                let editAnswer = "";
                let dateCommit = "";
                let html = "";
                ulComments.empty();

                for (let i = 0; i < list.length; i++) {
                    let d = new Date(list[i].dateFormat);
                    let dateFormat = ("0" + d.getDate()).slice(-2) + "." + ("0"+(d.getMonth()+1)).slice(-2) + "." +
                        d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
                    // if (list[i].mainComment == null) {
                    if (user_id === list[i].user.id + '') {
                        removeComment = '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteComment(' + list[i].id + ')"></span>'
                        editComment = '<span class="edit-comment glyphicon glyphicon-pencil comment-functional"></span>'

                    } else {
                        removeComment = '';
                        editComment = '';

                    }
                    html +=
                        '<li class="list-group-item comment-item">' +
                        '<div id="comment' + list[i].id + '" class="comment">' +
                        '<span class="comment-name">' + list[i].user.lastName + ' ' + list[i].user.firstName + '</span>' +
                        removeComment +
                        editComment +
                        '<span class="hide-show glyphicon glyphicon-comment comment-functional"></span>' +
                        '<span  class="comment-functional">'+ dateFormat +'</span>' +
                        '<p class="comment-text" ">' + list[i].content + '</p>' +
                        '<div class="form-answer">' +
                        '<div class="form-group">' +
                        '<textarea class="form-control textcomplete" id="new-answer-for-comment' + list[i].id + '" placeholder="Напишите ответ"></textarea>' +
                        '<button class="btn btn-md btn-success comment-button" onclick="sendAnswer(' + list[i].id + ', \'test_message\')"> Сохранить </button>' +
                        '</div>' +
                        '</div>' +
                        '<div class="form-edit">' +
                        '<div class="form-group">' +
                        '<textarea class="form-control edit-textarea textcomplete"' +
                        ' id="edit-comment' + list[i].id + '" placeholder="Редактор"></textarea>' +
                        '<button class="btn btn-md btn-success comment-button" onclick="editComment(' + list[i].id + ')"> Отредактировать </button>' +
                        '</div>' +
                        '</div>' +
                        '<ul class="answer-list comment-item" id="comment-' + list[i].id + 'answers">';
                    let answers = list[i].commentAnswers;
                    for (let i = 0; i < answers.length; i++) {
                        let d = new Date(answers[i].dateFormat);
                        let dateFormat = ("0" + d.getDate()).slice(-2) + "." + ("0"+(d.getMonth()+1)).slice(-2) + "." +
                            d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
                        if (user_id === answers[i].user.id + '') {
                            removeAnswer = '<span class="glyphicon glyphicon-remove comment-functional" onclick="deleteCommentAnswer(' + answers[i].id + ')"></span>'
                            editAnswer = '<span class="edit-answer glyphicon glyphicon-pencil comment-functional"></span>'
                        } else {
                            removeAnswer = '';
                            editAnswer = '';
                        }
                        html +=
                            '<li>\n' +
                            //comment
                            '<div id="answer' + answers[i].id + '" class="answer">\n' +
                            //comment-name
                            '<span class="comment-name">' + answers[i].user.lastName + ' ' + answers[i].user.firstName + '</span>' +
                            removeAnswer +
                            editAnswer +
                            '<span  class="comment-functional">'+ dateFormat +'</span>' +
                            //comment-text
                            '<p class="comment-text" ">' + answers[i].content + '</p>' +
                            '<div class="form-edit">' +
                            '<div class="form-group">' +
                            '<textarea class="form-control edit-textarea textcomplete" ' +
                            //edit-comment
                            ' id="edit-answer' + answers[i].id + '" placeholder="Редактор"></textarea>' +
                            '<button class="btn btn-md btn-success comment-button" onclick="editCommentAnswer(' + answers[i].id + ')"> Отредактировать </button>' +
                            '                                </div>\n' +
                            '                            </div>\n' +
                            '                        </div>\n' +
                            '                    </li>\n';
                    }

                    html += '</ul>' +
                        '</div>' +
                        '</li>';

                }
                ulComments.append(html);

                /* Client history first loading */
                $.ajax({
                    method: 'GET',
                    // url: '/rest/client/history/getHistory/' + client.id,
                    url: '/client/history/rest/getHistory/' + client.id,
                    data: {
                        page: 0,
                        isAsc: false
                    },
                    success: function (history) {
                        let history_table = $('#client-' + client.id + 'history').find("tbody");
                        let current = $(document.getElementsByClassName("upload-history"));
                        let upload_more_btn = current.parents("div.panel.panel-default").find(".upload-more-history");
                        if (history.length < 10) {
                            upload_more_btn.hide();
                        } else {
                            upload_more_btn.show();
                        }
                        //reset arrow
                        $('#date').find('i').removeClass('fa-sort-up').addClass('fa-sort-down');
                        //draw client history
                        drawClientHistory(history, history_table);
                    }
                });

            },
            error: function (error) {
                console.log(error);
            }
        });
    });
});

$('#slackLinkModal').on('hidden.bs.modal', function () {
    $('#main-modal-window').css('overflow-y', 'auto');
});

$('#slackLinkModal').on('show.bs.modal', function () {
    let field = $('#slack-invite-link-text');
    let clientId = $(this).data('clientId');
    field.val('');
    $.ajax({
        async: true,
        type: 'GET',
        url: '/rest/client/slack-invite-link/' + clientId,
        success: function (response) {
            field.val(response);
            navigator.clipboard.writeText(response);
        }
    });
});

function changeStatus(clientId, statusId) {
    $.ajax({
        async: false,
        type: 'POST',
        url: '/rest/status/client/change',
        data: {
            "statusId" : statusId,
            "clientId" : clientId
        },
        success: function () {
            reloadClientStatus(clientId);
        },
        error: function () {
            alert('Не задан статус по-умолчанию для нового студента!');
        }
    });
}

function reloadClientStatus(clientId) {
    $.ajax({
        async: false,
        type: 'GET',
        url: '/rest/client/' + clientId,
        data: {"clientId": clientId},
        success: function (client) {
            $('#client-set-status-button').text(client.status.name);
        }
    });
}

function client_has_telegram(client) {
    let has_telegram = false;
    for (let i = 0; i < client.socialProfiles.length; i++) {
        if (client.socialProfiles[i].socialNetworkType.name === 'telegram') {
            has_telegram = true;
            break;
        }
    }
    return has_telegram;
}

let interlocutor_profiles;

function get_interlocutors(clientId) {
    $.ajax({
        type: "GET",
        url: "/rest/conversation/interlocutors",
        data: {id: clientId},
        success: function (response) {
            interlocutor_profiles = response;
        }
    })
}

$(document).on('click','.confirm-skype-btn', function (e) {
    skypeCallDateOld = $('input[name="skypePostponeDateOld"]').data('daterangepicker').startDate._d;
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var currentStatus = $('.skype-notification');
    var editDate = $('#assign-skype' + clientId);

    let addCallSkype = {
        startDate: Date.UTC(skypeCallDateOld.getFullYear(), skypeCallDateOld.getMonth(), skypeCallDateOld.getDate(), skypeCallDateOld.getHours(), skypeCallDateOld.getMinutes(), 0, 0),
        clientId: clientId
    };

    this.setAttribute("disabled", "true");
    $.ajax({
        type: 'POST',
        url: '/rest/skype/addSkypeCallAndNotification',
        data: addCallSkype,
        success: function () {
            $('.assign-skype-call-btn').hide();
            $('#freeDate, .skype-panel, .skype-notification, .enter-mentor-list, .confirm-skype-btn').remove();
            editDate.after(
                '<div class="remove-tag confirm-skype-interceptor">' +
                '<div class="update btn-group">' +
                '<button id="assign-skype' + clientId + '" type="button" onclick="updateCallDate(' + clientId + ')" class="btn btn-default update-date-btn btn-sm"><span class="glyphicon glyphicon-pencil"></span> Изменить время беседы</button>' +
                '<button type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <span class="glyphicon glyphicon-remove"></span></button>'  +
                '<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="deleteDate">\n' +
                '<li><a onclick="deleteCallDate(' + clientId + ')" href="#">Удалить беседу</a></li>\n' +
                '<li><a href="#">Отмена</a></li>\n' +
                '</ul>' +
                '</div>' +
                '<div class="skype-notification" style="color:#229922">Время беседы назначено.</div>' +
                '</div>');
            this.setAttribute("disabled", "false");
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#d01717');
            currentStatus.text(error.responseText);
        }
    });
});

function updateCallDate(id) {
    var clientId = id;
    var btnBlockTask = $('div.confirm-skype-interceptor .assign-skype-call-btn');
    var currentBtn = $(document).find('.update', '.btn-group');
    var currentStatus = $('.skype-notification');
    var formData = {clientId: clientId};
    $(document).find('.update-date-btn').attr("disabled", "true");
    $.ajax({
        type: 'GET',
        url: '/rest/skype/' + clientId,
        data: formData,
        dataType: 'json',
        success: function (assignSkypeCall) {
            var oldDate = new Date(new Date(assignSkypeCall.skypeCallDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }));
            var date = new Date();
            var minutes =  Math.ceil((date.getMinutes() +1)/10)*10;
            var startDate = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), minutes , 0, 0);
            btnBlockTask.attr('id', 'assign-skype' + clientId);
            currentStatus.after('<button class="btn btn-info btn-sm update-skype-call">Подтвердить</button>');
            currentBtn.attr("disabled", "true");
            currentBtn.after(
                '<div class="panel-group skype-panel">' +
                '<div class="panel panel-default">' +
                '<div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                '<div class="panel-body">' + '<input readonly="false" type="text" class="form-control skype-postpone-date" name="skypePostponeDateNew" id="skypePostpone' + clientId +'"> </input>' +
                ' <form class="box-window"></form>' +'</div></div></div>');
            // drawCheckbox($(".add-box-window"), clientId);
            $('input[name="skypePostponeDateNew"]').daterangepicker({
                singleDatePicker: true,
                timePicker: true,
                timePickerIncrement: 10,
                timePicker24Hour: true,
                locale: {
                    format: 'DD.MM.YYYY HH:mm МСК'
                },
                minDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' })),
                startDate: oldDate
            });
            skypeCallDateOld = oldDate;
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#d01717');
            currentStatus.text(error);
        }
    });
};

$(document).on('click','.update-skype-call', function (e) {
    skypeCallDateNew = $('input[name="skypePostponeDateNew"]').data('daterangepicker').startDate._d;
    var skypeBtn2 = $('.update-skype-call, #mentor');
    var skypeBtn = $('.skype-postpone-date');
    var clientId = $(this).parents('#main-modal-window').data('clientId');
    var currentBtn = $(document).find('.update', '.btn-group');
    var currentStatus = $('.skype-notification');

    let updateEvent = {
        clientId: clientId,
        skypeCallDateNew: Date.UTC(skypeCallDateNew.getFullYear(), skypeCallDateNew.getMonth(), skypeCallDateNew.getDate(), skypeCallDateNew.getHours(), skypeCallDateNew.getMinutes() , 0, 0),
        skypeCallDateOld: Date.UTC(skypeCallDateOld.getFullYear(), skypeCallDateOld.getMonth(), skypeCallDateOld.getDate(), skypeCallDateOld.getHours(), skypeCallDateOld.getMinutes() , 0, 0)
    };
    $.ajax({
        type: 'POST',
        url: '/rest/mentor/updateEvent',
        data: updateEvent,
        success: function (e) {
            if (!document.getElementById('freeDate')) {
                currentBtn.after('<div id="freeDate"><span style="color:#229922">Новая дата назначена.</span></div>');
                $(document).find('.update-date-btn').removeAttr("disabled");
                skypeBtn2.remove();
                skypeBtn.hide();
                $('.skype-notification').hide();
                $('.skype-panel').remove();
                skypeCallDateOld = skypeCallDateNew;
            }
        },

        error: function (error) {
            console.log(error);
            currentStatus.css('color','#d01717');
            currentStatus.text(error.responseText);
        }
    });
});

function deleteCallDate(id) {
    var clientId = id;
    var skypeBtn2 = $('.confirm-skype-btn, #mentor');
    var skypeBtn = $('.skype-postpone-date');
    var formDataId = {clientId: clientId};
    var currentBtn = $(document).find('.update .btn-group');
    var currentStatus = $('.skype-notification');
    var btnBlockShow = $('div#assign-unassign-btns-skype');

    $.ajax({
        type: 'GET',
        url: '/rest/skype/' + clientId,
        data: formDataId,
        dataType: 'json',
        success: function (assignSkypeCall) {
            // Delete date
            var date = new Date(new Date(assignSkypeCall.skypeCallDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }));
            var startDateOld = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes() , 0, 0);

            let deleteEvent = {
                clientId: clientId,
                skypeCallDateOld: startDateOld
            };

            $.ajax({
                type: 'POST',
                url: '/rest/mentor/deleteEvent',
                data: deleteEvent,
                success: function() {
                    currentBtn.removeAttr("disabled");
                    skypeBtn2.remove();
                    skypeBtn.hide();
                    $('.skype-panel').remove();
                    $('.skype-text').remove();
                    $('.enter-mentor-list').remove();
                    $('.update.btn-group').hide();
                    $('.skype-notification').show();

                    if (btnBlockShow.length > 0)
                    {
                        $('.confirm-skype-interceptor').remove();
                        btnBlockShow.after('<div class="remove-tag confirm-skype-interceptor"><button id="assign-skype' + clientId + '" onclick="assignSkype(' + clientId + ')" class="btn btn-primary center-block assign-skype-call-btn btn-sm">Назначить первый созвон</button>\n' +
                            '<div class="skype-notification"></div>' +
                            '</div>')
                    } else {
                        $('.assign-skype-call-btn').show().removeAttr("disabled");
                    }
                },
                error: function (error) {
                    console.log(error);
                    currentStatus.css('color','#229922');
                    currentStatus.text(error);
                }
            });
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#229922');
            currentStatus.text(error);
        }
    });
};

var idMentor;
var skypeCallDateNew;
var skypeCallDateOld;

function assignSkype(id) {
    var clientId = id;
    var btnBlockTask = $('div.confirm-skype-interceptor .assign-skype-call-btn');
    var currentBtn = $(document).find('.assign-skype-call-btn');
    currentBtn.attr("disabled", "true");
    var currentStatus = $('.skype-notification');
    var formData = {clientId: clientId};
    var nowDate = new Date();
    var minutes =  Math.ceil((nowDate.getMinutes() +1 )/10)*10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes , 0, 0);
    var startDate = minDate;
    $.ajax({
        type: 'GET',
        url: '/rest/client/' + clientId,
        data: formData,
        success: function (client) {
            btnBlockTask.attr('id', 'assign-skype' + clientId);
            var clientSkype = client.skype;
            // if(clientSkype === null || 0 === clientSkype.length) {
            //     currentStatus.css('color', '#333');
            //     currentStatus.text("Введите Skype пользователя");
            //     currentStatus.after('<input class="enter-skype-login form-control"> </input>');
            //     $('.enter-skype-login').after('<br/>' + '<button onclick="confirmSkype(' + id + ')" type="button" class="btn btn-primary btn-sm confirm-skype-login">Подтвердить</button>');
            // } else {
                currentStatus.empty();
                currentStatus.after('<button class="btn btn-info btn-sm confirm-skype-btn">Подтвердить</button>');
                currentBtn.attr("disabled", "true");
                currentBtn.after(
                    '<div class="panel-group skype-panel"><div class="panel panel-default"><div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                    '<div class="panel-body">' + '<input readonly="false" type="text" class="form-control skype-postpone-date" name="skypePostponeDateOld" id="skypePostpone' + client.id +'"> </input>' +
                    ' <form class="box-window"></form>' +'</div></div>');
                $(drawCheckbox($(".add-box-window"), clientId));
                // drawCheckbox($(".add-box-window"), clientId);
                $('input[name="skypePostponeDateOld"]').daterangepicker({
                    singleDatePicker: true,
                    timePicker: true,
                    timePickerIncrement: 10,
                    timePicker24Hour: true,
                    locale: {
                        format: 'DD.MM.YYYY HH:mm МСК'
                    },
                    minDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' })),
                    startDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }))
                });
            // }
        },
        error: function (error) {
            console.log(error);
            currentStatus.css('color','#229922');
            currentStatus.text(error);
        }
    });
};

function confirmSkype(id) {
    var currentBtn = $(document).find('.assign-skype-call-btn');
    var clientId = id;
    var currentStatus = $('.skype-notification');
    var skypeLogin = $('.enter-skype-login').val();
    var formData = {clientId: clientId, skypeLogin: skypeLogin};
    var nowDate = new Date();
    var minutes =  Math.ceil((nowDate.getMinutes() +1)/10)*10;
    var startDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes , 0, 0);
    $.ajax({
        type: 'POST',
        url: '/rest/client/setSkypeLogin',
        data: formData,
        success: function (client) {
            $('#client-skype').text(skypeLogin);
            currentStatus.css('color', '#229922');
            currentStatus.text("Логин Skype успешно добавлен");
            $('.confirm-skype-login').remove();
            $('.enter-skype-login').remove();
            currentStatus.after('<button class="btn btn-info btn-sm confirm-skype-btn">Подтвердить</button>');
            currentBtn.attr("disabled", "true");
            currentBtn.after(
                '<div class="panel-group skype-panel"><div class="panel panel-default"><div class="panel-heading skype-panel-head">Укажите дату и время созвона</div>' +
                '<div class="panel-body">' + '<input readonly="false" type="text" class="form-control skype-postpone-date" name="skypePostponeDateOld" id="skypePostpone' + clientId +'"> </input>' +
                ' <form class="box-window"></form>' +'</div></div>');
            $(drawCheckbox($(".add-box-window"), clientId));
            $('input[name="skypePostponeDateOld"]').daterangepicker({
                singleDatePicker: true,
                timePicker: true,
                timePickerIncrement: 10,
                timePicker24Hour: true,
                locale: {
                    format: 'DD.MM.YYYY HH:mm МСК'
                },
                minDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' })),
                startDate: new Date(new Date(startDate).toLocaleString('en-US', { timeZone: 'Europe/Moscow' }))
            });
        },
        error: function () {
            currentStatus.css('color','#229922');
            currentStatus.text("Клиент с таким логином уже существует");
        }
    });
};

$(function () {
    $('.portlet-body').on('click', function (e) {
        if (e.target.className.startsWith("portlet-body") === true ) {
            var clientId = $(this).parents('.common-modal').data('cardId');
            var currentModal = $('#main-modal-window');
            currentModal.data('clientId', clientId);
            currentModal.modal('show');
            markAsReadMenu($(e.target).attr('client-id'),0)
        }
    });
});


$(function () {
    $('.portlet-header').on('click', function (e) {
        var clientId = $(this).parents('.common-modal').data('cardId');
        var currentModal = $('#main-modal-window');
        currentModal.data('clientId', clientId);
        currentModal.modal('show');
        markAsReadMenu($(e.target).attr('client-id'));

    });
});

$(document).ready(function () {
    var nowDate = new Date();
    var minutes = Math.ceil((nowDate.getMinutes() + 1) / 10) * 10;
    var minDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), minutes, 0, 0);
    var startDate = moment(minDate)/*.utcOffset(180)*/;
    $('input[name="postponeDate"]').daterangepicker({
        singleDatePicker: true,
        timePicker: true,
        timePickerIncrement: 10,
        timePicker24Hour: true,
        locale: {
            format: 'DD.MM.YYYY HH:mm'
        },
        minDate: startDate,
        startDate: startDate
    });
});

function hideClient(clientId) {
    let url = '/rest/client/postpone';
    let flag = document.querySelector(".isPostponeFlag").checked;
    let commentUrl = '/rest/comment/add';
    let comment = document.querySelector(".postponeComment").value;
    let formData = {
        clientId: clientId,
        date: $('#postponeDate' + clientId).val(),
        isPostponeFlag: flag,
        postponeComment: comment,
    };
    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {
            $.ajax({
                type: 'POST',
                dataType: 'json',
                url: commentUrl,
                data: {
                    clientId: clientId,
                    content: comment
                },
                success: function () {
                    let currentStatus = document.getElementById("postpone-status");
                    currentStatus.style.color = "limegreen";
                    if (flag) {
                        currentStatus.textContent = "Клиент успешно скрыт";
                    } else {
                        currentStatus.textContent = "Напоминание добавлено";
                    }
                },
            });
        },
        error: function (e) {
            currentStatus = $("#postponeStatus" + clientId)[0];
            currentStatus.textContent = "Произошла ошибка";
            console.log(e.responseText)
        }
    });
}

$(function () {
    $('body').on('hide.bs.modal', '.custom-modal', function () {
        var currentForm = $(this).find('.send-custom-template');
        currentForm.empty();
        $("input[type=checkbox]").prop('checked', false);
        $(this).find('.send-all-custom-message').removeAttr("disabled");
    });
});

//Отрпавка сообщений с кастомным текстом во все выбранные социальные сети, email, SMS.
$(function () {
    $('body').on('click', '.send-all-custom-message', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var current = $(this);
        var currentStatus = $(this).prev('.send-custom-template');
        var formData = {
            clientId : clientId,
            templateId : templateId,
            body : $('#custom-eTemplate-body').val()
        };
        var url = [];
        var err = [];
        $('input[type="checkbox"]:checked').each(function (el) {
            var valuecheck = $(this).val();
            switch ($(this).val()) {
                case ('email'):
                    url = '/rest/sendEmail';
                    break;
                case ('vk'):
                    url = '/rest/vkontakte';
                    break;
                case ('sms'):
                    url = '/user/sms/send/now/client';
                    break;
                case ('slack'):
                    url = '/slack/send/client';
                    break;
                //TODO временный адрес заглушка пока нету facebook, чтобы не нарушать работу методаю
                case ('facebook'):
                    url = '/temporary blank';
                    break;
            }
            if (url.length > 0) {
                $.ajax({
                    type: "POST",
                    url: url,
                    data: formData,
                    beforeSend: function () {
                        current.text("Отправка..");
                        current.attr("disabled", "true")
                    },
                    success: function (result) {
                        if (err.length === 0) {
                            $(".modal").modal('hide');
                            $('#custom-eTemplate-body').val("");
                            current.text("Отправить");
                            current.removeAttr("disabled");
                        }
                    },
                    error: function (e) {
                        err.push(valuecheck);
                        current.text("Отправить");
                        currentStatus.text("Не удалось отправить сообщение " + err);
                        console.log(e);
                    }
                });
            }
        });
    });
});

//Установка идентификаторов в модальное окно отправки сообщений с кастомным текстом.
$(function () {
    $('body').on('click', '.portlet-custom-btn', function () {
        var portlet = $(this).closest('.common-modal');
        var clientId = portlet.data('cardId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customMessageTemplate');
        var btn = currentModal.find('.send-all-custom-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

$(function () {
    $('body').on('click', '.test-custom-btn', function () {
        var portlet = $(this).closest('#main-modal-window');
        var clientId = portlet.data('clientId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#customMessageTemplate');
        var btn = currentModal.find('.send-all-custom-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

$(function () {
    $('body').on('hidden.bs.modal', '.fix-modal', function () {
        $('#main-modal-window').css('overflow-y', 'auto');
        var currentForm = $(this).find('.send-fixed-template');
        currentForm.empty();
        $("input[type=checkbox]").prop('checked', false);
        $(this).find('.send-all-message').removeAttr("disabled");
        $(".soc-network-box").remove();
    });
});

//Отправка сообщений с фиксированнм текстом во все выбранные социальные сети, email, SMS.
$(function () {
    $('body').on('click', '.send-all-message', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var current = $(this);
        var currentStatus = $(this).prev('.send-fixed-template');
        var formData = {
            clientId : clientId,
            templateId : templateId

        };
        var url = [];
        var err = [];
        $('input[type="checkbox"]:checked').each(function (el) {
            var valuecheck = $(this).val();
            switch (valuecheck) {
                case ('email'):
                    url = '/rest/sendEmail';
                    break;
                case ('vk'):
                    url = '/rest/vkontakte';
                    break;
                case ('sms'):
                    url = '/user/sms/send/now/client';
                    break;
                case ('slack'):
                    url = '/slack/send/client';
                    break;
                //TODO временный адрес заглушка пока нету facebook, чтобы не нарушать работу метода
                case ('facebook'):
                    url = '/temporary blank';
                    break;

            }
            if (url.length > 0) {
                $.ajax({
                    type: "POST",
                    url: url,
                    data: formData,
                    beforeSend: function () {
                        current.text("Отправка..");
                        current.attr("disabled", "true")
                    },
                    success: function (result) {
                        if (err.length === 0) {
                            currentStatus.text("Отправлено!");
                            current.text("Отправить");
                            current.removeAttr("disabled");
                        }
                    },
                    error: function (e) {
                        err.push(valuecheck);
                        current.text("Отправить");
                        currentStatus.text("Не удалось отправить сообщение " + err);
                        current.attr("disabled", "true");
                        console.log(e)
                    }
                });
            }
        });
    });
});

//Установка идентификаторов в модальное окно отправки сообщений с фиксированным текстом.
$(function () {
    $('body').on('click', '.portlet-send-btn', function () {
        var clientId = $(this).closest('.common-modal').data('cardId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#sendTemplateModal');
        var btn = currentModal.find('.send-all-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

$(function () {
    $('body').on('click', '.test-fix-btn', function () {
        var portlet = $(this).closest('#main-modal-window');
        var clientId = portlet.data('clientId');
        var templateId = $(this).data('templateId');
        var currentModal = $('#sendTemplateModal');
        var btn = currentModal.find('.send-all-message');
        btn.data('clientId', clientId);
        btn.data('templateId', templateId);
    });
});

$(function () {
    $('#customEmailMessageTemplate').on('hidden.bs.modal', function () {
        $('#main-modal-window').css('overflow-y', 'auto');
        var currentStatus = $(this).find('.send-email-err-status');
        currentStatus.empty();
    });
});

$(function () {
    $('#customVKMessageTemplate').on('hidden.bs.modal', function () {
        $('#main-modal-window').css('overflow-y', 'auto');
        var currentStatus = $(this).find('.send-custom-vk-status');
        currentStatus.empty();
    });
});

// Отправка кастомного сообщения в вк
$(function () {
    $('.send-vk-btn').on('click', function (event) {
        var clientId = $(this).data('clientId');
        var templateId = $(this).data('templateId');
        var currentStatus = $(this).prev('.send-custom-vk-status');
        let url = '/rest/vkontakte';
        let formData = {
            clientId: clientId,
            templateId: templateId,
            body: $('#custom-VKTemplate-body').val()
        };
        $.ajax({
            type: "POST",
            url: url,
            data: formData,

            success: function (result) {
                $(".modal").modal('hide');
                currentStatus.css('color', 'limegreen');
                currentStatus.text("Отправлено");
            },
            error: function (e) {
                currentStatus.css('color', 'red');
                currentStatus.text("Ошибка");
                console.log(e)
            }
        });
    });
});

//добавляем упоминания юзеров в полях комментариев карточки клиента
document.querySelector('.modal-comments').onclick = (e) => {
    const target = e.target;
    const area = target.getAttribute('id');
    if (area != null) {
        if (area.indexOf("new-text-for-client") === 0 || area.indexOf("new-answer-for-comment") === 0) {
            mentionUser();
        }
    }
};
//функция упоминания юзера
function mentionUser() {
    var url = '/rest/user';
    var userNames = [];
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (res) {
            for (var i = 0; i < res.length; i++) {
                userNames[i] = res[i].firstName + res[i].lastName;
            }
        },
        error: function (error) {
            console.log(error);
        }
    });

    $('#main-modal-window .textcomplete').textcomplete([
        {
            replace: function (mention) {
                return '@' + mention + ' ';
            },
            mentions: userNames,
            match: /\B@(\w*)$/,
            search: function (term, callback) {
                callback($.map(this.mentions, function (mention) {
                    $('.textcomplete-dropdown').css('z-index', '999999');
                    return mention.indexOf(term) === 0 ? mention : null;
                }));
            },
            index: 1
        }])
}

function unassign(id) {
    let
        url = '/rest/client/unassign',
        formData = {
            clientId: id
        },
        unassignBtn = $('#unassign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            let info_client = $('#info-client' + id);
            info_client.find("p[style*='display:none']").remove();
            info_client.find(".user-icon_card").remove();
            if (unassignBtn.length !== 0) {
                unassignBtn.before(
                    "<button " +
                    "   id='assign-client" + id + "' " +
                    "   onclick='assign(" + id + ")' " +
                    "   class='btn btn-sm btn-info remove-tag'>Взять себе карточку</button>"
                );
                unassignBtn.remove();
            }
            fillFilterList();
        },
        error: function (error) {
        }
    });
}

function unassignMentor(id) {
    let
        url = '/rest/client/unassignMentor',
        formData = {
            clientId: id
        },
        unassignBtn = $('#unassign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            let info_client = $('#info-client' + id);
            info_client.find("span[style*='display:none']").remove();
            info_client.find(".mentor-icon_card").remove();
            info_client.find(".ownerMentorId").remove();
            if (unassignBtn.length !== 0) {
                unassignBtn.before(
                    "<button " +
                    "   id='assign-client" + id + "' " +
                    "   onclick='assignMentor(" + id + ")' " +
                    "   class='btn btn-sm btn-info remove-tag'>Взять себе карточку</button>"
                );
                unassignBtn.remove();
            }

            fillFilterList();
        },
        error: function (error) {
        }
    });
}

function assign(id) {
    let
        url = '/rest/client/assign',
        formData = {
            clientId: id
        },
        assignBtn = $('#assign-client' + id);

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (owner) {
            assignBtn.before(
                "<button " +
                "   id='unassign-client" + id + "' " +
                "   onclick='unassign(" + id + ")' " +
                "   class='btn btn-sm btn-warning remove-tag'>Отказаться от карточки</button>"
            );
            assignBtn.remove();
            $('#info-client' + id).append(
                "<span class='user-icon_card' id='own-" + id + "' value=" + owner.firstName + "&nbsp" + owner.lastName + ">" +
                owner.firstName.substring(0, 1) + owner.lastName.substring(0, 1) +
                "</span>" +
                "<span style='display:none'>" + owner.firstName + " " + owner.lastName + "</span>"
            );
            fillFilterList()
        },
        error: function (error) {
        }
    });
}

//Заполняем список фильтрации в Меню на Доске
function fillFilterList() {
    $("#client_filter").empty();
    var names = $("#status-columns").find($(".user-icon_card"));
    if (names.length === 0) {
        $("#client_filter_group").hide();
    } else {
        $("#client_filter_group").show();
    }
    var uniqueNames = [];
    var temp = [];
    for (var i = 0; i < names.length; ++i) {
        if (~temp.indexOf(names[i].innerText)) {
            names.slice(temp.indexOf(names[i].innerText));
        } else {
            temp.push(names[i].innerText);
            uniqueNames.push(names[i]);
        }
    }
    $.each(uniqueNames, function (i, el) {
        $("#client_filter").append("<input class='check'  type=\"checkbox\" id = checkbox_" + el.innerText + " value=" + el.innerText + " ><label for=checkbox_" + el.innerText + ">" + el.getAttribute("value") + "</label></br>");
    });
}

function getHash() {
    let urlParams = window.location.href.split("?");
    if (urlParams.length > 1) {
        return urlParams[1];
    }
    return null;
}

function inviteSlack(clientEmail) {
    let SUCCESS_MESSAGE = 'Успешно! На почту клиента придет письмо с подтверджением регистрации. Перейдите по ссылке, чтобы задать имя и пароль и получить доступ к Slack.';
    let ERROR_MESSAGE = "";
    let url = '/slack/invitelink';
    let email = clientEmail;
    if  (email === "undefined"){
        ERROR_MESSAGE = "Email отсутствует";
    }
    let message = $('#message');
    $.ajax({
        url: url,
        async: true,
        type: 'POST',
        data: {
            'hash': getHash(),
            'name' : '',
            'lastName' : '',
            'email': email},
        success: function () {
            message.text(SUCCESS_MESSAGE);
        },
        error: function (data) {
            if (ERROR_MESSAGE === ""){
                message.text(data.responseText);
            } else {
                message.text(ERROR_MESSAGE);
            }
        }
    });
}

//Получаем текущего пользователя
function getUserLoggedIn(asyncr) {
    $.ajax({
        async: asyncr,
        type: "GET",
        url: "/rest/client/getPrincipal",
        success: function(user) {
            userLoggedIn = user;
        },
        error: function(error) {
            console.log(error);
        }
    });
}

let logged_in_profiles;

function get_us() {
    $.ajax({
        async: true,
        type: "GET",
        url: "/rest/conversation/us",
        success: function (response) {
            logged_in_profiles = response;
        }
    });
}

// Выбрать , отключить все чекбоксы в меню отправки сообщений в email.SMS, VK,FB.
$('body').on('click', '.select_all', function () {
    var currentForm = $(this).parents('.box-modal');
    currentForm.find('.my-checkbox-soc').prop('checked', true);
});

$('.confirm-skype-interceptor').on('click', '.select_all_skype_boxes', function (e) {
    var currentForm = $(this).parents('.box-window');
    currentForm.find('.my-checkbox-soc').prop('checked', true);
});


$('body').on('click', '.deselect_all', function () {
    var currentForm = $(this).parents('.box-modal');
    currentForm.find('.my-checkbox-soc').prop('checked', false);
});


$('body').on('show.bs.modal', '.fix-modal', function () {
    var currentForm = $(this).find('.box-modal');
    var clientId = $(this).find('.send-all-message').data('clientId');
    drawCheckbox(currentForm, clientId);
});

$('body').on('show.bs.modal', '.custom-modal', function () {
    var currentForm = $(this).find('.box-modal');
    var clientId = $(this).find('.send-all-custom-message').data('clientId');
    drawCheckbox(currentForm, clientId);
});

// Отрисовка чекбоксов социальных сетей
function drawCheckbox(currentForm, clientId) {
    let formData = {clientId: clientId};
    $.ajax({
        type: 'GET',
        url: '/rest/client/' + clientId,
        data: formData,
        beforeSend: function () {
            if (currentForm.find('.my-checkbox-soc').is('.my-checkbox-soc')) {
                return false;
            }
        },
        success: function (data) {
            var soc = data.socialProfiles;
            var email = data.email;
            var phoneNumber = data.phoneNumber;

            for (let i = 0; i < soc.length; i++) {
                currentForm.prepend("<label class='checkbox-inline soc-network-box'>" +
                    "<input type='checkbox'  value=" + soc[i].socialNetworkType.name + "  class='my-checkbox-soc' />" + soc[i].socialNetworkType.name +
                    "</label>");
            }
            if (email !== null) {
                currentForm.prepend("<label class='checkbox-inline soc-network-box'>" +
                    "<input type='checkbox'  value=" + 'email' + "  class='my-checkbox-soc' />" + 'e-mail' +
                    "</label>");
            }
            if (phoneNumber !== null) {
                currentForm.prepend("<label class='checkbox-inline soc-network-box'>" +
                    "<input type='checkbox'  value=" + 'sms' + "  class='my-checkbox-soc' />" + 'sms' +
                    "</label>");
            }
        }
    });
}