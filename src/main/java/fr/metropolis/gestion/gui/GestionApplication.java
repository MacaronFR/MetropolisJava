package fr.metropolis.gestion.gui;

import fr.metropolis.gestion.PluginLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class GestionApplication extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GestionApplication.class.getResource("login-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Gestion de projet");
		stage.setScene(scene);
		stage.show();
		PluginLoader pl = new PluginLoader();
		pl.loadPlugin();
	}

	public static void main(String[] args) {
		launch();
	}
}