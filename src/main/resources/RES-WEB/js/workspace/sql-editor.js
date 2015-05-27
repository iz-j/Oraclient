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
    $('#btn-table-info').on('click', _handleTableInfoClick);

    _textassist();
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
    if (disabled || _sql.type !== 'TABLE') {
      $('#btn-table-info').hide();
    } else {
      $('#btn-table-info').show();
      $('#current-table-name').text(_sql.tableName);
    }
  }

  function _handleTextChange(e) {
    _timerId && clearTimeout(_timerId);
    _timerId = setTimeout(function() {
      _sql.sentence = $('#sql-editor').val();
      _onChange && _onChange(_sql);
    }, 300);
  }
  
  function _handleTableInfoClick() {
    TableInfoDialog.show(_sql.tableName);
  }

  function _textassist() {
    // Setup.
    $('#sql-editor').textassist({
      find: function(term, callback) {
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
      ulClassName: 'dropdown-menu sql-editor-suggest',
      anchorClassName: 'sql-editor-suggest-item',
      activeClassName: 'active',
      item: function(source, term) {
        var display = source;
        var emphasis = term;
        var reservedWord = true;
        if (display.lastIndexOf('*', 0) === 0) {// Items of a table start with '*'.
          display = display.slice(1);
          reservedWord = false;
        }
        if (emphasis) {
          emphasis = emphasis.toUpperCase();
          display = display.replace(emphasis, '<b>' + emphasis + '</b>');
        }
        if (reservedWord) {
          return '<i class="fa fa-database"></i>&nbsp;' + display;
        } else {
          return '<i class="fa fa-table"></i>&nbsp;' + display;
        }
      },
      loadingHTML: '&nbsp;<i class="fa fa-spinner fa-2x fa-spin text-muted"></i>&nbsp;<span>Loading...</span>',
      noneHTML: '&nbsp;<i class="fa fa-ban"></i>&nbsp;<span>Nothing found.</span>',
      beforeFix: function(source) {
        return source.lastIndexOf('*', 0) === 0 ? source.slice(1) : source;
      },
      afterFix: function(value) {
        _handleTextChange(null);
      }
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
