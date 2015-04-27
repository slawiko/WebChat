package practice.chat.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static practice.chat.util.MessageUtil.*;
import static practice.chat.util.ServletUtil.getServerResponse;
import static practice.chat.util.ServletUtil.addDefaultData;
import static practice.chat.util.ServletUtil.TOKEN;
import static practice.chat.util.ServletUtil.DELETED;

import practice.chat.model.Message;
import practice.chat.storage.MessageStorage;
import practice.chat.util.ServletUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		addDefaultData();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter(TOKEN);

		if (token != null && !"".equals(token)) {
			int index = getIndex(token);
			String messages = getServerResponse(index);
			response.setContentType(ServletUtil.APPLICATION_JSON);
			PrintWriter out = response.getWriter();
			out.print(messages);
			out.flush();
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			MessageStorage.addMessage(message);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			String id = message.getId();
			Message messageToUpdate = MessageStorage.getMessageById(id);
			if (messageToUpdate != null) {
				messageToUpdate.setText(message.getText());
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
			}
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			message.setAuthor(DELETED);
			message.setText(DELETED);
			MessageStorage.setMessageById(message);
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
