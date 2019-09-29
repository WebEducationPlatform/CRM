// Fill table with radiobuttons and show modal window
function fillUsersTableForDelete(button) {
    deleted = $(button).data('id');
    var url = '/rest/users';
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (response) {
            console.log(response);
            var trHTML = '';
            for (var i = 0; i < response.length; i++) {
                if (deleted == response[i].id) continue;
                trHTML += '<tr><td>' + '<input type="radio" name="user" value="' + response[i].id + '">' +
                    " " + response[i].firstName +
                    " " + response[i].lastName + '</td></tr>';
            }
            $('#usersTable tbody').append(trHTML);
            $('#deleteUserModal').modal('show');
        },
        error: function (error) {
            console.log(error);
        }
    });

    // Delete user form listener
    $("#deleteUserForm").submit(function (event) {
        event.preventDefault();
        receiver = $("input[name='user']:checked").val();
        var url = '/rest/admin/user/deleteWithTransfer';
        var formData = {
            deleteId: deleted,
            receiverId: receiver
        };

        $.ajax({
            type: "POST",
            url: url,
            data: formData,
            success: function () {
                location.reload();
            },
            error: function (error) {
                console.log(error);
            }
        });
    });
}

// Reload page after modal is hidden for not to add new radiobuttons
$('#deleteUserModal').on('hide.bs.modal', function() {
    console.log("hide modal");
    $('#usersTable tbody tr').remove();
});

//Функция для удаления нового пользователя из меню на доске
function deleteNewUser(deleteId) {
    let url = '/rest/admin/user/delete';
    let data = {
        deleteId: deleteId
    };

    $.ajax({
        type: "POST",
        url: url,
        data: data,
        success: function () {
            location.reload();
        },
        error: function () {
            alert("Пользователь не был удален")
        }
    });
}


function reAvailableUser(id) {
    let url = '/rest/admin/user/reaviable';
    let formData = {
        deleteId: id
    };

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function () {
            $("#reAvailableUserModal" + id).modal("hide");
            location.reload();
        },
        error: function (e) {

        }
    });
}

function showall() {
    $('#client_filter input:checkbox').prop('checked', false);
    $('#client_filter input:checkbox').change();
}

$(document).ready(function () {
    $("#client_filter").change(function () {
        var allChecks = $('#client_filter input:checkbox');
        var data = [];
        for (var w = 0; w < allChecks.length; ++w) {
            if (allChecks[w].checked) {
                data[data.length] = allChecks[w].value;
            }
        }
        var jo = $("#status-columns").find($(".portlet"));
        if (data.length === 0) {
            jo.show();
            return;
        }
        jo.hide();
        jo.filter(function (i, v) {
            var d = $(this)[0].getElementsByClassName("user-icon_card");
            if (d.length === 0) {
                return false;
            }
            for (var w = 0; w < data.length; ++w) {
                if (d[0].innerText.indexOf(data[w]) !== -1) {
                    return true;
                }
            }
        }).show();
    });
});

(function ($) {
    $(document).ready(function () {
        var $panel = $('#panel');
        if ($panel.length) {
            var $sticker = $panel.children('#panel-sticker');
            var showPanel = function () {
                $sticker.hide();
                $panel.animate({
                    right: '+=350'
                }, 200, function () {
                    $(this).addClass('visible');
                });
                fillFilterList();
                drawHiddenStatusesTable();
                if (userLoggedIn.authorities.some(arrayEl => (arrayEl.authority === 'OWNER') || (arrayEl.authority === 'ADMIN') || (arrayEl.authority === 'HR'))) {
                    drawNewUsersTable();
                    drawVerifiedUsersTable();
                }
            };
            var hidePanel = function () {
                $panel.animate({
                    right: '-=350'
                }, 200, function () {
                    $(this).removeClass('visible');
                });
            };
            $sticker
                .children('span').click(function () {
                showPanel();
            });
            $(document.getElementById('close-panel-icon')).click(function () {
                hidePanel();
                $sticker.show();
            });
        }
    });
})(jQuery);
