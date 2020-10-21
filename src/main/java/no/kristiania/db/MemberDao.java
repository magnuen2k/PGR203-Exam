package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao extends AbstractDao<Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);

    }

    // Passing in sql statement - INSERT INTO - to insert data
    public void insertMember(Member member) throws SQLException {
        // Make connection to database
        try (Connection connection = dataSource.getConnection()) {
            // Create statement and execute it
            try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO members (first_name, last_name, email) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                insertStatement.setString(1, member.getFirstName());
                insertStatement.setString(2, member.getLastName());
                insertStatement.setString(3, member.getEmail());
                insertStatement.executeUpdate();

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    generatedKeys.next();
                    member.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public Member retrieve(Long id) throws SQLException {
        return retrieve(id, "SELECT * FROM members WHERE id = ?");
    }


    // List all members in Database
    // Passing in sql statement - SELECT * - loop through result of select and return a List with all members
    public List<Member> list() throws SQLException {
       return list("SELECT * FROM members");
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
