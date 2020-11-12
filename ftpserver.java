import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

/**********************************************************************
* FTPserver class that accounts for the commands taken in from the
* client.
* Makes use of sockets and threads.
*
* @author Christian Thompson, James Weitzmanm, Josh Hubbard, 
*         Lauren Vanderklok, & Scott Weaver 
**********************************************************************/
public class ftpserver extends Thread { 
        
    /** socket that reads and sends command outputs to the client */
    private Socket connectionSocket;

    /** port number modeled after one of our groupmates Gnumber */
    int port = 8909;

    /** variable used to hold the count */
    int count = 1;

    /** determines if a connection has been formed */
    boolean clientgo = true;
        
    /**********************************************************************
    * Initializer method for the FTPserver.
    * 
    * @param connectionSocket
    * The socket which communication will be done via.
    **********************************************************************/
    public ftpserver(Socket connectionSocket)  {
	    this.connectionSocket = connectionSocket;
    }

    /**********************************************************************
    * Run method that acts as our Main().
    **********************************************************************/
    public void run() {
        if(count==1) {
            System.out.println("User connected" + connectionSocket.getInetAddress());
        }
        count++;

        //perform the tasks/commands received by the client
	    try {
	        processRequest();
		
	    } catch (Exception e) {
		    System.out.println(e);
        }
	 
	}
    
    /**********************************************************************
    * Method that takes in the clients input and performs commands 
    * accordingly. 
    **********************************************************************/
	private void processRequest() throws Exception
	{
        String fromClient;
        String clientCommand;
        byte[] data;
        String frstln;
                    
        //while a connection has been formed
        while(true) {
            
            if(count==1) {
                System.out.println("User connected" + connectionSocket.getInetAddress());
            }
            count++;
            
            //used to hold what will be sent OUT to the client
            DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            //used to hold what come IN from the client
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //takes in the whole string from the client
            fromClient = inFromClient.readLine();
            
      		//System.out.println(fromClient);
            StringTokenizer tokens = new StringTokenizer(fromClient);
            
            frstln = tokens.nextToken();
            port = Integer.parseInt(frstln);

            clientCommand = tokens.nextToken();
            //System.out.println(clientCommand);

            //list command
            if(clientCommand.equals("list:")) { 
                String curDir = System.getProperty("user.dir");
       
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataOutputStream  dataOutToClient = 
                new DataOutputStream(dataSocket.getOutputStream());
                File dir = new File(curDir);
    
                String[] children = dir.list();
                if(children == null) {
                    // Either dir does not exist or is not a directory
                } 
                else {
                    for (int i=0; i<children.length; i++) {
                        // Get filename of file or directory
                        String filename = children[i];

                        if(filename.endsWith(".txt"))  {
                            dataOutToClient.writeUTF(children[i]);
                            //System.out.println(filename);
                        }
                        if(i-1==children.length-2) {
                            dataOutToClient.writeUTF("eof");
                            // System.out.println("eof");
                        }//if(i-1)

     
                    }//for

                    dataSocket.close();
		            //System.out.println("Data Socket closed");
                }//else
            }//if list:

            //get command
            else if(clientCommand.equals("get:")) {
                String filename = tokens.nextToken();
                System.out.println("filename:" + filename + "...");
                File file = new File(filename);

                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataOutputStream serverOutput = new DataOutputStream(dataSocket.getOutputStream());

                if (!file.exists()) {

                    serverOutput.writeUTF("status code: 550. file does not exist");
                    serverOutput.writeUTF("eof");
                    serverOutput.writeUTF("eof");
                }
                else {

                    serverOutput.writeUTF("status code 200. ok");
                    serverOutput.writeUTF("eof");


                    Scanner read = new Scanner(file);
                    String line;


                    while (read.hasNextLine()) {
                        line = read.nextLine();
                        serverOutput.writeUTF(line);
                    }
                    serverOutput.writeUTF("eof");
                    read.close();
                    serverOutput.close();
                    dataSocket.close();
                }

            }

            //stor command
            else if(clientCommand.equals("stor:")) {
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataInputStream clientInput = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    
                //get the file name from the client
                String fileName = tokens.nextToken();

                File storedFile = new File(fileName);
                PrintWriter write = new PrintWriter(storedFile);
                String buffer = "";

                System.out.println("Storing " + fileName + " in the current directory");

                //while a connection is still being held
                while (true) {

                    String line = clientInput.readUTF();

                    if (line.equals("eof")) {
                        break;
                    }
                    buffer = buffer.concat(line);
                    buffer = buffer.concat("\n");

                }

                //write file
                write.print(buffer);
                write.close();

                //stor has been performed, terminate the connection
                clientInput.close();
                dataSocket.close();
            }

            //close command
            else if (clientCommand.equals("close:")) {
				System.out.println("Closing connection " + connectionSocket.getInetAddress().getHostName() + ".");
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataOutputStream serverOutput = new DataOutputStream(dataSocket.getOutputStream());

                outToClient.close();
                inFromClient.close();
                connectionSocket.close();
                dataSocket.close();
                clientgo = false;
                serverOutput.writeUTF("close");
                return;
			}
        }
    }
}
