package ru.netology.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerC {
    static final File FILEWITHLOGGER = new File("src/main/java/ru/netology/client/File.log");
    private static LoggerC INSTANCE = null;

    private LoggerC() {
    }

    public static LoggerC getInstance() {
        if (INSTANCE == null) {
            synchronized (LoggerC.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoggerC();
                }
            }
        }
        return INSTANCE;
    }

    public void log(String message) {
        if (FILEWITHLOGGER.exists()) {
        try (BufferedWriter filewriter = new BufferedWriter(new FileWriter(FILEWITHLOGGER, true))) {
            filewriter.write("["  /*+ message.getTypeMessage()*/ + "#" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(" HH:mm dd-MM-yyyy")) + "]" + " === " + message);
            filewriter.append('\n');
            filewriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }else{
            try {
                FILEWITHLOGGER.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
