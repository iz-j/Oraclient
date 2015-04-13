var Ws = {};

Ws.connectionId = null;
Ws.sqlContainer = null;
Ws.sqlEditor = null;
Ws.adaptor = null;

/**
 * Adjust layout when window is resized.
 */
Ws.adjustContentsHeight = function() {
  $('#page-content').css('height', $('#page-content').css('min-height'));
};

/**
 * Initialize tables and templates search via Select2.
 */
Ws.initSearchSql = function() {
  // Search tables and sql templates.
  $('#search-sql').select2({
    minimumInputLength: 3,
    ajax: {
      url: '/workspace/sqlTemplates',
      dataType: 'json',
      quietMillis: 300,
      data: function (term, page) {
        return {
          connectionId: Ws.connectionId,
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

  // When sql template is selected.
  $('#search-sql').on('change', function(e) {
    $('#search-sql').select2('val', '');
    $.ajax({
      url: '/workspace/sqlItemView',
      type: 'post',
      dataType: 'html',
      contentType: 'application/json',
      data: JSON.stringify(e.added)
    }).done(function(res) {
      Ws.sqlContainer.addSqlNode(res);
    });
  });
};

/**
 * Event when SQL is selected in SqlContainer.
 */
Ws.onSqlSelected = function(sql) {
  Ws.sqlEditor.setSql(sql);
};

/**
 * Event when SQL sentence was changed
 */
Ws.onSqlChanged = function(sql) {
  Ws.sqlContainer.update(sql);
};

/**
 * OnReady.
 */
$(function() {
  Ws.connectionId = $('#connection-id').val();
  Ws.sqlContainer = new SqlContainer();
  Ws.sqlEditor = new SqlEditor();
  Ws.adaptor = new ProcessorAdaptor();

  $(window).on('resize orientationchange', Ws.adjustContentsHeight).resize();

  Ws.initSearchSql();

  Ws.sqlContainer.onChange = Ws.onSqlSelected;
  Ws.sqlEditor.onChange = Ws.onSqlChanged;
});