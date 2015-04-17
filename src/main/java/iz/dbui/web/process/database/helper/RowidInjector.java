package iz.dbui.web.process.database.helper;

import org.apache.commons.lang3.StringUtils;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.ColumnReference;
import com.foundationdb.sql.parser.NodeTypes;
import com.foundationdb.sql.parser.QueryTreeNode;
import com.foundationdb.sql.parser.ResultColumn;
import com.foundationdb.sql.parser.ResultColumnList;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.SelectNode;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;
import com.foundationdb.sql.unparser.NodeToString;

/**
 *
 * @author izumi_j
 *
 */
public final class RowidInjector {

	private RowidInjector() {
	}

	public static String inject(String sentence) {
		final SQLParser parser = new SQLParser();

		StatementNode stmt = null;
		try {
			stmt = parser.parseStatement(sentence);
			if (!StringUtils.equalsIgnoreCase("SELECT", stmt.statementToString())) {
				throw new IllegalArgumentException("SQL must be SELECT statement!");
			}

			final SelectColumnsExtractor extractor = new SelectColumnsExtractor();
			stmt.accept(extractor);

			injectRowid(extractor.columnList);

			return new NodeToString().toString(stmt);

		} catch (StandardException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void injectRowid(ResultColumnList columnList) {
		// XXX Is the correct usage???
		try {
			final ResultColumn rowid = new ResultColumn();
			rowid.setNodeType(NodeTypes.RESULT_COLUMN);
			final ColumnReference cr = new ColumnReference();
			cr.setNodeType(NodeTypes.COLUMN_REFERENCE);
			cr.init("rowid", null);

			rowid.init("rowid$", cr);

			columnList.add(0, rowid);

		} catch (StandardException e) {
			throw new IllegalStateException(e);
		}
	}

	private static class SelectColumnsExtractor implements Visitor {

		ResultColumnList columnList;

		@Override
		public Visitable visit(Visitable node) throws StandardException {
			final QueryTreeNode qn = (QueryTreeNode)node;

			switch (qn.getNodeType()) {
			case NodeTypes.SELECT_NODE:
				final SelectNode sn = (SelectNode)node;
				columnList = sn.getResultColumns();
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
			return columnList != null;
		}

		@Override
		public boolean skipChildren(Visitable node) throws StandardException {
			return false;
		}
	}
}
