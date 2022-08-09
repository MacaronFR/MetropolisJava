package fr.metropolis.gestion.gui.component;

import fr.metropolis.gestion.api.db.Cards;
import fr.metropolis.gestion.gui.ColumnID;
import fr.metropolis.gestion.gui.Draggable;
import fr.metropolis.gestion.gui.MainController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;

public class Card extends VBox {

	private final MainController ctrl;

	private final Cards.Card dbCard;

	private final TextField name;

	private final TextArea description;

	public Card(ColumnID colID, VBox col, MainController ctrl, Cards.Card dbCard) {
		setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
		this.dbCard = dbCard;
		this.ctrl = ctrl;
		HBox head = new HBox();
		name = new TextField(dbCard.getTitle());
		Button close = new Button("X");
		Label warning = new Label();
		description = new TextArea(dbCard.getDescription());
		VBox dateContainer = new VBox();
		Label startLabel = new Label("Début");
		DatePicker start = new DatePicker();
		if(dbCard.getStart() != null){
			start.setValue(dbCard.getStart().toLocalDate());
		}
		Label endLabel = new Label("Fin");
		DatePicker end = new DatePicker();
		if(dbCard.getEnd() != null){
			end.setValue(dbCard.getEnd().toLocalDate());
		}
		setCellStartDate(end, start, warning);
		setCellEndDate(end, start);
		HBox move = new HBox();
		Button left = new Button("<");
		Button right = new Button(">");
		this.getChildren().add(head);
		this.setStyle("-fx-background-color: white");
		head.getChildren().add(name);
		head.getChildren().add(close);
		head.getChildren().add(warning);
		warning.setStyle("-fx-text-fill: red");
		close.addEventHandler(MouseEvent.MOUSE_CLICKED, new RemoveCardHandler());
		startLabel.setLabelFor(start);
		start.addEventHandler(ActionEvent.ACTION, new ChangeStartDate());
		endLabel.setLabelFor(end);
		end.addEventHandler(ActionEvent.ACTION, new ChangeEndDate());
		dateContainer.getChildren().add(startLabel);
		dateContainer.getChildren().add(start);
		dateContainer.getChildren().add(endLabel);
		dateContainer.getChildren().add(end);
		this.getChildren().add(description);
		this.getChildren().add(dateContainer);
		this.getChildren().add(move);
		col.getChildren().add(col.getChildren().size() - 1, this);
		move.getChildren().add(left);

		description.setMaxWidth(290);

		left.addEventHandler(MouseEvent.MOUSE_CLICKED, (event1 -> {
			int id = colID.getId();
			if (!checkFirstColumn(id)) {
				Cards.Card tmpCard = ctrl.cards.getByColumnAndNext(id, this.dbCard.getId());
				if(tmpCard != null) {
					tmpCard.setNext(this.dbCard.getNext());
					ctrl.cards.update(tmpCard);
				}
				colID.setId(moveLeft(id, this));
				tmpCard = ctrl.cards.getByColumnAndNext(colID.getId(), -1);
				dbCard.setNext(-1);
				this.dbCard.setColumn(ctrl.columns.getById(colID.getId()));
				ctrl.cards.update(dbCard);
				if(tmpCard != null) {
					tmpCard.setNext(this.dbCard.getId());
					ctrl.cards.update(tmpCard);
				}
			}
		}));
		move.getChildren().add(right);
		right.setUserData(colID);
		right.addEventHandler(MouseEvent.MOUSE_CLICKED, (event1 -> {
			int id = colID.getId();
			if (!checkLastColumn(id)) {
				Cards.Card tmpCard = ctrl.cards.getByColumnAndNext(id, this.dbCard.getId());
				if(tmpCard != null) {
					tmpCard.setNext(this.dbCard.getNext());
					ctrl.cards.update(tmpCard);
				}
				colID.setId(moveRight(id, this));
				tmpCard = ctrl.cards.getByColumnAndNext(colID.getId(), -1);
				this.dbCard.setColumn(ctrl.columns.getById(colID.getId()));
				dbCard.setNext(-1);
				ctrl.cards.update(dbCard);
				if(tmpCard != null) {
					System.out.println("New prev card");
					System.out.println(tmpCard.getTitle());
					tmpCard.setNext(this.dbCard.getId());
					ctrl.cards.update(tmpCard);
				}
			}
		}));

		Draggable.Nature nature = new Draggable.Nature(this);
		nature.addListener((nature1, event1) -> {
			if (event1.equals(Draggable.Event.DragStart)) {
				this.setViewOrder(-1.0);
			}
			if (event1.equals(Draggable.Event.DragEnd)) {
				if (this.getTranslateY() > 2 * this.getHeight() / 3 || this.getTranslateY() < -2 * this.getHeight() / 3) {
					VBox container = (VBox) this.getParent();
					int i = container.getChildren().indexOf(this);
					int newI = ((int) (this.getTranslateY() / this.getHeight())) + i + (this.getTranslateY() > 0 ? 1 : -1);
					if (newI + 1 >= container.getChildren().size()) {
						newI = container.getChildren().size() - 2;
					}
					if (newI < 1) {
						newI = 1;
					}
					if(i != 1){
						Card tmpCard = ((Card)(container.getChildren().get(i-1)));
						tmpCard.dbCard.setNext(this.dbCard.getNext());
						ctrl.cards.update(tmpCard.dbCard);
					}
					if(newI != 1){
						Card tmpCard = ((Card)(container.getChildren().get(newI)));
						this.dbCard.setNext(tmpCard.dbCard.getNext());
						tmpCard.dbCard.setNext(this.dbCard.getId());
						ctrl.cards.update(tmpCard.dbCard);
					}else{
						this.dbCard.setNext(((Card)(container.getChildren().get(newI))).dbCard.getId());
					}
					ctrl.cards.update(this.dbCard);
					container.getChildren().remove(i);
					container.getChildren().add(newI, this);
				}
				this.setTranslateX(0);
				this.setTranslateY(0);
				this.setViewOrder(0.0);
			}
		});

		name.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if(oldValue != newValue && !newValue){
				dbCard.setTitle(name.getText());
				ctrl.cards.update(dbCard);
			}
		});

		description.focusedProperty().addListener(((observable, oldValue, newValue) -> {
			if(oldValue != newValue && !newValue){
				dbCard.setDescription(description.getText());
				ctrl.cards.update(dbCard);
			}
		}));

		start.focusedProperty().addListener(((observable, oldValue, newValue) -> {
			if(oldValue != newValue && !newValue){
				dbCard.setStart(java.sql.Date.valueOf(start.getValue()));
				ctrl.cards.update(dbCard);
			}
		}));

		end.focusedProperty().addListener(((observable, oldValue, newValue) -> {
			if(oldValue != newValue && !newValue){
				dbCard.setEnd(java.sql.Date.valueOf(end.getValue()));
				ctrl.cards.update(dbCard);
			}
		}));
	}

	private class ChangeEndDate implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			changeEndDate(event);
		}
	}

	private class ChangeStartDate implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			changeStartDate(event);
		}
	}

	private class RemoveCardHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			removeCard(event);
		}
	}

	private void removeCard(MouseEvent event) {
		VBox card = (VBox) ((Button) event.getSource()).getParent().getParent();
		((VBox) card.getParent()).getChildren().remove(card);
		ctrl.cards.delete(dbCard);
	}

	private void changeEndDate(ActionEvent event) {
		DatePicker end = (DatePicker) event.getSource();
		DatePicker start = (DatePicker) ((VBox) end.getParent()).getChildren().get(1);
		Label warning = (Label) ((HBox) ((VBox) end.getParent().getParent()).getChildren().get(0)).getChildren().get(2);
		setCellStartDate(end, start, warning);
	}

	private void setCellStartDate(DatePicker end, DatePicker start, Label warning){
		Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (end.getValue() != null && item.isAfter(end.getValue())) {
					setStyle("-fx-background-color: #FFC0CB;");
					Platform.runLater(() -> setDisable(true));
				}
			}
		};
		start.setDayCellFactory(dayCellFactory);
		setWarning(warning, end);
	}

	private void setWarning(Label warning, DatePicker end){
		if (end.getValue() != null && end.getValue().isBefore(LocalDate.now())) {
			warning.setText("Dépassé");
		} else {
			warning.setText("");
		}
	}

	private void changeStartDate(ActionEvent event) {
		DatePicker start = (DatePicker) event.getSource();
		DatePicker end = (DatePicker) ((VBox) start.getParent()).getChildren().get(3);
		setCellEndDate(end, start);
	}

	private void setCellEndDate(DatePicker end, DatePicker start){
		Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (start.getValue() != null && item.isBefore(start.getValue())) {
					setStyle("-fx-background-color: #FFC0CB;");
					Platform.runLater(() -> setDisable(true));
				}
			}
		};
		end.setDayCellFactory(dayCellFactory);
	}

	private boolean checkFirstColumn(int id) {
		Col tmp = this.ctrl.columnIDs.stream().filter(c -> c.getDbID() == id).findFirst().get();
		return this.ctrl.columnIDs.indexOf(tmp) == 0;
	}

	private boolean checkLastColumn(int id) {
		Col tmp = this.ctrl.columnIDs.stream().filter(c -> c.getDbID() == id).findFirst().get();
		return this.ctrl.columnIDs.indexOf(tmp) == this.ctrl.columnIDs.size() - 1;
	}

	private int moveLeft(int from, VBox card) {
		return move(from, card, -1);
	}

	private int moveRight(int from, VBox card) {
		return move(from, card, 1);
	}

	private int move(int from, VBox card, int direction) {
		Col tmp = this.ctrl.columnIDs.stream().filter(c -> c.getDbID() == from).findFirst().get();
		int index = this.ctrl.columnIDs.indexOf(tmp) + direction;
		((VBox) card.getParent()).getChildren().remove(card);
		Col newCol = this.ctrl.columnIDs.get(index);
		List<Node> list = ((VBox) ((AnchorPane) newCol.getContent()).getChildren().get(0)).getChildren();
		list.add(list.size()-1, card);
		return newCol.getDbID();
	}
}
