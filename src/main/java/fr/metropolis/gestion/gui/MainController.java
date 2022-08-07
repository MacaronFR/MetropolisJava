package fr.metropolis.gestion.gui;

import fr.metropolis.gestion.api.db.Cards;
import fr.metropolis.gestion.api.db.Columns;
import fr.metropolis.gestion.api.db.Projects;
import fr.metropolis.gestion.gui.component.Card;
import fr.metropolis.gestion.gui.component.Col;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	public MainController(){
		columnIDs = new ArrayList<>();
		projects = new Projects();
		columns = new Columns();
		cards = new Cards();
	}

	private final int userID = 1;
	private final Projects projects;
	public Columns columns;
	public Cards cards;

	private Projects.Project actualProject;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<Projects.Project> lp = projects.getProjectByUser(userID);
		initProjectsList(lp);
		if(!lp.isEmpty()){
			initProject(lp.get(0));
		}
	}

	private void initProjectsList(List<Projects.Project> list){
		menuProjects.getItems().clear();
		list.forEach((project) -> {
			MenuItem m = new MenuItem(project.getName());
			menuProjects.getItems().add(m);
			m.setUserData(project);
			m.setOnAction((event) -> {
				MenuItem obj = (MenuItem) event.getSource();
				Projects.Project p = (Projects.Project) obj.getUserData();
				initProject(p);
			});
		});
	}

	private void initProject(Projects.Project project){
		actualProject = project;
		kanban.getChildren().clear();
		List<Columns.Column> lc = columns.getByProject(actualProject.getId());
		for (Columns.Column column : lc) {
			Col col = new Col(column.getName(), column.getId(), this);
			col.setListener((observable, oldValue, newValue) -> {
				if (oldValue != newValue && !newValue) {
					columns.updateName(col.getName(), col.getDbID());
				}
			});
			columnIDs.add(col);
			kanban.getChildren().add(col);
		}
	}

	@FXML
	private HBox kanban;

	@FXML
	public final List<Col> columnIDs;

	@FXML
	private VBox root;

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
		kanban.getChildren().add(newCol);
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

	@FXML
	public void addProject() throws IOException {
		Stage newStage = new Stage();
		Parent root = FXMLLoader.load(AddProjectView.class.getResource("add-project-view.fxml"));
		newStage.setScene(new Scene(root));
		newStage.setTitle("Ajouter un projet");
		newStage.initModality(Modality.WINDOW_MODAL);
		newStage.initOwner(this.root.getScene().getWindow());
		newStage.show();
	}
}