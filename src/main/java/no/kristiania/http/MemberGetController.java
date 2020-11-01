package no.kristiania.http;
import no.kristiania.db.MemberDao;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class MemberGetController implements HttpController{
    private MemberDao memberDao;

    public MemberGetController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "<ul>";
        body += memberDao.list()
               .stream().map(m -> "<li>Name: " + m.getName() + " - Email: " + m.getEmail() + "</li>")
               .collect(Collectors.joining());
        body += "</ul>";
        return body;
    }
}
