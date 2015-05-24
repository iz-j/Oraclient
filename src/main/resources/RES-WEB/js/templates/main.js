var TemplateList = function() {

  var _onRemove = null;

  // PUBLIC --------------------------------------------------

  function init() {
    $('#list').on('click', _handleClick);
  }

  function setContent(html) {
    $('#list').find('tbody').empty().append(html);
  }

  function setOnRemove(fn) {
    _onRemove = fn;
  }

  // PRIVATE --------------------------------------------------

  function _handleClick(e) {
    var el = $(e.target);
    if (el.prop('tagName') == 'I') {
      el = el.parent();
    }

    if (el.hasClass('remove-btn') && el.data('id')) {
      el.prop("disabled", true);
      _onRemove(el.data('id'), el.data('name'));
    }
  }

  return {
    'init': init,
    'setContent': setContent,
    'setOnRemove': setOnRemove
  };
}();



$(function() {
  TemplateList.init();

  // Load.
  $.ajax({
    url: '/templates/list',
    dataType: 'html'
  }).done(function(res) {
    TemplateList.setContent(res);
  });

  // Remove.
  TemplateList.setOnRemove(function(id, name) {
    $.ajax({
      url: '/templates/remove',
      type: 'post',
      data: { id: id },
      dataType: 'html'
    }).done(function(res) {
      TemplateList.setContent(res);
      Base.growl('Template "' + name + '" was removed.');
    });
  });
});