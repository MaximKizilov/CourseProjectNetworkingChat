package ru.netology;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.server.ClientHandler;
import ru.netology.server.Server;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ClientHandlerTest {

    @Test
    void testAuthorizationWhenNameEmpty() throws IOException {
        Socket socket = Mockito.mock(Socket.class);
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);

        when(socket.getInputStream()).thenReturn(System.in);
        when(socket.getOutputStream()).thenReturn(System.out);
        when(socket.isClosed()).thenReturn(false);

        ClientHandler clientHandler = new ClientHandler(socket);

        clientHandler.authorization(new String[]{"RESPONSE_NAME_USER", ""}, printWriter);

        Mockito.verify(printWriter).println("NAME_USED # Имя пользователя не может оставаться пустым!!");
    }
    @Test
    void testMessagingBetweenUsersNewMessage() throws IOException {
        Socket socket = Mockito.mock(Socket.class);
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);

        when(socket.getInputStream()).thenReturn(System.in);
        when(socket.getOutputStream()).thenReturn(System.out);
        when(socket.isClosed()).thenReturn(false);

        ClientHandler clientHandler = new ClientHandler(socket);
        clientHandler.setNameUser("User1");

        clientHandler.messagingBetweenUsers(new String[]{"NEW_MESSAGE", "Hello"});

        Mockito.verify(printWriter).println("NEW_MESSAGE # User1: Hello");
    }

}


