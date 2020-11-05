package no.kristiania.db.daos;

import no.kristiania.db.objects.Task;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class TaskDao extends AbstractDao<Task>{

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    // Passing in sql statement - INSERT INTO - to insert data
    public long insertTask(Task task) throws SQLException {
        return insert(task, "INSERT INTO tasks (task_name, task_desc, task_status) values (?, ?, ?)");
    }

    public void update(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // Create statement and execute it
            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE tasks SET project_id = (?) WHERE id = (?)"
            )) {
                insertStatement.setLong(1, task.getProjectId());
                insertStatement.setLong(2, task.getId());
                insertStatement.executeUpdate();
            }
        }
    }

    public void updateTask(Task task, long taskId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // Create statement and execute it
            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE tasks SET task_name = (?), task_desc = (?), task_status = (?) WHERE id = (?)"
            )) {
                insertStatement.setString(1, task.getTaskName());
                insertStatement.setString(2, task.getDesc());
                insertStatement.setBoolean(3, task.getTaskStatus());
                insertStatement.setLong(4, taskId);
                insertStatement.executeUpdate();
            }
        }
    }

    public Task retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM tasks WHERE id = ?");
    }

    public List<Task> list() throws SQLException {
        return list("SELECT * FROM tasks ORDER BY id ASC");
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskName(rs.getString("task_name"));
        task.setDesc(rs.getString("task_desc"));
        task.setId(rs.getLong("id"));
        task.setTaskStatus(rs.getBoolean("task_status"));

        Long projectId = rs.getLong("project_id");
        if(projectId != 0) {
            task.setProjectId(projectId);
        }
        return task;
    }

    @Override
    protected void insertObject(Task task, PreparedStatement insertStatement) throws SQLException {
        insertStatement.setString(1, task.getTaskName());
        insertStatement.setString(2, task.getDesc());
        insertStatement.setBoolean(3, task.getTaskStatus());
    }

}
