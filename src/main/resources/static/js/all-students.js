$('.checkbox').click(function() {
    var table, rows, i, status;
    table = document.getElementById("students-table");
    rows = table.rows;
    for (i = 1; i < rows.length; i++) {
        status = rows[i].getElementsByTagName("TD")[0];
        if (this.id == status.innerHTML) {
            rows[i].style.display = this.checked ? '' : 'none';
        }
    }
});

function sort_table(n, type) {
    var table, rows, switching, i, x, y, x_val, y_val, temp_x, temp_y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("students-table");
    switching = true;
    dir = "asc";
    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            x = rows[i].getElementsByTagName("TD")[n];
            y = rows[i + 1].getElementsByTagName("TD")[n];
            if(type == "href") {
                x_val = x.innerText.toLowerCase();
                y_val = y.innerText.toLowerCase();
            } else if(type == "date") {
                temp_x = x.innerHTML.toLowerCase().split(".");
                temp_y = y.innerHTML.toLowerCase().split(".");
                x_val = new Date(temp_x[2], temp_x[1] - 1, temp_x[0]);
                y_val = new Date(temp_y[2], temp_y[1] - 1, temp_y[0]);
            } else {
                x_val = x.innerHTML.toLowerCase();
                y_val = y.innerHTML.toLowerCase();
            }
            if (dir == "asc") {
                if (x_val > y_val) {
                    shouldSwitch= true;
                    break;
                }
            } else if (dir == "desc") {
                if (x_val < y_val) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchcount ++;
        } else {
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

$('.button_edit').click(function () {
    var currentModal = $('#student-edit-modal');
    currentModal.data('student_id', this.value);
    currentModal.modal('show');
});

$('#update-student').click(function () {
   console.log("update");
});

//--------------------------------------------------------------------------------------

$(function () {
    $('#student-edit-modal').on('show.bs.modal', function () {
        var currentModal = $(this);
        var student_id = $(this).data('student_id');
        console.log(student_id);

//         let formData = {clientId: clientId};
//         $.ajax({
//             type: 'GET',
//             url: 'rest/client/' + clientId,
//             data: formData,
//             success: function (client) {
//                 $.get('rest/client/getPrincipal', function (user) {
//                     if (client.ownerUser != null) {
//                         var owenerName = client.ownerUser.firstName + ' ' + client.ownerUser.lastName;
//                     }
//                     var adminName = user.firstName + ' ' + user.lastName;
//                     $('#main-modal-window').data('userId', user.id);
//
//                     currentModal.find('.modal-title').text(client.name + ' ' + client.lastName);
//                     $('#client-email').text(client.email);
//                     $('#client-phone').text(client.phoneNumber);
//                     if (client.canCall && user.ipTelephony) {
//                         $('#client-phone').after('<td id="web-call-voximplant" class="remove-tag">' + '<button class="btn btn-default btn btn-light btn-xs call-to-client" onclick="webCallToClient(' + client.phoneNumber + ')">' + '<span class="glyphicon glyphicon-earphone call-icon">' + '</span>' + '</button>' + '</td>')
//                             .after('<td id="callback-call-voximplant" class="remove-tag">' + '<button class="btn btn-default btn btn-light btn-xs callback-call" onclick="callToClient(' + user.phoneNumber + ', ' + client.phoneNumber + ')">' + '<span class="glyphicon glyphicon-phone">' + '</span>' + '</button>' + '</td>');
//                     }
//
//                     if (client.age > 0) {
//                         $('#client-age').text(client.age);
//                     }
//                     $('#client-sex').text(client.sex);
//
//                     if (client.email == null) {
//                         $('#email-href').hide();
//                     } else {
//                         $('#email-href').show();
//                     }
//                     // здесь вставка ссылок в кнопки вк и фб
//
//
//                     $('#vk-href').hide();
//                     $('#fb-href').hide();
//
//                     for (var i = 0; i < client.socialNetworks.length; i++) {
//                         if (client.socialNetworks[i].socialNetworkType.name == 'vk') {
//                             $('#vk-href').attr('href', client.socialNetworks[i].link);
//                             $('#vk-href').show();
//                         }
//                         if (client.socialNetworks[i].socialNetworkType.name == 'facebook') {
//                             $('#fb-href').attr('href', client.socialNetworks[i].link);
//                             $('#fb-href').show();
//                         }
//                     }
//                     var btnBlock = $('div#assign-unassign-btns');
//                     if (client.ownerUser === null) {
//                         btnBlock.append('<button class="btn btn-sm btn-info remove-tag" id="assign-client' + client.id + '"onclick="assign(' + client.id + ')"> взять себе карточку </button>');
//                     }
//                     if (client.ownerUser !== null && owenerName === adminName) {
//                         btnBlock.prepend('<button class="btn btn-sm btn-warning remove-tag" id="unassign-client' + client.id + '" onclick="unassign(' + client.id + ')"> отказаться от карточки </button>');
//                     }
//                     btnBlock.prepend('<a href="/client/clientInfo/' + client.id + '">' +
//                         '<button class="btn btn-info btn-sm" id="client-info"  rel="clientInfo" "> расширенная информация </button>' + '</a');
//                 });
//
//                 $('.send-all-custom-message').attr('clientId', clientId);
//                 $('.send-all-message').attr('clientId', clientId);
//                 $('#hideClientCollapse').attr('id', 'hideClientCollapse' + client.id);
//                 $('#postponeDate').attr('id', 'postponeDate' + client.id);
//                 $('#postpone-accordion').append('<h4 class="panel-title remove-element">' + '<a href="#hideClientCollapse' + client.id + '" сlass="font-size" data-toggle="collapse" data-parent="#hideAccordion" > Скрыть карточку  </a>' + '</h4>');
//                 $('#postpone-div').append('<button class="btn btn-md btn-info remove-element" onclick="hideClient(' + client.id + ')"> OK </button>');
//                 $('.postponeStatus').attr('id', 'postponeStatus' + client.id);
//                 $('.textcomplete').attr('id', 'new-text-for-client' + client.id);
//                 $('.comment-div').append('<button class="btn btn-sm btn-success comment-button remove-element" id="assign-client' + client.id + '"  onclick="sendComment(' + client.id + ', \'test_message\')"> Сохранить </button>');
//                 $('.main-modal-comment').attr('id', 'client-' + client.id + 'comments');
//                 $('.upload-history').attr('data-id', client.id).attr('href', '#collapse' + client.id);
//                 $('.client-collapse').attr('id', 'collapse' + client.id);
//                 $('.history-line').attr('id', 'client-' + client.id + 'history');
//                 $('.upload-more-history').attr('data-clientid', client.id);
//             }
//         });
    });
});

// $(function () {
//     $('#main-modal-window').on('hidden.bs.modal', function () {
//         $('.assign-skype-call-btn').removeAttr("disabled");
//         $('div#assign-unassign-btns').empty();
//         $('.skype-notification').empty();
//         $('.confirm-skype-login').remove();
//         $('.enter-skype-login').remove();
//         $('.skype-panel').remove();
//         $('.skype-text').empty();
//         $('.remove-element').remove();
//         $('.hide-client-collapse').attr('id', 'hideClientCollapse');
//         $('.postpone-date').attr('id', 'postponeDate');
//         $('.textcomplete').removeAttr('id');
//         $('.main-modal-comment').removeAttr('id');
//         $('.remove-tag').remove();
//         $('.history-line').find("tbody").empty();
//         $('#sendEmailTemplateStatus').empty();
//         $('#sendSocialTemplateStatus').empty();
//         $('.client-collapse').collapse('hide');
//         $('.remove-history').remove();
//         $('.upload-more-history').removeAttr('data-clientid');
//         $('.upload-more-history').attr("data-page", 1);
//     });
// });

