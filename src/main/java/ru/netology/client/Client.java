package ru.netology.client;

import ru.netology.MessageType;
import ru.netology.RxTx;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final File FILEWITHSOCKET = new File("src/main/resources/settings.txt");
    private static String IPv4;
    private static int PORT;

    static LoggerC loggerC = LoggerC.getInstance();


    public Client(String IPv4, int PORT) {
        this.IPv4 = IPv4;
        this.PORT = PORT;
    }

    public static void main(String[] args) {

        Client client = null;
        if (FILEWITHSOCKET.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(FILEWITHSOCKET))) {
                String s;
                while ((s = br.readLine()) != null) {
                    String[] parts = s.split(":");
                    String address = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    client = new Client(address, port);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Создайте файл src/main/resources/settings.txt c записью в формате: 'ipv4:port' !");

        }

        try (Socket socket = new Socket(IPv4, PORT);
             RxTx rxTx = new RxTx(socket);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)) ){
            loggerC.log("Клиент подключен к серверу!");
            // Ожидание и обработка ответа от сервера
            while (true) {
                String request = rxTx.rx();
                loggerC.log(request);
                String[] requestParts = request.split(" # ");
                MessageType messageType = MessageType.valueOf(requestParts[0]);
                String messageContent = requestParts[1];

                switch (messageType) {
                    case REQUEST_NAME_USER:
                        System.out.println(messageContent);
                        String userInput = reader.readLine().trim();
                        rxTx.txAndLog(MessageType.RESPONSE_NAME_USER + " # " + userInput + "\n");
                        loggerC.log(MessageType.RESPONSE_NAME_USER + " # " + userInput + "\n");
                        break;

                    case NAME_USED:
                        System.out.println(messageContent);
                        userInput = reader.readLine().trim();
                        break;


                    case CONNECTED:
                        System.out.println(messageContent);
                        System.out.println("Для выхода из чата введите '/exit'.");
                        break;

                    case USER_ACCEPTED:
                        System.out.println(messageContent.replace("[", "").replace("]", ""));

                        // Запуск потока для приема сообщений от сервера
                        new Thread(() -> {
                            try {
                                while (true) {
                                    String serverMessage = rxTx.rx();
                                    System.out.println(serverMessage);
                                    loggerC.log(serverMessage);
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }).start();

                        // Цикл отправки сообщений от клиента
                        while (true) {
                            String clientMessage = reader.readLine().trim();
                            if ("/exit".equals(clientMessage)) {
                                rxTx.txAndLog(MessageType.DISCONNECTED + " #" + "\n");
                                loggerC.log("Отключение от сервера!");
                                break;
                            }
                            rxTx.txAndLog(MessageType.NEW_MESSAGE + " # " + clientMessage + "\n");
                            loggerC.log(MessageType.NEW_MESSAGE + " # " + clientMessage);
                        }
                        return;

                    default:
                        System.err.println("Неизвестный тип сообщения от сервера: " + messageType);
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}


