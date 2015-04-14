/**
 * SQL editor.
 */

var SqlEditor = function() {

  var _onChange = null;
  var _onExecute = null;
  var _sql = null;

  var _timerId = null;

  // PUBLIC --------------------------------------------------

  function init() {
    $('#sql-editor').prop('disabled', true).on('input propertychange', _handleTextChange);
    disableButtons(true);
    $('#btn-format').on('click', _handleFormatClick);
    $('#btn-execute').on('click', _handleExecuteClick);
  }

  function setSql(sql) {
    _sql = sql;
    if (_sql) {
      $('#sql-editor').val(sql.sentence).prop('disabled', false).focus();
      disableButtons(false);
    } else {
      $('#sql-editor').val('').prop('disabled', true);
      disableButtons(true);
    }
  }

  function getSql() {
    _sql.sentence = $('#sql-editor').val();
    return _sql;
  }

  function disableButtons(disabled) {
    $('#btn-format').prop('disabled', disabled);
    $('#btn-execute').prop('disabled', disabled);
  }

  function setOnChange(fn) {
    _onChange = fn;
  }

  function setOnExecute(fn) {
    _onExecute = fn;
  }

  // PRIVATE --------------------------------------------------

  function _handleTextChange(e) {
    _timerId && clearTimeout(_timerId);
    _timerId = setTimeout(function() {
      _sql.sentence = $('#sql-editor').val();
      _onChange && _onChange(_sql);
    }, 300);
  }

  function _handleFormatClick(e) {
    if (!_sql) {
      return;
    }

    disableButtons(true);
    $.ajax({
      url: '/workspace/formatSql',
      type: 'post',
      dataType: 'text',
      contentType: 'application/json',
      data: JSON.stringify(_sql)
    }).done(function(res) {
      _sql.sentence = res;
      $('#sql-editor').val(res);
      _onChange && _onChange(_sql);
    }).always(function() {
      disableButtons(false);
    });
  }

  function _handleExecuteClick(e) {
    (_sql && _onExecute) && _onExecute(_sql);
  }

  return {
    'init': init,
    'setSql': setSql,
    'getSql': getSql,
    'disableButttons': disableButtons,
    'setOnChange': setOnChange,
    'setOnExecute': setOnExecute
  };
}();
