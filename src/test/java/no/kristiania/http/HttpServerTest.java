package no.kristiania.http;

import no.kristiania.db.daos.*;
import no.kristiania.db.objects.Member;
import no.kristiania.db.objects.MemberTasks;
import no.kristiania.db.objects.Project;
import no.kristiania.db.objects.Task;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

import static no.kristiania.db.daos.MemberDaoTest.exampleMember;
import static no.kristiania.db.daos.ProjectDaoTest.exampleProject;
import static no.kristiania.db.daos.TaskDaoTest.exampleTask;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    private JdbcDataSource dataSource;
    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:members;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();

        server = new HttpServer(0, dataSource);
    }

   @Test
    void checkStatusCodeOK() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void checkIfNotFound() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void correctContentLength() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=RaidShadowLegends");
        assertEquals("17", client.getResponseHeader("Content-Length"));
    }

    @Test
    void correctBodyContent() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=BigBrain");
        assertEquals("BigBrain", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        File contentRoot = new File("target/test-classes");

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void affirmContentTypeHTML() throws IOException {
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("localhost", server.getPort(), "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void affirmContentTypeCSS() throws IOException {
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "style.css").toPath(), "body { margin: 0 }");

        HttpClient client = new HttpClient("localhost", server.getPort(), "/style.css");
        assertEquals("text/css", client.getResponseHeader("Content-Type"));
    }

    @Test
    void affirmContentTypeTxt() throws IOException {
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "Hei.txt").toPath(), "Hei");

        HttpClient client = new HttpClient("localhost", server.getPort(), "/Hei.txt");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void affirmFileNotFound404() throws IOException {
        File contentRoot = new File("target/test-classes");

        HttpClient client = new HttpClient("localhost", server.getPort(), "Hei");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewMember() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/members", "POST", "first_name=test&last_name=dao&email_address=test%40dao.com");
        assertEquals(302, client.getStatusCode());
        assertThat(server.getMemberNames())
                .extracting(Member::getName)
                .contains("test dao");
    }

   @Test
    void shouldPostNewTask() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/tasks", "POST", "task_name=task1&task_desc=dao&task_status=true");
        assertEquals(302, client.getStatusCode());
        TaskDao taskDao = new TaskDao(dataSource);
        assertThat(taskDao.list())
                .extracting(Task::getTaskName)
                .contains("task1");
    }

    @Test
    void shouldPostNewProject() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projects", "POST", "project_name=project1&project_desc=dao&project_status=true");
        assertEquals(302, client.getStatusCode());
        ProjectDao projectDao = new ProjectDao(dataSource);
        assertThat(projectDao.list())
                .extracting(Project::getProjectName)
                .contains("project1");
    }

    @Test
    void shouldReturnExistingMembers() throws IOException, SQLException {
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = new Member();
        member.setFirstName("Arild");
        member.setLastName("Svensen");
        member.setEmail("arild@sykkel.no");
        memberDao.insert(member);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projectMembers");
        assertThat(client.getResponseBody()).contains("<li>Name: Arild Svensen - Email: arild@sykkel.no</li>");
    }

    @Test
    void shouldReturnExistingTasks() throws IOException, SQLException {
        TaskDao taskDao = new TaskDao(dataSource);
        Task task = exampleTask();
        taskDao.insert(task);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projectTasks");
        assertThat(client.getResponseBody())
                .contains(task.getTaskName());
    }

    @Test
    void shouldReturnExistingProjects() throws IOException, SQLException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = exampleProject();
        projectDao.insert(project);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projectList");
        assertThat(client.getResponseBody())
                .contains(project.getProjectName());
    }

    @Test
    void shouldFilterTasksByMemberAndStatus() throws SQLException, IOException {
        MemberDao memberDao = new MemberDao(dataSource);
        // Create member
        Member m1 = exampleMember();

        // Insert members to db
        m1.setId(memberDao.insert(m1));

        // Create task
        TaskDao taskDao = new TaskDao(dataSource);
        Task t1 = exampleTask();
        t1.setId(taskDao.insert(t1));
        Task t2 = new Task();
        t2.setTaskName("Test");
        t2.setDesc("Should not be there");
        t2.setTaskStatus(false);
        t2.setId(taskDao.insert(t2));

        // Add members to task
        MemberTasksDao memberTasksDao = new MemberTasksDao(dataSource);
        MemberTasks mt1 = new MemberTasks();
        mt1.setMemberId(m1.getId());
        mt1.setTaskId(t1.getId());
        memberTasksDao.insert(mt1);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projectTasks?memberId=" + m1.getId() + "&status=true" );
        assertThat(client.getResponseBody())
                .contains(t1.getTaskName(), m1.getName())
                .doesNotContain(t2.getTaskName());
    }

    @Test
    void shouldUpdateProject() throws SQLException, IOException {
        ProjectDao projectDao = new ProjectDao(dataSource);

        // Create project
        Project project = exampleProject();
        project.setId(projectDao.insert(project));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/updateProject", "POST", "project_name=test&project_desc=dao&project_status=true&id=" + project.getId());
        assertThat(projectDao.retrieve(project.getId()))
                .extracting(Project::getProjectName)
                .isEqualTo("test");
    }

    @Test
    void shouldUpdateTask() throws SQLException, IOException {
        TaskDao taskDao = new TaskDao(dataSource);

        // Create task
        Task task = exampleTask();
        task.setId(taskDao.insert(task));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/updateTasks", "POST", "task_name=yes&task_desc=dao&task_status=true&id=" + task.getId());
        assertThat(taskDao.retrieve(task.getId()))
                .extracting(Task::getTaskName)
                .isEqualTo("yes");
    }

    @Test
    void shouldUpdateMember() throws SQLException, IOException {
        MemberDao memberDao = new MemberDao(dataSource);

        // Create member
        Member member = exampleMember();
        member.setId(memberDao.insert(member));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/updateMember", "POST", "first_name=yes&last_name=dao&email=test%40yes&id=" + member.getId());
        assertThat(memberDao.retrieve(member.getId()))
                .extracting(Member::getEmail)
                .isEqualTo("test@yes");
    }

    @Test
    void shouldAddMemberToTask() throws SQLException, IOException {
        // Create member
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = exampleMember();
        member.setId(memberDao.insert(member));

        // Create task
        TaskDao taskDao = new TaskDao(dataSource);
        Task task = exampleTask();
        task.setId(taskDao.insert(task));

        MemberTasksDao memberTasksDao = new MemberTasksDao(dataSource);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/updateMemberTasks", "POST", "memberId=" + member.getId() + "&taskId=" + task.getId());
        assertThat(memberTasksDao.list())
                .extracting(MemberTasks::getMemberId)
                .contains(member.getId());
    }

    @Test
    void shouldAddTaskToProject() throws SQLException, IOException {
        // Create task
        TaskDao taskDao = new TaskDao(dataSource);
        Task task = exampleTask();
        task.setId(taskDao.insert(task));

        // Create project
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = exampleProject();
        project.setId(projectDao.insert(project));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addTaskToProject", "POST", "taskId=" + task.getId() + "&projectId=" + project.getId());
        assertThat(taskDao.retrieve(task.getId()))
                .extracting(Task::getProjectId)
                .isEqualTo(project.getId());
    }

    @Test
    void shouldGetAllTaskOptions() throws SQLException, IOException {
        TaskDao taskDao = new TaskDao(dataSource);
        // Create example tasks
        Task task1 = exampleTask();
        task1.setId(taskDao.insert(task1));
        Task task2 = exampleTask();
        task2.setId(taskDao.insert(task2));
        Task task3 = exampleTask();
        task3.setId(taskDao.insert(task3));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/taskOptions" );
        assertThat(client.getResponseBody())
                .contains("<option value=" + task1.getId() + ">" + task1.getTaskName() + "</option>");
    }

    @Test
    void shouldGetAllProjectOptions() throws SQLException, IOException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        // Create example tasks
        Project p1 = exampleProject();
        p1.setId(projectDao.insert(p1));
        Project p2 = exampleProject();
        p2.setId(projectDao.insert(p2));
        Project p3 = exampleProject();
        p3.setId(projectDao.insert(p3));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projectOptions" );
        assertThat(client.getResponseBody())
                .contains("<option value=" + p1.getId() + ">" + p1.getProjectName() + "</option>");
    }

    @Test
    void shouldGetAllMemberOptions() throws SQLException, IOException {
        MemberDao memberDao = new MemberDao(dataSource);
        // Create example tasks
        Member m1 = exampleMember();
        m1.setId(memberDao.insert(m1));
        Member m2 = exampleMember();
        m2.setId(memberDao.insert(m2));
        Member m3 = exampleMember();
        m3.setId(memberDao.insert(m3));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/memberOptions" );
        assertThat(client.getResponseBody())
                .contains("<option value=" + m1.getId() + ">" + m1.getName() + "</option>");
    }
}