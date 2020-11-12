import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

    public class ftpserver extends Thread { 
        
        /** controlSocket that reads and sends command outputs to the client */
        private Socket connectionSocket;

        int port = 8909;
        int count = 1;
   
        public ftpserver(Socket connectionSocket)  {
	        this.connectionSocket = connectionSocket;
    }


      public void run() {
                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;

	   try {
	       processRequest();
		
	    } catch (Exception e) {
		    System.out.println(e);
	    }
	 
	}
	
	
	private void processRequest() throws Exception
	{
            String fromClient;
            String clientCommand;
            byte[] data;
            String frstln;
                    
            while(true)
            {
                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;
         
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                fromClient = inFromClient.readLine();
            
      		    //System.out.println(fromClient);
                StringTokenizer tokens = new StringTokenizer(fromClient);
            
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);

                clientCommand = tokens.nextToken();
                //System.out.println(clientCommand);


                  if(clientCommand.equals("list:")) { 
                    String curDir = System.getProperty("user.dir");
       
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream  dataOutToClient = 
                    new DataOutputStream(dataSocket.getOutputStream());
                    File dir = new File(curDir);
    
                    String[] children = dir.list();
                    if(children == null) 
                      {
                          // Either dir does not exist or is not a directory
                      } 
                      else 
                      {
                          for (int i=0; i<children.length; i++)
                          {
                            // Get filename of file or directory
                            String filename = children[i];

                            if(filename.endsWith(".txt"))
                                dataOutToClient.writeUTF(children[i]);
                                //System.out.println(filename);
                            if(i-1==children.length-2)
                            {
                                dataOutToClient.writeUTF("eof");
                                // System.out.println("eof");
                            }//if(i-1)

     
                          }//for

                           dataSocket.close();
		            //System.out.println("Data Socket closed");
                    }//else
        

                }//if list:


                if(clientCommand.equals("get:"))
                {
                        String filename = tokens.nextToken();
                        System.out.println("filename:" + filename + "...");
                        File file = new File(filename);


                        if (!file.exists()) {
                            System.out.println("status code: 550. file does not exist");
                        }else {
                            System.out.println("status code 200. ok");
                            Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                            DataOutputStream serverOutput = new DataOutputStream(dataSocket.getOutputStream());

                            Scanner read = new Scanner(file);
                            String line;


                            while (read.hasNextLine()) {
                                line = read.nextLine();
                                System.out.println("line:" + line);
                                serverOutput.writeUTF(line);
                            }
                            serverOutput.writeUTF("eof");
                            read.close();
                            serverOutput.close();
                            dataSocket.close();
                        }

                }

                if(clientCommand.equals("stor:"))
                {
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataInputStream clientInput = new DataInputStream(
                    new BufferedInputStream(dataSocket.getInputStream()));
                    
                    //read data from client
                    byte[] dataIn = new byte[clientInput.readByte()];

                    //added due to java.io.EOFException
                    while(clientInput.available() == 0) {
                        Thread.sleep(10);
                    }
                    //get the working directory
                    String filePath = System.getProperty("user.dir") + "/";
                    //get the file name from the client
                    String fileName = clientInput.readUTF();
                    filePath += fileName;

                    System.out.println("Storing " + fileName + " in the current directory");
                
                    //write bytes to file
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        fos.write(dataIn);
                    }

                    //stor has been performed, terminate the connection
                    clientInput.close();
                    dataSocket.close();
                }
            }
            //main
        }
}
