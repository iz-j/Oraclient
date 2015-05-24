/**
 * Connection list.
 */

var ConnectionList = function() {

  var _onRemove = null;

  // PUBLIC --------------------------------------------------

  function init() {
    $('#connection-list').on('click', _handleClick);
  }

  function setContent(html) {
    $('#connection-list').find('tbody').empty().append(html);
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

