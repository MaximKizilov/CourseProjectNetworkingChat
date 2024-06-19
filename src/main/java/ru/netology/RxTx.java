package ru.netology;



import ru.netology.server.LoggerS;

import java.io.*;
import java.net.Socket;

public class RxTx implements Closeable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final LoggerS loggers;

    public RxTx(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.loggers = LoggerS.getInstance();

    }

    public  void txAndLog(String message) {
        out.write(message);
        out.flush();

    }

    public  String rx() throws IOException, ClassNotFoundException {
        String message = in.readLine().trim();
        loggers.log(message);
        return message;
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
