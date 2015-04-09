$(function() {
  var connectionId = $('#connection-id').val();
  var sqlContainer = new SqlContainer();

  // Search sql templates.
  $('#search-sql').select2({
    minimumInputLength: 3,
    ajax: {
      url: '/workspace/sqlTemplates',
      dataType: 'json',
      quietMillis: 300,
      data: function (term, page) {
        return {
          connectionId: connectionId,
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
    escapeMarkup: function(m) { return m; }
  });

  $('#search-sql').on('change', function(e) {
    $('#search-sql').select2('val', '');
    $.ajax({
      url: '/workspace/sqlItemView',
      type: 'post',
      dataType: 'html',
      contentType: 'application/json',
      data: JSON.stringify(e.added)
    }).done(function(res) {
      sqlContainer.addSqlNode(res);
    });
  });
});