package practice.chat.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import practice.chat.storage.MessageStorage;
import practice.chat.storage.XMLStorage;
import practice.chat.model.Message;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static practice.chat.util.MessageUtil.ID;
import static practice.chat.util.MessageUtil.AUTHOR;
import static practice.chat.util.MessageUtil.TEXT;
import static practice.chat.util.MessageUtil.DATE;

import static practice.chat.util.MessageUtil.getToken;

public final class ServletUtil {
	public  static final String APPLICATION_JSON = "application/json";
	public  static final String TOKEN = "token";
	public  static final String DELETED = "DELETED";
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

	public static String getServerResponse(int index) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MESSAGES, MessageStorage.getSubMessagesByIndex(index));
		jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
		return jsonObject.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public static String formResponse(int index) throws ParserConfigurationException, SAXException, IOException {
		List<Message> messages = MessageStorage.getSubMessagesByIndex(index);
		JSONObject jsonObject = new JSONObject();
		JSONArray ArrayMessages = new JSONArray();
		for(int i = 0; i < messages.size(); i++) {
			JSONObject elementOfArray = new JSONObject();
			elementOfArray.put(ID, messages.get(i).getId());
			if(!messages.get(i).getText().isEmpty()) {
				elementOfArray.put(TEXT, messages.get(i).getText());
			}
			if(!messages.get(i).getAuthor().isEmpty()) {
				elementOfArray.put(DATE, messages.get(i).getDate());
				elementOfArray.put(AUTHOR, messages.get(i).getAuthor());
			}
			ArrayMessages.add(elementOfArray);
		}
		jsonObject.put(MESSAGES, ArrayMessages);
		jsonObject.put(TOKEN, getToken(XMLStorage.getStorageSize()));
		return jsonObject.toJSONString();
	}

	public static void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
		if (XMLStorage.isExist()) {
			List<Message> messages = XMLStorage.getMessages();
			for(Message i : messages) {
				System.out.println("Прочитано сообщение из history.xml: " + i.getDate() + " [" + i.getAuthor() + "] : " + i.getText());
			}
			MessageStorage.addAll(messages);
		} else {
			XMLStorage.createStorage();
		}
	}
}