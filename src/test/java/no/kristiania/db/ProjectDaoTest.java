package no.kristiania.db;

import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.objects.Project;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectDaoTest {
    private ProjectDao projectDao;
    private Random r = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:members;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        projectDao = new ProjectDao(dataSource);
    }

    @Test
    void shouldListAllTasks() throws SQLException {
        Project project1 = exampleProject();
        Project project2 = exampleProject();
        projectDao.insert(project1);
        projectDao.insert(project2);
        assertThat(projectDao.list())
                .extracting(Project::getProjectName)
                .contains(project1.getProjectName(), project2.getProjectName());
    }

    private Project exampleProject() {
        Project p = new Project();
        p.setProjectName(exampleProjectName());
        p.setDesc(exampleProjectDesc());
        return p;
    }

    private String exampleProjectDesc() {
        String[] options = {"We should make great pc for gaming!", "Hello gais! We play csgo with drink!", "To play pc u need to make good pc", "RP is good for heart!"};
        return options[r.nextInt(options.length)];
    }

    private String exampleProjectName() {
        String[] options = {"Build PC", "Drink PC", "Play PC", "RP THAT SHIT"};
        return options[r.nextInt(options.length)];
    }
}
