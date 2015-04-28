package practice.chat.util;

import org.w3c.dom.NodeList;
import practice.chat.model.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.soap.Node;

public final class MessageUtil {
	private static final String TN = "TN";
	private static final String EN = "EN";
	public static final String ID = "id";
	public static final String AUTHOR = "author";
	public static final String TEXT = "text";
	public static final String DATE = "date";
	public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";

	public static String getToken(int index) {
		Integer number = index * 8 + 11;
		return TN + number + EN;
	}

	public static int getIndex(String token) {
		return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
	}

	public static JSONObject stringToJson(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(data.trim());
	}

	public static Message jsonToMessage(JSONObject json) {
		Object id = json.get(ID);
		Object author = json.get(AUTHOR);
		Object text = json.get(TEXT);
		return new Message(id.toString(), author.toString(), text.toString());
	}

	public static Message nodeToMessage(Node node) {
		NodeList childNodes = node.getChildNodes();
		Message message = null;
		message.setId(node.getAttributes().toString());
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (AUTHOR.equals(childNodes.item(i).getNodeName())) {
				message.setAuthor(childNodes.item(i).getTextContent());
			}else if (TEXT.equals(childNodes.item(i).getNodeName())) {
				message.setText(childNodes.item(i).getTextContent());
			}else if (DATE.equals(childNodes.item(i).getNodeName())) {
				message.setDate(childNodes.item(i).getTextContent());
			}
		}
		return message;
	}
}