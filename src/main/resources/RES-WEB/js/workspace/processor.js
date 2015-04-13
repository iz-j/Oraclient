/**
 * Processor.
 * @public
 */
Processor = function() {
  this._ht = $('#data-table').handsontable({
    data: Handsontable.helper.createSpreadsheetData(100, 100),
    rowHeaders: true,
    colHeaders: true,
    contextMenu: true
  }).handsontable('getInstance');
};
