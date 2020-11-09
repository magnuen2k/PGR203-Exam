package no.kristiania.db.daos;

import no.kristiania.db.objects.Member;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class MemberDao extends AbstractDao<Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);
    }

    // Passing in sql statement - INSERT INTO - to insert data
    public long insert(Member member) throws SQLException {
        return insert(member, "INSERT INTO members (first_name, last_name, email) values (?, ?, ?)");
    }

    public void update(Member member, long id) throws SQLException {
        update(member, "UPDATE members SET first_name = (?), last_name = (?), email = (?) WHERE id = (?)", id, 4);
    }

    public Member retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM members WHERE id = ?");
    }

    // List all members in Database
    // Passing in sql statement - SELECT * - loop through result of select and return a List with all members
    public List<Member> list() throws SQLException {
       return list("SELECT * FROM members ORDER BY id ASC");
    }

    // Should not be a concatenated string
    public List<Member> getMembersOnTask(long id) throws SQLException {
        return list("select m.* " +
                "from tasks t, members m, member_tasks mt " +
                "where t.id = mt.task_id " +
                "AND mt.member_id = m.id " +
                "AND mt.task_id =" + id);
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        return member;
    }

    @Override
    protected void insertObject(Member member, PreparedStatement insertStatement) throws SQLException {
        insertStatement.setString(1, member.getFirstName());
        insertStatement.setString(2, member.getLastName());
        insertStatement.setString(3, member.getEmail());
    }
}
