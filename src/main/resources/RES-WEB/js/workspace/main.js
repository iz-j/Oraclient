/**
 * Main script in workspace.
 */

var Workspace = function() {

  var _connectionId = null;

  var _resizeTimerId = null;

  // PUBLIC --------------------------------------------------

  function init() {
    _connectionId = $('#connection-id').val();

    NamingDialog.init();
    CompositeDialog.init();
    TableInfoDialog.init(_connectionId);
    SqlList.init();
    SqlEditor.init(_connectionId);
    ProcessorAdaptor.init(_connectionId);

    SqlList.setOnChange(_handleSqlListChange);
    SqlList.setOnRemove(_handleSqlListRemove);
    SqlEditor.setOnChange(_handleSqlEditorChange);
    SqlEditor.setOnExecute(_handleSqlEditorExecute);

    $(window).on('resize orientationchange', _handleResize).resize();
    _setupSearch();

    $('#btn-free-sql').on('click', fireNewFreeSql);
    $('#btn-clear-sql').on('click', _handleClearSqlClick)
    $('#btn-save-composite').on('click', _handleSaveCompositeClick);
    $('#btn-load-composite').on('click', _handleLoadCompositeClick);
    $('#btn-clear-cache').on('click', _handleClearCacheClick);

    setTimeout(function() {
      $('#search-sql').select2('open');
    }, 200);
  }


  function fireNewFreeSql() {
    _getSqlItemView();
  }

  // PRIVATE --------------------------------------------------

  function _handleResize(e) {
    // Adjust contents height.
    _resizeTimerId && clearTimeout(_resizeTimerId);
    _resizeTimerId = setTimeout(function() {
      $('#page-content').css('height', $('#page-content').css('min-height'));
    }, 200);
  }

  function _setupSearch() {
    $('#search-sql').select2({
      minimumInputLength: 1,
      ajax: {
        url: '/workspace/sqlTemplates',
        dataType: 'json',
        quietMillis: 300,
        data: function (term, page) {
          return {
            connectionId: _connectionId,
            term: term,
            page: page
          };
        },
        results: function (data) {
          return {
            results: data
          };
        },
        cache: true
      },
      formatResult: function(obj, container, query) {
        var term = query.term.toUpperCase();
        var val = obj.name.replace(term, '<b>' + term + '</b>');
        if (obj.type == 'TABLE') {
          return '<span><i class="fa fa-table"/>&nbsp;' + val + '</span>';
        } else {
          return '<span><i class="fa fa-file-text-o"/>&nbsp;' + val + '</span>';
        }
      },
      formatSelection: function(obj) {
        return obj.name;
      },
      nextSearchTerm: function(obj, term) {
        return term;
      }
    }).on('change', function(e) {
      $('#search-sql').select2('val', '');
      _getSqlItemView(e.added);
    });
  }

  function _getSqlItemView(sql) {
    $.ajax({
      url: '/workspace/sqlItemView',
      type: 'post',
      dataType: 'html',
      contentType: 'application/json',
      data: sql ? JSON.stringify(sql) : null
    }).done(function(res) {
      SqlList.addContent(res);
    });
  }

  function _handleSqlListChange(sql) {
    SqlEditor.setSql(sql);
    ProcessorAdaptor.setSql(sql);
  }

  function _handleSqlEditorChange(sql) {
    SqlList.updateSql(sql);
    ProcessorAdaptor.updateSql(sql);
  }

  function _handleSqlEditorExecute(sql) {
    ProcessorAdaptor.executeSql(sql);
  }

  function _handleClearCacheClick() {
    $.ajax({
      url: '/workspace/clearCache',
      type: 'post'
    }).done(function(res) {
      Base.growl('Cache was cleared.');
    });
  }
  
  function _handleSaveCompositeClick() {
    var templates = SqlList.getSqlList();
    if (templates.length === 0) {
      Base.growl('There are no SQL to save.', 'warning');
      return;
    }
    
    NamingDialog.show(function(name) {
      var composite = {
        id: $('#composite-info').data('id'),
        name: name,
        templates: templates
      };
      
      $.ajax({
        url: '/workspace/saveComposite',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify(composite)
      }).done(function(res) {
        Base.growl('SQL list saved as ' + name);
        _setCompositeInfo(composite);
      });
    }, $('#composite-info').data('id') ? $('#composite-info').text() : '');
  }
  
  function _handleClearSqlClick() {
    SqlList.clear();
    _setCompositeInfo(null);
  }
  
  function _handleLoadCompositeClick() {
    CompositeDialog.show(function(composite) {
      $.ajax({
        url: '/workspace/sqlItemViews',
        type: 'post',
        dataType: 'html',
        contentType: 'application/json',
        data: JSON.stringify(composite.templates)
      }).done(function(res) {
        SqlList.clear();
        SqlList.addContent(res);
        _setCompositeInfo(composite);
      });
    });
  }

  function _handleSqlListRemove(sql) {
    if (SqlList.isEmpty()) {
      _setCompositeInfo(null);
    }
    ProcessorAdaptor.removeSql(sql);
  }
  
  function _setCompositeInfo(composite) {
    if (composite) {
      $('#composite-info').text(composite.name)
        .data('id', composite.id).removeClass('label-default').addClass('label-info');
    } else {
      $('#composite-info').text('Do as you please')
        .removeData('id').removeClass('label-info').addClass('label-default');
    }
  }
  
  return {
    'init': init,
    'fireNewFreeSql': fireNewFreeSql
  };
}();

$(function() {
  Workspace.init();

  // Shortcut key.
  $(document).on('keydown', function(e) {
    switch (e.keyCode) {
    case 120://F9
      SqlEditor.fireExecute();
      break;
    case 70://F
      (e.ctrlKey && e.shiftKey) && SqlEditor.fireFormat();
      break;
    case 81://Q
      (e.ctrlKey) && Workspace.fireNewFreeSql();
      break;
    default:
      break;
    }
  });
});