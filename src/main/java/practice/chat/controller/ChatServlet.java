package practice.chat.controller;

import java.io.IOException;

import javax.servlet.AsyncContext;
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
import static practice.chat.controller.RequestQueue.*;

import org.xml.sax.SAXException;
import practice.chat.model.Message;
import practice.chat.storage.XMLStorage;
import practice.chat.util.ServletUtil;

@WebServlet(urlPatterns = "/chat", asyncSupported = true)
public class ChatServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(ChatServlet.class);

	@Override
	public void init() throws ServletException {
		try {
			loadHistory(logger);
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			logger.error(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(300000000);
		addAsyncContext(asyncContext);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json = stringToJson(data);
			Message temp = jsonToMessage(json);
			if (temp != null) {
				Message message = new Message(temp.getAuthor(), temp.getText());
				logger.info("Post message: {" + message.getAuthor() + "} : {" + message.getText() + "}");
				XMLStorage.addData(message);
				response.setStatus(HttpServletResponse.SC_OK);
				replyAllClients();
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message is empty");
				logger.error("BAD_REQUEST: Message is empty");
			}
		} catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
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
				logger.info("Put message: {" + message.getId() + "} {" + message.getAuthor() + "} : {" + message.getText() + "}");
				XMLStorage.updateData(message);
				response.setStatus(HttpServletResponse.SC_OK);
				replyAllClients();
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
			if (message != null) {
				logger.info("Delete message: " + message.getDate() + " {" + message.getAuthor() + "} : {" + message.getText() + "}");
				XMLStorage.removeData(message);
				replyAllClients();
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
				logger.error("BAD_REQUEST: Message does not exist");
			}
		} catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}