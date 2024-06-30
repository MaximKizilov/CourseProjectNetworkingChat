package ru.netology.server;


import ru.netology.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    static final File FILEWITHSOCKET = new File("src/main/resources/settings.txt");
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static Set<ClientHandler> allUserChat = Collections.newSetFromMap(new ConcurrentHashMap<>());
    static LoggerS loggerS = LoggerS.getInstance();


    public Server(int port, String info) {
        try (ServerSocket serverSocket = new ServerSocket(port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Сервер запущен на сокете " + info);
            loggerS.log("Сервер запущен на сокете " + info);

            Thread readThread = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        System.out.println("Для выхода напиши '/exit'");
                        String serverCommand = reader.readLine();
                        if (serverCommand.equalsIgnoreCase("/exit")) {
                            System.out.println(MessageType.INFO + " # Server initializes output");
                            serverSocket.close();
                            loggerS.log(MessageType.INFO + " #  EXIT");
                            break;
                        }
                    } catch (IOException e) {
                        return;
                    }
                }
            });
            readThread.start();

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new ClientHandler(clientSocket));
                loggerS.log("Новое подключение: " + clientSocket.getInetAddress().toString().replace("/", " "));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (FILEWITHSOCKET.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(FILEWITHSOCKET))) {
                String s;
                while ((s = br.readLine()) != null) {
                    int port = Integer.parseInt(s.substring(s.indexOf(":")).replaceAll(":", ""));
                    Server server = new Server(port, s);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Создайте файл src/main/resources/settings.txt c записью в формате: 'ipv4:port' !");
        }
    }

}

