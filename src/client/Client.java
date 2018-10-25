package client;
import java.net.*;
import java.io.*;

import bkmessprotocol.BKMessProtocolClient;

public class Client {
	public static void main(String[] args) {

		String request = "{ \"type\": \"CHECK_LOGIN\", \"input\": { \"user_name\": \"user_2\", \"password\": \"1234\", \"IpAddr\": \"163434.4343\"}}";
		//String request = "{ \"type\": \"SEND_FILE\", \"input\": { \"user_name\": \"user_1\"}}";
		//create Socket client
		BKMessProtocolClient client = new BKMessProtocolClient();
		System.out.println("Connecting to server ... ");
		
			//create socket to server
			client.connectToServer("192.168.100.14", 2018);
			System.out.println("Connected! ");
			client.sendRequest(request);
			String res = client.receiveMessages();
			System.out.println(res);
//			if (res.equals("OK")) {
//				client.sendFileToServer("/home/be-ta/Downloads/hang.txt");
//			}
//			System.out.println("ISSENT");
			//System.out.println("Server: " + client.receiveMessages());		
			client.close();
		
	}
}

