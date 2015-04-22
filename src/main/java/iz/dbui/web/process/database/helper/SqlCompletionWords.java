package iz.dbui.web.process.database.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public final class SqlCompletionWords {
	private SqlCompletionWords() {
	}

	public static final List<String> WORDS = Collections.unmodifiableList(Arrays.asList("SELECT"));

}
