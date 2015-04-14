/**
 * Base functions.<br>
 * cf. Revealing Module Pattern
 */
var Base = function() {

  /**
   * Show notification.
   */
  function growl(message, opt_type) {
    $.bootstrapGrowl('<p>' + message + '</p>', {
      ele: 'body',
      type: opt_type || 'info',
      offset: {from: 'top', amount: 60},
      align: 'right',
      //width: 'auto',
      delay: 3000,
      allow_dismiss: true,
      stackup_spacing: 10
    });
  }

  return {
    'growl': growl
  };
}();
