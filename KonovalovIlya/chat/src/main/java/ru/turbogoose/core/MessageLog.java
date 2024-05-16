package ru.turbogoose.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MessageLog implements Iterable<Message> {
    // sorted by timestamp and then by id
    private final List<Message> messages = Collections.synchronizedList(new LinkedList<>());

    public void addMessage(Message message) {
        int indexToInsert;
        for (indexToInsert = 0; indexToInsert < messages.size(); indexToInsert++) {
            Message msgInLog = messages.get(indexToInsert);
            if (!(message.getTimestamp().after(msgInLog.getTimestamp()) ||
                    message.getTimestamp().equals(msgInLog.getTimestamp()) &&
                    message.getId().compareTo(msgInLog.getId()) < 0)) {
                break;
            }
        }
        messages.add(indexToInsert, message);
    }

    public boolean contains(Message message) {
        for (Message msg : messages) {
            if (msg.equals(message)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Message> iterator() {
        return messages.iterator();
    }
}
