/**
 * SQL list.
 */

var SqlList = function() {

  var _onChange = null;
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

  function setOnChange(fn) {
    _onChange = fn;
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
    if (wk.hasClass('sql-item-root')) {
      a = wk.children().first();
    } else {
      while(wk) {
        if (wk.hasClass('sql-item')) {
          a = wk;
          break;
        }
        wk = wk.parent();
      }
    }

    // Fire each events when functional buttons are clicked.
    if (el.hasClass('sql-item-save')) {
      NamingDialog.show(function(name) {
        var sql = _createModel($(a));
        sql.name = name;
        a.find('.sql-name').text(name);
        a = null;// Remove reference to dom in closure!
        _saveTemplate(sql);
      });
    } else if (el.hasClass('sql-item-remove')) {
      $(a).parent().remove();
      _select(null);
    } else {
      // Otherwise, fire change event.
      _select($(a).data('id'));
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
    'setOnChange': setOnChange
  };
}();
