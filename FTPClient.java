import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
class FTPClient { 

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
        int port1 = 8907;
        int port = 8909;
        String statusCode;
        boolean clientgo = true;

        System.out.println("Welcome to the simple FTP App   \n" +
                "     Commands  \n" +
                "connect servername port# connects to a specified server \n" +
                "list: lists files on server \n" +
                "get: fileName.txt downloads that text file to your current directory \n" +
                "stor: fileName.txt Stores the file on the server \n" +
                "close terminates the connection to the server");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken(); //severname is ip
            port1 = Integer.parseInt(tokens.nextToken());
            System.out.println("You are connected to " + serverName);
            Socket ControlSocket = new Socket(serverName, port1);
            while (isOpen && clientgo) {

                sentence = inFromUser.readLine();
                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

                if (sentence.equals("list:")) {


                    System.out.println(port);
                    ServerSocket welcomeData = new ServerSocket(port); //opens port to listen to data connection

                    System.out.println("\n \n \nThe files on this server are:");
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    Socket dataSocket = welcomeData.accept(); //accept data connection
                    System.out.println("data connection established");
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        if (modifiedSentence.equals("eof"))
                            break;
                        System.out.println("	" + modifiedSentence);
                    }

                    modifiedSentence = null;
                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");

                } else if (sentence.startsWith("get: ")) {

                    System.out.println(port);
                    ServerSocket welcomeData = new ServerSocket(port); //opens port to listen to data connection
                    System.out.println("\n \n Downloading File ...:");
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    String filename = sentence.substring(5);
                    File downloadedFile = new File(filename);
                    PrintWriter write = new PrintWriter(downloadedFile);
                    String buffer = ""; //because printwriter has no append function for strings, Im storing everything in this and then writing it to the file
                    //we may want to change this later but this seems simple and workable for now
                    Socket dataSocket = welcomeData.accept(); //accept data connection
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    while (true) {
                        modifiedSentence = inData.readUTF();

                        if (modifiedSentence.equals("eof")) {
                            break;
                        }
                        buffer = buffer.concat(modifiedSentence);
                        buffer = buffer.concat("\n");

                    }




                    write.print(buffer);

                    buffer = null;
                    modifiedSentence = null;
                    write.close();
                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");


                } else if (sentence.startsWith("stor: ")) {

                    String filename = sentence.substring(6);
                    System.out.println("filename string: "+ filename);
                    File uploadedFile = new File(filename);
                    System.out.println("filepath: "+ uploadedFile.toString());

                    if (!uploadedFile.exists()) {
                        System.out.println("file does not exist in working directory");
                    } else {

                        System.out.println(port);
                        ServerSocket welcomeData = new ServerSocket(port);
                        outToServer.writeBytes(port + " " + sentence + " " + '\n');
                        System.out.println("\n \n Uploading File ...:");
                        Scanner read = new Scanner(uploadedFile);
                        Socket dataSocket = welcomeData.accept(); //accept data connection
                        DataOutputStream outData = new DataOutputStream(dataSocket.getOutputStream());

                        while (read.hasNextLine()) {
                            modifiedSentence = read.nextLine();
                            System.out.println("line1:" + modifiedSentence);
                            outData.writeUTF(modifiedSentence);
                        }


                        read.close();
                        modifiedSentence = null;
                        welcomeData.close();
                        dataSocket.close();
                    }
                    System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");
                } else {
                    if (sentence.equals("close")) {
                        clientgo = false;
                        ControlSocket.close();
                        //this may need to be added to idk
                    }
                    System.out.print("No server exists with that name or server not listening on that port try agian");
                }






                }
            }
        }
    }