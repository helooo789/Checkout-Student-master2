package main;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class Context {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;

    public Context(Socket socket, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
