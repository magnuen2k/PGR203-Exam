package no.kristiania.http.controllers;

import no.kristiania.db.objects.MemberTasks;
import no.kristiania.db.daos.MemberTasksDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberTaskUpdateController implements HttpController {
    private MemberTasksDao memberTasksDao;

    public MemberTaskUpdateController(MemberTasksDao memberTasksDao) {
        this.memberTasksDao = memberTasksDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    private HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString responseParameter = new QueryString(request.getBody());

        Long memberId = Long.valueOf(responseParameter.getParameter("memberId"));
        Long taskId = Long.valueOf(responseParameter.getParameter("taskId"));

        MemberTasks memberTasks = memberTasksDao.retrieve(memberId);
        memberTasks.setMemberId(memberId);
        memberTasks.setTaskId(taskId);

        HttpMessage response = new HttpMessage();

        // Check if member is already assigned to task
        try {
            memberTasksDao.insert(memberTasks);
            response.setStartLine("HTTP/1.1 302 Redirect");
            response.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        } catch (PSQLException error) {
            response = new HttpMessage("Member is already assigned to this task!");
        }
        return response;
    }
}
