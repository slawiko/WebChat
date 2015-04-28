package practice.chat.model;

import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

import static practice.chat.util.MessageUtil.DATE_FORMAT;

public class Message {
    private String id;
    private String author;
    private String text;
    private String date;

    private Message() {
    }

    public Message(String id, String author, String text, String date) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.date = date;
    }

    public Message(String id, String author, String text) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.date = takeDate();
    }

    public Message(String author, String text) {
        this.id = uniqueId();
        this.text = text;
        this.author = author;
        this.date = takeDate();
    }

    public Message(Message message) {
        this.id = message.getId();
        this.author = message.getAuthor();
        this.text = message.getText();
        this.date = message.getDate();
    }

    private String uniqueId() {
        return UUID.randomUUID().toString();
    }
    private String takeDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"author\":\"" + this.author + "\",\"text\":\"" + this.text + "\" }";
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
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}