package iz.dbui.web.process.database.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author iz_j
 *
 */
public final class DateTimeValueParser {
	private DateTimeValueParser() {
	}

	private static final List<DateTimeFormatter> SUPPORTED_FORMATS = Collections.unmodifiableList(Arrays.asList(
			DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"), DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss"),
			DateTimeFormat.forPattern("yyyy-MM-dd"), DateTimeFormat.forPattern("yyyy/MM/dd")));

	public static DateTime parse(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		final MutableObject<DateTime> retval = new MutableObject<>();
		SUPPORTED_FORMATS.forEach(formatter -> {
			try {
				final DateTime parsed = formatter.parseDateTime(value);
				retval.setValue(parsed);
			} catch (IllegalArgumentException e) {
				// Ignore this.
			}
		});

		return retval.getValue();
	}

}
