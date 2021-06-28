package integration;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class CommTest {
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    private static boolean exit;

    public static void main(String[] args) {
        initConnection();
        readData();
        readInput();
        closeConnection();
    }

    private static void readInput() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!exit) {
                String msg = reader.readLine();
                if (msg.equals("exit"))
                    exit = true;
                sendMsg(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            exit = true;
        }
    }

    private static void readData() {
        new Thread(() -> {
            try {
                while (true)
                    System.out.println(in.readUTF());
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private static void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    private static void initConnection() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
        System.out.println("connection established");
    }

    private static void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
