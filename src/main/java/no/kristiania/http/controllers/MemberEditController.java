package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.controllers.HttpController;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;


public class MemberEditController implements HttpController {
    private MemberDao memberDao;

    public MemberEditController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }


    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage respone = new HttpMessage(getBody());
        respone.write(clientSocket);
    }

    private String getBody() throws SQLException {
        return memberDao.list()
                .stream().map(m -> "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                        "<form method='POST' action='/api/updateMember'>" +
                        "<label>First name: <input type='text' name='first_name' value='" + m.getFirstName() + "' /></label>" +
                        "<label>Last name: <input type='text' name='last_name' value='" + m.getLastName() + "' /></label>" +
                        "<label>Member ID<input type='hidden' name='id' value='" + m.getId() + "'/></label>" +
                        "<label>Email: <input type='text' name='email' value='" + m.getEmail() + "' /></label>" +
                        "<br><button>Edit member</button>" +
                        "</form>" +
                        "</div>")
                .collect(Collectors.joining());
    }
}
