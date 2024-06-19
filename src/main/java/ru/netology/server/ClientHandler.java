package ru.netology.server;

import ru.netology.MessageType;
import ru.netology.RxTx;

import java.io.IOException;
import java.net.Socket;


public class ClientHandler extends Thread {
    //    Logger logger = Logger.getInstance();
    private final Socket socket;
    private final Server server;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public   String authorization(RxTx rxTx) throws ClassNotFoundException {
        try {
            while (true) {
            rxTx.txAndLog(MessageType.REQUEST_NAME_USER.toString() + " #" + " Здравствуйте, для регистрации, введите свое Имя и Фамилию через пробел" + "\n");
                String response = rxTx.rx();
                String[] msg = response.split(" # ");
                String messageType = msg[0];
                String username = msg[1];
                if (server.allUserChat.containsKey(username)) {
                    rxTx.txAndLog(MessageType.NAME_USED + " # " + "Такой пользователь уже авторизован! Нажмите ENTER чтобы продолжить" + "\n");
                    continue;
                }
                if (messageType.equals(MessageType.RESPONSE_NAME_USER.toString()) && username != null && !response.isEmpty()) {
                    server.allUserChat.putIfAbsent(response.substring(response.indexOf("#") + 1).trim(), rxTx);
                    server.sendMessageAllUsers(MessageType.CONNECTED + " # " + username + " присоединился к чату!" + "\n");
                    rxTx.txAndLog(MessageType.USER_ACCEPTED + " # " + "Уже подключены: " + server.allUserChat.keySet() + "\n");
                    return username;
                } else {
                    rxTx.txAndLog(MessageType.NAME_USED + " # " + "Имя пользователя не может оставаться пустым!!" + "\n");
                }
        }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void messagingBetweenUsers(RxTx rxTx, String user) {
        while (true) {
            try {
                String message = rxTx.rx();
                String func = message.substring(0, message.indexOf(" #"));
                if (func.equals(MessageType.NEW_MESSAGE.toString())) {
                    String text = message.substring(message.indexOf("#") + 1);
                    String textmessage = String.format("%s: %s\n", user, text);
                    server.sendMessageAllUsers(MessageType.NEW_MESSAGE + " # " + textmessage);
                }
                if (func.equals(MessageType.DISCONNECTED.toString())) {
                    server.sendMessageAllUsers(MessageType.DISCONNECTED + user + " покинул чат!");
                    server.allUserChat.remove(user);
                    rxTx.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void run() {
        try {
            RxTx rxTx = new RxTx(socket);
            String nameUser = authorization(rxTx);
            messagingBetweenUsers(rxTx, nameUser);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
