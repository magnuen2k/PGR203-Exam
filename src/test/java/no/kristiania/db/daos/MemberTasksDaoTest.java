package no.kristiania.db.daos;

import no.kristiania.db.objects.Member;
import no.kristiania.db.objects.MemberTasks;
import no.kristiania.db.objects.Task;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static no.kristiania.db.daos.MemberDaoTest.exampleMember;
import static org.assertj.core.api.Assertions.assertThat;


public class MemberTasksDaoTest {
    private MemberTasksDao memberTasksDao;
    private MemberDao memberDao;
    private TaskDao taskDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:members;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberTasksDao = new MemberTasksDao(dataSource);
        memberDao = new MemberDao(dataSource);
        taskDao = new TaskDao(dataSource);
    }

    @Test
    void shouldFilterTasksForSelectedMemberAndStatus() throws SQLException {
        // Create member
        // Create member
        Member m1 = exampleMember();

        // Insert members to db
        m1.setId(memberDao.insert(m1));

        // Create task
        Task t1 = TaskDaoTest.exampleTask();
        t1.setId(taskDao.insert(t1));
        Task t2 = TaskDaoTest.exampleTask();
        t2.setId(taskDao.insert(t2));

        // Add members to task
        MemberTasks mt1 = new MemberTasks();
        mt1.setMemberId(m1.getId());
        mt1.setTaskId(t1.getId());
        memberTasksDao.insert(mt1);

        MemberTasks mt2 = new MemberTasks();
        mt2.setMemberId(m1.getId());
        mt2.setTaskId(t2.getId());
        memberTasksDao.insert(mt2);

        assertThat(memberTasksDao.filter(m1.getId(), true))
                .extracting(MemberTasks::getTaskId)
                .contains(t1.getId(), t2.getId());
    }
}
