package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class MemberGetController implements HttpController {
    private final MemberDao memberDao;

    public MemberGetController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        return memberDao.list()
                .stream().map(m -> "<li>Name: " + m.getName() + " - Email: " + m.getEmail() + "</li>")
                .collect(Collectors.joining());
    }
}
