package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;

public class MemberTasksDao extends AbstractDao<MemberTasks>{

    public MemberTasksDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected MemberTasks mapRow(ResultSet rs) throws SQLException {
        MemberTasks memberTasks = new MemberTasks();
        memberTasks.setMemberId(rs.getLong("member_id"));
        memberTasks.setTaskId(rs.getLong("task_id"));
        return memberTasks;
    }

    public void insert(MemberTasks memberTasks) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // Create statement and execute it
            try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO member_tasks (member_id, task_id) values (?, ?)")) {
                insertStatement.setLong(1, memberTasks.getMemberId());
                insertStatement.setLong(2, memberTasks.getTaskId());
                insertStatement.executeUpdate();
            }
        }
    }
}
