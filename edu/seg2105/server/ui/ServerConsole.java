package edu.seg2105.server.ui;

import java.io.*;
import java.util.Scanner;
import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {
    final public static int DEFAULT_PORT = 5555;
    EchoServer server;
    Scanner fromConsole;

    public ServerConsole(int port) {
        server = new EchoServer(port);
        fromConsole = new Scanner(System.in);
    }

    public void accept() {
        try {
            server.listen();
            String message;

            while (true) {
                message = fromConsole.nextLine();
                server.handleMessageFromServerUI(message);
            }
        } catch (IOException e) {
            System.out.println("Could not listen for clients.");
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }


    public void display(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        try {
            if (args.length > 0)
                port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Using default port.");
        }

        ServerConsole console = new ServerConsole(port);
        console.accept();
    }
}
