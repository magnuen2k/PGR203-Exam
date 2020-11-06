package no.kristiania.db.daos;

import no.kristiania.db.daos.TaskDao;
import no.kristiania.db.objects.Project;
import no.kristiania.db.objects.Task;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static no.kristiania.db.daos.ProjectDaoTest.exampleProject;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {

    private TaskDao taskDao;
    private ProjectDao projectDao;
    private static final Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
        projectDao = new ProjectDao(dataSource);
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
        taskDao.insertTask(task);
        assertThat(taskDao.list())
                .extracting(Task::getTaskStatus)
                .contains(task.getTaskStatus());
    }

    @Test
    void shouldAddTaskToProject() throws SQLException {
        // Create task
        Task task = exampleTask();
        taskDao.insertTask(task);

        // Create project
        Project project = exampleProject();
        projectDao.insert(project);

        // Update task with projectId
        task.setProjectId(project.getId());
        taskDao.addTaskToProject(task);
    }

    public static Task exampleTask() {
        Task task = new Task();
        task.setTaskName(exampleTaskName());
        task.setDesc(exampleTaskDesc());
        task.setTaskStatus(true);
        return task;
    }

    public static String exampleTaskName() {
        String[] options = {"Make food", "Drink", "Make baby", "Make gears"};
        return options[random.nextInt(options.length)];
    }

    public static String exampleTaskDesc() {
        String[] options = {"yes i make food", "nice drinks u got there baby", "yes", "selfexplaining what this is my g"};
        return options[random.nextInt(options.length)];
    }
}
