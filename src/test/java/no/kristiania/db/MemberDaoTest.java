package no.kristiania.db;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.db.objects.Member;
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

    @Test
    void shouldReturnAllDataFromDatabase() throws SQLException {
        Member m1 = exampleMember();
        Member m2 = exampleMember();
        memberDao.insertMember(m1);
        memberDao.insertMember(m2);

        assertThat(memberDao.list())
                .extracting(Member::getName)
                .contains(m1.getName(), m2.getName());
    }

    @Test
    void shouldReturnEmptyResultSet() throws SQLException {
        long id = -1;
        assertThat(memberDao.retrieve(id))
                .usingRecursiveComparison()
                .isEqualTo(null);
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