// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.ChatIF;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String loginID;
  private boolean isClosing = false;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    try {
    	openConnection();
    }catch(IOException e) {
    	clientUI.display("ERROR - Can't setup connection! Terminating client. ");
    	quit();
    }
    
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message){
	  if (message.startsWith("#")) {
          if (message.startsWith("#login")) {
              try {
                  sendToServer(message);
              } catch (IOException e) {
                  clientUI.display("Could not send message to server.");
              }
          } else {
              processCommand(message);
          }
      } else {
          if (isConnected()) {
              try {
                  sendToServer(message);
              } catch (IOException e) {
                  clientUI.display("Could not send message to server.");
              }
          } else {
              clientUI.display("Not connected to a server. Use #login to connect.");
          }
      }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
   if(!isClosing) {
	   isClosing=true;
	   try {
		   closeConnection();
	   }catch(IOException e) {
		   
	   }
	   System.exit(0);
   }
  }
  
  protected void connectionClosed(){
	  

	  if(!isClosing) {
		  clientUI.display("The server has shut down.");
		  quit();
	  }
  }
  
  protected void connectionException(Exception exception) {
	  System.out.println("connection error: " + exception.getMessage());
	  clientUI.display("Connection error: " + exception.getMessage());
	  quit();
  }
  
  
  protected void connectionEstablished() {
      try {
          sendToServer("#login " + loginID);
      } catch (IOException e) {
          clientUI.display("Error sending login ID to server: " + e.getMessage());
      }
  }

  
  
  private void processCommand(String message) {
	  String commandLine = message.substring(1);
	    String[] tokens = commandLine.split(" ");
	    String command = tokens[0];

	    switch (command.toLowerCase()) {
	        case "quit":
	            quit();
	            break;

	        case "logoff":
	            if (isConnected()) {
	                try {
	                    closeConnection();
	                    clientUI.display("connection closed.");
	                } catch (IOException e) {
	                    clientUI.display("Error logging off: " + e.getMessage());
	                }
	            } else {
	                clientUI.display("You are not connected to the server.");
	            }
	            break;

	        case "sethost":
	            if (!isConnected()) {
	                if (tokens.length > 1) {
	                    setHost(tokens[1]);
	                    clientUI.display("Host set to " + tokens[1]);
	                } else {
	                    clientUI.display("Usage: #sethost <host>");
	                }
	            } else {
	                clientUI.display("Cannot change host while connected. Please log off first.");
	            }
	            break;

	        case "setport":
	            if (!isConnected()) {
	                if (tokens.length > 1) {
	                    try {
	                        int newPort = Integer.parseInt(tokens[1]);
	                        setPort(newPort);
	                        clientUI.display("Port set to " + newPort);
	                    } catch (NumberFormatException e) {
	                        clientUI.display("Invalid port number.");
	                    }
	                } else {
	                    clientUI.display("Usage: #setport <port>");
	                }
	            } else {
	                clientUI.display("Cannot change port while connected. Please log off first.");
	            }
	            break;

	        case "login":
	            if (!isConnected()) {
	                try {
	                    openConnection();
	                    clientUI.display("You are now logged in.");
	                } catch (IOException e) {
	                    clientUI.display("Error logging in: " + e.getMessage());
	                }
	            } else {
	                clientUI.display("You are already connected.");
	            }
	            break;

	        case "gethost":
	            clientUI.display("Current host: " + getHost());
	            break;

	        case "getport":
	            clientUI.display("Current port: " + getPort());
	            break;

	        default:
	            clientUI.display("Unknown command: " + command);
	            break;
	    }
  }

  
}
//End of ChatClient class
