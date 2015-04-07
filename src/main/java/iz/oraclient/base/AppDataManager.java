package iz.oraclient.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save/Load data into the local.<br>
 * Using JAXB.
 *
 * @author izumi_j
 *
 */
public final class AppDataManager {
	private static final Logger logger = LoggerFactory.getLogger(AppDataManager.class);

	private static final String dirPath;
	static {
		final String appDataDir = System.getenv("AppData");
		if (StringUtils.isNotEmpty(appDataDir)) {
			dirPath = appDataDir;
		} else {
			dirPath = System.getProperty("user.home") + "/oraclient";
		}
		logger.info("Data directory is {}", dirPath);
	}

	/**
	 * @param data
	 */
	public static <D extends AppData> void save(D data) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(createFile(data.getClass()));

			JAXBContext context = JAXBContext.newInstance(data.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(data, out);

		} catch (JAXBException | IOException e) {
			logger.error("Failed to save.", e);
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * @param dataClass
	 * @return data
	 */
	@SuppressWarnings("unchecked")
	public static <D extends AppData> D load(Class<D> dataClass) {
		try {
			JAXBContext context = JAXBContext.newInstance(dataClass);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			File file = getFile(dataClass);
			return file != null ? (D)unmarshaller.unmarshal(createFile(dataClass)) : dataClass.newInstance();
		} catch (JAXBException | IOException | InstantiationException | IllegalAccessException e) {
			logger.error("Failed to load.", e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @param dataClass
	 */
	public static <D extends AppData> void delete(Class<D> dataClass) {
		try {
			createFile(dataClass).delete();
		} catch (IOException e) {
			logger.error("Failed to delete.", e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @param dataClass
	 * @return file for data
	 * @throws IOException
	 */
	private static final <D extends AppData> File createFile(Class<D> dataClass) throws IOException {
		File file = new File(mkdir(), dataClass.getName() + ".xml");
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * @param dataClass
	 * @return data file.
	 * @throws IOException
	 */
	private static final <D extends AppData> File getFile(Class<D> dataClass) throws IOException {
		File file = new File(mkdir(), dataClass.getName() + ".xml");
		return file.exists() ? file : null;
	}

	/**
	 * @return dir to save data
	 */
	private static final File mkdir() {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				logger.warn("Failed to mkdir! {}", dirPath);
			}
		}
		return dir;
	}
}
