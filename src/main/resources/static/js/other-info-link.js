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
    $('.onecheckbox').each(function () {
        let oimcList = [];
        let input = $(this);
        let name = input.attr('id');
        if ($(input).is(":checked")) {
            oimcList.push({
                value: true,
                name: name
            })
        } else {
            oimcList.push({
                value: false,
                name: name
            })
        }
        list.push({
            name: oimcList
        });
    });


    $('.textquestionall').each(function () {
        let oimcList = [];
        let name = $(this).find('.textquestion').attr('id');
        let cardField = $(this).find('.cardfield').attr('id');
        let value = $(this).find('.textquestion').val();
        oimcList.push({
            value: value,
            name: name,
            cardField: cardField
        });
        list.push({
            name: oimcList
        });
    });

    $('.multi').each(function () {
        let oimcList = [];
        let name = $(this).children('input').attr('id');
        let nameList = [];
        nameList.push({
            name: name
        });
        $(this).find('.variant').each(function () {
            let input = $(this).children('input');
            let name = input.attr('id');
            if ($(input).is(":checked")) {
                oimcList.push({
                    value: true,
                    name: name
                })
            } else {
                oimcList.push({
                    value: false,
                    name: name
                })
            }
        });
        list.push({
            oimcList: oimcList,
            name: nameList
        })
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