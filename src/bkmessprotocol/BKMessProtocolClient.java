package bkmessprotocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BKMessProtocolClient {
	private Socket           socket   = null;
	private BufferedInputStream  streamIn = null;
	private BufferedOutputStream  streamOut = null;
	
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private OutputStream os = null;
	
	
	public void connectToServer(String address, int port) {
		try {
			socket = new Socket(address,port);
			open();  
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 public void sendFileToServer(String url) {
		 File myFile = new File (url);
         byte [] mybytearray  = new byte [(int)myFile.length()];
         try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
         bis = new BufferedInputStream(fis);
         try {
			bis.read(mybytearray,0,mybytearray.length);
			os = this.socket.getOutputStream();
			System.out.println("Send successful!");
			os.write(mybytearray,0,mybytearray.length);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	
	public void open()
	{  try
      {  streamIn  = new BufferedInputStream(socket.getInputStream());
      	 streamOut  = new BufferedOutputStream(socket.getOutputStream());
      }
      catch(IOException ioe)
      {  System.out.println("Error getting stream: " + ioe);
      }
	}
	
	public void sendRequest(String _mess)
	{  
		 try {
			byte[] bytes = _mess.getBytes();
			streamOut.write(bytes,0,bytes.length);
		} catch (IOException e) {
		}
	}
	 
	 public String receiveMessages()
		{  
			String messRec = null;
			try {
				 byte [] mybytearray  = new byte [10485760];
				int bytesRead = streamIn.read(mybytearray,0,mybytearray.length);
				int  current = bytesRead;
				do {
					bytesRead = streamIn.read(mybytearray, current, (mybytearray.length-current));
					if (bytesRead >= 0) 
						current += bytesRead;
				} while(bytesRead > -1);
				byte [] res = new byte [current];
				for (int i = 0; i < current; i++) 
					res[i] = mybytearray[i];
				//this.process(new String(res));
				return new String(res);
			} catch (IOException e) {
				//close();
			}
			return messRec;
	}
 
   public void close()
   {  try
      {  if (streamIn != null) streamIn.close();
      	 if (streamOut != null) streamOut.close();
      	 if (socket != null) socket.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing stream: " + ioe);
      }
   }
}






