package fr.metropolis.gestion.gui.component;

import fr.metropolis.gestion.api.db.Cards;
import fr.metropolis.gestion.gui.ColumnID;
import fr.metropolis.gestion.gui.MainController;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public final class Col extends ScrollPane {

	private final TextField name;
	private final int id;
	private final MainController ctrl;
	private final VBox cardList;

	public Col(String colName, int id, MainController ctrl){
		super();
		this.id = id;
		this.ctrl = ctrl;
		AnchorPane pane = new AnchorPane();
		VBox list = new VBox();
		HBox title = new HBox();
		name = new TextField(colName);
		Button delCol = new Button("X");
		Button addCard = new Button("Ajouter carte");
		ColumnID colID = new ColumnID();
		colID.setId(id);
		addCard.setUserData(colID);
		addCard.setOnMouseClicked(new AddCardHandler(this.ctrl));
		delCol.setUserData(colID);
		delCol.setOnMouseClicked(new RemoveColumnHandler(this.ctrl));
		title.getChildren().add(name);
		title.getChildren().add(delCol);
		list.getChildren().add(title);
		list.getChildren().add(addCard);
		pane.getChildren().add(list);
		setContent(pane);
		setPrefWidth(300);
		cardList = list;
		getCards();
	}

	public void getCards(){
		List<Cards.Card> cards = ctrl.cards.getByColumn(id);
		cards.forEach(card -> {
			ColumnID colID = new ColumnID();
			colID.setId(id);
			new Card(colID, this.cardList, ctrl, card);
		});
	}

	public String getName() {
		return name.getText();
	}

	public int getDbID(){
		return id;
	}

	public void setListener(ChangeListener<Boolean> listener){
		name.focusedProperty().addListener(listener);
	}

	private static class AddCardHandler implements EventHandler<MouseEvent>{

		AddCardHandler(MainController ctrl){
			this.ctrl = ctrl;
		}

		MainController ctrl;
		@Override
		public void handle(MouseEvent event) {
			ctrl.onAddCardClick(event);
		}
	}

	private static class RemoveColumnHandler implements EventHandler<MouseEvent> {

		RemoveColumnHandler(MainController ctrl){
			this.ctrl = ctrl;
		}

		MainController ctrl;

		@Override
		public void handle(MouseEvent event) {
			ctrl.removeColumn(event);
		}
	}
}
