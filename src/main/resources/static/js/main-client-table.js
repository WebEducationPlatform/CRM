var invisibleStatuses, emailTmpl, verifiedUsers, newUsers, mentors, usersWithoutMentors, srchStr;

//Search clients in main
function clientsSearch() {
    $("#search-clients").keyup(function () {
        let jo = $(".portlet");
        let jo2 = jo.find($(".search_text"));
        let data = this.value.toLowerCase().split(" ");
        this.value.localeCompare("") === 0 ? jo.show() : jo.hide();

        for (let i = 0; i < jo2.length; i++) {
            let count = 0;
            for (let z = 0; z < data.length; z++) {
                if (jo2[i].innerText.toLowerCase().includes(data[z])) {
                    count++;
                }
            }
            if (count === data.length) {
                jo[i].style.display = 'block';
            }
        }
    });
}

function statusesSearch() {
    $("#search-statuses").keyup(function () {
        _this = this;
        $.each($("#table-hidden-statuses tbody tr"), function () {
            if ($(this).text().toLowerCase().replace('показать', "").indexOf($(_this).val().toLowerCase()) === -1) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    });
}

//Заготовка главной функции для отрисовки Доски на клиенте
$(document).ready(function renderMainClientTable () {

});

//Получаем список всех скрытых статусов
function getInvisibleStatuses() {
    let url = "/rest/status/all/invisible";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            invisibleStatuses = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Получаем список всех шаблонов рассылки
function getMessageTemplates() {
    let url = "/rest/message-template";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            emailTmpl = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Получаем список всех верифицированных пользователей
function getVerifiedUsers() {
    let url = "/rest/user/isverified";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            verifiedUsers = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Получаем список всех неверифицированных (новых) пользователей
function getUnverifiedUsers() {
    let url = "/rest/user/unverified";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            newUsers = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Получаем список всех менторов
function getAllMentor() {
    let url = "/rest/user/mentors";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            mentors = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Получаем список всех пользователей без менторов
function getAllWithoutMentors() {
    let url = "/rest/user/usersWithoutMentors";
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: function (response) {
            usersWithoutMentors = response;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Получаем направление вывода статусов на Доске и переключаем вид
function getRowStatusDirection() {
    let url = "/rest/user/isRowStatusDirection";
    $.ajax({
        type: 'GET',
        url: url,
        async: true,
        success: function (direction) {
            statusViewSwitch(direction);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

//Запоминаем направление вывода статусов на Доске
function setRowStatusDirection(direction) {
    let url = "/rest/user/isRowStatusDirection";
    $.ajax({
        type: 'POST',
        url: url,
        data: {direction : direction},
        async: true,
        error: function (error) {
            console.log(error);
        }
    });
}

//Заполняем таблицу всех скрытых статусов
function drawHiddenStatusesTable() {
    getInvisibleStatuses();
    let element = $('#tr-hidden-statuses');
    let trHTML = '';
    //Очистка содержимого таблицы после ключевого элемента
    element.nextAll().remove();
    if (invisibleStatuses.length !=0) {
        for (let i = 0; i < invisibleStatuses.length; i++) {
            if (invisibleStatuses[i].name != 'deleted') {
                trHTML += "<tr id = 'invisibleStatuses" + invisibleStatuses[i].id + "'><td width='70%'>" + invisibleStatuses[i].name + "</td>" +
                    "<td>" +
                        "<button type='button' class='show-status-btn btn' " +
                            "value='" + invisibleStatuses[i].id + "' onclick=\"showStatus(this.value)\">Показать</button>" +
                    "</td></tr>";
            }
        }
        element.after(trHTML);
    } else {
        element.after("<p>Пусто</p>");
    }
}

//Заполняем список всех шаблонов рассылки
function drawMessageTemplateList(clientId) {
    getMessageTemplates();
    let element = $("#messageTemplateList" + clientId);
    let liHTML = '';
    if (emailTmpl.length != 0) {
        for (let i = 0; i < emailTmpl.length; i++) {
            liHTML += "<li id='eTemplate" + emailTmpl[i].id + "'>";
            if (!emailTmpl[i].templateText.includes("%bodyText%")) {
                liHTML += "<a class='glyphicon glyphicon-ok portlet-send-btn' " +
                    "id='eTemplateBtn" + emailTmpl[i].id + "' data-toggle='modal' " +
                    "data-target='#sendTemplateModal' " +
                    "data-template-id='" + emailTmpl[i].id + "'>" + " " + emailTmpl[i].name + "</a>";
            } else {
                liHTML += "<a class='glyphicon glyphicon-plus portlet-custom-btn' " +
                    "id='eTemplateBtn" + emailTmpl[i].id + "' data-toggle='modal' " +
                    "data-target='#customMessageTemplate' " +
                    "data-template-id='" + emailTmpl[i].id + "'>" + " " + emailTmpl[i].name + "</a>";
            }
            liHTML += "</li>";
        }
        element.empty();
        element.append(liHTML);
    } else {
        element.empty();
        element.append("<li>Список шаблонов рассылки - пуст!</li>");
    }
}

//Заполняем таблицу всех верифицированных пользователей
function drawVerifiedUsersTable() {
    getVerifiedUsers();
    let element = $('#tr-verified-users');
    let trHTML = '';
    let grHTML = '';
    //Очистка содержимого таблицы после ключевого элемента
    element.nextAll().remove();
    //Ячейки с заголовками для групп сотрудников
    grHTML += '<tr id="admin"><th colspan="2" style="text-align:center">Админ</th></tr>' +
        '<tr id="mento"><th colspan="2" style="text-align:center">Ментор</th></tr>' +
        '<tr id="hr"><th colspan="2" style="text-align:center">Координатор</th></tr>';

    element.after(grHTML);

    let admGroup = $("#admin");
    let hrGroup = $("#hr");
    let mentorGroup = $("#mento");

    if (verifiedUsers.length !=0) {
        //Заполняем ячейки для каждлго сотрудника
        for (let i = 0; i < verifiedUsers.length; i++) {
            if (verifiedUsers[i].enabled) {
                trHTML += "<tr><td>" + verifiedUsers[i].firstName +
                    " " + verifiedUsers[i].lastName + "</td>";
            } else {
                trHTML += "<tr><td class='unEnabledUser'>" + verifiedUsers[i].firstName +
                    " " + verifiedUsers[i].lastName + "</td>";
            }
            trHTML += "<td class='editUserButtons'>" +
                "<a href='/admin/user/" + verifiedUsers[i].id + "'>" +
                "<button type='button' class='glyphicon glyphicon glyphicon-edit'></button>" +
                "</a>";
            if (userLoggedIn.authorities.some(arrayEl => arrayEl.authority = 'OWNER')) {
                if (verifiedUsers[i].id != userLoggedIn.id) {
                    trHTML += "<button type='button' data-toggle='modal' class='glyphicon glyphicon-remove'" +
                        "data-target='#reAvailableUserModal" + verifiedUsers[i].id + "'></button>";
                    trHTML += "<button type='button' data-toggle='modal' class='glyphicon glyphicon-trash'" +
                        "data-id='" + verifiedUsers[i].id + "' onclick='fillUsersTableForDelete(this)'></button>";
                }
            }

            trHTML += "</td></tr>";
            //Привязывая сотрудника к группе
            for (let j = 0; j < verifiedUsers[i].role.length; j++) {
                if (verifiedUsers[i].role[j].roleName === "ADMIN") {
                    admGroup.after(trHTML);
                    trHTML = '';
                    break;
                }
                if (verifiedUsers[i].role[j].roleName === "HR") {
                    hrGroup.after(trHTML);
                    trHTML = '';
                    break;
                }
                if (verifiedUsers[i].role[j].roleName === "MENTOR") {
                    mentorGroup.after(trHTML);
                    trHTML = '';
                    break;
                }
            }
        }

    } else {
        element.after("<p>Пусто</p>");
    }
}


//Фильтр. Отображать только сотрудников, соответствующих результатам поиска (по имени)
function showUserMatchCondition() {
    drawVerifiedUsersTable();
    $('#searchInput').keyup(function(){
        srchStr = this.value.toLowerCase();
        if (srchStr!=="") {
            drawVerifiedUsersTable();
            $("#tbl-verified-users tr").each(function () {
                if ($(this).text().toLowerCase().startsWith(srchStr) | $(this).text() == 'Админ' | $(this).text() == 'Координатор' | $(this).text() == 'Ментор') {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        } else {
            $("#tbl-verified-users tr").each(function () {
                $(this).hide();
            });
        }
    });
}

//Заполняем таблицу новых (неверифицированных) пользователей
function drawNewUsersTable() {
    getUnverifiedUsers();
    let element = $('#tr-new-user');
    let trHTML = '';
    //Очистка содержимого таблицы после ключевого элемента
    element.nextAll().remove();
    if (newUsers.length !=0) {
        for (let i = 0; i < newUsers.length; i++) {
            trHTML += "<tr><td>" + newUsers[i].firstName + " " + newUsers[i].lastName + "</td>" +
                "<td class='editUserButtons'>" +
                "<a href='/admin/user/" +  newUsers[i].id + "'>" +
                "<button type='button' class='glyphicon glyphicon-ok'></button>" +
                "</a>" +
                "<button type='button' data-toggle='modal' class='glyphicon glyphicon-remove'" +
                "data-target='#deleteNewUserModal" + newUsers[i].id + "'</button>" +
                "</td></tr>";
        }
        element.after(trHTML);
    } else {
        element.after("<p>Пусто</p>");
    }
}

//Отрисовываем меню карточки клиента
function drawClientCardMenu(clientId) {
    getInvisibleStatuses();
    getAllMentor();
    getAllWithoutMentors();
    let element = $(".open-description-btn[data-id=" + clientId + "]");
    let liHTML = '';
    let divider = "<li class='divider'></li>";
    element.prevAll().remove();
    if (userLoggedIn.authorities.some(arrayEl => (arrayEl.authority === 'OWNER') || (arrayEl.authority === 'ADMIN') || (arrayEl.authority === 'HR'))) {
        //Список работников
        liHTML += "<li class='dropdown-header'>Назначить работника:</li>";
        if (usersWithoutMentors.length !=0) {
            for (let i = 0; i < usersWithoutMentors.length; i++) {
                liHTML += "<li><a onclick='assignUser(" + clientId + ", " + usersWithoutMentors[i].id + ", " + userLoggedIn.id + ")'>" +
                    usersWithoutMentors[i].fullName + "</a></li>";
            }
        } else {
            liHTML += "<li>Список работников - пуст!</li>";
        }
        liHTML += divider;

        //Список менторов
        liHTML += "<li class='dropdown-header'>Назначить ментора:</li>";
        if (mentors.length !=0) {
            for (let i = 0; i < mentors.length; i++) {
                liHTML += "<li><a onclick='assignMentor(" + clientId + ", " + mentors[i].id + ", " + userLoggedIn.id + ")'>" +
                    mentors[i].fullName + "</a></li>";
            }
        } else {
            liHTML += "<li>Список менторов - пуст!</li>";
        }
        liHTML += divider;

        //Список скрытых статусов
        liHTML += "<li class='dropdown-header'>Скрыть в статус:</li>";
        if (invisibleStatuses.length !=0) {
            for (let i = 0; i < invisibleStatuses.length; i++) {
                if (invisibleStatuses[i].name != 'deleted') {
                    liHTML += "<li><a class='invisible-client'" +
                        "onclick='invisibleClient(" +clientId + ", " + invisibleStatuses[i].id + ")'>" +
                        invisibleStatuses[i].name + "</a></li>";
                }
            }
        } else {
            liHTML += "<li>Нет скрытых статусов!</li>";
        }
        liHTML += divider;

        element.before(liHTML);
    }
}

$(document).ready(function() {
    $('#status-as-columns').on('click', function() {
        statusViewSwitch(false);
        setRowStatusDirection(false);
    });

    $('#status-as-rows').on('click', function() {
        statusViewSwitch(true);
        setRowStatusDirection(true);
    });

    getRowStatusDirection();
});

function statusViewSwitch(is_row_status_direction) {
    if (is_row_status_direction) {
        $(".status-columns").css("flex-direction", "row");
        $(".clients-cards").css({'padding-right' : 'inherit', 'flex-direction' : 'column'});
        $("#status-as-columns").css("background-color", "white");
        $("#status-as-rows").css("background-color", "red");
    } else {
        $(".status-columns").css("flex-direction", "column");
        $(".clients-cards").css({'padding-right' : '50px', 'flex-direction' : 'row'});
        $("#status-as-rows").css("background-color", "white");
        $("#status-as-columns").css("background-color", "red");
    }
}