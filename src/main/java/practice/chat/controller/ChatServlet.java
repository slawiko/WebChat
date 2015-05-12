package practice.chat.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import static practice.chat.util.MessageUtil.*;
import static practice.chat.util.ServletUtil.*;

import org.xml.sax.SAXException;
import practice.chat.model.Message;
import practice.chat.storage.XMLStorage;
import practice.chat.util.ServletUtil;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
	private static final String TOKEN = "token";
	private static final String VERSION = "version";
	private Integer serverVersion;
	private static final Logger logger = Logger.getLogger(ChatServlet.class);

	@Override
	public void init() throws ServletException {
		try {
			versionUpdate();
			loadHistory(logger);
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			logger.error(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter(TOKEN);
		logger.info("Token: " + token);
		String clientVersion = request.getParameter(VERSION);
		logger.info("ClientVersion: " + clientVersion);
		if (token != null && !"".equals(token)) {
			int index = getIndex(token);
			String messages = null;
			try {
				if (serverVersion.toString().equals(clientVersion)) {
					messages = getServerResponse(index, serverVersion);
					logger.info("Get messages from history: " + messages);
				} else {
					messages = getServerResponse(0, serverVersion);
					logger.info("Get messages from history: " + messages);
				}
			} catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
				logger.error(e);
			}
			response.setContentType(APPLICATION_JSON);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(messages);
			out.flush();
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
			logger.error("BAD_REQUEST: 'token' parameter needed");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json = stringToJson(data);
			Message temp = jsonToMessage(json);
			Message message = new Message(temp.getAuthor(), temp.getText());
			logger.info("Post message: {" + message.getAuthor() + "} : {" + message.getText() + "}");
			XMLStorage.addData(message);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			if (message != null) {
				XMLStorage.updateData(message);
				logger.info("Put message: {" + message.getId() + "} {" + message.getAuthor() + "} : {" + message.getText() + "}");
				versionUpdate();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
				logger.error("BAD_REQUEST: Message does not exist");
			}
		} catch (ParseException | SAXException | ParserConfigurationException | TransformerException | XPathExpressionException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			logger.info("Delete message: " + message.getDate() + " {" + message.getAuthor() + "} : {" + message.getText() + "}");
			XMLStorage.removeData(message);
			versionUpdate();
		} catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void versionUpdate(){
		if (serverVersion != null) {
			serverVersion++;
			logger.info("Version changed: " + serverVersion);
		} else {
			serverVersion = 0;
		}
	}
}