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

  function setPreProcess(fn) {
    _postProcess = fn;
  }

  function setPostProcess(fn) {
    _preProcess = fn;
  }

  function execute(sql, callback) {
    $('#data-table').hide();
    $('#alert-success').hide();
    $('#alert-error').hide();

    $.ajax({
      url: '/processor/execute/' + _connectionId,
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify(sql),
      dataType: 'json'
    }).done(function(res) {
      console.log(res);
      if (res.query) {
        _handsontable(res);
      } else {
        $('#success-message').text(res.updatedCount + ' rows were affected.');
        $('#alert-success').show();
      }
    }).always(function() {
      callback && callback();
    }).fail(function(xhr, status, error) {
      $('#error-message').text(error);
      $('#alert-error').show();
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
      colHeaders: _htColHeaders(res),
      columns: _htColumns(res),
      width: size['w'],
      height: size['h']
    });
  }

  function _htColHeaders(res) {
    // Remove rowid if necessary.
    res.hasRowid && res.columnIds.shift();
    return res.columnIds;
  }

  function _htColumns(res) {
    // Hide rowid column if necessary.
    // This will be called after _htColHeaders, so rowid has already been removed.
    var columns = [];
    $.each(res.columnIds, function(i, v) {
      var index = res.hasRowid ? i + 1 : i;
      columns.push({ 'data': index});
    });
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