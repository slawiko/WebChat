package practice.chat.util;

import org.json.simple.JSONObject;

import practice.chat.storage.MessageStorage;
import practice.chat.model.Message;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;

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
		jsonObject.put(MESSAGES, MessageStorage.getSubMessageByIndex(index));
		jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
		return jsonObject.toJSONString();
	}

	public static void addDefaultData() {
		Message[] defaultMessages = {
			new Message("slawiko", "Hello! How are you"),
			new Message("maz", "Hi, I'm find and you?"),
			new Message("slawiko", "Me too")};
		MessageStorage.addAll(defaultMessages);
	}
}
