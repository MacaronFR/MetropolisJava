package fr.metropolis.gestion.api.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Projects extends DB<Projects.Project> {

	private final UsersOnProjects uop;

	public Projects() {
		tableName = "PROJECT";
		idCol = "id_project";
		uop = new UsersOnProjects();
	}

	@Override
	public Project getById(int id) {
		return new Project().buildFromResultSet(getDataById(id));
	}

	public List<Project> getProjectByUser(int id) {
		List<Project> res = new ArrayList<>();
		ResultSet projects = uop.getDataByColumn("user", String.valueOf(id));
		try {
			do{
				res.add(new Project().buildFromResultSet(getDataByColumn(idCol, String.valueOf(projects.getInt("project")))));
			}while (projects.next());
		}catch(SQLException e){
			e.printStackTrace();
		}
		return res;
	}

	public static class Project implements TableRow<Project> {

		private int id;
		private String name;

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public Project buildFromResultSet(ResultSet data) {
			try {
				id = data.getInt("id_project");
				name = data.getString("name");
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			} catch (NullPointerException e) {
				return null;
			}
			return this;
		}
	}
}
