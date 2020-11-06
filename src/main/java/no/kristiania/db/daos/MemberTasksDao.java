package no.kristiania.db.daos;

import no.kristiania.db.objects.Member;
import no.kristiania.db.objects.MemberTasks;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class MemberTasksDao extends AbstractDao<MemberTasks>{

    public MemberTasksDao(DataSource dataSource) {
        super(dataSource);
    }

    public long insert(MemberTasks memberTasks) throws SQLException {
        return insert(memberTasks, "INSERT INTO member_tasks (member_id, task_id) values (?, ?)");
    }

    public MemberTasks retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM member_tasks WHERE id = ?");
    }

    //There could potentially be SQL injection exploits a third party could take advantage of, caused by the
    //concatenation of the strings. We find this acceptable for this task, but would probably use a different approach
    //in a larger and public application.
    public List<MemberTasks> filter(Long memberId, boolean status) throws SQLException {
        return list("SELECT member_id, task_id\n" +
                "FROM member_tasks\n" +
                "Left JOIN members m on member_id = m.id\n" +
                "RIGHT JOIN tasks t on task_id = t.id\n" +
                "WHERE task_status = " + status + " AND member_id = " + memberId);
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
