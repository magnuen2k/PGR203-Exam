package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;

public class MemberTasksDao extends AbstractDao<MemberTasks>{

    public MemberTasksDao(DataSource dataSource) {
        super(dataSource);
    }

    public long insert(MemberTasks memberTasks) throws SQLException {
        return insert(memberTasks, "INSERT INTO member_tasks (member_id, task_id) values (?, ?)");
    }

    @Override
    protected MemberTasks mapRow(ResultSet rs) throws SQLException {
        MemberTasks memberTasks = new MemberTasks();
        memberTasks.setMemberId(rs.getLong("member_id"));
        memberTasks.setTaskId(rs.getLong("task_id"));
        return memberTasks;
    }

    @Override
    protected void insertObject(MemberTasks memberTasks, PreparedStatement insertStatement) throws SQLException {
        insertStatement.setLong(1, memberTasks.getMemberId());
        insertStatement.setLong(2, memberTasks.getTaskId());
    }

}
