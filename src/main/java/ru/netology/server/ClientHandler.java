package ru.netology.server;

import ru.netology.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;

import static ru.netology.server.Server.allUserChat;

public class ClientHandler implements Runnable {
    static LoggerS loggerS = LoggerS.getInstance();

    private final Socket SOCKET;
    private String nameUser = "Anonimus";

    public ClientHandler(Socket socket) {
        this.SOCKET = socket;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public void sendMessageAllUsers(String message) {
        for (ClientHandler user : allUserChat) {
            if (!user.equals(this)) {
                PrintWriter userOut = null;
                try {
                    userOut = new PrintWriter(user.SOCKET.getOutputStream(), true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                userOut.println(message);
            }
        }
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
             PrintWriter out = new PrintWriter(SOCKET.getOutputStream(), true)) {
            while (!SOCKET.isClosed()) {
                String response;
                if (this.getNameUser().equals("Anonimus")) {
                    out.println(MessageType.REQUEST_NAME_USER + " #" + " Здравствуйте, для регистрации, введите свое Имя и Фамилию через пробел");
                    loggerS.log(MessageType.REQUEST_NAME_USER + " #" + " Здравствуйте, для регистрации, введите свое Имя и Фамилию через пробел");
                    Thread.sleep(Duration.ofSeconds(3));
                    response = in.readLine();
                    String[] msg = response.split(" # ");
                    authorization(msg, out);
                } else
                    response = in.readLine();
                String[] msg = response.split(" # ");
                messagingBetweenUsers(msg);
            }
        } catch (IOException e) {
            System.err.println("Error occurred in client connection: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //   авторизация Клиента
    public void authorization(String[] msg, PrintWriter out) {
        if (msg.length == 2 && msg[0].equals(MessageType.RESPONSE_NAME_USER.toString())) {
            String username = msg[1].trim();
            if (username.isEmpty()) {
                out.println(MessageType.NAME_USED + " # " + "Имя пользователя не может оставаться пустым!!");
                loggerS.log(MessageType.NAME_USED + " # " + "Имя пользователя не может оставаться пустым!!");
            } else if (allUserChat.stream().anyMatch(u -> u.getNameUser().equals(username))) {
                out.println(MessageType.NAME_USED + " # " + "Такой пользователь уже авторизован!");
                loggerS.log(MessageType.NAME_USED + " # " + "Такой пользователь уже авторизован!");
            } else {
                setNameUser(username);
                allUserChat.add(this);
                out.println(MessageType.INFO + " # " + "Добро пожаловать! Для выхода из чата напишите '/exit' !");
                sendMessageAllUsers(MessageType.CONNECTED + " # " + nameUser + " присоединился к чату!");
                loggerS.log(MessageType.CONNECTED + " # " + nameUser + " присоединился к чату!");
                out.println(MessageType.USER_ACCEPTED + " # " + "Уже подключены: " + allUserChat);
            }
        } else {
            out.println(MessageType.INVALID_INPUT + " # " + "Проверьте тип сообщения, и повторите попытку.");
            loggerS.log(MessageType.INVALID_INPUT + " # " + "Проверьте тип сообщения, и повторите попытку.");

        }
    }

    public synchronized void messagingBetweenUsers(String[] msg) {
        if (msg[0].equals(MessageType.NEW_MESSAGE.toString())) {
            String textmessage = String.format("%s: %s", getNameUser(), msg[1]);
            sendMessageAllUsers(MessageType.NEW_MESSAGE + " # " + textmessage);
            loggerS.log(MessageType.NEW_MESSAGE + " # " + textmessage);
        } else if (msg[0].equals(MessageType.DISCONNECTED.toString()) && msg[1].equals("/exit")) {
            sendMessageAllUsers(MessageType.DISCONNECTED + " # " + getNameUser() + " покинул чат!");
            loggerS.log(MessageType.DISCONNECTED + " # " + getNameUser() + " покинул чат!");
            allUserChat.remove(this);
            try {
                loggerS.log("Сокет закрыт!");
                SOCKET.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String toString() {
        return "ClientHandler{" + nameUser + SOCKET.getInetAddress().toString().replace("/", " ") + ":" + SOCKET.getPort() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(SOCKET, that.SOCKET) && Objects.equals(nameUser, that.nameUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SOCKET, nameUser);
    }
}



