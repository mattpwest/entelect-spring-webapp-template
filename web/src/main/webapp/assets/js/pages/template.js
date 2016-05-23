(function($) {
    $(document).ready(function() {
        var templateName;
        $('#templatesList').dataTable({
            "preDrawCallback": function( settings ) {
                settings.sSearchSource = "search/templates";
                settings.dataSource = "templates";
            },
            "sAjaxSource" : 'api/templates',
            "columns": [
                { "data": "name" },
                { "data": "subject" },
                { "data": "id" }
            ],
            "columnDefs": [
                {
                    "render": function ( data, type, row ) {
                        if(data != null){
                            templateName = data;
                            return '<strong>' + templateName + '</strong>&nbsp;<span>';
                        }
                    },
                    "targets": 0
                },
                {
                    "render": function ( data, type, row ) {
                        return data;
                    },
                    "targets": 1
                },
                {
                    "orderable": false,
                    "render": function ( data, type, row ) {
                        return '<a class="btn btn-xs btn-primary table-action" href="admin/templates/edit/' + templateName + '">Edit</a>';
                    },
                    "targets": 2
                }
            ],
            "serverSide" : true
        });
    });
})(jQuery);
