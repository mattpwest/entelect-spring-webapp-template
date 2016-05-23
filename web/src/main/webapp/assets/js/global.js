(function ($) {
    $(document).ready(function () {
        // Handle logout link clicks
        $('a#logoutLink').on('click', function() {
            $('form#logoutForm').submit();
        });


        // Customise toastr notifications
        toastr.options = {
            "closeButton" : true,
            "progressBar" : true,
            "newestOnTop" : true,
            "positionClass" : "toast-bottom-center"
        };

        crudConfirmDelete();

        loadChosenDropDowns();

        setHeadersForAJAXCalls();

        initBootstrapTooltips();

        configureValidationEngine();
    });
})(jQuery);

function configureValidationEngine() {
    $.validationEngine.defaults.promptPosition = 'inline';
    $.validationEngine.defaults.scroll = false;
    $.validationEngine.defaults.prettySelect = true;
    $.validationEngine.defaults.useSuffix = '_chosen';
    $.validationEngine.defaults.bootstrapCheckbox = true;
    $.validationEngine.defaults.maxErrorsPerField = 1;
    $.validationEngine.defaults.validateAttribute = "data-validation-engine";


    $('form').each(function () {
        $(this).validationEngine();
    });
}

function crudConfirmDelete() {
    $('body').on('click', 'button.action-delete, a.action-delete', handleModalConfirmDeleteClick);
}

function handleModalConfirmDeleteClick() {
    var modalId = $(this).data('delete-modal');
    if (modalId === undefined || modalId === null) {
        modalId = 'confirmDelete';
    }

    var modal = $('#' + modalId);
    modal.find('form').attr('action', $(this).data('delete-uri'));
    modal.modal('show');
}

function initBootstrapTooltips() {
    $('[data-toggle="tooltip"]').tooltip();
}

function constructPopupMessage(data) {
    var onClickOverride = '';
    if (data.link != '') {
        onClickOverride = {
            onclick: function () {
                window.location = data.link;
            }
        };
    }

    toastr[data.status.toLowerCase()](data.message, data.title, onClickOverride);
}

function loadChosenDropDowns() {
    $.fn.select2.defaults.set("minimumResultsForSearch", 10);
    $('.chosen-select').select2();
}

function setHeadersForAJAXCalls() {
    $.ajaxPrefilter(function (options) {
        if (!options.beforeSend) {
            options.beforeSend = function (xhr) {

            }
        }
    });
    $.ajaxSetup({
        xhr: function () {
            var xhr = new window.XMLHttpRequest();
            xhr.upload.addEventListener("progress", function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = evt.loaded / evt.total;
                    percentComplete = parseInt(percentComplete * 100);
                    $(document).trigger('uploadProgress', [percentComplete]);
                    if (percentComplete === 100) {
                        $(document).trigger('uploadComplete');
                    }
                }
            }, false);
            return xhr;
        }
    });
}
