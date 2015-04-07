/**
 * Table for list connection.
 * @public
 */
ConnectionTable = function() {
  this._table = $('#tbl');
};

/**
 * Set tbody html.
 * @public
 */
ConnectionTable.prototype.setBody = function(trList) {
  this._table.find('tbody').empty().append(trList);
};

/**
 * @private
 */
ConnectionTable.prototype._table = null;
