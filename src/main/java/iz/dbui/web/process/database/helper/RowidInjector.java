package iz.dbui.web.process.database.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.AllResultColumn;
import com.foundationdb.sql.parser.ColumnReference;
import com.foundationdb.sql.parser.FromBaseTable;
import com.foundationdb.sql.parser.NodeTypes;
import com.foundationdb.sql.parser.QueryTreeNode;
import com.foundationdb.sql.parser.ResultColumn;
import com.foundationdb.sql.parser.ResultColumnList;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.SelectNode;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.parser.TableName;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;
import com.foundationdb.sql.unparser.NodeToString;

/**
 *
 * @author izumi_j
 *
 */
@Deprecated
public final class RowidInjector {
	private static final Logger logger = LoggerFactory.getLogger(RowidInjector.class);

	private RowidInjector() {
	}

	public static String inject(String sentence) {
		logger.trace("#inject");
		final SQLParser parser = new SQLParser();

		StatementNode stmt = null;
		try {
			stmt = parser.parseStatement(sentence);
			if (!StringUtils.equalsIgnoreCase("SELECT", stmt.statementToString())) {
				throw new IllegalArgumentException("SQL must be SELECT statement!");
			}

			final ElementHolder holder = new ElementHolder();
			stmt.accept(holder);

			injectRowid(holder);

			return new NodeToStringEx().toString(stmt);

		} catch (StandardException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void injectRowid(ElementHolder holder) {
		// XXX Is the correct usage???
		try {
			// Add rowid.
			final ResultColumn rowid = new ResultColumn();
			rowid.setNodeType(NodeTypes.RESULT_COLUMN);
			final ColumnReference cr = new ColumnReference();
			cr.setNodeType(NodeTypes.COLUMN_REFERENCE);
			cr.init("rowid", null);

			rowid.init("rowid_", cr);

			holder.columnList.add(0, rowid);

			// If all result column with no alias exists, add alias.
			if (holder.allColumn != null && !holder.allColumn.isHavingAlias()) {
				if (holder.baseTable.getCorrelationName() != null) {
					final TableName tn = new TableName();
					tn.init(null, holder.baseTable.getCorrelationName());
					holder.allColumn.init(tn);
				} else {
					holder.allColumn.init(holder.baseTable.getTableName());
				}
			}

		} catch (StandardException e) {
			throw new IllegalStateException(e);
		}
	}

	private static class ElementHolder implements Visitor {

		ResultColumnList columnList;
		AllResultColumn allColumn;
		FromBaseTable baseTable;

		@Override
		public Visitable visit(Visitable node) throws StandardException {
			final QueryTreeNode qn = (QueryTreeNode)node;

			switch (qn.getNodeType()) {
			case NodeTypes.SELECT_NODE:
				final SelectNode sn = (SelectNode)node;
				columnList = sn.getResultColumns();
				break;
			case NodeTypes.ALL_RESULT_COLUMN:
				allColumn = (AllResultColumn)node;
				logger.trace("AllResultColumn found.");
				break;
			case NodeTypes.FROM_BASE_TABLE:
				baseTable = (FromBaseTable)node;
				logger.trace("FromBaseTable =  {}.", baseTable.getTableName().getFullTableName());
				break;
			default:
				break;
			}

			return node;
		}

		@Override
		public boolean visitChildrenFirst(Visitable node) {
			return false;
		}

		@Override
		public boolean stopTraversal() {
			return false;
		}

		@Override
		public boolean skipChildren(Visitable node) throws StandardException {
			return false;
		}
	}

	private static class NodeToStringEx extends NodeToString {
		@Override
		protected String fromBaseTable(FromBaseTable node) throws StandardException {
			String tn = toString(node.getOrigTableName());
			String n = maybeQuote(node.getCorrelationName());
			if (n == null) {
				return tn;
			} else {
				return tn + " " + n;
			}
		}
	}
}
