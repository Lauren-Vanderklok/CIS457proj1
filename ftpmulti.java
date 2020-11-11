import java.net.*;
import java.io.*;

public class ftpmulti {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
	    ftpserver thread;

        try {
            serverSocket = new ServerSocket(1200);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1200.");
            System.exit(-1);
        }

	while (listening)
	{
	    
	    thread = new ftpserver(serverSocket.accept());
	    Thread t = new Thread(thread);
	    t.start();
	   
	   
	}

       
    }
}

