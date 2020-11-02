package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {
    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long insert(T object, String sql) throws SQLException{
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            insertObject(object, insertStatement);
            insertStatement.executeUpdate();

            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            generatedKeys.next();

            // If object is "connection table" do not return id
            if(object instanceof MemberTasks){
                return 0;
            }

            return generatedKeys.getLong("id");
        }
    }

    public T retrieve(Long id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public List<T> list(String sql) throws SQLException {
        // Make connection to database
        try (Connection connection = dataSource.getConnection()) {
            // Create statement
            try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
                // Execute statement and store result in variable
                try (ResultSet res = selectStatement.executeQuery()) {
                    List<T> task = new ArrayList<>();
                    // Loop through result of sql query and build a list with all task
                    while(res.next()){
                        task.add(mapRow(res));
                    }
                    return task;
                }
            }
        }
    }

    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void insertObject(T obj, PreparedStatement insertStatement) throws SQLException;
}
