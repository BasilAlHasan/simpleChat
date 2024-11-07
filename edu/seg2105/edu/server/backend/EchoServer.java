package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

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
  public EchoServer(int port) 
  {
    super(port);
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
    String message=msg.toString();
    try {
        if (message.startsWith("#login")) {
            if (client.getInfo("loginID") == null) {
                String[] tokens = message.split(" ");
                if (tokens.length > 1) {
                    client.setInfo("loginID", tokens[1]);
                    System.out.println("Client " + tokens[1] + " logged in.");
                } else {
                    client.sendToClient("Login ID required.");
                    client.close();
                }
            } else {
                client.sendToClient("Already logged in.");
                client.close();
            }
        } else {
            if (client.getInfo("loginID") == null) {
                client.sendToClient("You need to login first.");
                client.close();
            } else {
                String loginID = (String) client.getInfo("loginID");
                String prefixedMessage = loginID + ": " + message;
                System.out.println("Message received: " + prefixedMessage + " from " + client);
                sendToAllClients(prefixedMessage);
            }
        }
    } catch (IOException e) {
        System.out.println("An error has occured");
    }
  }
  
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("Client connected: " + client);
  }
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
      System.out.println("Client disconnected: " + client);
  }
  
  public void handleMessageFromServerUI(String message) {
      if (message.startsWith("#")) {
          handleCommand(message);
      } else {
          String fullMessage = "SERVER MSG> " + message;
          System.out.println(fullMessage);
          sendToAllClients(fullMessage);
      }
  }

  private void handleCommand(String message) {
      String[] tokens = message.split(" ");
      String command = tokens[0];

      try {
          switch (command) {
              case "#quit":
                  close();
                  System.exit(0);
                  break;
              case "#stop":
                  stopListening();
                  break;
              case "#close":
                  close();
                  break;
              case "#setport":
                  if (!isListening() && getNumberOfClients() == 0) {
                      if (tokens.length > 1) {
                          setPort(Integer.parseInt(tokens[1]));
                          System.out.println("Port set to " + getPort());
                      } else {
                          System.out.println("Wrogn format");
                      }
                  } else {
                      System.out.println("Cannot change port while server is open or clients are connected.");
                  }
                  break;
              case "#start":
                  if (!isListening()) {
                      listen();
                  } else {
                      System.out.println("Server is already listening.");
                  }
                  break;
              case "#getport":
                  System.out.println("Current port: " + getPort());
                  break;
              default:
                  System.out.println("Unknown command.");
                  break;
          }
      } catch (IOException e) {
          System.out.println("Error processing command.");
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
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  
}
//End of EchoServer class