package server;
import java.net.*;
import java.io.*; 

import bkmessprotocol.BKMessProtocolServer;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


public class Server {

  private static ServerSocket serverSocket = null;
  private static Socket clientSocket = null;
  private static final int maxClientsCount = 30;
  private static final ClientThread[] threads = new ClientThread[maxClientsCount];

  public static void main(String args[]) {
	  
    int portNumber = 2018;
    try {
    	//create server socket
    	InetAddress inetAddress = InetAddress.getByName("192.168.100.14");
    	System.out.println("IP Address Server:- " + inetAddress.getHostAddress());	//inetAddress.toString() 
    	System.out.println("Server is running ... ");
    	serverSocket = new ServerSocket(portNumber,50,inetAddress);
    	System.out.println("Server is ready!");
    } 
    catch (IOException e) 
    {
      System.out.println("Cannot run server!!!");
    }
    
    while (true) {
    	try {
    		clientSocket = serverSocket.accept();
    		int i = 0;
    		for (i = 0; i < maxClientsCount; i++) {
    			if (threads[i] == null) {
    				(threads[i] = new ClientThread(clientSocket, i, threads)).start();
    				break;
    			}
    		}
    		if (i == maxClientsCount) {
    			PrintStream os = new PrintStream(clientSocket.getOutputStream());
    			os.println("Overloading");
    			os.close();
    			clientSocket.close();
    		}
    	} catch (IOException e) {
    		System.out.println(e);
    	}
    }
  }
}


//class ClientThread extends Thread {
//	private DataInputStream in = null;
//	private DataOutputStream out = null;
//	private Socket clientSocket = null;
//	private final ClientThread[] clientThreads;
//	private int maxClientsCount;
//	
//	public ClientThread(Socket clientSocket, ClientThread[] threads) {
//	   this.clientSocket = clientSocket;
//	   this.clientThreads = threads;
//	   maxClientsCount = threads.length;
//	}
//
//	
//	public void run() {
//	    int maxClientsCount = this.maxClientsCount;
//	    ClientThread[] threads = this.clientThreads;
//	    try {
//	    	  System.out.println("LOG: Have client login to server");
//		      //Create BKMEssProtocolServer object to operate with client
//	    	  BKMessProtocolServer chat = new BKMessProtocolServer(clientSocket);
//		      chat.run();
//		      for (int i = 0; i < maxClientsCount; i++) {
//		        	if (threads[i] == this) {
//		        		threads[i] = null;
//		        	}
//		      }
//		      in.close();
//		      out.close();
//		      clientSocket.close();
//	    }
//	    catch (IOException e) {
//	    	
//	    }
//	 }
//}
