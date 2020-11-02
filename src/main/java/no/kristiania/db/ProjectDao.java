package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class ProjectDao extends AbstractDao<Project>{
    public ProjectDao(DataSource dataSource) {
        super(dataSource);
    }

    // Passing in sql statement - INSERT INTO - to insert data
    public long insert(Project project) throws SQLException {
       return insert(project, "INSERT INTO projects (project_name, project_desc, project_status) values (?, ?, ?)");
    }

    public void update(Project project, int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // Create statement and execute it
            try (PreparedStatement insertStatement = connection.prepareStatement("UPDATE projects SET project_name = (?), project_status = (?) WHERE id = (?)" )) {
                insertStatement.setString(1, project.getProjectName());
                insertStatement.setBoolean(2, project.getProjectStatus());
                insertStatement.setInt(3, id);
                insertStatement.executeUpdate();
            }
        }
    }

    public Project retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM projects WHERE id = ?");
    }

    public List<Project> list() throws SQLException {
        return list("SELECT * FROM projects ORDER BY id ASC");
    }


    @Override
    protected Project mapRow(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setProjectName(rs.getString("project_name"));
        project.setDesc(rs.getString("project_desc"));
        project.setProjectStatus(rs.getBoolean("project_status"));
        return project;
    }

    @Override
    protected void insertObject(Project project, PreparedStatement insertStatement) throws SQLException {
        insertStatement.setString(1, project.getProjectName());
        insertStatement.setString(2, project.getDesc());
        insertStatement.setBoolean(3, project.getProjectStatus());
    }
}
