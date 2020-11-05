package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.db.daos.MemberTasksDao;
import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.objects.MemberTasks;
import no.kristiania.db.objects.Task;
import no.kristiania.db.daos.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskGetController implements HttpController {
    private TaskDao taskDao;
    private MemberTasksDao memberTasksDao;
    private MemberDao memberDao;
    private ProjectDao projectDao;

    public TaskGetController(TaskDao taskdao, MemberTasksDao memberTasksDao, MemberDao memberDao, ProjectDao projectDao) {
        this.taskDao = taskdao;
        this.memberTasksDao = memberTasksDao;
        this.memberDao = memberDao;
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody(request));
        response.write(clientSocket);
    }

    public String getBody(HttpMessage request) throws SQLException {

        String requestLine = request.getStartLine();
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');

        if(questionPos != -1 ) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos+1));
            Long memberId = Long.parseLong(queryString.getParameter("memberId"));
            boolean status = Boolean.parseBoolean(queryString.getParameter("status"));
            return memberTasksDao.filter(memberId, status)
                    .stream().map(mt -> {
                        try {
                            Task t = taskDao.retrieve(mt.getTaskId());
                            String projectName = "Not assigned";
                            if(t == null) {
                                return null;
                            }
                            if(t.getProjectId() != null) {
                                projectName = projectDao.retrieve(t.getProjectId()).getProjectName();
                            }
                            return "<br>" + projectName + " - " + t.getTaskName() + " - " + (t.getTaskStatus() ? "Active" : "Inactive") + " - " + memberDao.retrieve(mt.getMemberId()).getName();
                        } catch (SQLException throwable) {
                            throwable.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.joining());
        }

        // TODO Try using streams
        String body = "";
        for(Task t : taskDao.list()) {
            // System.out.println("--------------------");
            body += "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                    "<p>Task: " + t.getTaskName() + " - " + (t.getTaskStatus() ? "Active" : "Inactive") + "</p>" +
                    "<p>Description: " + t.getDesc() + "</p>";

            body += "<select>";
            List<Long> memberIds = memberDao.getMembersOnTask(t.getId());
//            if(memberIds.size() != 0) {
//                for (int i = 0; i < memberIds.size(); i++) {
//                    System.out.println("Task" + t.getId()  + "Contains: " + memberIds.get(i));
//                }
//            }
            for (int i = 0; i < memberIds.size(); i++) {
                    body += "<option value='" + memberDao.retrieve(memberIds.get(i)).getId() + "'>" + memberDao.retrieve(memberIds.get(i)).getName() + "</option>";
            }
            body += "</select></div>";
        }

        return body;

        /*return taskDao.list()
                .stream().map(t -> "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                        "<p>Task: " + t.getTaskName() + " - " + (t.getTaskStatus() ? "Active" : "Inactive") + "</p>" +
                        "<p>Description: " + t.getDesc() + "</p>" +
                        "</div>")
                .collect(Collectors.joining());*/
    }
}
