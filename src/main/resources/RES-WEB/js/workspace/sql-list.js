/**
 * SQL list.
 */

var SqlList = function() {

  var _onChange = null;
  var _onRemove = null;
  
  var _selected = null;

  // PUBLIC --------------------------------------------------

  function init() {
    $('#sql-list').on('click', _handleClick);
  }

  function addContent(html) {
    if (!html) {
      return;
    }
    var el = $(html);
    $('#sql-list').append(el);
    // Select first element and show.
    var id = el.first().children().first().data('id');
    _select(id);
    $('#sql-list').scrollTop($('#sql-items').height());
  }

  function updateSql(sql) {
    var a = _find(sql.id);
    a.find('.sql-name').text(sql.name);
    a.find('.sql-sentence').text(sql.sentence);
  }

  function getSqlList() {
    var list = [];
    $('#sql-list .sql-item').each(function(index, a) {
      list.push(_createModel($(a)));
    });
    return list;
  }
  
  function clear() {
    $('#sql-list').empty();
    _select(null);
    if (_onRemove) {
      $('#sql-list .sql-item').each(function(index, a) {
        _onRemove(_createModel($(a)));
      });
    }
  }
  
  function isEmpty() {
    return $('#sql-list .sql-item').length === 0;
  }
  
  function setOnChange(fn) {
    _onChange = fn;
  }

  function setOnRemove(fn) {
    _onRemove = fn;
  }
  
  // PRIVATE --------------------------------------------------

  function _select(id) {
    if (id == _selected) {
      return;
    }

    var a;

    // Deselect.
    if (_selected) {
      a = _find(_selected);
      a.children().first().removeClass('themed-background-info').addClass('themed-background');
      a = null;
    }

    // Select.
    _selected = id;
    if (_selected) {
      a = _find(_selected);
      a.children().first().removeClass('themed-background').addClass('themed-background-info');
    }

    // Fire event.
    _onChange(_createModel(a));
  }

  function _find(id) {
    return $('#sql-' + id);
  }

  function _createModel(a) {
    return a ? {
      id: a.data('id'),
      type: a.data('type'),
      tableName: a.data('table-name'),
      name: a.find('.sql-name').text(),
      sentence: a.find('.sql-sentence').text()
      } : null;
  }

  function _handleClick(e) {
    var el = $(e.target);

    // Find anchor.
    var a = null;
    var wk = el;
    while(wk.attr('id') !== 'sql-list') {
      if (wk.hasClass('sql-item')) {
        a = wk;
        break;
      }
      wk = wk.parent();
    }

    // Fire each events when functional buttons are clicked.
    if (el.hasClass('sql-item-save')) {
      var sql = _createModel($(a));
      NamingDialog.show(function(name) {
        sql.name = name;
        a.find('.sql-name').text(name);
        a = null;// Remove reference to dom in closure!
        _saveTemplate(sql);
      }, sql.name);
    } else if (el.hasClass('sql-item-remove')) {
      $(a).parent().remove();
      _select(null);
      _onRemove && _onRemove(_createModel($(a)));
    } else {
      // Otherwise, fire change event.
      a && _select($(a).data('id'));
    }
  }

  function _saveTemplate(sql) {
    $.ajax({
      url: '/workspace/saveTemplate',
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify(sql)
    }).done(function(res) {
      Base.growl('SQL saved as ' + sql.name);
    });
  }

  return {
    'init': init,
    'addContent': addContent,
    'updateSql': updateSql,
    'getSqlList': getSqlList,
    'clear': clear,
    'isEmpty': isEmpty,
    'setOnChange': setOnChange,
    'setOnRemove': setOnRemove,
  };
}();
