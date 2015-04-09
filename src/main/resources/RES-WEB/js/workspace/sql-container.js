/**
 * Container for SQL templates.
 * @public
 */
SqlContainer = function() {
  this._root = $('#sql-container');
};

/**
 * @public
 */
SqlContainer.prototype.addSqlNode = function(el) {
  this._root.append(el);
};

/**
 * @private
 */
SqlContainer.prototype._root = null;