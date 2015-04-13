var Proc = {};

Proc.resizeTimerId = null;
Proc.connectionId = document.getElementById('connection-id').value;

Proc.ht = new Handsontable(document.getElementById('data-table'), {
  data: Handsontable.helper.createSpreadsheetData(100, 100),
  rowHeaders: true,
  colHeaders: true,
  contextMenu: true
});

/**
 * Resize handsontable size.
 */
Handsontable.Dom.addEvent(window, 'resize', function() {
  clearTimeout(Proc.resizeTimerId);
  Proc.resizeTimerId = setTimeout(function() {
    var container = document.getElementById('data-table');
    var w = Handsontable.Dom.innerWidth(document.body);
    var h = Handsontable.Dom.innerHeight(document.body);
    container.style.width = w + 'px';
    container.style.height = h + 'px';
    Proc.ht.updateSettings({
      width: w,
      height: h
    });
  }, 100);
});

/**
 * Procedure to execute SQL.
 * @param sql
 */
function proc_execute(sql) {
  $.ajax({
    url: '/processor/execute/' + Proc.connectionId,
    type: 'post',
    contentType: 'application/json',
    data: JSON.stringify(sql),
    dataType: 'json'
  }).done(function(res) {
    console.log(res);
    Proc.ht.loadData(res.records);
    Proc.ht.render();
  });
};

// Adjust size.
window.dispatchEvent(new Event('resize'));