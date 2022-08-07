package fr.metropolis.gestion.api.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Users extends DB<Users.User>{
	public Users(){
		tableName = "USERS";
		idCol = "id_user";
	}

	@Override
	public User getById(int id) {
		ResultSet data = getDataById(id);
		return new User().buildFromResultSet(data);
	}

	public User getByName(String name){
		ResultSet data = getDataByColumn("name", name);
		if(data == null){
			return null;
		}
		return new User().buildFromResultSet(data);
	}

	static public class User implements TableRow<User>{

		public int getId() {
			return id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setSalt(String salt) {
			this.salt = salt;
		}

		public String getName() {
			return name;
		}

		public String getPassword() {
			return password;
		}

		public String getSalt() {
			return salt;
		}

		private int id;
		private String name;
		private String password;
		private String salt;

		public User buildFromResultSet(ResultSet data){
			try {
				id = data.getInt("id_user");
				name = data.getString("name");
				password = data.getString("password");
				salt = data.getString("salt");
			}catch (SQLException e){
				e.printStackTrace();
				return null;
			}catch(NullPointerException e){
				return null;
			}
			return this;
		}
	}
}
