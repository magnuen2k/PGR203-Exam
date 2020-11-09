package no.kristiania.db.daos;

import no.kristiania.db.objects.Member;
import no.kristiania.db.objects.MemberTasks;
import no.kristiania.db.objects.Task;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberDaoTest {
    private MemberDao memberDao;
    private TaskDao taskDao;
    private MemberTasksDao memberTasksDao;
    private static final Random r = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:members;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberDao = new MemberDao(dataSource);
        taskDao = new TaskDao(dataSource);
        memberTasksDao = new MemberTasksDao(dataSource);
    }

    @Test
    void shouldListInsertedMembers() throws SQLException {

        memberDao.insert(exampleMember());
        memberDao.insert(exampleMember());
        Member member = exampleMember();
        member.setId(memberDao.insert(member));
        assertThat(member).hasNoNullFieldsOrProperties();
        assertThat(memberDao.retrieve(member.getId()))
                .usingRecursiveComparison()
                .isEqualTo(member);
    }

    @Test
    void shouldReturnAllDataFromDatabase() throws SQLException {
        Member m1 = exampleMember();
        Member m2 = exampleMember();
        memberDao.insert(m1);
        memberDao.insert(m2);

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

    @Test
    void shouldUpdateMember() throws SQLException {
        Member m1 = exampleMember();
        m1.setId(memberDao.insert(m1));
        Member m2 = exampleMember();
        memberDao.update(m2, m1.getId());
        assertThat(memberDao.retrieve(m1.getId()))
                .extracting(Member::getName)
                .isEqualTo(m2.getName());
    }

    // Both method and test might be better in a different dao? MemberTasksDao?
    @Test
    void shouldGetAllMemberIdsOnTask() throws SQLException {
        // Create members
        Member m1 = exampleMember();
        Member m2 = exampleMember();
        Member m3 = exampleMember();

        // Insert member to db
        m1.setId(memberDao.insert(m1));
        m2.setId(memberDao.insert(m2));
        m3.setId(memberDao.insert(m3));

        // Create task
        Task task = TaskDaoTest.exampleTask();
        task.setId(taskDao.insert(task));

        // Add members to task
        MemberTasks mt1 = new MemberTasks();
        mt1.setMemberId(m1.getId());
        mt1.setTaskId(task.getId());
        memberTasksDao.insert(mt1);

        MemberTasks mt2 = new MemberTasks();
        mt2.setMemberId(m3.getId());
        mt2.setTaskId(task.getId());
        memberTasksDao.insert(mt2);

        assertThat(memberDao.getMembersOnTask(task.getId()))
                .extracting(Member::getName)
                .contains(m1.getName(), m3.getName());
    }

    public static Member exampleMember() {
        Member member = new Member();
        member.setFirstName(exampleFirstName());
        member.setLastName(exampleLastName());
        member.setEmail("testmail@mail.yahoo");
        return member;
    }

    public static String exampleFirstName() {
        String[] firstNames = {"Magnus", "Stian", "Kai", "Ibrahim", "Lauri", "Dan"};
        return firstNames[r.nextInt(firstNames.length)];
    }

    public static String exampleLastName() {
        String[] lastNames = {"Yes", "No", "Yoink", "Maybe", "Yahoo", "Preben"};
        return lastNames[r.nextInt(lastNames.length)];
    }
}