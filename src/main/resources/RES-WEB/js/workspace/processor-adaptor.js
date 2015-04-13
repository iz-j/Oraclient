/**
 * Processor Adaptor.
 * @public
 */
ProcessorAdaptor = function() {
  $('#btn-execute').on('click', this._handleExecute.bind(this));
};

/**
 * @public
 */
ProcessorAdaptor.prototype.setSql = function(sql, connectionId) {
  var ifrm = null;
  // Hide current.
  if (this._sql) {
    ifrm = this._findIframe(this._sql.id);
    if (ifrm) {
      $(ifrm).hide();
    }
  }

  // Show new.
  this._sql = sql;
  var ifrm = this._findIframe(sql.id);
  if (!ifrm) {
    $('#processor-root')
      .append('<iframe src="/processor/' + connectionId + '" id="processor-' + sql.id + '" />');
    ifrm = this._findIframe(sql.id);
  }
  $(ifrm).show();
};

/**
 * @public
 */
ProcessorAdaptor.prototype.update = function(sql) {
  if (this._sql.id == sql.id) {
    this._sql.sentence = sql.sentence;
    return;
  }
};

/**
 * @public
 */
ProcessorAdaptor.prototype.removeSql = function(sql) {
  delete this._processors[sql.id];
  this._find(sql.id).remove();
};

/**
 * Current Sql model.
 * @private
 */
ProcessorAdaptor.prototype._sql = null;

/**
 * @private
 * @returns iframe
 */
ProcessorAdaptor.prototype._findIframe = function(sqlId) {
  return $('#processor-' + sqlId)[0];
};

/**
 * @private
 * @returns element in iframe
 */
ProcessorAdaptor.prototype._findInIFrame = function(id) {
  if (!this._sql) {
    return null;
  }
  var ifrm = this._findIframe(this._sql.id);
  return ifrm ? $('#' + id, ifrm.contentWindow.document) : null;
};

/**
 * @private
 */
ProcessorAdaptor.prototype._callIFrameFunc = function(funcName, opt_arg) {
  if (!this._sql) {
    return null;
  }
  var ifrm = this._findIframe(this._sql.id);
  return ifrm ? ifrm.contentWindow[funcName](opt_arg) : null;
};

/**
 * @private
 */
ProcessorAdaptor.prototype._handleExecute = function() {
  this._callIFrameFunc('proc_execute', this._sql);
};