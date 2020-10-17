package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class MemberDaoTest {
    private MemberDao memberDao;
    private  Random r = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:members;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberDao = new MemberDao(dataSource);
    }

    @Test
    void shouldListInsertedProducts() throws SQLException {
        memberDao.insertMember(exampleMember());
        memberDao.insertMember(exampleMember());
        Member member = exampleMember();
        memberDao.insertMember(member);
        assertThat(member).hasNoNullFieldsOrProperties();
        assertThat(memberDao.retrieve(member.getId()))
                .usingRecursiveComparison()
                .isEqualTo(member);
    }

    private Member exampleMember() {
        Member member = new Member();
        member.setFirstName(exampleFirstName());
        member.setLastName(exampleLastName());
        member.setEmail("testmail@mail.yahoo");
        return member;
    }

    private String exampleFirstName() {
        String[] firstNames = {"Magnus", "Stian", "Kai", "Ibrahim", "Lauri", "Dan"};
        return firstNames[r.nextInt(firstNames.length)];
    }

    private String exampleLastName() {
        String[] lastNames = {"Yes", "No", "Yoink", "Maybe", "Yahoo", "Preben"};
        return lastNames[r.nextInt(lastNames.length)];
    }
}