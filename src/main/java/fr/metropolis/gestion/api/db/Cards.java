package fr.metropolis.gestion.api.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cards extends DB<Cards.Card>{

	public Cards() {
		tableName = "CARDS";
		idCol = "id_card";
	}

	private String fields = "name, description, start, end, next, `column`";

	@Override
	public Card getById(int id) {
		return new Card().buildFromResultSet(getDataById(id));
	}

	public List<Card> getByColumn(int idColumn){
		ResultSet res = getDataByColumn("`column`", Integer.valueOf(idColumn).toString());
		List<Card> list = new ArrayList<>();
		try {
			if(res == null){
				return list;
			}
			do{
				list.add(new Card().buildFromResultSet(res));
			}while (res.next());
			List<Card> tmp = new ArrayList<>();
			tmp.add(0, list.stream().filter(card -> card.getNext() == -1).findFirst().get());
			while(tmp.size() != list.size()){
				tmp.add(0, list.stream().filter(card -> card.getNext() == tmp.get(0).id).findFirst().get());
			}
			return tmp;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return list;
	}

	public Card getByColumnAndNext(int idColumn, int next){
		String req = "SELECT ".concat(fields).concat(", ").concat(idCol).concat(" FROM ").concat(tableName).concat(" WHERE `column`=").concat(String.valueOf(idColumn)).concat(" AND next");
		if(next != -1){
			req = req.concat("=").concat(String.valueOf(next));
		}else{
			req = req.concat(" IS NULL;");
		}
		try {
			ResultSet res = con.createStatement().executeQuery(req);
			if(res.next()){
				Card c = new Card();
				c.buildFromResultSet(res);
				return c;
			}else{
				return null;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public boolean add(Card card){
		String req = "INSERT INTO CARDS (".concat(fields).concat(") VALUE (?, ?, ?, ?, ?, ?);");
		try{
			PreparedStatement stmt = con.prepareStatement(req);
			stmt.setString(1, card.getTitle());
			stmt.setString(2, card.getDescription());
			stmt.setDate(3, card.getStart());
			stmt.setDate(4, card.getEnd());
			if(card.getNext() == -1){
				stmt.setNull(5, Types.INTEGER);
			}else{
				stmt.setInt(5, card.getNext());
			}
			stmt.setInt(6, card.getColumn().getId());
			if(stmt.executeUpdate() != 0){
				ResultSet res = con.createStatement().executeQuery("SELECT LAST_INSERT_ID() as id;");
				if(res.next()){
					card.id = res.getInt("id");
					con.createStatement().execute("UPDATE CARDS SET next=".concat(String.valueOf(card.id).concat(" WHERE `column`=".concat(String.valueOf(card.column.getId()).concat(" AND next IS NULL AND ".concat(idCol.concat("<>".concat(String.valueOf(card.id)))))))));
					return true;
				}
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public void update(Card card){
		String req = "UPDATE CARDS SET name=?, description=?, start=?, end=?, next=?, `column`=? WHERE ".concat(idCol).concat( "=?");
		try{
			PreparedStatement stmt = con.prepareStatement(req);
			stmt.setString(1, card.getTitle());
			stmt.setString(2, card.getDescription());
			stmt.setDate(3, card.getStart());
			stmt.setDate(4, card.getEnd());
			if(card.getNext() == -1){
				stmt.setNull(5, Types.INTEGER);
			}else{
				stmt.setInt(5, card.getNext());
			}
			stmt.setInt(6, card.getColumn().getId());
			stmt.setInt(7, card.getId());
			stmt.executeUpdate();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	static public class Card implements TableRow<Card>{

		private int id;
		private String title;
		private String description;
		private Date start;
		private Date end;
		private int next;
		private Columns.Column column;

		public int getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getStart() {
			return start;
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public Date getEnd() {
			return end;
		}

		public void setEnd(Date end) {
			this.end = end;
		}

		public int getNext(){
			return next;
		}

		public void setNext(int next){
			this.next = next;
		}

		public Columns.Column getColumn() {
			return column;
		}

		public void setColumn(Columns.Column column) {
			this.column = column;
		}

		@Override
		public Card buildFromResultSet(ResultSet data) {
			try{
				id = data.getInt("id_card");
				title = data.getString("name");
				description = data.getString("description");
				data.getDate("start");
				start = data.getDate("start");
				end = data.getDate("end");
				if(data.getObject("next") == null){
					next = -1;
				}else{
					next = data.getInt("next");
				}
				Columns c = new Columns();
				column = new Columns.Column().buildFromResultSet(c.getDataById(data.getInt("column")));
			}catch (SQLException e){
				e.printStackTrace();
				return null;
			}catch (NullPointerException e){
				return null;
			}
			return this;
		}
	}
}
