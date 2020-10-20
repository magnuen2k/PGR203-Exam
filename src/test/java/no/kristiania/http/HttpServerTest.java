package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpClient;
import no.kristiania.http.HttpServer;
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

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:members;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

   @Test
    void checkStatusCodeOK() throws IOException {
        new HttpServer(33332);
        HttpClient client = new HttpClient("localhost", 33332, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void checkIfNotFound() throws IOException {
        new HttpServer(12222);
        HttpClient client = new HttpClient("localhost", 12222, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void correctContentLength() throws IOException {
        new HttpServer(44444);
        HttpClient client = new HttpClient("localhost", 44444, "/echo?body=RaidShadowLegends");
        assertEquals("17", client.getResponseHeader("Content-Length"));
    }

    @Test
    void correctBodyContent() throws IOException {
        new HttpServer(23234);
        HttpClient client = new HttpClient("localhost", 23234, "/echo?body=BigBrain");
        assertEquals("BigBrain", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        HttpServer server = new HttpServer(5555);
        File contentRoot = new File("target/test-classes");

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 5555, "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void affirmContentTypeHTML() throws IOException {
        HttpServer server = new HttpServer(30006);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("localhost", 30006, "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void affirmContentTypeTxt() throws IOException {
        HttpServer server = new HttpServer(30056);
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "Hei.txt").toPath(), "Hei");

        HttpClient client = new HttpClient("localhost", 30056, "/Hei.txt");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }


    @Test
    void affirmFileNotFound404() throws IOException {
        HttpServer server = new HttpServer(2227);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", 2227, "Hei");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewMember() throws IOException, SQLException {
        HttpServer server = new HttpServer(10008, dataSource);
        HttpClient client = new HttpClient("localhost", 10008, "/api/members", "POST", "first_name=test&last_name=dao&email_address=test%40dao.com");
        assertEquals(200, client.getStatusCode());
        assertThat(server.getMemberNames())
                .extracting(member -> member.getName())
                .contains("test dao");
    }

    @Test
    void shouldReturnExistingMembers() throws IOException, SQLException {
        HttpServer server = new HttpServer(10009, dataSource);
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = new Member();
        member.setFirstName("Arild");
        member.setLastName("Svensen");
        member.setEmail("arild@sykkel.no");
        memberDao.insertMember(member);
        HttpClient client = new HttpClient("localhost", 10009, "/api/projectMembers");
        assertThat(client.getResponseBody()).contains("<li>Name: Arild Svensen - Email: arild@sykkel.no</li>");
    }
}