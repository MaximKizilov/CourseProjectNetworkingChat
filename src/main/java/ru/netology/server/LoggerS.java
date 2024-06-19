package ru.netology.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerS {
    static final File FILEWITHLOGGER = new File("src/main/java/ru/netology/server/File.log");
    private static LoggerS INSTANCE = null;

    private LoggerS() {
    }

    public static LoggerS getInstance() {
        if (INSTANCE == null) {
            synchronized (LoggerS.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoggerS();
                }
            }
        }
        return INSTANCE;
    }

    public void log(String message) {
        try (BufferedWriter filewriter = new BufferedWriter(new FileWriter(FILEWITHLOGGER, true))) {
            filewriter.write("["  /*+ message.getTypeMessage()*/ + "#" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(" HH:mm dd-MM-yyyy")) + "]" + " === " + message);
            filewriter.append('\n');
            filewriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
