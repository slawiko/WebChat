package practice.chat.util;

import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import practice.chat.storage.XMLStorage;
import practice.chat.model.Message;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static practice.chat.util.MessageUtil.getToken;

public final class ServletUtil {
	public static final String APPLICATION_JSON = "application/json";
	public static final String TOKEN = "token";
	public static final String POST = "post";
	public static final String DELETE = "delete";
	public static final String PUT = "put";
	private static final String MESSAGES = "messages";

	private ServletUtil() {
	}

	public static String getMessageBody(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static String getServerResponse(int index) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MESSAGES, XMLStorage.getSubNodeList(index));
		jsonObject.put(TOKEN, getToken(XMLStorage.getStorageSize()));
		return jsonObject.toJSONString();
	}

	public static void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
		if (XMLStorage.isExist()) {
			List<Message> messages = XMLStorage.getListMessages();
			for(Message message : messages) {
				System.out.println("Read a message from history.xml: " + message.getDate() + " {" + message.getAuthor() + "} : {" + message.getText() + "}" );
			}
		} else {
			XMLStorage.createStorage();
		}
	}
}