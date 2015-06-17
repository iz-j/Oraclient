/**
 * Connection dialog.
 */

var ConnectionDialog = function() {

  var _onPost = null;

  // PUBLIC --------------------------------------------------

  function init() {
    _setupValidation();
    $('#connection-dialog').on('shown.bs.modal', function (e) {
      $('#name').focus();
    });
    $('#btn-submit').on('click', _handleOkClick);
  }

  function show() {
    $('#connection-form').find('textarea, :text, select').val('').end().find(':checked').prop('checked', false);
    $('#connection-dialog').modal('show');
  }

  function hide() {
    $('#connection-dialog').modal('hide');
  }

  function setOnPost(fn) {
    _onPost = fn;
  }

  // PRIVATE --------------------------------------------------

  function _handleOkClick(e) {
    if ($('#connection-form').valid()) {
      var data = {};
      $.each($('#connection-form').serializeArray(), function(i, v) {
        data[v.name] = v.value;
      });
      _onPost(data);
    }
  }

  function _setupValidation() {
    var REQUIRED_MSG = 'This field is required.';
    $('#connection-form').validate({
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
        'name': {
          required: true
        },
        'host': {
          required: false
        },
        'port': {
          required: false,
          number: true
        },
        'sid': {
          required: false
        },
        'username': {
          required: true
        },
        'password': {
          required: true
        }
      },
      messages: {
        'name': REQUIRED_MSG,
        'host': REQUIRED_MSG,
        'port': {
          required: REQUIRED_MSG,
          number: 'Port must be numeric.'
        },
        'sid': REQUIRED_MSG,
        'username': REQUIRED_MSG,
        'password': REQUIRED_MSG
      }
    });
  }

  return {
    'init': init,
    'show': show,
    'hide': hide,
    'setOnPost': setOnPost
  };
}();

