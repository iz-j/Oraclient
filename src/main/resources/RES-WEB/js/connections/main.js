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
      data: data,
      dataType: 'html'
    }).done(function(res) {
      tbl.setBody(res);
      dlg.hide();
    });
  };

  // Others
  $('#btn-new').on('click', function() {
    dlg.show();
  });
});