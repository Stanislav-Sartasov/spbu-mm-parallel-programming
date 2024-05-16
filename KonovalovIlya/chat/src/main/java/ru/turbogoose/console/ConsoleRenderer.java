package ru.turbogoose.console;

import ru.turbogoose.core.MessageLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConsoleRenderer {
    private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");

    public void render(MessageLog messageLog) {
        System.out.println("------------------------ Messages ------------------------");
        messageLog.forEach(msg -> System.out.printf("[%s] %s: %s%n",
                dateFormat.format(msg.getTimestamp()), msg.getAuthor(), msg.getContent()));
        System.out.printf("----------------------------------------------------------%n%n");
    }
}
