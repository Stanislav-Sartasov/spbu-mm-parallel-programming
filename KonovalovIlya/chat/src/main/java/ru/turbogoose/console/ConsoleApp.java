package ru.turbogoose.console;

import ru.turbogoose.core.Peer;

import java.util.Scanner;

public class ConsoleApp {
    public static final String HELP = """
            
            ========================== Help ==========================
            Commands:
            /connect - connect to another peer by its port
            /exit    - exit chat
            /help    - get help prompt
            
            Any other input considered as a message for other peers
            ==========================================================
            """;

    public static final String LOCALHOST = "127.0.0.1";

    public static void run() {
        System.out.println("Welcome to P2P chat");
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter peer name: ");
            String name = sc.nextLine().strip();
            System.out.print("Enter peer port: ");
            String portLine = sc.nextLine().strip();
            System.out.println("Start receiving messages");
            System.out.println(HELP);

            Peer peer = null;
            while (peer == null) {
                try {
                    int port = Integer.parseInt(portLine);
                    peer = new Peer(name, port);
                    peer.start();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port format: " + portLine);
                } catch (Exception e) {
                    System.out.println("Failed to create peer: " + e.getMessage());
                    peer = null;
                }
            }
            peer.setRenderer(new ConsoleRenderer());

            while (true) {
                String line = sc.nextLine().strip();
                if (line.startsWith("/")) {
                    line = line.substring(1);
                    if ("help".equals(line)) {
                        System.out.println(HELP);
                    } else if ("exit".equals(line)) {
                        peer.shutdown();
                        break;
                    } else if ("connect".equals(line)) {
                        System.out.print("Enter peer port: ");
                        portLine = sc.nextLine().strip();
                        try {
                            int port = Integer.parseInt(portLine);
                            peer.connect(LOCALHOST, port);
                            System.out.println("Connected to peer " + LOCALHOST + ":" + port);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid port format: " + portLine);
                        } catch (Exception e) {
                            System.out.println("Failed to connect to " + LOCALHOST + ":" + portLine);
                        }
                    } else {
                        System.out.println("Unknown command: " + line);
                    }
                } else {
                    peer.sendMessage(line);
                }
            }
            System.out.println("Thanks for chatting, come back soon :)");
        }
    }
}
