package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {

    private TaskDao taskDao;
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
    }

    @Test
    void shouldListAllTasks() throws SQLException {
        Task task1 = exampleTask();
        Task task2 = exampleTask();
        taskDao.insertTask(task1);
        taskDao.insertTask(task2);
        assertThat(taskDao.list())
                .extracting(Task::getTaskName)
                .contains(task1.getTaskName(), task2.getTaskName());
    }

    @Test
    void checkIfTaskIsActiveAfterInsert() throws SQLException {
        Task task = exampleTask();
        task.setTaskStatus(true);
        taskDao.insertTask(task);
        assertThat(task).hasNoNullFieldsOrProperties();
        assertThat(taskDao.list())
                .extracting(Task::getTaskStatus)
                .contains(task.getTaskStatus());
    }

    private Task exampleTask() {
        Task task = new Task();
        task.setTaskName(exampleTaskName());
        task.setDesc(exampleTaskDesc());
        return task;
    }

    private String exampleTaskName() {
        String[] options = {"Make food", "Drink", "Make baby", "Make gears"};
        return options[random.nextInt(options.length)];
    }

    private String exampleTaskDesc() {
        String[] options = {"yes i make food", "nice drinks u got there baby", "yes", "selfexplaining what this is my g"};
        return options[random.nextInt(options.length)];
    }
}
