package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class MemberOptionsController implements HttpController {
    private final MemberDao memberDao;

    public MemberOptionsController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    private String getBody() throws SQLException {
        return memberDao.list()
                .stream().map(m -> "<option value=" + m.getId() + ">" + m.getName() + "</option>")
                .collect(Collectors.joining());
    }
}
