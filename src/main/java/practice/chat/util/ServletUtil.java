package practice.chat.util;

import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import practice.chat.model.Message;
import practice.chat.storage.XMLStorage;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static practice.chat.util.MessageUtil.getToken;

public final class ServletUtil {
	public static final String APPLICATION_JSON = "application/json";
	private static final String TOKEN = "token";
	private static final String MESSAGES = "messages";
	private static final String VERSION = "version";

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
}