package fr.metropolis.gestion.api.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Columns extends DB<Columns.Column>{

    public Columns(){
        idCol = "id_column";
        tableName = "COLUMNS";
    }

    @Override
    public Column getById(int id) {
        return new Column().buildFromResultSet(getDataById(id));
    }

    public List<Column> getByProject(int idProject){
        ResultSet res = getDataByColumn("project", Integer.valueOf(idProject).toString());
        List<Column> list = new ArrayList<>();
        try{
            if(res == null){
                return list;
            }
            do{
                list.add(new Column().buildFromResultSet(res));
            }while(res.next());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public int add(String title, int project){
        String req = getInsertQuery(List.of("title", "project"));
        try{
            PreparedStatement stmt = con.prepareStatement(req, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, title);
            stmt.setInt(2, project);
            stmt.executeQuery();
            ResultSet res = con.createStatement().executeQuery("SELECT LAST_INSERT_ID() as id;");
            if(res.next()){
                return res.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateName(String name, int id){
        String req = "UPDATE COLUMNS SET title = ? WHERE ".concat(idCol).concat(" = ?;");
        try{
            PreparedStatement stmt = con.prepareStatement(req);
            stmt.setString(1, name);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id){
        try{
            PreparedStatement stmt = con.prepareStatement(getDelete());
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    static public class Column implements TableRow<Column>{

        private int id;
        private String name;
        private Projects.Project project;

        @Override
        public Column buildFromResultSet(ResultSet data) {
            try {
                id = data.getInt("id_column");
                name = data.getString("title");
                Projects p = new Projects();
                project = new Projects.Project().buildFromResultSet(p.getDataById(data.getInt("project")));
            }catch (SQLException e){
                e.printStackTrace();
                return null;
            }catch (NullPointerException e){
                return null;
            }
            return this;
        }

        public int getId(){
            return id;
        }

        public String getName() {
            return name;
        }

        public Projects.Project getProject() {
            return project;
        }
    }
}
