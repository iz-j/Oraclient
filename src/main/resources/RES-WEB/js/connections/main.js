$(function() {
  ConnectionList.init();
  ConnectionDialog.init();

  // Load connection list.
  $.ajax({
    url: '/connections/list',
    dataType: 'html'
  }).done(function(res) {
    ConnectionList.setContent(res);
  });

  // Post new connection.
  ConnectionDialog.setOnPost(function(data) {
    $.ajax({
      url: '/connections/new',
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify(data),
      dataType: 'html'
    }).done(function(res) {
      ConnectionList.setContent(res);
      ConnectionDialog.hide();
      Base.growl('New connection added.');
    });
  });

  // Remove connection.
  ConnectionList.setOnRemove(function(id, name) {
    $.ajax({
      url: '/connections/remove',
      type: 'post',
      data: { id: id },
      dataType: 'html'
    }).done(function(res) {
      ConnectionList.setContent(res);
      Base.growl('Connection "' + name + '" was removed.');
    });
  });

  // Others
  $('#btn-new').on('click', function() {
    ConnectionDialog.show();
  });
});