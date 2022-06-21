package fr.metropolis.gestion.gui;

import fr.metropolis.gestion.api.db.Cards;
import fr.metropolis.gestion.api.db.Columns;
import fr.metropolis.gestion.api.db.Projects;
import fr.metropolis.gestion.api.db.Users;
import fr.metropolis.gestion.gui.component.Card;
import fr.metropolis.gestion.gui.component.Col;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	public MainController(){
		columnIDs = new ArrayList<>();
		users = new Users();
		projects = new Projects();
		columns = new Columns();
		cards = new Cards();
	}

	private int userID = 1;

	private Users users;
	private Projects projects;
	public Columns columns;
	public Cards cards;

	private Projects.Project actualProject;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<Projects.Project> lp = projects.getProjectByUser(userID);
		lp.forEach((project) -> {
			MenuItem m = new MenuItem(project.getName());
			menuProjects.getItems().add(m);
		});
		if(!lp.isEmpty()){
			actualProject = lp.get(0);
			List<Columns.Column> lc = columns.getByProject(actualProject.getId());
			for(int i = 0; i < lc.size(); ++i){
				Col col = new Col(lc.get(i).getName(), lc.get(i).getId() , this);
				col.setListener((observable, oldValue, newValue) -> {
					if(oldValue != newValue && !newValue){
						columns.updateName(col.getName(), col.getDbID());
					}
				});
				columnIDs.add(col);
				kanban.getChildren().add(kanban.getChildren().size() - 1, col);
			}
		}
	}

	@FXML
	private HBox kanban;

	@FXML
	public final List<Col> columnIDs;

	private Col findColByName(String title){
		Optional<Col> col = columnIDs.stream().filter((c) -> c.getName().equals(title)).findFirst();
		return col.orElse(null);
	}

	@FXML
	protected void onAddClick() {
		int i = 1;
		String title = "Titre ".concat(String.valueOf(i));
		while(findColByName(title) != null){
			++i;
			title = "Titre ".concat(String.valueOf(i));
		}
		int id = columns.add(title, actualProject.getId());
		Col newCol = new Col(title, id, this);
		newCol.setListener((observable, oldValue, newValue) -> {
			if(oldValue != newValue && !newValue){
				columns.updateName(newCol.getName(), newCol.getDbID());
			}
		});
		columnIDs.add(newCol);
		kanban.getChildren().add(kanban.getChildren().size() - 1, newCol);
	}

	@FXML
	private Menu menuProjects;

	@FXML
	public void onAddCardClick(MouseEvent event){
		ColumnID cID = (ColumnID)((Button)event.getSource()).getUserData();
		VBox vBox = (VBox) ((Button)event.getSource()).getParent();
		Cards.Card card = new Cards.Card();
		card.setTitle("Title");
		card.setDescription("Description");
		card.setStart(null);
		card.setEnd(null);
		card.setNext(-1);
		card.setColumn(columns.getById(cID.getId()));
		cards.add(card);
		new Card(cID, vBox, this, card);
	}

	public void removeColumn(MouseEvent event){
		int id = ((ColumnID)((Button)event.getSource()).getUserData()).getId();
		columnIDs.stream().filter((col -> col.getDbID() == id)).findFirst().ifPresent(col -> {
			columnIDs.remove(col);
			kanban.getChildren().remove(col);
			columns.delete(id);
		});
	}
}