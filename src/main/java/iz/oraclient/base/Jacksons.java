package iz.oraclient.base;

import java.io.IOException;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 *
 * @author izumi_j
 *
 */
public final class Jacksons {
	private Jacksons() {
	}

	private static final ObjectMapper MAPPER;
	static {
		MAPPER = new ObjectMapper();
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);

		MAPPER.setTimeZone(TimeZone.getDefault());

		MAPPER.registerModule(new JodaModule());
	}

	/**
	 * @return mapper
	 */
	public static ObjectMapper getObjectMapper() {
		return MAPPER;
	}

	public static String toJson(Object o) {
		try {
			return getObjectMapper().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> T toObject(String s, Class<T> type) {
		try {
			return getObjectMapper().readValue(s, type);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
