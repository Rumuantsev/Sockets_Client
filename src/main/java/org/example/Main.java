package org.example;

import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter your nickname:");
            String nickname = userInput.readLine();
            out.write(nickname + "\n");
            out.flush();

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving message: " + e.getMessage());
                }
            }).start();

            String input;
            while (true) {
                System.out.println("Enter message type (BROADCAST, PRIVATE, LIST):");
                String type = userInput.readLine();

                switch (type.toUpperCase()) {
                    case "BROADCAST":
                        System.out.println("Enter message to broadcast:");
                        input = userInput.readLine();
                        out.write("BROADCAST:" + input + "\n");
                        out.flush();
                        break;
                    case "PRIVATE":
                        out.write("LIST:\n");
                        out.flush();
                        Thread.sleep(500); // Wait for user list
                        System.out.println("Enter recipient:");
                        String recipient = userInput.readLine();
                        System.out.println("Enter private message:");
                        input = userInput.readLine();
                        out.write("PRIVATE:" + recipient + ":" + input + "\n");
                        out.flush();
                        break;
                    case "LIST":
                        out.write("LIST:\n");
                        out.flush();
                        break;
                    default:
                        System.out.println("Unknown message type.");
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
