package chat.practice.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageStorage {
	private static List<Message> storage = Collections.synchronizedList(new ArrayList<Message>());

	public static void addMessage(Message message) {
		storage.add(message);
	}

	public static int getSize() {
		return storage.size();
	}

	public static Message getMessageByIndex(int index) {
		return storage.get(index);
	}

	public static Message getMessageById(String id) {
		for (Message mess : storage) {
			if (mess.getId().equals(id)) {
				return mess;
			}
		}
		return null;
	}
}
