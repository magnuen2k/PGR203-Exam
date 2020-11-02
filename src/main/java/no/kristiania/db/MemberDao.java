package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class MemberDao extends AbstractDao<Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);

    }

    // Passing in sql statement - INSERT INTO - to insert data
    public long insertMember(Member member) throws SQLException {
        return insert(member, "INSERT INTO members (first_name, last_name, email) values (?, ?, ?)");
    }

    @Override
    protected void insertObject(Member member, PreparedStatement insertStatement) throws SQLException {
        insertStatement.setString(1, member.getFirstName());
        insertStatement.setString(2, member.getLastName());
        insertStatement.setString(3, member.getEmail());
    }

    public Member retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM members WHERE id = ?");
    }


    // List all members in Database
    // Passing in sql statement - SELECT * - loop through result of select and return a List with all members
    public List<Member> list() throws SQLException {
       return list("SELECT * FROM members ORDER BY id ASC");
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
}
