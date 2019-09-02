function createOtherInformationLink() {
    console.log("Зашел");
    var baseUrl = window.location.host;
    var url = "/otherInformation/link/create";

    var clientId = getAllUrlParams(window.location.href).id;
    var hash = (+new Date).toString(36);
    var setting = {
        clientId: clientId,
        hash: hash
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: url,
        data: JSON.stringify(setting),
        success: function () {
            var contractLink = 'https://' + baseUrl + '/information/' + hash;
            $('#other-information-link-input').val(contractLink);
        },
        error: function () {
            alert('Ошибка создания ссылки!')
        }
    });
}

function createListOtherInformation() {
    let hash = document.getElementById('hiddenHash').value;
    let list = [];
    $('form#other-information-form :input').each(function () {
        var input = $(this);
        let name = input.attr('id');
        if (input.attr('type') === 'checkbox') {
            if ($(input).is(":checked")) {
                list.push({
                    value: true,
                    name: name
                })
            } else {
                list.push({
                    value: false,
                    name: name
                })
            }
        } else {
            list.push({
                value: input.val(),
                name: name
            })
        }
    });
    let inputValueAndHash = {};
    inputValueAndHash['hash'] = hash;
    inputValueAndHash['otherInformationInputValues'] = list;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/otherInformation/link/link",
        data: JSON.stringify(inputValueAndHash),
        success: function () {
            var contractLink = 'https://' + window.location.host + '/information/thanks';
            window.location.replace(contractLink);
        },
        error: function (xhr, status, errorThrown) {
            console.log(errorThrown);
            console.log(status);
            console.log(xhr.statusText);
        }
    });
}

function deleteOtherInformation() {
    var url = "/otherInformation/link/delete/" + getAllUrlParams(window.location.href).id;;
    $.ajax({
        type: "DELETE",
        dataType: 'json',
        url: url,
        complete: function () {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    });
}