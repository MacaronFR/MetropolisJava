package fr.metropolis.gestion.gui;

import fr.metropolis.gestion.api.db.Projects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddProjectView {

	private final Projects projects = new Projects();

	@FXML
	public TextField nom;

	public int idUser;

	@FXML
	private AnchorPane root;

	@FXML
	private Label error;

	@FXML
	public void addProject(){
		if(projects.addProject(nom.getText(), idUser)){
			((Stage)root.getScene().getWindow()).close();
		}else{
			error.setText("Erreur lors de l'ajout du projet");
		}
	}
}
