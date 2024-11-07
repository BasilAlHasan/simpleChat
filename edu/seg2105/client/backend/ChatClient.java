// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

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
  private String  loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
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
  public void handleMessageFromClientUI(String message)
  {
	  if (message.startsWith("#")) {
          handleCommand(message);
      } else {
          try {
              sendToServer(message);
          } catch (IOException e) {
              clientUI.display("Could not send message to server. Terminating client.");
              quit();
          }
      }
  }
  
  private void handleCommand(String message) {
	  String[] tokens = message.split(" ");
	  String command =tokens[0];
	  
	  try {
          switch (command) {
              case "#quit":
                  quit();
                  break;
              case "#logoff":
                  closeConnection();
                  break;
              case "#sethost":
                  if (!isConnected()) {
                      if (tokens.length > 1) {
                          setHost(tokens[1]);
                          clientUI.display("Host set to " + getHost());
                      } else {
                          clientUI.display("Wrong format");
                      }
                  } else {
                      clientUI.display("Cannot change host while connected.");
                  }
                  break;
              case "#setport":
                  if (!isConnected()) {
                      if (tokens.length > 1) {
                          setPort(Integer.parseInt(tokens[1]));
                          clientUI.display("Port set to " + getPort());
                      } else {
                          clientUI.display("Wrong format");
                      }
                  } else {
                      clientUI.display("Cannot change port while connected.");
                  }
                  break;
              case "#login":
                  if (!isConnected()) {
                      openConnection();
                  } else {
                      clientUI.display("Already connected.");
                  }
                  break;
              case "#gethost":
                  clientUI.display("Current host: " + getHost());
                  break;
              case "#getport":
                  clientUI.display("Current port: " + getPort());
                  break;
              default:
                  clientUI.display("Unknown command.");
                  break;
          }
      } catch (IOException e) {
          clientUI.display("Error processing command.");
      }
  }
  
  protected void connectionEstablished() {
	  try {
		  sendToServer("#login " + loginID);
	  }catch(IOException e) {
		  clientUI.display("Error sending loginID to server");
	  }
  }
  
  protected void connectionClosed() {
	  clientUI.display("Connection closed.");
  }
  
  protected void connectionException(Exception exception) {
	  clientUI.display("Server has shut down");
	  quit();
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class