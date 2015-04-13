/**
 * Dialog to create new connection.
 * @public
 */
ConnectionDialog = function() {
  this._validate();
  $('#dlg-new').on('shown.bs.modal', function (e) {
    $('#name').focus();
  })
  $('#btn-submit').on('click', this._handleSubmit.bind(this));
};

/**
 * Event on submit.
 * @public
 */
ConnectionDialog.prototype.onSubmit = function(data){
  console.log(data);
};

/**
 * Show dialog.
 * @public
 */
ConnectionDialog.prototype.show = function() {
  $('#frm-new').find('textarea, :text, select').val('').end().find(':checked').prop('checked', false);
  $('#dlg-new').modal('show');
};

/**
 * Hide dialog.
 * @public
 */
ConnectionDialog.prototype.hide = function() {
  $('#dlg-new').modal('hide');
};

/**
 * Validation settings.
 * @private
 */
ConnectionDialog.prototype._validate = function() {
  var REQUIRED_MSG = 'This field is required.';
  $('#frm-new').validate({
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
        required: true
      },
      'port': {
        required: true,
        number: true
      },
      'sid': {
        required: true
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
};

/**
 * Handle submit action.
 * @private
 */
ConnectionDialog.prototype._handleSubmit = function(e) {
  if ($('#frm-new').valid()) {
    var data = {};
    $.each($('#frm-new').serializeArray(), function(i, v) {
      data[v.name] = v.value;
    });
    this.onSubmit(data);
  }
};