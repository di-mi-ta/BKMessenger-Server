package bkmessprotocol;

import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;

import java.net.Socket;
import database.ChatData;
import server.ClientThread;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BKMessProtocolServer {
	private Socket           socket   = null;
	private DataInputStream  streamIn = null;
	private DataOutputStream  streamOut = null;
	private ChatData data = null;
	private int indexThread = -1;
	private final ClientThread[] clientThreads;
	
	 private static final int maxClientsCount = 30;
	
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private OutputStream os = null;
	
	public BKMessProtocolServer (Socket _socket, int indexThread , ClientThread[] threads)
	{  
		socket   = _socket;
		this.indexThread = indexThread;
		this.clientThreads = threads;
		data = new ChatData("jdbc:mysql://localhost:3306/ChatData","root", ""); 
		open();  
	}
	
	public void open()
	{  
		try
		{  
			streamIn  = new DataInputStream(socket.getInputStream());
			streamOut  = new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException ioe)
		{  
			System.out.println("Error getting stream: " + ioe);
		}
	}
	
	 public void sendMess(String _mess)
	 {  
		 try 
		 {
			 streamOut.writeUTF(_mess);
		 }
		 catch (IOException e)
		 {
			 close();
		 }
	 }
	 
	 public Api getApi(String type) {
		 Api api = null;
		 
		 switch (type)
		 {
		 case "CHECK_LOGIN":
			 api = new CheckLogin(this.indexThread, this.clientThreads);
			 break;
		 case "GET_INBOX":
			 api = new GetInbox();
			 break;
		 case "GET_MESSAGE":
			 api = new GetMessages();
			 break;
		 case "SEND_TEXT_MESSAGE":
			 api = new SendTextMessage();
			 break;
//		 case "CHECK_FRIEND":
//			 api = new CheckFriend();
//			 break;
//		 case "ADD_FRIEND":
//			 api = new AddFriend();
//			 break;
//		 case "ACCEPT_FRIEND":
//			 api = new AcceptFriend();
//			 break;
//		 case "CANCEL_ADD_FRIEND":
//			 api = new CancelAddFriend();
//			 break;
//		 case "NO ACCEPT FRIEND REQUEST":
//			 api = new NoAcceptFriendRequest();
//			 break;
		 case "GET_CONNECT_VC":
			 api = new GetConnectVideoCall();
			 break;
		 case "RES_CONNECT_VC_FROM_RECEIVER":
			 api = new ResConnectVideoCallFromReceiver();
			 break;	 		 
		 case "DELETE_MESSAGE":
			 api = new DeleteMessage();
			 break;
		 case "DELETE_ALL_MESSAGE":
			 api = new DeleteAllMessage();
			 break;
		 case "SEND_FILE_MESSAGE":
			 api = new SendFileMessage();
			 break;
		 case "GET_CONTENT":
			 api = new GetContent();
			 break;
		 }
		 return api;
	 }
	 
	 public void process(String _mess)
	 {  
		 System.out.println("GET: " + _mess);
		 JSONParser parser = new JSONParser();
		 try {
			 JSONObject obj = (JSONObject)parser.parse(_mess);
			 String type = (String) obj.get("type");
			 ArrayList<String> res = getApi(type).processMessage(obj, data);
			 Iterator<String> iter = res.iterator();
			 if (type.equals("SEND_TEXT_MESSAGE") || type.equals("SEND_FILE_MESSAGE") || type.equals("GET_CONNECT_VC")) {
				 String resMess = iter.next();
				 while (iter.hasNext()) {
					 String user = iter.next();
					 for (int i = 0; i < maxClientsCount; i++) {
						 if (this.clientThreads[i] != null) {
							 if (this.clientThreads[i].getOwner().equals(user)) {
								 this.clientThreads[i].getChat().sendMess(resMess);
								 System.out.println(this.clientThreads[i].getOwner() + "  " + resMess);
							 }
						 }
					 }
					 
				 }
				 if (!type.equals("GET_CONNECT_VC")) {
					 System.out.println("RES: " + resMess);
					 this.sendMess(resMess);
				 }
			 }
			 else if (type.equals("RES_CONNECT_VC_FROM_RECEIVER")) {
				 String resMess = iter.next();
				 while (iter.hasNext()) {
					 String user = iter.next();
					 for (int i = 0; i < maxClientsCount; i++) {
						 if (this.clientThreads[i] != null) {
							 if (this.clientThreads[i].getOwner().equals(user)) {
								 this.clientThreads[i].getChat().sendMess(resMess);
								 System.out.println(this.clientThreads[i].getOwner() + "  " + resMess);
							 }
						 }
					 }
					 
				 }
			 }
			 else {
				 while (iter.hasNext())
					 this.sendMess(iter.next());
				 System.out.println("RES TO "+ this.clientThreads[this.indexThread].getOwner() + ": " + res);
			 }
		 }
		 catch (ParseException e)
		 {
			 System.out.println("LOG: 444 error");
			 this.sendMess("444");
		 }
	 }
	 
	 
//	 public void process(String _mess)
//	 {  
//		 System.out.println("GET: "+ _mess);
//		 JSONParser parser = new JSONParser();
//		 try {
//			 JSONObject obj = (JSONObject)parser.parse(_mess);
//			 String type = (String) obj.get("type");
//			 if (type.equals("SEND_FILE")) {
//				 try {
//					String url = ((JSONObject)obj.get("input")).get("url").toString();
//					String response = "{ \"type\": \"RES_SEND_FILE\", \"url\": \""  + url +"\"}";
//					System.out.println(response);
//					this.sendMess(response);
//					this.receiveFileFromClient("ABC","test10.jpg");
//					System.out.println("OK");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			 }
//			 else if (type.equals("SEND_TEXT_MESSAGE")) {
//				 String res = getApi(type).processMessage(obj, data);
//				 
//				 
//			 }
//			 else {
//				 String res = getApi(type).processMessage(obj, data);
//				 this.sendMess(res);
//				 System.out.println("RES TO: "+ this.clientThreads[this.indexThread].getOwner()+ "  " + res);
//			 }
//		 }
//		 catch (ParseException e)
//		 {
//			 System.out.println("LOG: 444 error");
//			 this.sendMess("444");
//		 }
//	 }
	 
	 public void sendFileToClient(String url) {
		 File myFile = new File (url);
         byte [] mybytearray  = new byte [(int)myFile.length()];
         try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
		}
         bis = new BufferedInputStream(fis);
         try {
			bis.read(mybytearray,0,mybytearray.length);
			os = this.socket.getOutputStream();
			System.out.println("Send successful!");
			os.write(mybytearray,0,mybytearray.length);
			os.flush();
		} catch (IOException e) {
		}
	 }
	 
	 public void receiveFileFromClient(String userNameCli, String fileName) throws IOException {
	 	int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    String dest = "./assets/" + fileName;
	    try {
	      byte [] mybytearray  = new byte [10485760];
	      InputStream is = this.socket.getInputStream();
	      fos = new FileOutputStream(dest);
	      bos = new BufferedOutputStream(fos);
	      bytesRead = is.read(mybytearray,0,mybytearray.length);
	      current = bytesRead;
	      do {
	         bytesRead =
	            is.read(mybytearray, current, (mybytearray.length-current));
	         if(bytesRead >= 0) current += bytesRead;
	      } while(bytesRead > -1);

	      bos.write(mybytearray, 0 , current);
	      bos.flush();
	      System.out.println("Received Successfulel");
	    }
	    finally {
	      if (fos != null) fos.close();
	      if (bos != null) bos.close();
	    }
	 }
	 
	 
	 public void run() {
		 while(true)
		 {
			 try
			 {
				 this.process(streamIn.readUTF());
			 }
			 catch (IOException e) {
				 close();
			 }
		 }
	}
   
	 public void close()
	 {
		 try
		 {
			 if (streamIn != null) streamIn.close();
			 if (streamOut != null) streamOut.close();
		 }
		 catch(IOException ioe)
		 {
			 System.out.println("Error closing stream: " + ioe);
		 }
	 }
}




