package chat.practice.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageStorage {
	private static List<Message> storage = Collections.synchronizedList(new ArrayList<Message>());

	public static void addMessage(Message message) {
		storage.add(message);
	}

}
