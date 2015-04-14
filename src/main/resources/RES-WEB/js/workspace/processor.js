var Processor = function() {

  var _connectionId = null;
  var _ht = null;

  var _resizeTimerId = null;

  // PUBLIC --------------------------------------------------

  function init() {
    _connectionId = $('#connection-id').val();
    $(window).on('resize orientationchange', _handleResize).resize();

    console.log('Processor initialized.');
  }

  function execute(sql) {
    $('#indicator').show();
    $('#data-table').hide();
    $.ajax({
      url: '/processor/execute/' + _connectionId,
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify(sql),
      dataType: 'json'
    }).done(function(res) {
      console.log(res);
      _handsontable(res);
    }).always(function() {
      $('#indicator').hide();
    });
  };

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
    return {
      'w': Handsontable.Dom.innerWidth(document.body),
      'h':Handsontable.Dom.innerHeight(document.body)
    }
  }

  function _handsontable(res) {
    $('#data-table').show();
    _ht && _ht.destroy();
    var size = _calculateTableSize();
    _ht = new Handsontable(document.getElementById('data-table'), {
      rowHeaders: true,
      contextMenu: true,
      data: res.records,
      colHeaders: _htColHeaders(res.columnIds),
      columns: _htColumns(res.columnIds.length),
      width: size['w'],
      height: size['h']
    });
  }

  function _htColHeaders(columnIds) {
    // Remove rowid
    columnIds.shift();
    return columnIds;
  }

  function _htColumns(size) {
    // Hide rowid column.
    var columns = [];
    for (var i = 1; i < size; i++) {
      columns.push({ 'data': i });
    }
    return columns;
  }

  return {
    'init': init,
    'execute': execute
  };
}();

$(function() {
  Processor.init();
});