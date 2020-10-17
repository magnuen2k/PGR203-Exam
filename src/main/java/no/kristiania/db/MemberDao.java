package no.kristiania.db;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

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
    public String list() throws SQLException {
        // Make connection to database
        try (Connection connection = dataSource.getConnection()) {
            // Create statement
            try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM members")) {
                // Execute statement and store result in variable
                try (ResultSet res = selectStatement.executeQuery()) {
                    String members = "";
                    // Loop through result of sql query and build a response string
                    while(res.next()){
                        members += "<li>Name: " + res.getString("first_name") + " " + res.getString("last_name") + " - Email: " + res.getString("email") + "</li>";
                    }
                    return members;
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        Properties properties = new Properties();
        try(FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        MemberDao db = new MemberDao(dataSource);
        Member member = new Member();

        System.out.println("Add new member");
        System.out.println("First name:");

        // Create scanner to take input from user
        Scanner scanner = new Scanner(System.in);
        String firstName = scanner.nextLine();

        System.out.println("Last name:");
        String lastName = scanner.nextLine();

        System.out.println("Email:");
        String email = scanner.nextLine();

        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);

        // Add input from user to database
        db.insertMember(member);

        // Display members from database
        System.out.println(db.list());
    }

}
