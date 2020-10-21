package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao extends AbstractDao<Task>{

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    // Passing in sql statement - INSERT INTO - to insert data
    public void insertTask(Task task) throws SQLException {
        // Make connection to database
        try (Connection connection = dataSource.getConnection()) {
            // Create statement and execute it
            try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO tasks (task_name) values (?)", Statement.RETURN_GENERATED_KEYS)) {
                insertStatement.setString(1, task.getTaskName());
                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    generatedKeys.next();
                    task.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public Task retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM tasks WHERE id = ?");
    }

    public List<Task> list() throws SQLException {
        return list("SELECT * FROM tasks");
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskName(rs.getString("task_name"));
        task.setId(rs.getLong("id"));
        return task;
    }
}