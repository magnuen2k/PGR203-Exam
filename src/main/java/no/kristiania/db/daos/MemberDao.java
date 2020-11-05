package no.kristiania.db.daos;

import no.kristiania.db.objects.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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

    public List<Long> getMembersOnTask(long id) throws SQLException {
       /* List<Member> members = list("select member_id from tasks left join member_tasks mt on tasks.id = mt.task_id where task_id =" + id);
        List<Long> memberIds = new ArrayList<>();
        for (Member m : members) {
            memberIds.add(m.getId());
        }
        return memberIds;*/

        try (Connection connection = dataSource.getConnection()) {
            // Create statement
            try (PreparedStatement selectStatement = connection.prepareStatement("select member_id from tasks left join member_tasks mt on tasks.id = mt.task_id where task_id =" + id)) {
                // Execute statement and store result in variable
                try (ResultSet res = selectStatement.executeQuery()) {
                    List<Long> task = new ArrayList<>();
                    // Loop through result of sql query and build a list with all task
                    while(res.next()){
                        task.add(res.getLong("member_id"));
                    }
                    return task;
                }
            }
        }
    }
}
