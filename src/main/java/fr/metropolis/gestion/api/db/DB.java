package fr.metropolis.gestion.api.db;

import java.sql.*;
import java.util.List;

abstract public class DB<T> {

	public static final Connection con;

	static {
		try {
			con = DriverManager.getConnection("jdbc:mariadb://151.80.58.137:1653/PA", "macaron", System.getenv("DB_PASS"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected String tableName;
	protected String idCol;

	public abstract T getById(int id);

	protected int insert(List<String> fields, List<String> values){
		String req = "INSERT INTO ".concat(tableName).concat(" (");
		for(int i = 0; i < fields.size() - 1; i++){
			req = req.concat(fields.get(i).concat(", "));
		}
		req = req.concat(fields.get(fields.size() - 1)).concat(") VALUES (");
		for(int i = 0; i < values.size() - 1; i++){
			req = req.concat("?, ");
		}
		req = req.concat("?);");
		try{
			PreparedStatement stmt = con.prepareStatement(req);
			for(int i = 0; i < values.size(); i++){
				stmt.setObject(i, values.get(i));
			}
			ResultSet res = stmt.executeQuery();
			if(res.next()){
				return res.getInt("id_column");
			}
			return -1;
		}catch (SQLException e){
			e.printStackTrace();
			return -1;
		}
	}

	protected String getInsertQuery(List<String> fields){
		String req = "INSERT INTO ".concat(tableName).concat(" (");
		for(int i = 0; i < fields.size() - 1; i++){
			req = req.concat(fields.get(i).concat(", "));
		}
		req = req.concat(fields.get(fields.size() - 1)).concat(") VALUES (");
		for(int i = 0; i < fields.size() - 1; i++){
			req = req.concat("?, ");
		}
		return req.concat("?);");
	}

	private ResultSet makeQuery(String statement){
		try{
			Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(statement);
			if(res.next()){
				return res;
			}else{
				return null;
			}
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	protected final ResultSet getDataById(int id){
		String req = "SELECT * FROM ".concat(tableName.concat(" WHERE ").concat(idCol.concat("=".concat(Integer.toString(id)))));
		return makeQuery(req);
	}

	protected final ResultSet getDataByColumn(String column, String value){
		String req = "SELECT * FROM ".concat(tableName.concat(" WHERE ").concat(column.concat("=".concat(value))));
		return makeQuery(req);
	}

	protected final String getDelete(){
		return "DELETE FROM ".concat(tableName).concat(" WHERE ").concat(idCol).concat(" = ?;");
	}
}
