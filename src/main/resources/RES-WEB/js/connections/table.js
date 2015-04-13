/**
 * Table for list connection.
 * @public
 */
ConnectionTable = function() {
  $('#tbl').on('click', this._handleClick.bind(this));
};

/**
 * Event on Click remove button.
 * @public
 */
ConnectionTable.prototype.onRemoveClick = function(id, name){
  console.log('Remove button clicked. id = ' + id + ', name = ' + name);
};

/**
 * Set tbody html.
 * @public
 */
ConnectionTable.prototype.setBody = function(trList) {
  $('#tbl').find('tbody').empty().append(trList);
};

/**
 * @private
 */
ConnectionTable.prototype._table = null;

/**
 * @private
 */
ConnectionTable.prototype._handleClick = function(e) {
  var el = $(e.target);
  if (el.prop('tagName') == 'I') {
    el = el.parent();
  }

  if (el.hasClass('remove-btn') && el.data('id')) {
    el.prop("disabled", true);
    this.onRemoveClick(el.data('id'), el.data('name'));
  }
};