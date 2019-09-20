$(document).ready(function () {
    $.each(statuses, function (name, students) {
        $('<div></div>', {
            class: 'column ui-sortable',
            id: 'status-column-' + name.replace(/\s/g, ''),
            text: name
        }).appendTo('#main-row');
        $.each(students, function (i, student) {
            $('<div></div>', {
                class: 'portlet common-modal panel panel-default',
                id: student.id,
                onmouseover: 'displayOption(' + student.id + ')',
                value: student.id,
                'data-card-id': student.id,
            }).appendTo('#status-column-' + name.replace(/\s/g, ''));

            $('<div></div>', {
                class: 'portlet-body',
                'client-id': student.id,
                name: 'client-' + student.id + '-modal',
                onclick: 'showCurrentModal(' + student.id + ')',
                text: student.name + " " + student.lastName
            }).appendTo('div#' + student.id + '.portlet');
        })
    });
    $('<div></div>', {
        class: 'row center-block',
        id: 'row-button',
        style: 'display: flex; justify-content: center; flex-flow:row wrap;'
    }).appendTo('#page-container');
    $('<div></div>', {
        class: 'column',
        id: 'column-button',
    }).appendTo('#row-button');
    $('<button></button>', {
        class: 'btn btn-lg btn-primary btn-block',
        id: 'refresh-button',
        onclick: 'refresh()',
        text: 'Обновить'
    }).appendTo('#column-button');
});

function showCurrentModal(studentId) {
    var clientId = studentId;
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}

function refresh() {
    $.get('/hr/refresh')
        .done(function () {
            location.reload();
            $('#error').remove();
            $('<p></p>', {
                id: 'error',
                text: 'Done'
            }).appendTo('#refresh-button')
        })
        .fail(function () {
            $('#error').remove();
            $('<p></p>', {
                id: 'error',
                text: 'Server error'
            }).appendTo('#refresh-button')
        })
}

