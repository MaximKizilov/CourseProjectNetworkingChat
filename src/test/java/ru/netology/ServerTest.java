package ru.netology;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.netology.server.ClientHandler;
import ru.netology.server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerTest {
    @Test
    void messagingBetweenUsersShouldSendMessageToAllUsers() throws IOException, ClassNotFoundException {
        // Arrange
        RxTx mockRxTx = Mockito.mock(RxTx.class);
        when(mockRxTx.rx()).thenReturn("NEW_MESSAGE # Hello, World!");
        Server mockServer = Mockito.mock(Server.class);
        ClientHandler clientHandler = new ClientHandler(new Socket(), mockServer);

        // Act
        clientHandler.messagingBetweenUsers(mockRxTx, "TestUser");

        // Assert
        verify(mockServer, times(1)).sendMessageAllUsers("NEW_MESSAGE # TestUser: Hello, World!\n");
    }

    // Add more test cases for messagingBetweenUsers method

    // Add more unit tests for other methods in ClientHandler


    // Add more test cases for authorization method
}

