import java.util.UUID;

public class Message {

    private String id;
    private String author;
    private String text;

    public Message() {
        this.id = "";
        this.author = "";
        this.text = "";
    }

    public Message(String id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
    }

    public Message(String author, String text) {
        this.text = text;
        this.author = author;
        this.id = uniqueId();
    }

    public Message(Message message) {
        this.id = message.getId();
        this.author = message.getAuthor();
        this.text = message.getText();
    }

    public Message(Object o) {
        Message message = (Message)o;
        this.id = message.getId();
        this.author = message.getAuthor();
        this.text = message.getText();
    }

    public String uniqueId() {
        String id = UUID.randomUUID().toString();
        return id;
    }

    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"author\":\"" + this.author + "\",\"text\":" + this.text + "}";
    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return this.author;
    }
    public void setUsername(String author) {
        this.author = author;
    }

    public String getText() {
        return this.text;
    }
    public void setMessage(String text) {
        this.text = text;
    }
}