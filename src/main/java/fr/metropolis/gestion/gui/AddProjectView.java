package fr.metropolis.gestion.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddProjectView {

	@FXML
	TextField nom;

	@FXML
	public void addProject(){
		System.out.println(nom);
	}
}
