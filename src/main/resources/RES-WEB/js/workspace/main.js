/**
 * Main script in workspace.
 */

var Workspace = function() {

  var _connectionId = null;

  var _resizeTimerId = null;

  // PUBLIC --------------------------------------------------

  function init() {
    _connectionId = $('#connection-id').val();

    SqlList.init();
    SqlEditor.init();
    ProcessorAdaptor.init(_connectionId);

    SqlList.setOnChange(_handleSqlListChange);
    SqlEditor.setOnChange(_handleSqlEditorChange);
    SqlEditor.setOnExecute(_handleSqlEditorExecute);

    $(window).on('resize orientationchange', _handleResize).resize();
    _setupSearch();
    $('#btn-free-sql').on('click', _handleFreeSqlClick);
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
      minimumInputLength: 3,
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
    });

    $('#search-sql').on('change', function(e) {
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
    ProcessorAdaptor.setSql(sql, _connectionId);
  }

  function _handleSqlEditorChange(sql) {
    SqlList.updateSql(sql);
    ProcessorAdaptor.updateSql(sql);
  }

  function _handleSqlEditorExecute(sql) {
    ProcessorAdaptor.executeSql(sql);
  }
  
  function _handleFreeSqlClick() {
    _getSqlItemView();
  }

  return {
    'init': init
  };
}();

$(function() {
  Workspace.init();
});