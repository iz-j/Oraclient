var Processor = function() {

  var _connectionId = null;
  var _ht = null;
  var _resizeTimerId = null;
  var _renderTimerId = null;

  var _editable = false;
  var _tableName = null;
  var _dbColumns = null;
  var _editedMap = null;
  var _removedRowids = null;

  var _localRowid = 0;

  // PUBLIC --------------------------------------------------

  function init() {
    _connectionId = $('#connection-id').val();
    $(window).on('resize orientationchange', _handleResize).resize();
    $('#add-first-row').on('click', _handleAddFirstRowClick);
  }

  function execute(sql) {
    $('#data-table').hide();
    $('#add-first-row').hide();
    $('#alert-success').hide();
    $('#alert-error').hide();
    _clearData();

    _blockUI();
    $.ajax({
      url: '/processor/execute/' + _connectionId,
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify(sql),
      dataType: 'json'
    }).done(function(res) {
      if (res.query) {
        _handsontable(res);
      } else {
        $('#success-message').text(res.updatedCount + ' rows were affected.');
        $('#alert-success').show('slow');
      }
    }).always(function() {
      _unblockUI();
    }).fail(_handleAjaxError);
  }

  function save() {
    if (!_editable) {
      _growl('This view is read only.');
      return;
    }
    if (Object.keys(_editedMap).length == 0 && _removedRowids.length == 0) {
      _growl('There are no changes.');
      return;
    }

    _blockUI();
    $.ajax({
      url: '/processor/save/' + _connectionId,
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify({
        tableName: _tableName,
        columns: _dbColumns,
        editedMap: _editedMap,
        removedRowids: _removedRowids
      })
    }).done(function(res) {
      // Merge new rowid.
      $.each(_ht.getData(), function(i, data) {
        var oldRowid = data[0];
        var newRowid = res[oldRowid];
        if (newRowid) {
          data[0] = newRowid;
        }
      });
      // Clear and notify.
      _editedMap = {};
      _removedRowids = [];
      _ht.render();
      _growl('Changes has been saved.');
    }).always(function() {
      _unblockUI();
    }).fail(_handleAjaxError);
  }

  // PRIVATE --------------------------------------------------

  function _handleResize(e) {
    if (!_ht) {
      return;
    }
    _resizeTimerId && clearTimeout(_resizeTimerId);
    _resizeTimerId = setTimeout(function() {
      var container = document.getElementById('data-table');
      var size = _calculateTableSize();
      container.style.width = size['w'] + 'px';
      container.style.height = size['h'] + 'px';
      _ht.updateSettings({
        width: size['w'],
        height: size['h']
      });
    }, 200);
  }

  function _calculateTableSize() {
    var statusBarHeight = Handsontable.Dom.outerHeight(document.getElementById('status-bar'));
    return {
      'w': Handsontable.Dom.innerWidth(document.body),
      'h': Handsontable.Dom.innerHeight(document.body) - statusBarHeight
    }
  }

  function _handsontable(res) {
    // Initialize.
    $('#data-table').show();
    _ht && _ht.destroy();
    _ht = null;

    $('#record-size').text(res.records.length);
    _editable = res.editable;
    _tableName = res.tableName;
    _dbColumns = res.columns;
    _editedMap = {};
    _removedRowids = [];
    if (_editable && res.records.length == 0) {
      $('#add-first-row').show();
    }

    // Handsontable.
    var size = _calculateTableSize();
    _ht = new Handsontable(document.getElementById('data-table'), {
      data: res.records,
      rowHeaders: true,
      colHeaders: res.columnNames,
      columns: _htColumns(res),
      width: size['w'],
      height: size['h'],
      contextMenu: _htContextMenu(),
      undo: false,
      readOnly: !_editable,
      readOnlyCellClassName: null,
      afterChange: _htAfterChange,
      afterRender: _htAfterRender,
      afterCreateRow: _htAfterCreateRow,
      beforeRemoveRow: _htBeforeRemoveRow
    });
  }

  function _htColumns(res) {
    var columns = [];
    $.each(res.columns, function(i, c) {
      columns.push({
        data: i + 1,// Increment to hide rowid column.
        renderer: _htRenderer
      });
    });
    return columns;
  }

  function _htContextMenu() {
    var isDisabled = function() {
      // Avoid error about context menu clicked when no rows.
      return (!_editable) || (_ht.countRows() == 0);
    }

    return {
      callback: function(key, options) {

      },
      items: {
        'row_above': { disabled: isDisabled },
        'row_below': { disabled: isDisabled },
        'remove_row': { disabled: isDisabled },
        'hsep1': '---------'
      }
    };
  }

  function _htAfterChange(changes, source) {
    if (!_editable) {
      return;
    }
    if (source != 'edit' && source != 'autofill' && source != 'paste') {
      return;
    }

    // Hold changes by each rowid.
    $.each(changes, function(i, change) {
      var row = change[0];
      var col = change[1];
      var oldVal = change[2];
      var newVal = change[3];
      if (oldVal != newVal) {
        var data = _ht.getSourceDataAtRow(row);
        var rowid = data[0];
        if (!(rowid in _editedMap)) {
          _editedMap[rowid] = {};
        }
        _editedMap[rowid][col - 1] = newVal;
      }
    });

    // Render.
    _renderTimerId && clearTimeout(_renderTimerId);
    _renderTimerId = setTimeout(function() {
      _ht.render();
    }, 200);
  }

  function _htRenderer(instance, TD, row, col, prop, value, cellProperties) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    // text-align.
    var dbCol = _dbColumns[col];
    if ('NUMBER' == dbCol['dataType']) {
      TD.style.textAlign = 'right';
    }
    // Change row & cell color.
    if (!_editable) {
      return;
    }
    var data = instance.getSourceDataAtRow(row);
    var rowid = data[0];
    var isNewRow = _isNewRowid(rowid);
    $(TD).toggleClass('row-added', isNewRow);
    if (!isNewRow) {
      var rowChanged = (rowid in _editedMap);
      $(TD).toggleClass('row-changed', rowChanged);
      rowChanged && $(TD).toggleClass('cell-changed', (col in _editedMap[rowid]));
    }
  }

  function _htAfterRender(isForced) {
    // Show edited number and some control.
    if (_editable) {
      $('#modified-rows').text(Object.keys(_editedMap).length);
      $('#removed-rows').text(_removedRowids.length);
      if (_ht && _ht.countRows() == 0) {
        $('#add-first-row').show();
      }
    }
  }

  function _htAfterCreateRow(index, amount) {
    if (!_ht) {
      return;
    }
    var data = _ht.getSourceDataAtRow(index);
    // Assign rowid for new record and hold new row as edited.
    if (data[0] == null) {
      data[0] = _newRowid();
      _editedMap[data[0]] = {};
    }
  }

  function _htBeforeRemoveRow(index, amount) {
    var data = _ht.getSourceDataAtRow(index);
    var rowid = data[0];
    if (!rowid) {
      return;// This is spare row.
    }
    // Remember rowid of removed row.
    if (!_isNewRowid(rowid)) {
      _removedRowids.push(rowid);
    }
    // Remove from edited.
    delete _editedMap[rowid];
  }

  function _clearData() {
    _editable = false;
    _tableName = null;
    _dbColumns = null;
    _editedMap = null;
    _removedRowids = null;
    $('#record-size').text('0');
    $('#modified-rows').text('0');
  }

  function _newRowid() {
    return 'new$' + (++_localRowid);
  }

  function _isNewRowid(rowid) {
    return (rowid) && (rowid.lastIndexOf('new$', 0) == 0);
  }

  function _handleAddFirstRowClick() {
    _ht && _ht.alter('insert_row');
    $('#add-first-row').hide();
  }

  function _handleAjaxError(xhr, status, error) {
    var message = null;
    if (xhr.responseJSON) {
      // Select error row.
      var rowid = xhr.responseJSON['rowid'];
      var errorRow = -1;
      if (rowid) {
        $.each(_ht.getData(), function(i, data) {
          if (data[0] == rowid) {
            _ht.selectCell(i, 0, i, 0, true);
            errorRow = i;
            return false;
          }
        });
      }
      // Show error.
      message = xhr.responseJSON['message'];
      if (errorRow > -1) {
        message = '[Row = ' + (errorRow + 1) + '] ' + message;
      }
    } else {
      message = xhr.responseText;
    }
    _growl(message, 'danger');
  }

  function _blockUI() {
    window.parent['Base']['blockUI']();
  }

  function _unblockUI() {
    window.parent['Base']['unblockUI']();
  }

  function _growl(message, opt_type) {
    window.parent['Base']['growl'](message, opt_type);
  }

  return {
    'init': init,
    'execute': execute,
    'save': save
  };
}();

$(function() {
  Processor.init();
});