/**
 * Editor for SQL sentence.
 * @public
 */
SqlEditor = function() {
  $('#sql-editor')
    .prop('disabled', true)
    .on('input propertychange', this._handleTextChange.bind(this));
};

/**
 * @public
 */
SqlEditor.prototype.onChange = function(sql) {
  console.log('Sentence changed ->\n' + sql);
};

/**
 * @public
 */
SqlEditor.prototype.setSql = function(sql) {
  this._sql = sql;
  $('#sql-editor').val(sql.sentence).prop('disabled', false).focus();
};

/**
 * @public
 */
SqlEditor.prototype.getSql = function() {
  this._sql.sentence = $('#sql-editor').val();
  return this._sql;
};

/**
 * Current SQL model
 * @private
 */
SqlEditor.prototype._sql = null;


/**
 * @private
 */
SqlEditor.prototype._timerId = null;

/**
 * @private
 */
SqlEditor.prototype._handleTextChange = function(e) {
  if (this._timerId) {
    clearTimeout(this._timerId);
  }
  this._timerId = setTimeout(this._delayedTextChange.bind(this), 300);
};

/**
 * @private
 */
SqlEditor.prototype._delayedTextChange = function() {
  this._sql.sentence = $('#sql-editor').val();
  this.onChange(this._sql);
};