/**
 * SQL editor.
 */

var SqlEditor = function() {

  var _connectionId = null;
  
  var _onChange = null;
  var _onExecute = null;
  var _sql = null;

  var _timerId = null;

  // PUBLIC --------------------------------------------------

  function init(connectionId) {
    _connectionId = connectionId;
    _disableButtons(true);

    $('#sql-editor').prop('disabled', true).on('input propertychange', _handleTextChange);
    $('#btn-format').on('click', _handleFormatClick);
    $('#btn-execute').on('click', _handleExecuteClick);

    _textcomplete();
  }

  function setSql(sql) {
    _sql = sql;
    if (_sql) {
      $('#sql-editor').val(sql.sentence).prop('disabled', false).focus();
      _disableButtons(false);
    } else {
      $('#sql-editor').val('').prop('disabled', true);
      _disableButtons(true);
    }
  }

  function getSql() {
    _sql.sentence = $('#sql-editor').val();
    return _sql;
  }

  function setOnChange(fn) {
    _onChange = fn;
  }

  function setOnExecute(fn) {
    _onExecute = fn;
  }

  // PRIVATE --------------------------------------------------

  function _disableButtons(disabled) {
    $('#btn-format').prop('disabled', disabled);
    $('#btn-execute').prop('disabled', disabled);
  }

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

    _disableButtons(true);
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
      _disableButtons(false);
    });
  }

  function _handleExecuteClick(e) {
    (_sql && _onExecute) && _onExecute(_sql);
  }

  function _textcomplete() {
    // Setup.
    $('#sql-editor').textcomplete([{
      match: /(^|\x20)([A-Za-z]\w*)$/,
      search: function (term, callback) {
        $.getJSON('/workspace/sqlCompletions', {
          connectionId: _connectionId,
          term: term,
          tableName: _sql.tableName
        }).done(function (res) {
          callback(res);
        }).fail(function () {
          callback([]);
        });
      },
      replace: function (value) {
        return ' ' + value + ' ';
      }
    }], {
      appendTo: $('#for-textcomplete')// To resolve css conflict!
    });
  }

  return {
    'init': init,
    'setSql': setSql,
    'getSql': getSql,
    'setOnChange': setOnChange,
    'setOnExecute': setOnExecute
  };
}();
