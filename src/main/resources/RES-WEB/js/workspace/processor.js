var Processor = function() {

  var _connectionId = null;
  var _ht = null;
  var _resizeTimerId = null;
  var _renderTimerId = null;

  var _hasRowid = false;
  var _tableName = null;
  var _dbColumns = null;
  var _editedMap = null;
  var _removedRowids = null;

  var _localRowid = 0;

  // PUBLIC --------------------------------------------------

  function init() {
    _connectionId = $('#connection-id').val();
    $(window).on('resize orientationchange', _handleResize).resize();
  }

  function execute(sql) {
    $('#data-table').hide();
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
        $('#alert-success').show();
      }
    }).always(function() {
      _unblockUI();
    }).fail(function(xhr, status, error) {
      $('#error-message').text(xhr.responseJSON['message']);
      $('#alert-error').show();
    });
  }

  function save() {
    if (!_hasRowid) {
      _growl('This view is read only.');
      return;
    }
    if (Object.keys(_editedMap).length == 0) {
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
        columnNames: _ht.getSettings().colHeaders,
        editedMap: _editedMap,
        removedRowids: _removedRowids
      })
    }).done(function(res) {
      _editedMap = {};
      _removedRowids = [];
      _ht.render();
      _growl('Changes has been saved.');
    }).always(function() {
      _unblockUI();
    }).fail(function(xhr, status, error) {
      _growl(xhr.responseJSON['message'], 'danger');
    });
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
    $('#data-table').show();
    _ht && _ht.destroy();
    _ht = null;

    $('#record-size').text(res.records.length);
    _hasRowid = res.hasRowid;
    _tableName = res.tableName;
    _dbColumns = res.columns;
    _editedMap = {};
    _removedRowids = [];

    var size = _calculateTableSize();
    _ht = new Handsontable(document.getElementById('data-table'), {
      data: res.records,
      rowHeaders: true,
      colHeaders: _htColHeaders(res),
      columns: _htColumns(res),
      width: size['w'],
      height: size['h'],
      contextMenu: _htContextMenu(),
      undo: false,
      minSpareRows: 2,
      readOnly: !_hasRowid,
      readOnlyCellClassName: null,
      afterChange: _htAfterChange,
      afterRender: _htAfterRender,
      afterCreateRow: _htAfterCreateRow,
      beforeRemoveRow: _htBeforeRemoveRow
    });
  }

  function _htColHeaders(res) {
    // Remove rowid if necessary.
    _hasRowid && res.columnIds.shift();
    return res.columnIds;
  }

  function _htColumns(res) {
    var columns = [];
    $.each(res.columns, function(i, c) {
      // Hide rowid column if necessary.
      if (_hasRowid && i == 0) {
        return undefined;
      }
      columns.push({
        data: i,
        renderer: _htRenderer
      });
    });
    return columns;
  }

  function _htContextMenu() {
    return _hasRowid ? ['row_above', 'row_below', 'remove_row'] : false;
  }

  function _htAfterChange(changes, source) {
    if (!_hasRowid) {
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
    var dbCol = _hasRowid ? _dbColumns[col + 1] : _dbColumns[col];
    if ('NUMBER' == dbCol['dataType']) {
      TD.style.textAlign = 'right';
    }
    // Change row & cell color if edited.
    if (!_hasRowid) {
      return;
    }
    var data = instance.getSourceDataAtRow(row);
    var rowid = data[0];
    if (_isNewRowid(rowid)) {
      $(TD).toggleClass('row-added', true);
    } else {
      var rowChanged = (rowid in _editedMap);
      $(TD).toggleClass('row-changed', rowChanged);
      rowChanged && $(TD).toggleClass('cell-changed', (col + 1 in _editedMap[rowid]));
    }
  }

  function _htAfterRender(isForced) {
    // Show edited number.
    if (_hasRowid) {
      $('#modified-rows').text(Object.keys(_editedMap).length);
      $('#removed-rows').text(_removedRowids.length);
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
    // Remember rowid of removed row.
    if (!_isNewRowid(rowid)) {
      _removedRowids.push(rowid);
    }
    // Remove from edited.
    delete _editedMap[rowid];
  }

  function _clearData() {
    _hasRowid = false;
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
    return rowid && rowid.lastIndexOf('new$', 0) == 0;
  }

  function _blockUI() {
    window.parent['Base']['blockUI']();
  }

  function _unblockUI() {
    window.parent['Base']['unblockUI']();
  }

  function _growl(message) {
    window.parent['Base']['growl'](message);
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