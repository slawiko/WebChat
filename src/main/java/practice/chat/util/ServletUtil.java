package practice.chat.util;

import org.json.simple.JSONObject;
import practice.chat.model.MessageStorage;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;

import static practice.chat.util.MessageUtil.MESSAGES;
import static practice.chat.util.MessageUtil.TOKEN;
import static practice.chat.util.MessageUtil.getToken;

public final class ServletUtil {
	public static final String APPLICATION_JSON = "application/json";

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
}
