package practice.chat.controller;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
import practice.chat.dao.MessageDao;
import practice.chat.model.Message;
import practice.chat.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static practice.chat.util.MessageUtil.*;

//import static practice.chat.util.ServletUtil.*;
//import practice.chat.storage.XMLStorage;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
	private static final String TOKEN = "token";
	private static final String VERSION = "version";
	private static final String MESSAGES = "messages";
	private Integer serverVersion;
	private static Logger logger = Logger.getLogger(ChatServlet.class);
	private MessageDao messageDao;

	@Override
	public void init() throws ServletException {
		try {
			this.messageDao = new MessageDao();
			versionUpdate();
			loadHistory();
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
		try {
			if (token != null && !"".equals(token)) {
				int index = getIndex(token);
				logger.info("Index: " + index);
				String messages = null;
				if (serverVersion.toString().equals(clientVersion) && index == messageDao.selectAll().size()) {
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				} else {
					if (serverVersion.toString().equals(clientVersion)) {
						messages = getServerResponse(index, serverVersion);
					} else {
						messages = getServerResponse(0, serverVersion);
					}
				}
				response.setContentType(ServletUtil.APPLICATION_JSON);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.print(messages);
				out.flush();
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' and 'version' parameters needed");
			}
		} catch (SAXException | ParserConfigurationException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json = stringToJson(data);
			Message temp = jsonToMessage(json);
			Message message = new Message(temp.getAuthor(), temp.getText());
			logger.info("Post message: {" + message.getAuthor() + "} : {" + message.getText() + "}");
			//XMLStorage.addData(message);
			messageDao.add(message);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			if (message != null) {
				//XMLStorage.updateData(message);
				messageDao.update(message);
				logger.info("Put message: {" + message.getId() + "} {" + message.getAuthor() + "} : {" + message.getText() + "}");
				versionUpdate();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
				logger.error("BAD_REQUEST: Message does not exist");
			}
		} catch (ParseException e) {
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
			//XMLStorage.removeData(message);
			messageDao.delete(message);
			versionUpdate();
		} catch (ParseException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void versionUpdate() {
		if (serverVersion != null) {
			serverVersion++;
			logger.info("Version changed: " + serverVersion);
		} else {
			serverVersion = 0;
		}
	}

	private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
		/*if (XMLStorage.isExist()) {
			List<Message> messages = XMLStorage.getListMessages();
			for(Message message : messages) {
				logger.info("Read a message from history.xml: " + message.getDate() + " {" + message.getAuthor() + "} : {" + message.getText() + "}" );
			}
		} else {
			XMLStorage.createStorage();
		}*/
		List<Message> messages = messageDao.selectAll();
		logger.info(messages);
	}

	public String getServerResponse(int index, Integer version) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		List<Message> messages = messageDao.selectAll();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MESSAGES, messages.subList(index, messages.size()));
		jsonObject.put(TOKEN, getToken(messages.size()));
		jsonObject.put(VERSION, version.toString());
		return jsonObject.toJSONString();
	}
}