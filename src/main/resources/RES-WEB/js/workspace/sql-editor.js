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
    $('#btn-format').on('click', fireFormat);
    $('#btn-execute').on('click', fireExecute);

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

  function fireExecute() {
    (_sql && _onExecute) && _onExecute(_sql);
  }

  function fireFormat() {
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

  function _textcomplete() {
    // Setup.
    $('#sql-editor').textcomplete([{
      match: /(^|\s)([A-Za-z]\w*)$/,
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
        value = value.replace(/\*/, '');
        // To avoid replacing white space or line break. Is this correct?
        var whole = $('#sql-editor').val();
        var check = whole.match(this.match);
        if (!check) {
          return value;
        }
        var str = check[1];
        if (str == ' ') {
          value = ' ' + value;
        } else if (str == '\n') {
          value = '\n' + value;
        }
        console.log(value);
        return value + ' ';
      },
      template: function(value) {
        if (value.lastIndexOf('*', 0) == 0) {
          return value.replace(/\*/, '');
        } else {
          return '<b>' + value + '</b>';
        }
      }
    }], {
      appendTo: $('#for-textcomplete')// To resolve css conflict!
    }).on('textComplete:select', function(e, value) {
      _handleTextChange(e);
    });
  }

  return {
    'init': init,
    'setSql': setSql,
    'getSql': getSql,
    'setOnChange': setOnChange,
    'setOnExecute': setOnExecute,
    'fireFormat': fireFormat,
    'fireExecute': fireExecute
  };
}();
