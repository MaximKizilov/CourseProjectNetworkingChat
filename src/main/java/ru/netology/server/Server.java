package ru.netology.server;

import ru.netology.RxTx;

import java.io.*;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    static final File FILEWITHSOCKET = new File("src/main/resources/settings.txt");
    public final ConcurrentHashMap<String, RxTx> allUserChat = new ConcurrentHashMap<>();

    public Server(int port, String info) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на сокете " + info);
            while (true) {
                new ClientHandler(serverSocket.accept(), this).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
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

    public void sendMessageAllUsers(String message) {
        for (Map.Entry<String, RxTx> user : allUserChat.entrySet()) {
            user.getValue().txAndLog(message);
//            }
        }
    }
}