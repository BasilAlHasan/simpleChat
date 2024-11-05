package edu.seg2105.server.ui;
import java.io.*;
import java.util.Scanner;

import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF{
	final public static int DEFAULT_PORT = 5555;
	EchoServer server;
	Scanner fromConsole;
	
	public ServerConsole(int port) {
		server = new EchoServer(port, this);
        fromConsole = new Scanner(System.in);
    }
	
	 public void accept() {
	    try {
	    	String message;

	        while (true) {
	            message = fromConsole.nextLine();
	            if (message.startsWith("#quit")) {
	                server.handleMessageFromServerUI(message);
	                System.exit(0);
	            } else {
	                server.handleMessageFromServerUI(message);
	            }
	        }
	    } catch (Exception ex) {
	        System.out.println("Unexpected error while reading from console: " + ex.getMessage());
	    }
	}
	 
	 public void display(String message) {
        System.out.println(message);
    }
	
	 public static void main(String[] args) {
        int port = DEFAULT_PORT; // Port to listen on

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]); // Get port from command line
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Using default port " + DEFAULT_PORT);
        }

        ServerConsole serverConsole = new ServerConsole(port);

        try {
            serverConsole.server.listen(); // Start listening for connections
        } catch (IOException ex) {
            System.out.println("Error: Could not listen for clients!");
        }

        serverConsole.accept(); // Start accepting console input
    }

}
