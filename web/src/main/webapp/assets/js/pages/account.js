(function ($) {
    $(document).ready(function () {
        // Enable password strength meter
        var passwordContainer = $('#passwordStrengthContainer');
        var passwordField = $('#password1');
        passwordField.pwstrength({
            common: {
                onKeyUp: function () {
                    if (passwordField.val().length === 0 && passwordContainer.is(':visible')) {
                        passwordContainer.hide(400);
                    } else if (passwordField.val().length >= 0 && !passwordContainer.is(':visible')) {
                        passwordContainer.show(400);
                    }
                }
            },
            ui: {
                bootstrap4: true,
                showVerdictsInsideProgressBar: true,
                container: '#passwordContainer',
                viewports: {
                    progress: '#passwordStrengthContainer'
                }
            }
        });

        $('#usersList').dataTable({
            "preDrawCallback": function (settings) {
                settings.dataSource = "data";
                settings.sSearchSource = "";
                settings.customData = true;
            },
            "sAjaxSource": 'api/public/users',
            "columns": [
                {"data": "firstName"},
                {"data": "lastName"},
                {"data": "roles"},
                {"data": "enabled"}
            ],
            "columnDefs": [
                {
                    "width": "25%",
                    "targets": 0
                },
                {
                    "width": "25%",
                    "targets": 1
                },
                {
                    "orderable": false,
                    "render": renderRolesData,
                    "width": "10%",
                    "targets": 2
                },
                {
                    "orderable": false,
                    "width": "40%",
                    "class": "actions",
                    "render": function (data, type, row) {
                        var csrfToken = $("meta[name='_csrf']").attr("content");
                        var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");

                        var html = '';
                        html += '<div class="table-action"><a href="/users/'+row.id+'" class="btn btn-xs btn-primary">View</a></div>';
                        html += '<div class="table-action"><a href="/users/'+row.id+'/edit" class="btn btn-xs btn-primary">Edit</a></div>';
                        html += '<a class="btn btn-xs btn-primary table-action" href="admin/users/' + row.id + '/roles">Roles</a>' +
                            '<form action="admin/users/resetPassword" method="POST" class="table-action">' +
                                '<input type="hidden" name="userId" value="' + row.id + '" />' +
                                '<input type="hidden" name="' + csrfParameter + '" value="' + csrfToken + '" />' +
                            '<button class="btn btn-xs btn-primary" type="submit">Reset Password</button></form>';
                        if (data === true) {
                            html = html + '<form action="admin/users/disable" method="POST" class="table-action">' +
                                    '<input type="hidden" name="userId" value="' + row.id + '" />' +
                                    '<input type="hidden" name="' + csrfParameter + '" value="' + csrfToken + '" />' +
                                '<button class="btn btn-xs btn-warning" type="submit">Disable</button></form>';
                        } else {
                            html = html + '<form action="admin/users/enable" method="POST" class="table-action">' +
                                '<input type="hidden" name="userId" value="' + row.id + '" />' +
                                '<input type="hidden" name="' + csrfParameter + '" value="' + csrfToken + '" />' +
                                '<button class="btn btn-xs btn-warning" type="submit">Enable</button></form>';
                        }

                        html += '<div class="table-action"><a href="/admin/users/'+row.id+'/enableChangeEmail" class="btn btn-xs btn-info">Enable Email Change</a></div>';
                        return html;
                    },
                    "targets": 3
                }
            ],
            "order": [
                [1, 'asc']
            ],
            "serverSide": true
        });
    });

    function renderRolesData(data, type, row) {
        var html = "";
        for (i = 0; i < data.length; i++) {
            var margin = '';
            if (i != 0) {
                margin = 'style="margin-left: 0.5em;"';
            }

            html = html + '<span class="label label-success" ' + margin + '>' + data[i] + '</span>';
        }

        return html;
    }
})(jQuery);

