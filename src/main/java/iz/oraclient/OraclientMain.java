package iz.oraclient;

import iz.oraclient.fx.MainController;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Launch JavaFX application.
 *
 * @author izumi_j
 *
 */
public final class OraclientMain extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("--- Start ---");

		Application.launch(args);

		System.out.println("--- End ---");
	}

	@Override
	public void start(Stage stage) throws IOException {
		final FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("RES-FX/Main.fxml"));
		final Pane root = fxmlLoader.load();

		final MainController mainController = (MainController)fxmlLoader.getController();
		mainController.setStage(stage);

		stage.setTitle("Oraclient");
		stage.setScene(new Scene(root));
		stage.setResizable(false);
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("RES-FX/hekeke32.png")));
		stage.show();

		// TODO Move into system tray when window is minimized.
	}
}
