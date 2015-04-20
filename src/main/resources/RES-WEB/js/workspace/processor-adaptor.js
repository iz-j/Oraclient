/**
 * Processor adaptor.
 */

var ProcessorAdaptor = function() {

  var _connectionId = null;
  var _sql = null;

  // PUBLIC --------------------------------------------------

  function init(connectionId) {
    _connectionId = connectionId;
    $('#btn-save').on('click', _handleSaveClick);
  }

  function setSql(sql) {
    var ifrm = null;

    // Remove if sql is null.
    if (!sql) {
      ifrm = _findIframe(_sql.id);
      ifrm && $(ifrm).remove();
      return;
    }

    // Hide current.
    if (_sql) {
      ifrm = _findIframe(_sql.id);
      ifrm && $(ifrm).hide();
      ifrm = null;
    }

    // Show new.
    _sql = sql;
    ifrm = _findIframe(sql.id);
    if (!ifrm) {
      $('#processor-root')
        .append('<iframe src="/processor/' + _connectionId + '" id="processor-' + sql.id + '" />');
      ifrm = _findIframe(sql.id);
    }
    $(ifrm).show();
  }

  function updateSql(sql) {
    if (!sql) {
      return;
    }
    if (_sql.id == sql.id) {
      _sql.sentence = sql.sentence;
    }
  }

  function removeSql(sql) {
    _find(sql.id).remove();
  }

  function executeSql(sql) {
    _callProcessor('execute', sql);
  }

  // PRIVATE --------------------------------------------------

  function _findIframe(id) {
    return $('#processor-' + id)[0];
  }

  function _findInIframe(id) {
    if (_sql) {
      return null;
    }
    var ifrm = _findIframe(_sql.id);
    return ifrm ? $('#' + id, ifrm.contentWindow.document) : null;
  }

  function _callProcessor(funcName, opt_arg) {
    if (!_sql) {
      return null;
    }
    var ifrm = _findIframe(_sql.id);
    if (!ifrm) {
      return null;
    }
    var proc = ifrm.contentWindow['Processor'];
    return proc[funcName].apply(this, $.map(arguments, function(arg, idx) {
      return idx > 0 ? arg : null;
    }));
  }

  function _handleSaveClick() {
    _callProcessor('save');
  }

  return {
    'init': init,
    'setSql': setSql,
    'updateSql': updateSql,
    'removeSql': removeSql,
    'executeSql': executeSql
  };
}();
