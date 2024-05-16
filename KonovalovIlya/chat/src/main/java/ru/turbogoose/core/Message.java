package ru.turbogoose.core;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Message {
    private UUID id;
    private Date timestamp;
    private String content;
    private String author;

    public Message() {
    }

    public Message(String content, String author) {
        this.content = content;
        this.author = author;
        this.id = UUID.randomUUID();
        this.timestamp = new Date();
    }

    public UUID getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
