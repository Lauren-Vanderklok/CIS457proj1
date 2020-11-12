import java.net.*;
import java.io.*;

/**********************************************************************
* FTPmulti class that creates a socket and or thread
* on the server side so that communication can occur between the 
* client and the server.
*
* @author Christian Thompson, James Weitzmanm, Josh Hubbard, 
*         Lauren Vanderklok, & Scott Weaver 
**********************************************************************/
public class ftpmulti {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
	    ftpserver thread;
        int port = 8907;
        try {
            //create a socket with the given port parameter
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        //while connection has been made and is still open
	    while (listening)
	    {
	        //create new thread on the socket that was created
	        thread = new ftpserver(serverSocket.accept());
	        Thread t = new Thread(thread);
	        t.start();}
     }
}

