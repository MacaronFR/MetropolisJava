package fr.metropolis.gestion.api.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersOnProjects extends DB<UsersOnProjects.UserOnProject>{

	public UsersOnProjects(){
		tableName = "USERS_ON_PROJECTS";
		idCol = "id_project";
	}

	@Override
	public UserOnProject getById(int id){
		return new UserOnProject().buildFromResultSet(getDataById(id));
	}

	public final class UserOnProject implements TableRow<UserOnProject>{

		private int id;
		private int project;
		private int user;

		public int getId(){
			return id;
		}

		public int getProject() {
			return project;
		}

		public int getUser() {
			return user;
		}

		@Override
		public UserOnProject buildFromResultSet(ResultSet data) {
			try{
				id = data.getInt("id");
				project = data.getInt("project");
				user = data.getInt("user");
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
