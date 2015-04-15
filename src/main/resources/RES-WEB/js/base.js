/**
 * Base functions.<br>
 * cf. Revealing Module Pattern
 */
var Base = function() {

  var _blocking = 0;

  // PUBLIC --------------------------------------------------

  function growl(message, opt_type) {
    $.bootstrapGrowl('<p>' + message + '</p>', {
      ele: 'body',
      type: opt_type || 'info',
      offset: {
        from: 'top',
        amount: 60
      },
      align: 'right',
      // width: 'auto',
      delay: 3000,
      allow_dismiss: true,
      stackup_spacing: 10
    });
  }

  function blockUI() {
    if (_blocking == 0) {
      $.blockUI({
        message: $('#block-ui')
      });
    }
    _blocking++;
  }

  function unblockUI() {
    _blocking--;
    if (_blocking == 0) {
      $.unblockUI();
    }
  }

  return {
    'growl': growl,
    'blockUI': blockUI,
    'unblockUI': unblockUI
  };
}();

$(function() {

  var _xhrs = {};
  var _xhrId = 0;

  // Ajax handling.
  $(document).on('ajaxSend', function(event, xhr, options) {
    // Abort request when page will chage.
    $(window).on('beforeunload.Base', function() {
      xhr.abort();
    });
    // Remember xhr.
    xhr['_xhrId'] = ++_xhrId;
    _xhrs[_xhrId] = xhr;
  }).on('ajaxComplete', function(event, xhr, options) {
    // Forget xhr.
    delete _xhrs[xhr['_xhrId']];
  }).on('ajaxStop', function() {
    // Unbind event to abort xhr when all ajax has been completed.
    $(window).off('beforeunload.Base');
  });

  $('#cancel-request').on('click', function() {
    // This is lazy implementation... Must not cancel all, should cancel only target!
    $.each(_xhrs, function(k, v) {
      v.abort();
    });
    $.ajax({
      url: '/abortSql',
      type: 'post'
    });
  })
});