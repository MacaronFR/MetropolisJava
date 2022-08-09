package fr.metropolis.gestion.gui;

import fr.metropolis.gestion.Utils;
import fr.metropolis.gestion.api.db.Users;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView {

	@FXML
	private Button login;

	@FXML
	private TextField user;

	@FXML
	private TextField mdp;

	@FXML
	private Label loginError;

	@FXML
	private Label mdpError;

	@FXML
	private AnchorPane root;

	private final Users users = new Users();

	@FXML
	public void onLoginClick() throws IOException {
		Users.User u = users.getByName(user.getText());
		if(u == null){
			loginError.setText("Nom d'utilisateur inconnu");
			user.setText("");
			mdp.setText("");
			return;
		}
		String hashedPassword = u.getPassword();
		String salt = u.getSalt();
		String hash = Utils.hashPassword(mdp.getText(), salt);
		if(hash.equals(hashedPassword)){
			FXMLLoader fxmlLoader = new FXMLLoader(GestionApplication.class.getResource("main-view.fxml"));
			Stage stage = (Stage)root.getScene().getWindow();
			Scene main = new Scene(fxmlLoader.load(), 500, 500);
			MainController controller = fxmlLoader.getController();
			controller.userID = u.getId();
			stage.setScene(main);
		}else{
			mdpError.setText("Mot de passe incorrect");
			mdp.setText("");
		}
	}

}
