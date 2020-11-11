import java.net.*;
import java.io.*;

public class ftpmulti {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
	    ftpserver thread;
        int port = 8907;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
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

