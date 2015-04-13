$(function() {
  var tbl = new ConnectionTable();
  var dlg = new ConnectionDialog();

  // Load connection list.
  $.ajax({
    url: '/connections/list',
    type: 'get',
    dataType: 'html'
  }).done(function(res) {
    tbl.setBody(res);
  });

  // Post new connection.
  dlg.onSubmit = function(data) {
    $.ajax({
      url: '/connections/new',
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify(data),
      dataType: 'html'
    }).done(function(res) {
      tbl.setBody(res);
      dlg.hide();
      Base.growl('New connection added.');
    });
  };

  // Remove connection.
  tbl.onRemoveClick = function(id, name) {
    $.ajax({
      url: '/connections/remove',
      type: 'post',
      data: {id: id},
      dataType: 'html'
    }).done(function(res) {
      tbl.setBody(res);
      Base.growl('Connection "' + name + '" was removed.');
    });
  };

  // Others
  $('#btn-new').on('click', function() {
    dlg.show();
  });
});