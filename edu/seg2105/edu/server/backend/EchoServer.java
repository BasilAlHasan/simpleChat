package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
import edu.seg2105.client.common.ChatIF;

import java.io.IOException;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */


public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  
  ChatIF serverUI;
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String message = msg.toString();
    try {
    	System.out.println("Message received: " + message + " from " + client.getInfo("loginID"));
    	if(message.startsWith("#login ")) {
    		String loginID = message.substring(7).trim();
    		if(client.getInfo("loginID")!= null) {
    			client.sendToClient("Error: You are already logged in");
    			client.close();
    		}else {
    			client.setInfo("loginID", loginID);
    			System.out.println("client " + client.getInetAddress().getHostAddress() + "logged in as " + loginID);
    		}
    		
    		sendToAllClients(loginID + " has logged on.");
    	}else {
    		if(client.getInfo("loginID")== null) {
    			client.sendToClient("Error: you must login first");
    			client.close();
    		}else {
    			String loginID=client.getInfo("loginID").toString();
    			String messageToSend = loginID + ": " + message;
    			System.out.println("Message received: " + message + " from " + client.getInfo("loginID"));
    			this.sendToAllClients(messageToSend);
    		}
    	}
    }catch(IOException e) {
    	e.printStackTrace();
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  public void serverClosed() {
	  System.out.println("Server closed.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  
  
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A new client has connected to the server.");
  }
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println("Disconnected from Client");
  }
  
  public void handleMessageFromServerUI(String message) {
      if (message.startsWith("#")) {
          processCommand(message);
      } else {
          String serverMessage = "SERVER MSG> " + message;
          serverUI.display(serverMessage);
          sendToAllClients(serverMessage);
      }
  }
  
  private void processCommand(String message) {
      // Remove the '#' character
      String commandLine = message.substring(1);
      String[] tokens = commandLine.split(" ");
      String command = tokens[0];

      switch (command.toLowerCase()) {
          case "quit":
              try {
                  close();
              } catch (IOException e) {
                  serverUI.display("Error closing server: " + e.getMessage());
              }
              
              break;

          case "stop":
              stopListening();
              serverUI.display("Server has stopped listening for new clients.");
              break;

          case "close":
              try {
                  close();
                  serverUI.display("Server closed. All clients disconnected.");
              } catch (IOException e) {
                  serverUI.display("Error closing server: " + e.getMessage());
              }
              break;

          case "setport":
              if (isListening() || getNumberOfClients() > 0) {
                  serverUI.display("Cannot change port while server is open or clients are connected.");
              } else {
                  if (tokens.length > 1) {
                      try {
                          int newPort = Integer.parseInt(tokens[1]);
                          setPort(newPort);
                          serverUI.display("Port set to " + newPort);
                      } catch (NumberFormatException e) {
                          serverUI.display("Invalid port number.");
                      }
                  } else {
                      serverUI.display("Usage: #setport <port>");
                  }
              }
              break;

          case "start":
              if (!isListening()) {
                  try {
                      listen();
                      serverUI.display("Server started listening for new clients.");
                  } catch (IOException e) {
                      serverUI.display("Error starting server: " + e.getMessage());
                  }
              } else {
                  serverUI.display("Server is already listening.");
              }
              break;

          case "getport":
              serverUI.display("Current port: " + getPort());
              break;

          default:
              serverUI.display("Unknown command: " + command);
              break;
      }
  }
  
 
}
//End of EchoServer class
