var NamingDialog = function() {

  var _onOk = null;

  // PUBLIC --------------------------------------------------

  function init() {
    _setupValidation();
    $('#naming-dialog').on('shown.bs.modal', function (e) {
      $('#naming-value').focus();
    });
    $('#naming-ok').on('click', _handleOkClick);
  }

  function show(onOk, defVal) {
    _onOk = onOk;
    $('#naming-value').val(defVal);
    $('#naming-dialog').modal('show');
  }

  // PRIVATE --------------------------------------------------

  function _handleOkClick() {
    if ($('#naming-form').valid()) {
      $('#naming-dialog').modal('hide');
      _onOk && _onOk($('#naming-value').val());
      _onOk = null;
    }
  }

  function _setupValidation() {
    var REQUIRED_MSG = 'This field is required.';
    $('#naming-form').validate({
      errorClass: 'help-block animation-slideUp',
      errorElement: 'div',
      errorPlacement: function(error, e) {
        e.parents('.form-group > div').append(error);
      },
      highlight: function(e) {
        $(e).closest('.form-group').removeClass('has-success has-error').addClass('has-error');
        $(e).closest('.help-block').remove();
      },
      success: function(e) {
        e.closest('.form-group').removeClass('has-success has-error');
        e.closest('.help-block').remove();
      },
      rules: {
        'naming-value': {
          required: true
        }
      },
      messages: {
        'naming-value': REQUIRED_MSG
      }
    });
  }

  return {
    'init': init,
    'show': show
  };
}();
