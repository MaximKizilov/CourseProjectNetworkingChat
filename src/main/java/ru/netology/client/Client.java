package ru.netology.client;

import ru.netology.MessageType;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final File FILEWITHSOCKET = new File("src/main/resources/settings.txt");
    static LoggerC loggerC = LoggerC.getInstance();
    private static String IPv4;
    private static int PORT;

    public static void main(String[] args) {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILEWITHSOCKET))) {
            String[] parts = fileReader.readLine().split(":");
            IPv4 = parts[0];
            PORT = Integer.parseInt(parts[1]);
        } catch (IOException e) {
            System.out.println("Создайте файл src/main/resources/settings.txt c записью в формате: 'ipv4:port' !");
            return;
        }


        try (Socket socket = new Socket(IPv4, PORT);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            loggerC.log("Клиент подключен к серверу!");

            String serverResponse;
            while ((serverResponse = serverIn.readLine()) != null) {
                loggerC.log(serverResponse);
                String[] requestParts = serverResponse.split(" # ");
                MessageType messageType = MessageType.valueOf(requestParts[0]);
                String messageContent = requestParts[1];
                switch (messageType) {
                    case REQUEST_NAME_USER:
                        System.out.println(messageContent);
                        String name = consoleReader.readLine().trim();
                        serverOut.println(MessageType.RESPONSE_NAME_USER + " # " + name);
                        break;
                    case CONNECTED, INFO, NAME_USED, DISCONNECTED , INVALID_INPUT:
                        System.out.println(messageContent);
                        break;
                    case USER_ACCEPTED, NEW_MESSAGE:
                        System.out.println(messageContent);
                        enterNewMessageLoop(serverOut, consoleReader, socket, MessageType.NEW_MESSAGE).start();
                        break;

                    default:
                        System.err.println("Неизвестный тип сообщения от сервера: " + messageType);
                        break;
                }
//                if(consoleReader)
            }
        } catch (IOException e) {
            loggerC.log("Клиент отключен от сервера");
            System.exit(0);
        }
    }


    private static Thread enterNewMessageLoop(PrintWriter out, BufferedReader reader, Socket socket, MessageType type) throws IOException {
        return new Thread(() -> {
            String clientMessage;
            try {
                while ((clientMessage = reader.readLine().trim()) != null) {
                    if ("/exit".equals(clientMessage)) {
                        out.println(MessageType.DISCONNECTED + " # /exit");
                        loggerC.log("Отключение от сервера!");
                        socket.close();
                        break;
                    }
                    out.println(type + " # " + clientMessage);
                    loggerC.log(type + " # " + clientMessage);
                }
                } catch(IOException e){
                    throw new RuntimeException(e);
                }


        });
    }
}

