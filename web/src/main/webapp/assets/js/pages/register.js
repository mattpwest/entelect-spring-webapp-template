(function($) {
    $(document).ready(function() {
        // Enable password strength meter
        var passwordContainer = $('#passwordStrengthContainer');
        var passwordField = $('#password1');
        passwordField.pwstrength({
            common: {
                onKeyUp: function() {
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
    });
})(jQuery);
