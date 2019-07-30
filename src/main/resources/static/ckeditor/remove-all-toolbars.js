// Все ненужные кнопки с тулбара удалены

CKEDITOR.editorConfig = function( config ) {
    config.toolbar = [
        { name: 'clipboard', items: [ 'Cut', 'Copy', 'PasteText', '-', 'Undo', 'Redo' ] },
        { name: 'editing', items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },
        { name: 'basicstyles', items: ['RemoveFormat'] },
        { name: 'tools', items: [ 'Maximize'] },
        { name: 'about', items: [ 'About' ] }
    ];
};