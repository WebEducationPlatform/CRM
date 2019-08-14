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
});

function showCurrentModal(studentId) {
    var clientId = studentId;
    var currentModal = $('#main-modal-window');
    currentModal.data('clientId', clientId);
    currentModal.modal('show');
}
