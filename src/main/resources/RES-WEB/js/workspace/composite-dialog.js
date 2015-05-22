var CompositeDialog = function() {

  var _onOk = null;
  var _composites = null;
  
  var _filterTimer = null;
  
  // PUBLIC --------------------------------------------------

  function init() {
    $('#composite-dialog').on('shown.bs.modal', _loadComposites);
    $('#composite-list').on('click', _handleListClick);
    $('#composite-filter').on('input propertychange', _handleFilterChange);
  }

  function show(onOk) {
    _onOk = onOk;
    $('#composite-list > a').remove();
    $('#composite-list > p').hide();
    $('#composite-dialog').modal('show');
  }

  // PRIVATE --------------------------------------------------

  function _loadComposites() {
    $.ajax({
      url: '/workspace/sqlComposites',
      dataType: 'json'
    }).done(function(res) {
      _composites = res;
      if (!_composites || _composites.length === 0) {
        $('#composite-list > p').show();
      } else {
        $.each(_composites, function(index, composite) {
          var a = $('<a class="list-group-item" href="javascript:void(0)">' + composite.name + '</a>');
          a.data('id', composite.id);
          $('#composite-list').append(a);
        });
      }
      $('#composite-filter').val('').focus();
    });
  }

  function _handleListClick(e) {
    var el = $(e.target);
    var id = el.data('id');
    if (!id) {
      return;
    }
    
    var composite = null;
    $.each(_composites, function(index, c) {
      if (c.id === id) {
        composite = c;
        return false;
      }
    });
    if (!composite) {
      return;//Should not reach here!
    }
    
    $('#composite-dialog').modal('hide');
    _onOk && _onOk(composite);
    _onOk = null;
  }
  
  function _handleFilterChange(e) {
    _filterTimer && clearTimeout(_filterTimer);
    _filterTimer = setTimeout(_filterComposites(), 200);
  }
  
  function _filterComposites() {
    var term = $('#composite-filter').val();
    var cnt = 0;
    $('#composite-list > a').each(function(index, a) {
      var show = (!term);
      if (term) {
        show = $(a).text().indexOf(term) > -1;
      }
      if (show) {
        $(a).show();
        ++cnt;
      } else {
        $(a).hide();
      }
    });
    
    if (cnt === 0) {
      $('#composite-list > p').show();
    } else {
      $('#composite-list > p').hide();
    }
  }
  
  return {
    'init': init,
    'show': show
  };
}();
