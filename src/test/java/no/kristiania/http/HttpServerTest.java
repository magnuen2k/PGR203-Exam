package no.kristiania.http;

import no.kristiania.db.objects.Member;
import no.kristiania.db.daos.MemberDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

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
    void shouldReturnExistingMembers() throws IOException, SQLException {
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = new Member();
        member.setFirstName("Arild");
        member.setLastName("Svensen");
        member.setEmail("arild@sykkel.no");
        memberDao.insertMember(member);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/projectMembers");
        assertThat(client.getResponseBody()).contains("<li>Name: Arild Svensen - Email: arild@sykkel.no</li>");
    }
}