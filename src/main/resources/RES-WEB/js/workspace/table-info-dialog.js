var TableInfoDialog = function() {
  
  var _connectionId = null;
  var _tableName = null;
  
  // PUBLIC --------------------------------------------------

  function init(connectionId) {
    _connectionId = connectionId;
    $('#table-info-dialog').on('shown.bs.modal', _loadInfo);
  }

  function show(tableName) {
    _tableName = tableName;
    $('#table-info-title').text(_tableName);
    $('#table-info-subtitle').text('');
    $('#table-info-body').empty();
    $('#table-info-dialog').modal('show');
  }

  // PRIVATE --------------------------------------------------

  function _loadInfo() {
    $.ajax({
      url: '/workspace/tableInfoView',
      type: 'get',
      dataType: 'html',
      data: {connectionId: _connectionId, tableName: _tableName}
    }).done(function(res) {
      $('#table-info-body').append(res);
      $('#table-info-tabs a:first').tab('show');
      var tableComments = $('#table-info-comments').val();
      tableComments && $('#table-info-subtitle').text('(' + tableComments + ')');
    });
  }
  
  return {
    'init': init,
    'show': show
  };
}();
