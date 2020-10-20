package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {

    private final DataSource dataSource;

    public MemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
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
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM members WHERE id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToMember(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private Member mapRowToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        return member;
    }

    // List all members in Database
    // Passing in sql statement - SELECT * - loop through result of select and return a List with all members
    public List<Member> list() throws SQLException {
        // Make connection to database
        try (Connection connection = dataSource.getConnection()) {
            // Create statement
            try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM members")) {
                // Execute statement and store result in variable
                try (ResultSet res = selectStatement.executeQuery()) {
                    List<Member> members = new ArrayList<>();
                    // Loop through result of sql query and build a list with all members
                    while(res.next()){
                         members.add(mapRowToMember(res));
                    }
                    return members;
                }
            }
        }
    }
}
