package server;
import java.net.*;
import java.io.*; 

import bkmessprotocol.BKMessProtocolServer;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


public class ClientThread extends Thread {
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private Socket clientSocket = null;
	private final ClientThread[] clientThreads;
	private int maxClientsCount;
	private int indexThread = -1;
	private String owner_thread = null;
	private BKMessProtocolServer chat= null;
	
	public ClientThread(Socket clientSocket, int i, ClientThread[] threads) {
	   this.clientSocket = clientSocket;
	   this.clientThreads = threads;
	   this.indexThread = i;
	   maxClientsCount = threads.length;
	}

	public void setOwner(String owner) {
		this.owner_thread = owner;
	}
	
	public String getOwner() {
		return this.owner_thread;
	}
	
	public BKMessProtocolServer getChat() {
		return this.chat;
	}
	
	public void run() {
	    int maxClientsCount = this.maxClientsCount;
	    ClientThread[] threads = this.clientThreads;
	    try {
	    	  System.out.println("LOG: Have client login to server");
		      //Create BKMEssProtocolServer object to operate with client
	    	  chat = new BKMessProtocolServer(clientSocket, this.indexThread, threads);
		      chat.run();
		      for (int i = 0; i < maxClientsCount; i++) {
		        	if (threads[i] == this) {
		        		threads[i] = null;
		        	}
		      }
		      in.close();
		      out.close();
		      clientSocket.close();
	    }
	    catch (IOException e) {
	    	
	    }
	 }
}
