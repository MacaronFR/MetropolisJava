package fr.metropolis.gestion.api.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersOnProjects extends DB<UsersOnProjects.UserOnProject>{

	public UsersOnProjects(){
		tableName = "USERS_ON_PROJECTS";
		idCol = "id_project";
	}

	@Override
	public UserOnProject getById(int id){
		return new UserOnProject().buildFromResultSet(getDataById(id));
	}

	public void addUserOnProject(int user, int project){
		String req = "INSERT INTO USERS_ON_PROJECTS (user, project) VALUES (?, ?);";
		try{
			PreparedStatement prep = con.prepareStatement(req);
			prep.setInt(1, user);
			prep.setInt(2, project);
			prep.executeQuery();
		}catch(SQLException e){
			e.printStackTrace();
		}
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
