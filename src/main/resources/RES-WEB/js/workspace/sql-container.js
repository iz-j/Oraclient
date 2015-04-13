/**
 * Container for SQL templates.
 * @public
 */
SqlContainer = function() {
  $('#sql-items').on('click', this._handleClick.bind(this));
};

/**
 * @public
 */
SqlContainer.prototype.onChange = function(data) {
  console.log(data);
};

/**
 * @public
 */
SqlContainer.prototype.addSqlNode = function(html) {
  if (!html) {
    return;
  }
  var el = $(html);
  $('#sql-items').append(el);
  // Select first element and show.
  this.select(el.first().children().first().attr('id'));
  $('#sql-items').scrollTop($('#sql-items').height());
};

/**
 * @public
 */
SqlContainer.prototype.select = function(id) {
  if (id == this._selected) {
    return;
  }
  var a;

  // Deselect.
  if (this._selected) {
    a = this._find(this._selected);
    a.children().first().removeClass('themed-background-info').addClass('themed-background');
  }

  // Select.
  this._selected = id;
  var a = this._find(id);
  a.children().first().removeClass('themed-background').addClass('themed-background-info');

  // Fire event.
  this.onChange(this._createModelOf(a));
};

/**
 * @public
 * @param sql model
 */
SqlContainer.prototype.update = function(sql) {
  var a = this._find(sql.id);
  a.data('name', sql.name).data('sentence', sql.sentence);
  a.find('.sql-name').text(sql.name);
  a.find('.sql-sentence').text(sql.sentence);
};

/**
 * @private
 */
SqlContainer.prototype._selected = null;

/**
 * @private
 * @returns model of sql template {id, type, name, sentence}
 */
SqlContainer.prototype._createModelOf = function(a) {
  return {
    id: $(a).attr('id'),
    type: $(a).data('type'),
    name: $(a).data('name'),
    sentence: $(a).data('sentence')
  };
};

/**
 * @private
 * @returns anchor element
 */
SqlContainer.prototype._find = function(id) {
  return $('#' + id);
};

/**
 * @private
 */
SqlContainer.prototype._handleClick = function(e) {
  var el = $(e.target);

  // Find anchor.
  var a = null;
  if (el.hasClass('sql-item-root')) {
    a = el.children().first();
  } else {
    while(el) {
      if (el.hasClass('sql-item')) {
        a = el;
        break;
      }
      el = el.parent();
    }
  }

  // Fire each events when functional buttons are clicked.

  // Otherwise, fire change event.
  this.select($(a).attr('id'));
};