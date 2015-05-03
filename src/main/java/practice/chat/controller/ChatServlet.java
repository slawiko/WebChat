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

	@Override
	public void init() throws ServletException {
		try {
			versionUpdate();
			loadHistory();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter(TOKEN);
		String clientVersion = request.getParameter(VERSION);
		if (token != null && !"".equals(token)) {
			int index = getIndex(token);
			String messages = null;
			try {
				if (serverVersion.toString().equals(clientVersion)) {
					messages = getServerResponse(index, serverVersion);
				} else {
					messages = getServerResponse(0, serverVersion);
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			response.setContentType(APPLICATION_JSON);
			response.setCharacterEncoding("UTF-8");
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
			Message temp = jsonToMessage(json);
			Message message = new Message(temp.getAuthor(), temp.getText());
			System.out.println("Post message: " + message.getDate() + " {" + message.getAuthor() + "} : {" + message.getText() + "}");
			XMLStorage.addData(message);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			if (message != null) {
				XMLStorage.updateData(message);
				versionUpdate();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
			}
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			System.out.println("Delete message: " + message.getDate() + " {" + message.getAuthor() + "} : {" + message.getText() + "}");
			XMLStorage.removeData(message);
			versionUpdate();
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	private void versionUpdate(){
		if (serverVersion != null) {
			serverVersion++;
		} else {
			serverVersion = 0;
		}
	}
}