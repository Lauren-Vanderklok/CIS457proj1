import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

public class FTPServer {

    /** controlSocket that reads and sends command outputs to the client */
    Socket controlSocket;

    /** Stream that holds whatever output goes to the client */
    DataOutputStream outputClient;

    /** Stream that holds what input was taken from the client */
    BufferedReader inputClient;

    /** List of Strings
     *      holds the commands given by the client because more than
     *      one input/command is supported */
    List<String> clientCommands;

    /** Holds whatever command that came from the client */
    String command;

    /** Checks to see if the connection is still being held */
    boolean serverRun = true;

    public void run() {
        try {
            while(serverRun) {
                //take in the command given by the client
                String fromClient = inputClient.readLine();

                //list command
                if(command.equals("list:")) {
                    //list command should only have 1 input, "list"
                    //thus get the first String in the list
                    Socket dataSocket = makeServerSocket(Integer.parseInt(clientCommands.get(0)));
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                    
                    String files = listFiles();

                    //send the list of files to the client
                    DataOutputStream dataOutToClient.writeUTF(files);
                    
                    //clean up the stream before closing.
				    dataOutToClient.flush();
				    dataOutToClient.close();
				    System.out.println("eof");
				    dataSocket.close();
                    System.out.println("Data Socket closed.\n");
                }
                //get or retr command
                else if(command.equals("get:")) {
                    








    /******************************************************************
	 * Creates a server socket for the server and client to send data
	 * over.
	 * 
	 * @param port 
     *      The port the server will run on.
	 * @return Socket 
     *      The dataSocket object that was created.
	 *****************************************************************/
    private Socket makeServerSocket(int port) {
        System.out.println("Making data socket...");
            try {
                return new Socket(controlSocket.getInetAddress(), port);
            } catch (IOException e) {
                System.out.println("Error " + e.toString());
            }
            return null;
    }

    /******************************************************************
	 * Returns a list of all the files in the current directory.
	 * 
	 * @return list 
     *      A string of all the files.
	 *****************************************************************/
	private static String listFiles() {
		File curDir = new File(".");
		File[] files = curDir.listFiles();
		String list = "";
		
		for (int i = 0; i < files.length; i++)
			if (files[i].isFile())
				list += files[i].getName() + "\n";
		return list;
	}
}
