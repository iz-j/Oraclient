package iz.oraclient.fx;

import iz.oraclient.base.AppDataManager;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for FX.
 *
 * @author izumi_j
 *
 */
public class MainController implements Initializable {
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private AnchorPane root;
	@FXML
	private TextField txtPort;
	@FXML
	private Button btnServer;
	@FXML
	private ImageView imgStatus;
	@FXML
	private CheckBox cbAutoStart;
	@FXML
	private TextArea txtLog;
	@FXML
	private Button btnBrowser;

	private static final Image IMG_RUNNING = new Image(ClassLoader.getSystemResourceAsStream("RES-FX/running.gif"));
	private static final Image IMG_READY = new Image(ClassLoader.getSystemResourceAsStream("RES-FX/ready.png"));

	private static final String LOG_READY = "Ready to start.";
	private static final String LOG_RUNNING = "Service is running.";
	private static final String SERVER_START = "Start";
	private static final String SERVER_STOP = "Stop";

	@Override
	public void initialize(URL paramURL, ResourceBundle paramResourceBundle) {
		logger.trace("#initialize.");

		toReady();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					lazyInitialize();
				} catch (Throwable e) {
					logger.error("Some error occurred!", e);
				}
			}
		});
	}

	private void lazyInitialize() {
		// Save and load config.
		root.getScene().getWindow().setOnCloseRequest(event -> {
			saveConfig();
		});
		loadConfig();

		btnServer.setOnAction(event -> {
			if (StringUtils.equals(btnServer.getText(), SERVER_START)) {
				// Start server.
				toRunning();
			} else {
				// Stop server.
				toReady();
			}
		});
	}

	private void saveConfig() {
		if (StringUtils.isNumeric(txtPort.getText())) {
			AppDataManager.save(new LaunchConfig(Integer.parseInt(txtPort.getText()), cbAutoStart.isSelected()));
		}
	}

	private void loadConfig() {
		final LaunchConfig cfg = AppDataManager.load(LaunchConfig.class);
		txtPort.setText(String.valueOf(cfg.port));
		cbAutoStart.setSelected(cfg.autoStart);
	}

	private void toReady() {
		putLog(LOG_READY);
		btnServer.setText(SERVER_START);
		imgStatus.setImage(IMG_READY);
		txtPort.setDisable(false);
		cbAutoStart.setDisable(false);
	}

	private void toRunning() {
		putLog(LOG_RUNNING);
		btnServer.setText(SERVER_STOP);
		imgStatus.setImage(IMG_RUNNING);
		txtPort.setDisable(true);
		cbAutoStart.setDisable(true);
	}

	private void putLog(String msg) {
		txtLog.appendText(LocalTime.now().toString("HH:mm:ss ") + msg + System.lineSeparator());
	}

}
