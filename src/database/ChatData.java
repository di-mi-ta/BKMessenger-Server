package database;
//import java.io.ObjectOutputStream.PutField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;


import com.mysql.jdbc.Statement;

public class ChatData {
	private Connection con = null;
	public ChatData(String url, String username, String password) {
		try
		{
			con = DriverManager.getConnection(url, username, password);
			System.out.println("Connected to database!");
        }
		catch (Exception e)
		{
			System.out.println("Cannot connect to database!");
			System.out.println(e);
		}
	}

	public ResultSet query(String sql) {
		Statement stmt;
		try
		{
			stmt = (Statement) con.createStatement();
		}
		catch (SQLException e1)
		{
			return null;
		}  
		
		try {
			return stmt.executeQuery(sql);
		}
		catch (SQLException e) {
			return null;
		}
	}

	public void update(String sql) {
		Statement stmt;
		try {
			stmt = (Statement) con.createStatement();
			stmt.executeUpdate(sql);
		}
		catch (SQLException e1)
		{
			System.out.println("UPDATE DATABASE ERROR");
			System.out.println(e1.getMessage());
		}  
	}
	
	@SuppressWarnings("unchecked")
	public String getContent(long idFile)
	{
		String sqlGetContentFile = "select contentFile from media where idFile = " + idFile + ";";
		ResultSet resGetContentFile = this.query(sqlGetContentFile);
		String contentFile = "";
		try
		{
			resGetContentFile.next();
			contentFile = resGetContentFile.getString(1);
		}
		catch (SQLException e)
		{
			System.out.println("GET CONTENT ERROR");
			System.out.println(e.getMessage());
		}
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_GET_CONTENT");
		JSONObject subObj = new JSONObject();
		subObj.put("idMess", idFile);
		subObj.put("content", contentFile);
		obj.put("output", subObj);
		return obj.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public String getInbox(String userName) {
		String sql = "select * from ChatData.inbox where sender = '" + userName + "'";
		ResultSet res = this.query(sql);
		JSONArray inboxes = new JSONArray();
		try
		{
			while(res.next())
			{
				String idLastMess = res.getString(1);
				String lastTime = res.getString(2);
				String receiverId = res.getString(4);
				String sqlLastMessage = "select type, content, timeSeen from ChatData.messages where idMess = '" +  idLastMess + "';";
				ResultSet resLastMessage = this.query(sqlLastMessage);
				resLastMessage.next();
				//content of last message
				String type = resLastMessage.getString(1);
				String contentLastMessage = resLastMessage.getString(2) ;
				if (type.equals("img"))
					contentLastMessage = "A photo was sent";
				else if (type.equals("pdf"))
					contentLastMessage = "A attached file was sent";
				
				String timeSeen = resLastMessage.getString(3);
				boolean seen = (timeSeen == null) ? false : true;
				
				String sqlReceiver = "select * from ChatData.receiver where idRec = '" + receiverId + "';";
				ResultSet resReceiver = this.query(sqlReceiver);
				resReceiver.next();		
				String receiverName = "";
				boolean isGroup = false;
				String groupId = resReceiver.getString(3);
				long longGroupId = 0;
				isGroup = (groupId == null) ? false : true;
				
				JSONObject inbox = new JSONObject();
				inbox.put("idReceiver", Integer.parseInt(receiverId));
				if (isGroup)
				{
					String sqlGroupName = "select group_name from ChatData.group where group_id = " + groupId + ";";
					ResultSet resGroupName = this.query(sqlGroupName);
					resGroupName.next();
					receiverName = resGroupName.getString(1);
					longGroupId = Long.parseLong(groupId);
					inbox.put("group_id", longGroupId);
				}
				else
				{
					receiverName = resReceiver.getString(2);
					inbox.put("group_id", null);
				}
				
				inbox.put("Receiver", receiverName);
				inbox.put("LastMess", contentLastMessage);
				inbox.put("TimeOfLastMess", lastTime);
				inbox.put("Seen", seen);
				inboxes.add(inbox);
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			JSONObject obj = new JSONObject();
			obj.put("type", "RES_GET_INBOX");
			JSONObject obj2 = new JSONObject();
			obj2.put("user_name", userName);
			obj2.put("code", 444);
			obj.put("output", obj2);
			return "[" + obj.toString() + "]";
		}
		catch (NullPointerException e)
		{
			System.out.println(e.getMessage());
			return "No row has been returned";
		}
		
		JSONObject obj = new JSONObject();
		obj.put("type", "RESPONSE_INBOX");
		JSONObject subObj = new JSONObject();
		subObj.put("user_name", userName);
		subObj.put("inbox", inboxes);
		obj.put("output", subObj);
		return obj.toString();
	}
	
	@SuppressWarnings("unchecked")
	public String checkLogin(String user_name, String pass, String IpAddr) {
		String sql = "Select * from user where user_name = '"  + user_name + "' and password = '" + pass + "'"; 
		ResultSet res = this.query(sql);
		boolean check  = false;
		try
		{
			while(res.next())
				check = true;
		}
		catch (SQLException e)
		{
			JSONObject obj = new JSONObject();
			obj.put("type", "RES_CHECK_LOGIN");
			JSONObject obj2 = new JSONObject();
			obj2.put("user_name", user_name);
			obj2.put("code", 444);
			obj.put("output", obj2);
			return obj.toString();
		}
		
		if (check == true)
		{
			sql = "UPDATE user SET IpAddr = '"+ IpAddr + "' where user_name = '" + user_name + "'";
			this.update(sql);
			sql = "UPDATE user SET status = 1 where user_name = '" + user_name + "';";
			this.update(sql);
			JSONObject obj = new JSONObject();
			obj.put("type", "RES_CHECK_LOGIN");
			JSONObject obj2 = new JSONObject();
			obj2.put("user_name", user_name);
			obj2.put("code", 777);
			obj.put("output", obj2);
			return obj.toString();
		}
		else
		{
			JSONObject obj = new JSONObject();
			obj.put("type", "RES_CHECK_LOGIN");
			JSONObject obj2 = new JSONObject();
			obj2.put("user_name", user_name);
			obj2.put("code", 444);
			obj.put("output", obj2);
			return obj.toString();
		}
	}
	
	private static boolean numberInListNumbers(String number, String listNumbers)
	{
		String foo = "";
		for (int i = 0; i < listNumbers.length(); ++i)
		{
			char character = listNumbers.charAt(i);
			if (character >= '0' && character <= '9')
				foo = foo + character;
			else
			{
				if (foo.equals(number))
					return true;
				foo = "";
			}
		}
		if (foo.equals(number))
			return true;
		return false;
	}
	
	private String indexOfTheUser(long groupId, String userName)
	{
		String sqlIndexUserInGroup = "select STT from ChatData.mapusergroup where group_id = " + groupId + " and user_name = '" + userName + "';";
		ResultSet resIndexUserInGroup = this.query(sqlIndexUserInGroup);
		String index;
		try
		{
			resIndexUserInGroup.next();
			index = resIndexUserInGroup.getString(1);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println("User is not in group");
			return "-11";
		}
		return index;
	}
	
	@SuppressWarnings("unchecked")
	public String getMessages(String userName, long receiver) {
		String sql = "Select * from ChatData.receiver where idRec = "  + receiver; 
		ResultSet res = this.query(sql);
		boolean isGroup = false;
		String userNameOfReceiver = null;
		try
		{
			res.next();
			userNameOfReceiver = res.getString(2);
				
			isGroup = (userNameOfReceiver == null) ? true : false;
			
			if (isGroup)
			{
				String sqlMessageGroup = "Select * from ChatData.messages where receiver = "  + receiver;
				ResultSet resMessageGroup = this.query(sqlMessageGroup);
				JSONObject obj = new JSONObject();
				obj.put("type", "RESPONSE_OF_GET_MESSAGE");
				JSONObject obj1 = new JSONObject();
				obj1.put("user_name", userName);
				obj1.put("idReceiver", receiver);
				JSONArray messages = new JSONArray();
				while(resMessageGroup.next())
				{
					String inVisible = resMessageGroup.getString(8);
					boolean visible = true;
					if (inVisible != null) 
					{
						String index = indexOfTheUser(res.getInt(3), userName);
						visible = !numberInListNumbers(index, inVisible);
					}
					if (visible)
					{
						JSONObject subObj = new JSONObject();
						subObj.put("idMess", resMessageGroup.getInt(1));
						subObj.put("type", resMessageGroup.getString(3));
						subObj.put("sender", resMessageGroup.getString(4));
						subObj.put("isLoaded", 0);
						subObj.put("content", resMessageGroup.getString(2));
						subObj.put("timeSent", resMessageGroup.getString(6));		//DateTime?
						subObj.put("timeSeen", resMessageGroup.getString(7));
						messages.add(subObj);
					}
				}
				obj1.put("messages", messages);
				obj.put("output", obj1);
				return obj.toString();
			}
			else
			{
				long idUserName = getIdReceiver(userName);
				String sqlMessageUser = "Select * from ChatData.messages where (sender = '" + userNameOfReceiver +
						"' and receiver = " + idUserName + " and (invisible <> '1' or invisible is null))" +
						"or (sender = '" + userName + "' and receiver = " + receiver + " and (invisible <>"
								+ "'0' or invisible is null)) order by timeSent;";
				ResultSet resMessageUser = this.query(sqlMessageUser);
				JSONObject obj = new JSONObject();
				obj.put("type", "RESPONSE_OF_GET_MESSAGE");
				JSONObject obj1 = new JSONObject();
				obj1.put("user_name", userName);
				obj1.put("idReceiver", receiver);
				JSONArray messages = new JSONArray();
				while(resMessageUser.next())
				{
					JSONObject subObj = new JSONObject();
					subObj.put("idMess", resMessageUser.getInt(1));
					subObj.put("type", resMessageUser.getString(3));
					subObj.put("sender", resMessageUser.getString(4));
					subObj.put("isLoaded", 0);
					subObj.put("content", resMessageUser.getString(2));
					subObj.put("timeSent", resMessageUser.getString(6));
					subObj.put("timeSeen", resMessageUser.getString(7));
					messages.add(subObj);
				}
				obj1.put("messages", messages);
				obj.put("output", obj1);
				return obj.toString();
			}
		}
		catch (SQLException e)
		{
			return "SQL Exception";		
		}
		catch (NullPointerException e)
		{
			return "Null pointer Exception";
		}
	}
	
	private long getIdReceiver(String userName)
	{
		String sqlGetIdReceiver = "select idRec from receiver where user_name = '" + userName + "';";
		ResultSet resGetIdReceiver = this.query(sqlGetIdReceiver);
		try 
		{
			resGetIdReceiver.next();
			return resGetIdReceiver.getInt(1);
		}
		catch (SQLException e)
		{
			System.out.println("Error when get id receiver");
			return -1;
		}
	}
	
	public void deleteMessage(String userName, int idMessage)
	{
		String sqlGetMessage = "select * from messages where idMess = " + idMessage + ";";
		ResultSet resGetMessage = this.query(sqlGetMessage);
		try
		{
			//Get Last id message of userName in inbox table
			int idLastMess = 0;
			
			//Check whether receiverId is an id of a group
			resGetMessage.next();
			int receiverId = resGetMessage.getInt(5);
			String sqlReceiver = "Select * from ChatData.receiver where idRec = "  + receiverId; 
			ResultSet resReceiver = this.query(sqlReceiver);
			resReceiver.next();
			boolean isGroup = false;
			String userNameOfReceiver = resReceiver.getString(2);
			isGroup = (userNameOfReceiver == null) ? true : false;
			if (!isGroup)
			{
				//Get Sender
				String sender = resGetMessage.getString(4);
				String invisible = (userName.equals(sender)) ? "0" : "1";
				String currentInvisible = resGetMessage.getString(8);
				long idOfSender = getIdReceiver(sender);
				long idOfReceiverInInbox = (userName.equals(sender)) ? receiverId : idOfSender;
				String sqlGetLastIdMess = "select idLastMess from inbox where sender = '" + userName + "' and receiver = " + idOfReceiverInInbox;
				ResultSet resGetLastIdMess = this.query(sqlGetLastIdMess);
				resGetLastIdMess.next();
				idLastMess = resGetLastIdMess.getInt(1);
				if (currentInvisible == null)
				{
					String sqlUpdateInvisible = "update messages set invisible = '" + invisible + "' where idMess = " + idMessage + ";";
					this.update(sqlUpdateInvisible);
				}
				else
				{
					String sqlDeleteRow = "delete from messages where idMess = " + idMessage + ";";
					this.update(sqlDeleteRow);
					String type = resGetMessage.getString(3);
					if (!type.equals("text"))
					{
						String sqlDeleteFile = "delete from media where idFile = " + idMessage + ";";
						this.update(sqlDeleteFile);
					}
				}
				
				if (idMessage == idLastMess)
				{
					
					String visible = (invisible == "0") ? "1" : "0";
					String sqlGetAllVisibleIndexes = "select idMess from messages where (sender = '" + sender + "' and " +
							"receiver = " + receiverId + " and (invisible is null or invisible <> '" + invisible + "'))" +
							" or (sender = '" + userNameOfReceiver + "' and receiver = " + idOfSender + " and (" +
							" invisible is null or invisible <> '" + visible + "')) order by timeSent desc;";
					ResultSet resGetAllVisibleIndexes = this.query(sqlGetAllVisibleIndexes);

					int newIdLast = 0;
					long mainIdReceiver = (invisible == "0") ? receiverId : idOfSender;
					try
					{
						resGetAllVisibleIndexes.next();
						newIdLast = resGetAllVisibleIndexes.getInt(1);
					}
					catch (SQLException e)
					{
						String sqlDeleteInbox = "delete from inbox where sender = '" + userName + "' and receiver = " + mainIdReceiver + ";";
						this.update(sqlDeleteInbox);
					}
					String sqlUpdateLastId = "update inbox set idLastMess = " + newIdLast + " where sender = '" + userName + "' and receiver = " + mainIdReceiver + ";";
					this.update(sqlUpdateLastId);
				}
			}
			else
			{
				String index = indexOfTheUser(resReceiver.getInt(3), userName);
				if (index.equals("-11"))
					return;
				String currentInvisible = resGetMessage.getString(8);
				if (currentInvisible == null)
				{
					String sqlUpdateInvisible = "update messages set invisible = '" + index + "' where idMess = " + idMessage + ";";
					this.update(sqlUpdateInvisible);
				}
				else
				{
					currentInvisible = currentInvisible + " " + index;
					String sqlUpdateInvisible = "update messages set invisible = '" + currentInvisible + "' where idMess = " + idMessage + ";";
					this.update(sqlUpdateInvisible);
				}
				String sqlGetLastIdMess = "select idLastMess from inbox where sender = '" + userName + "' and receiver = " + receiverId + ";";
				ResultSet resGetLastIdMess = this.query(sqlGetLastIdMess);
				resGetLastIdMess.next();
				idLastMess = resGetLastIdMess.getInt(1);
				if (idMessage == idLastMess)
				{
					String sqlMessageGroup = "Select idMess, invisible from ChatData.messages where receiver = "  + receiverId + " order by timeSent desc";
					ResultSet resMessageGroup = this.query(sqlMessageGroup);
					int newIdMess = -1;
					
					while (resMessageGroup.next())
					{
						String anotherInvisible = resMessageGroup.getString(2);
						if(anotherInvisible == null || !numberInListNumbers(index, anotherInvisible))
						{
							newIdMess = resMessageGroup.getInt(1);
							break;
						}
					}
					if (newIdMess == -1)
					{
						String sqlDelete = "delete from inbox where sender = '" + userName + "' and receiver = " + receiverId + ";";
						this.update(sqlDelete);
					}
					else
					{
						String sqlUpdate = "update inbox set idLastMess = " + newIdMess + " where sender = '" + userName + "' and receiver = " + receiverId + ";";
						this.update(sqlUpdate);
					}
				}
			}
			
		}
		catch (SQLException e)
		{
			System.out.println("DELETE MESSAGE ERROR");
			System.out.println(e.getMessage());
		}
	}

	public void deleteAllMessages(String sender, long receiver)
	{
		String sqlCheckExistsInbox = "select * from inbox where sender = '" + sender + "' and receiver = " + receiver + ";";
		ResultSet resCheckExistsInbox = this.query(sqlCheckExistsInbox);
		
		try
		{
			resCheckExistsInbox.next();
			resCheckExistsInbox.getInt(1);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return;
		}
		String sqlDeleteRow = "delete from inbox where sender = '" + sender + "' and receiver = " + receiver + ";";
		this.update(sqlDeleteRow);
		String sqlGetReceiver = "select * from receiver where idRec = " + receiver;
		ResultSet resGetReceiver = this.query(sqlGetReceiver);
		try
		{
			resGetReceiver.next();
			String userNameOfReceiver = resGetReceiver.getString(2);
			boolean isGroup = (userNameOfReceiver == null) ? true : false;
			if (isGroup)
			{
				int groupId = resGetReceiver.getInt(3);
				String index = indexOfTheUser(groupId, sender);
				String template1 = index, template2 = " " + index, template3 = " " + index + " ", template4 = index + " ";
				String sql = "select idMess, invisible from messages where receiver = " + receiver + " and (invisible is null or ("
						+ "invisible <> '" + template1 + "' and invisible not like '%" + template2 + "' and invisible not like"
						+ " '%" + template3 + "%' and invisible not like '" + template4 + "%'));";
				ResultSet res = this.query(sql);
				while (res.next())
				{
					int idMessage = res.getInt(1);
					String currentInvisible = res.getString(2);
					if (currentInvisible == null)
					{
						String sqlUpdateInvisible = "update messages set invisible = '" + index + "' where idMess = " + idMessage + ";";
						this.update(sqlUpdateInvisible);
					}
					else
					{
						currentInvisible = currentInvisible + " " + index;
						String sqlUpdateInvisible = "update messages set invisible = '" + currentInvisible + "' where idMess = " + idMessage + ";";
						this.update(sqlUpdateInvisible);
					}
				}
			}
			else
			{
				long idOfSender = getIdReceiver(sender);
				String sql = "select * from messages where (sender = '" + sender + "' and receiver = " + receiver + " and ("
						+ "invisible is null or invisible = '1')) or (sender = '" + userNameOfReceiver + "' and receiver = " + idOfSender + " "
						+ "and (invisible is null or invisible = '0'));";
				ResultSet res = this.query(sql);
				while (res.next())
				{
					int idMessage = res.getInt(1);
					String currentInvisible = res.getString(8);
					int rec = res.getInt(5);
					if (currentInvisible == null)
					{
						String index = (rec == receiver) ? "0" : "1";
						String sqlUpdateInvisible = "update messages set invisible = '" + index + "' where idMess = " + idMessage + ";";
						this.update(sqlUpdateInvisible);
					}
					else
					{
						String sqlDelete = "delete from messages where idMess = " + idMessage + ";";
						this.update(sqlDelete);
						String type = res.getString(3);
						if (!type.equals("text"))
						{
							String sqlDeleteFile = "delete from media where idFile = " + idMessage + ";";
							this.update(sqlDeleteFile);
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN DELETE ALL MESSAGES");
			System.out.println(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> sendFileMessage(String sender, long receiver, String type,  String content ,String name)
	{
		ArrayList<String> lst = new ArrayList<String>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String lastTime = dtf.format(now);
		String sqlCheckExistsInbox = "select * from ChatData.inbox where sender = '" + sender + "' and receiver = " + receiver + ";";
		ResultSet resCheckExistsInbox = this.query(sqlCheckExistsInbox);
		boolean isExists = true;
		try {
			resCheckExistsInbox.next();
			resCheckExistsInbox.getString(3);
		}
		catch (SQLException e)
		{
			isExists = false;
		}
		boolean isGroup = false;
		try 
		{
			String sqlInsertMessage = "insert into ChatData.messages (content, type, sender, receiver, timeSent) values ('"
					+ name + "', '" + type + "', '" + sender + "', " + receiver + ", '" + lastTime + "');";
			this.update(sqlInsertMessage);
			
			String sqlLastIdMess = "select distinct last_insert_id() from ChatData.messages;";
			ResultSet resLastIdMess = this.query(sqlLastIdMess);
			resLastIdMess.next();
			int lastIdMess = resLastIdMess.getInt(1);
			String sqlAddFile = "insert into media(idFile, contentFile) values (" + lastIdMess + ", '" + content + "');";
			this.update(sqlAddFile);
			//Get Last Id Message
			if (isExists) {
				String sqlUpdateLastMess = "update ChatData.inbox set idLastMess = " + lastIdMess + ", lastTime = '" + lastTime + "' where sender = '" + sender + "' and receiver = " + receiver;
				this.update(sqlUpdateLastMess);
			}
			else {
				String sqlInsertInbox = "insert into ChatData.inbox values (" + lastIdMess + ", '" + lastTime + "', '" + sender + "', " + receiver + ");";
				this.update(sqlInsertInbox);
			}
			
			String sqlReceiver = "select * from ChatData.receiver where idRec = " + receiver;
			ResultSet resReceiver = this.query(sqlReceiver);
			resReceiver.next();
			String userNameOfReceiver = resReceiver.getString(2);
			isGroup = (userNameOfReceiver == null) ? true : false;
			
			String sqlGetIdReceiverOfSender = "select idRec from ChatData.receiver where user_name = '" + sender + "';";
			ResultSet resGetIdReceiverOfSender = this.query(sqlGetIdReceiverOfSender);
			resGetIdReceiverOfSender.next();
			int idOfSender = resGetIdReceiverOfSender.getInt(1);
			//Create JSONObject for returning
			JSONObject obj = new JSONObject();
			obj.put("type", "NOTIFICATION");
			JSONObject subObj = new JSONObject();
			subObj.put("idReceiver", receiver);
			
			JSONObject message = new JSONObject();
			message.put("idMess", lastIdMess);
			message.put("type", type);
			message.put("sender", sender);
			message.put("content", name);
			message.put("timeSent", lastTime);
			message.put("timeSeen", null);
			subObj.put("message", message);
			obj.put("output", subObj);
			lst.add(obj.toString());
			if (!isGroup)
			{	
				boolean _isExists = true;
				String sqlCheckAnotherExistsInbox = "select * from ChatData.inbox where sender = '" + userNameOfReceiver + "' and receiver = " + idOfSender + ";";
				ResultSet resCheckAnotherExistsInbox = this.query(sqlCheckAnotherExistsInbox);
				try {
					resCheckAnotherExistsInbox.next();
					resCheckAnotherExistsInbox.getString(3);
				}
				catch (SQLException e){
					_isExists = false;
				}
				
				if (_isExists) {
					String sqlUpdateIdLastMess = "update ChatData.inbox set idLastMess = " + lastIdMess + ", lastTime = '" + lastTime + "' where sender = '" + userNameOfReceiver + "' and receiver = " + idOfSender;
					this.update(sqlUpdateIdLastMess);
				}
				else
				{
					String sqlInsertInbox = "insert into ChatData.inbox values (" + lastIdMess + ", '" + lastTime + "', '" + userNameOfReceiver + "', " + idOfSender + ");";
					this.update(sqlInsertInbox);
				}
				lst.add(userNameOfReceiver);
			}
			else
			{
				int groupId = resReceiver.getInt(3);
				String sqlGetMembersOfGroup = "select * from mapusergroup where group_id = " + groupId + ";";
				ResultSet resGetMembersOfGroup = this.query(sqlGetMembersOfGroup);
				while (resGetMembersOfGroup.next())
				{
					String memberName = resGetMembersOfGroup.getString(3);
					if (!memberName.equals(sender))
						lst.add(memberName);
					boolean _isExists = true;
					String sqlCheckAnotherExistsInbox = "select * from ChatData.inbox where sender = '" + memberName + "' and receiver = " + receiver + ";";
					ResultSet resCheckAnotherExistsInbox = this.query(sqlCheckAnotherExistsInbox);
					try {
						resCheckAnotherExistsInbox.next();
						resCheckAnotherExistsInbox.getString(3);
					}
					catch (SQLException e){
						_isExists = false;
					}
					
					if (_isExists) {
						String sqlUpdateIdLastMess = "update ChatData.inbox set idLastMess = " + lastIdMess + ", lastTime = '" + lastTime + "' where sender = '" + memberName + "' and receiver = " + receiver;
						this.update(sqlUpdateIdLastMess);
					}
					else
					{
						String sqlInsertInbox = "insert into ChatData.inbox values (" + lastIdMess + ", '" + lastTime + "', '" + memberName + "', " + receiver + ");";
						this.update(sqlInsertInbox);
					}
				}		
			}
		}
		catch (NullPointerException e)
		{
			System.out.println("SEND FILE MESSAGE ERROR");
			System.out.println("Null pointer Exception");
		}
		catch (SQLException e)
		{
			System.out.println("SEND FILE MESSAGE ERROR");
			System.out.println(e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> sendTextMessage(String sender, long receiver, String content)
	{	
		ArrayList<String> lst = new ArrayList<String>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String lastTime = dtf.format(now);
		String sqlCheckExistsInbox = "select * from ChatData.inbox where sender = '" + sender + "' and receiver = " + receiver + ";";
		ResultSet resCheckExistsInbox = this.query(sqlCheckExistsInbox);
		boolean isExists = true;
		try {
			resCheckExistsInbox.next();
			resCheckExistsInbox.getString(3);
		}
		catch (SQLException e)
		{
			isExists = false;
		}
		boolean isGroup = false;
		try 
		{
			String sqlInsertMessage = "insert into ChatData.messages (content, type, sender, receiver, timeSent) values ('"
					+ content + "', 'text', '" + sender + "', " + receiver + ", '" + lastTime + "');";
			this.update(sqlInsertMessage);
			String sqlLastIdMess = "select distinct last_insert_id() from ChatData.messages;";
			ResultSet resLastIdMess = this.query(sqlLastIdMess);
			resLastIdMess.next();
			int lastIdMess = resLastIdMess.getInt(1);
			//Get Last Id Message
			if (isExists) {
				String sqlUpdateLastMess = "update ChatData.inbox set idLastMess = " + lastIdMess + ", lastTime = '" + lastTime + "' where sender = '" + sender + "' and receiver = " + receiver;
				this.update(sqlUpdateLastMess);
			}
			else {
				String sqlInsertInbox = "insert into ChatData.inbox values (" + lastIdMess + ", '" + lastTime + "', '" + sender + "', " + receiver + ");";
				this.update(sqlInsertInbox);
			}
			
			String sqlReceiver = "select * from ChatData.receiver where idRec = " + receiver;
			ResultSet resReceiver = this.query(sqlReceiver);
			resReceiver.next();
			String userNameOfReceiver = resReceiver.getString(2);
			isGroup = (userNameOfReceiver == null) ? true : false;
			
			String sqlGetIdReceiverOfSender = "select idRec from ChatData.receiver where user_name = '" + sender + "';";
			ResultSet resGetIdReceiverOfSender = this.query(sqlGetIdReceiverOfSender);
			resGetIdReceiverOfSender.next();
			int idOfSender = resGetIdReceiverOfSender.getInt(1);
			//Create JSONObject for returning
			JSONObject obj = new JSONObject();
			obj.put("type", "NOTIFICATION");
			JSONObject subObj = new JSONObject();
			subObj.put("idReceiver", receiver);
			subObj.put("sender", sender);
			JSONObject message = new JSONObject();
			message.put("sender", sender);
			message.put("idMess", lastIdMess);
			message.put("type", "text");
			message.put("content", content);
			message.put("timeSent", lastTime);
			message.put("timeSeen", null);
			subObj.put("message", message);
			obj.put("output", subObj);
			lst.add(obj.toString());
			if (!isGroup)
			{	
				boolean _isExists = true;
				String sqlCheckAnotherExistsInbox = "select * from ChatData.inbox where sender = '" + userNameOfReceiver + "' and receiver = " + idOfSender + ";";
				ResultSet resCheckAnotherExistsInbox = this.query(sqlCheckAnotherExistsInbox);
				try {
					resCheckAnotherExistsInbox.next();
					resCheckAnotherExistsInbox.getString(3);
				}
				catch (SQLException e){
					_isExists = false;
				}
				
				if (_isExists) {
					String sqlUpdateIdLastMess = "update ChatData.inbox set idLastMess = " + lastIdMess + ", lastTime = '" + lastTime + "' where sender = '" + userNameOfReceiver + "' and receiver = " + idOfSender;
					this.update(sqlUpdateIdLastMess);
				}
				else
				{
					String sqlInsertInbox = "insert into ChatData.inbox values (" + lastIdMess + ", '" + lastTime + "', '" + userNameOfReceiver + "', " + idOfSender + ");";
					this.update(sqlInsertInbox);
				}
				lst.add(userNameOfReceiver);
			}
			else
			{
				int groupId = resReceiver.getInt(3);
				String sqlGetMembersOfGroup = "select * from mapusergroup where group_id = " + groupId + ";";
				ResultSet resGetMembersOfGroup = this.query(sqlGetMembersOfGroup);
				while (resGetMembersOfGroup.next())
				{
					String memberName = resGetMembersOfGroup.getString(3);
					if (!memberName.equals(sender))
						lst.add(memberName);
					boolean _isExists = true;
					String sqlCheckAnotherExistsInbox = "select * from ChatData.inbox where sender = '" + memberName + "' and receiver = " + receiver + ";";
					ResultSet resCheckAnotherExistsInbox = this.query(sqlCheckAnotherExistsInbox);
					try {
						resCheckAnotherExistsInbox.next();
						resCheckAnotherExistsInbox.getString(3);
					}
					catch (SQLException e){
						_isExists = false;
					}
					
					if (_isExists) {
						String sqlUpdateIdLastMess = "update ChatData.inbox set idLastMess = " + lastIdMess + ", lastTime = '" + lastTime + "' where sender = '" + memberName + "' and receiver = " + receiver;
						this.update(sqlUpdateIdLastMess);
					}
					else
					{
						String sqlInsertInbox = "insert into ChatData.inbox values (" + lastIdMess + ", '" + lastTime + "', '" + memberName + "', " + receiver + ");";
						this.update(sqlInsertInbox);
					}
				}		
			}
		}
		catch (NullPointerException e)
		{
			System.out.println("SEND TEXT MESSAGE ERROR");
			System.out.println("Null pointer Exception");
		}
		catch (SQLException e)
		{
			System.out.println("SEND TEXT MESSAGE ERROR");
			System.out.println(e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		System.out.println(lst.toString());
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	public String getRegistration(String userName, String password, String fullName)
	{
		String sqlUsers = "select user_name, password from ChatData.user";
		ResultSet resUsers = this.query(sqlUsers);
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_REGISTRATION");
		JSONObject subObj = new JSONObject();
		String isExists = "False";
		try
		{
			while (resUsers.next())
			{
				String usr = resUsers.getString(1);
				String pass = resUsers.getString(2);
				if (usr.equals(userName) && pass.equals(password))
				{
					isExists = "True";
					break;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN GET REGISTRATION");
			System.out.println(e.getMessage());
			return "ERROR WHEN GET REGISTRATION";
		}
		if (isExists.equals("False"))
		{
			String insertUser = "insert into ChatData.user(user_name, full_name, password) values ('" + userName + "', '" + fullName + "', '" + password + "');";
			this.update(insertUser);
		}

		subObj.put("isExistedUser", isExists);
		obj.put("output", subObj);
		return obj.toString();
	}

	@SuppressWarnings("unchecked")
	public String createGroup(String groupName, String creator)
	{
		String sqlCreateGroup = "insert into ChatData.group(group_name, creator) values ('" + groupName + "', '" + creator + "');";
		this.update(sqlCreateGroup);
		String sqlLastIdGroup = "select distinct last_insert_id() from ChatData.group;";
		ResultSet resLastIdGroup = this.query(sqlLastIdGroup);
		long lastIdGroup = 0;
		try 
		{
			resLastIdGroup.next();
			lastIdGroup = resLastIdGroup.getInt(1);
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN CREATE GROUP");
			System.out.println(e.getMessage());
			return "ERROR WHEN CREATE GROUP";
		}
		String sqlAddFirstMember = "insert into mapusergroup(group_id, user_name) values (" + lastIdGroup + ", '" + creator + "');";
		this.update(sqlAddFirstMember);
		String sqlAddReceiver = "insert into receiver(user_name, group_id) values (NULL, " + lastIdGroup + ");";
		this.update(sqlAddReceiver);
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_CREATE_GROUP");
		JSONObject subObj = new JSONObject();
		subObj.put("groupId", lastIdGroup);
		obj.put("output", subObj);
		return obj.toString();
	}

	@SuppressWarnings("unchecked")
	public void addMemberToGroup(long groupId, String memberName)
	{
		String sqlCheckExists = "select * from mapusergroup where group_id = " + groupId + " and user_name = '" + memberName + "';";
		ResultSet resCheckExists = this.query(sqlCheckExists);
		boolean isExists = true;
		try
		{
			resCheckExists.next();
			String foo = resCheckExists.getString(1);
		}
		catch (SQLException e)
		{
			isExists = false;
		}
		if (isExists)
		{
			System.out.println("Member has been already added before");
		}
		else
		{
			String sqlAddMember = "insert into mapusergroup(group_id, user_name) values (" + groupId + ", '" + memberName + "');";
			this.update(sqlAddMember);
			String sqlGetIdReceiver = "select idRec from receiver where group_id = " + groupId + ";";
			ResultSet resGetIdReceiver = this.query(sqlGetIdReceiver);
			try 
			{
				resGetIdReceiver.next();
				long idReceiver = resGetIdReceiver.getInt(1);
				String sqlGetGroupMessages = "select idMess, timeSent from ChatData.messages where receiver = " + idReceiver + " order by timeSent desc;";
				ResultSet resGetGroupMessages = this.query(sqlGetGroupMessages);
				long idLastMess = 0;
				String lastTime = "";
				if (resGetGroupMessages.next()) {
					try
					{
						idLastMess = resGetGroupMessages.getInt(1);
						lastTime = resGetGroupMessages.getString(2);
					}
					catch (SQLException e)
					{
						System.out.println(e.getMessage());
					}
					String sqlInsertToInbox = "insert into ChatData.inbox values (" + idLastMess + ", '" + lastTime + "', '" + memberName + "', " + idReceiver + ");";
					this.update(sqlInsertToInbox);
				}
			}
			catch (SQLException e)
			{
				System.out.println("ERROR WHEN ADD MEMBER TO GROUP");
				System.out.println(e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> deleteMember(long groupId, String memberName, String memberDelete)
	{
		String sqlGetReceiver = "select idRec from receiver where group_id = " + groupId + ";";
		ResultSet resGetReceiver = this.query(sqlGetReceiver);
		ArrayList<String> lst = new ArrayList<String>();
		try
		{
			resGetReceiver.next();
			long idReceiver = resGetReceiver.getInt(1);
			String sqlDeleteInbox = "delete from ChatData.inbox where receiver = " + idReceiver + " and sender = '" + memberName + "';";
			this.update(sqlDeleteInbox);
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN DELETE MEMBER");
			System.out.println(e.getMessage());
			return lst;
		}
		String sqlDeleteMember = "delete from mapusergroup where group_id = " + groupId + " and user_name = '" + memberName + "';";
		this.update(sqlDeleteMember);
		JSONObject obj = new JSONObject();
		obj.put("type", "NOTIFICATION_DELETE");
		JSONObject subObj = new JSONObject();
		subObj.put("group_id", groupId);
		subObj.put("member_do", memberDelete);
		subObj.put("member_name", memberName);
		obj.put("output", subObj);
		lst.add(obj.toString());
		lst.add(memberName);
		
//		String sqlAllMembersOfGroup = "select user_name from mapusergroup where group_id = " + groupId + ";";
//		ResultSet resAllMembersOfGroup = this.query(sqlAllMembersOfGroup);
//		try
//		{
//			while (resAllMembersOfGroup.next())
//			{
//				String userName = resAllMembersOfGroup.getString(1);
//				lst.add(userName);
//			}
//		}
//		catch (SQLException e)
//		{
//			System.out.println(e.getMessage());
//		}
		return lst;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> outGroup(long groupId, String memberName)
	{
		String sqlGetReceiver = "select idRec from receiver where group_id = " + groupId + ";";
		ResultSet resGetReceiver = this.query(sqlGetReceiver);
		ArrayList<String> lst = new ArrayList<String>();
		try
		{
			resGetReceiver.next();
			long idReceiver = resGetReceiver.getInt(1);
			String sqlDeleteInbox = "delete from ChatData.inbox where receiver = " + idReceiver + " and sender = '" + memberName + "';";
			this.update(sqlDeleteInbox);
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN DELETE MEMBER");
			System.out.println(e.getMessage());
			return lst;
		}
		String sqlDeleteMember = "delete from mapusergroup where group_id = " + groupId + " and user_name = '" + memberName + "';";
		this.update(sqlDeleteMember);
		String sqlAllMembersOfGroup = "select user_name from mapusergroup where group_id = " + groupId + ";";
		ResultSet resAllMembersOfGroup = this.query(sqlAllMembersOfGroup);
		String isEmpty = "True";
		try
		{
			while (resAllMembersOfGroup.next())
			{
				String userName = resAllMembersOfGroup.getString(1);
				isEmpty = "False";
				lst.add(userName);
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return new ArrayList<String>();
		}
		JSONObject obj = new JSONObject();
		obj.put("type", "NOTIFICATION");
		JSONObject subObj = new JSONObject();
		subObj.put("group_id", groupId);
		subObj.put("user_name", memberName);
		subObj.put("isEmpty", isEmpty);
		obj.put("output", subObj);
		lst.add(0, obj.toString());
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray filterFriend(String[] friends, int sizeFriends, String[] members, int sizeMembers)
	{
		JSONArray arr = new JSONArray();
		for (int i = 0; i < sizeFriends; ++i)
		{
			boolean belong = false;
			for (int j = 0; j < sizeMembers; ++j)
			{
				if (friends[i].equals(members[j]))
				{
					belong = true;
					break;
				}
			}
			if (!belong)
			{
				JSONObject subObj = new JSONObject();
				subObj.put("user_name", friends[i]);
				arr.add(subObj);
			}
		}
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	public String listOutsideFriend(long groupId, String userName)
	{
		String sqlGetAllFriends = "select left_friend, right_friend from ChatData.friend where (left_friend = '" + userName + "' or right_friend = '" + userName + "') and accepted = 1;";
		ResultSet resGetAllFriends = this.query(sqlGetAllFriends);
		String sqlGetAllMembers = "select user_name from mapusergroup where group_id = " + groupId + " and user_name <> '" + userName + "';";
		ResultSet resGetAllMembers = this.query(sqlGetAllMembers);
		String[] friends = new String[50];
		String[] members = new String[50];
		int i = 0, j = 0;
		try
		{
			while (resGetAllFriends.next())
			{
				String left = resGetAllFriends.getString(1), right = resGetAllFriends.getString(2);
				String friend = (right.equals(userName)) ? left : right;
				friends[i] = friend;
				i++;
			}
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN GET OUTSIDE FRIEND");
			System.out.println(e.getMessage());
			return "";
		}
		
		try {
			while (resGetAllMembers.next())
			{
				String member = resGetAllMembers.getString(1);
				members[j] = member;
				j++;
			}
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN GET OUTSIDE FRIEND");
			System.out.println(e.getMessage());
			return "";
		}
		System.out.println("OT");
		JSONArray arr = this.filterFriend(friends, i, members, j);
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_GET_OUTSIDE_FRIEND");
		JSONObject subObj = new JSONObject();
		subObj.put("friends", arr);
		obj.put("output", subObj);
		return obj.toString();
	}
	
	public void deleteGroup(long groupId)
	{
		String sqlGetReceiver = "select idRec from receiver where group_id = " + groupId + ";";
		ResultSet resGetReceiver = this.query(sqlGetReceiver);
		long idReceiver;
		try
		{
			resGetReceiver.next();
			idReceiver = resGetReceiver.getInt(1);
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN DELETE GROUP");
			System.out.println(e.getMessage());
			return;
		}
		String sqlDeleteGroupInMessages = "delete from messages where receiver = " + idReceiver + ";";
		this.update(sqlDeleteGroupInMessages);
		String sqlDeleteReceiver = "delete from receiver where group_id = " + groupId + ";";
		this.update(sqlDeleteReceiver);
		String sqlDeleteGroup = "delete from ChatData.group where group_id = " + groupId + ";";
		this.update(sqlDeleteGroup);
	}
	
	@SuppressWarnings("unchecked")
	public String checkFriend(String left_user, String right_user)
	{
		String sqlGetRelationship = "select * from friend where (left_friend = '" + left_user + "' and right_friend = '" + right_user + "') "
				+ "or (left_friend = '" + right_user + "' and right_friend = '" + left_user + "');";
		ResultSet resGetRelationship = this.query(sqlGetRelationship);
		boolean left = true, right = true;
		try 
		{
			resGetRelationship.next();
			int accepted = resGetRelationship.getInt(3);
			if (accepted == 0)
			{
				String leftFriend = resGetRelationship.getString(1);
				if (leftFriend.equals(left_user))
				{
					right = false;
				}
				else
					left = false;
			}
		}
		catch (SQLException e)
		{
			//System.out.println(e.getMessage());
			left = right = false;
		}
		JSONObject obj = new JSONObject();
		obj.put("type", "CHECK_FRIEND_RESPONSE");
		JSONObject subObj = new JSONObject();
		subObj.put("Enable_left", left);
		subObj.put("Enable_right", right);
		obj.put("output", subObj);
		return obj.toString();
	}
	
	
	public void addFriend(String left_user, String right_user)
	{
		String sqlCheckFriendship = "select * from friend where (left_friend = '" + left_user + "' and right_friend = '" + right_user + "') "
				+ "or (left_friend = '" + right_user + "' and right_friend = '" + left_user + "');";
		ResultSet resCheckFriendship = this.query(sqlCheckFriendship);
		try 
		{
			resCheckFriendship.next();
			resCheckFriendship.getString(1);
			System.out.println("Error in adding friend, duplicate row in table");
			return;
		}
		catch (SQLException e)
		{
			System.out.println("Ready for adding friend");
		}
		String sqlAddFriend = "insert into friend values ('" + left_user + "', '" + right_user + "', 0);";
		this.update(sqlAddFriend);
	}
	
	public void acceptFriend(String left_user, String right_user)
	{
		String sqlCheckExists = "select * from friend where left_friend = '" + right_user + "' and right_friend = '" + left_user + "';";
		ResultSet resCheckExists = this.query(sqlCheckExists);
		try
		{
			resCheckExists.next();
			resCheckExists.getString(1);
		}
		catch (SQLException e)
		{
			System.out.println("Error when accepting add-friend request!");
			return;
		}
		String sqlAcceptFriendship = "update friend set accepted = 1 where left_friend = '" + right_user + "' and right_friend = '" + left_user + "';";
		this.update(sqlAcceptFriendship);
	}
	
	public void cancelAddFriend(String left_user, String right_user)
	{
		String sqlCheckCurrentFriendship = "select * from friend where left_friend = '" + left_user + "' and right_friend = '" + right_user + "';";
		ResultSet resCheckCurrentFriendship = this.query(sqlCheckCurrentFriendship);
		try
		{
			resCheckCurrentFriendship.next();
			int accepted = resCheckCurrentFriendship.getInt(3);
			if (accepted == 1)
			{
				System.out.println("Error when cancel add friend request");
				return;
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error when cancel add friend request");
			return;
		}
		String sqlRemoveAddFriendRequest = "delete from friend where left_friend = '" + left_user + "' and right_friend = '" + right_user + "';";
		this.update(sqlRemoveAddFriendRequest);
	}
	
	public void noAcceptFriendRequest(String left_user, String right_user)
	{
		String sqlCheckCurrentFriendship = "select * from friend where left_friend = '" + right_user + "' and right_friend = '" + left_user + "';";
		ResultSet resCheckCurrentFriendship = this.query(sqlCheckCurrentFriendship);
		try
		{
			resCheckCurrentFriendship.next();
			resCheckCurrentFriendship.getInt(3);
		}
		catch (SQLException e)
		{
			System.out.println("Error when deny add friend request");
			return;
		}
		String sqlRemoveAddFriendRequest = "delete from friend where left_friend = '" + right_user + "' and right_friend = '" + left_user + "';";
		this.update(sqlRemoveAddFriendRequest);
	}	

	public void unfriend(String left_user, String right_user) 
	{
		String sqlCheckCurrentFriendship = "select * from friend where (left_friend = '" + right_user + "' and right_friend = '" + left_user + "') "
				+ "or (left_friend = '" + left_user + "' and right_friend = '" + right_user + "');";
		ResultSet resCheckCurrentFriendship = this.query(sqlCheckCurrentFriendship);
		String left = "";
		try 
		{
			resCheckCurrentFriendship.next();
			left = resCheckCurrentFriendship.getString(1);
		}
		catch (SQLException e)
		{
			System.out.println("Error when unfriend");
		}
		String right = (left.equals(left_user)) ? right_user : left_user;
		String sqlRemoveFriendship = "delete from friend where left_friend = '" + left + "' and right_friend = '" + right + "';";
		this.update(sqlRemoveFriendship);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getConnectVideoCall(String sender, long receiver) 
	{
		ArrayList<String> lst = new ArrayList<String>();
		boolean isGroup = false;
		try 
		{	
			String sqlReceiver = "select * from ChatData.receiver where idRec = " + receiver;
			ResultSet resReceiver = this.query(sqlReceiver);
			String userNameOfReceiver = null;
			resReceiver.next() ;
			userNameOfReceiver = resReceiver.getString(2);
			isGroup = (userNameOfReceiver == null) ? true : false;
			JSONObject obj = new JSONObject();
			obj.put("type", "GET_CONFIRM_VIDEO_CALL");
			JSONObject subObj = new JSONObject();
			subObj.put("idReceiver", receiver);
			subObj.put("key", sender);
			subObj.put("sender", sender);
			obj.put("output", subObj);
			lst.add(obj.toString());
			if (!isGroup)
			{	
				lst.add(userNameOfReceiver);
			}
			else
			{
				int groupId = resReceiver.getInt(3);
				String sqlGetMembersOfGroup = "select * from mapusergroup where group_id = " + groupId + ";";
				ResultSet resGetMembersOfGroup = this.query(sqlGetMembersOfGroup);
				while (resGetMembersOfGroup.next())
				{
					String memberName = resGetMembersOfGroup.getString(3);
					if (!memberName.equals(sender))
						lst.add(memberName);
				}		
			}
		}
		catch (NullPointerException e)
		{
			System.out.println("GET VIDEO CALL ERROR");
			System.out.println("Null pointer Exception");
		}
		catch (SQLException e)
		{
			System.out.println("GET VIDEO CALL ERROR");
			System.out.println(e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		System.out.println(lst.toString());
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> resConfirmVideoCall(String sender, long idReceiver, String confirm) 
	{
		ArrayList<String> lst = new ArrayList<String>();
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_CONFIRM_VC");
		JSONObject subObj = new JSONObject();
		subObj.put("sender", sender);
		subObj.put("idReceiver", idReceiver);
		subObj.put("confirm", confirm);
		obj.put("output", subObj);
		lst.add(obj.toString());
		lst.add(sender);
		return lst;
	}
	
	private int getStatus(String userName)
	{
		String sqlCheckOnline = "select status from ChatData.user where user_name = '" + userName + "';";
		ResultSet resCheckOnline = this.query(sqlCheckOnline);
		int status = 0;
		try
		{
			resCheckOnline.next();
			status = Integer.parseInt(resCheckOnline.getString(1));
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return -1;
		}
		return status;
	}
	
	@SuppressWarnings("unchecked")
	public String listFriend(String user) 
	{
		String sqlCheckCurrentFriendship = "select left_friend, right_friend from friend where (left_friend = '" + user + "' or right_friend = '" + user + "') and accepted = 1";
		ResultSet resCheckCurrentFriendship = this.query(sqlCheckCurrentFriendship);
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_LIST_FRIEND");
		JSONObject subObj = new JSONObject();
		JSONArray arr = new JSONArray();
		try 
		{
			while (resCheckCurrentFriendship.next())
			{
				String rightFriend = resCheckCurrentFriendship.getString(2), leftFriend = resCheckCurrentFriendship.getString(1);
				String friend = (rightFriend.equals(user)) ? leftFriend : rightFriend;
				JSONObject person = new JSONObject();
				person.put("user_name", friend);
				person.put("isGroup", 0);
				person.put("group_id", null);
				long idPerson = getIdReceiver(friend);
				person.put("idRec", idPerson);
				int online = this.getStatus(friend);
				if (online == -1)
				{
					System.out.println("ERROR WHEN GET STATUS");
					return "";
				}
				person.put("status", online);
				person.put("numOn", null);
				arr.add(person);
			}
		}
		catch (SQLException e)
		{
			System.out.println("ERROR WHEN LIST FRIEND");
			System.out.println(e);
			return "";
		}
		String sqlGetAllGroups = "select group_id from mapusergroup where user_name = '" + user + "';";
		ResultSet resGetAllGroups = this.query(sqlGetAllGroups);
		try
		{
			while (resGetAllGroups.next())
			{
				long groupId = resGetAllGroups.getInt(1);
				int numOnline = 0;
				String sqlGetAllMembers = "select user_name from mapusergroup where group_id = " + groupId + ";";
				ResultSet resGetAllMembers = this.query(sqlGetAllMembers);
				while (resGetAllMembers.next())
				{
					String member = resGetAllMembers.getString(1);
					int online = this.getStatus(member);
					if (online == 1)
						numOnline++;
				}
				JSONObject aGroup = new JSONObject();
				String sqlGetGroupName = "select group_name from ChatData.group where group_id = " + groupId + ";";
				ResultSet resGetGroupName = this.query(sqlGetGroupName);
				resGetGroupName.next();
				String groupName = resGetGroupName.getString(1);
				aGroup.put("user_name", groupName);
				aGroup.put("group_id", groupId); // add 
				aGroup.put("isGroup", 1);
				String sqlGetIdReceiver = "select idRec from ChatData.receiver where group_id = " + groupId + ";";
				ResultSet resGetIdReceiver = this.query(sqlGetIdReceiver);
				resGetIdReceiver.next();
				long idReceiver = resGetIdReceiver.getInt(1);
				aGroup.put("idRec", idReceiver);
				aGroup.put("status", null);
				aGroup.put("numOn", numOnline);
				arr.add(aGroup);
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return "";
		}
		subObj.put("friends", arr);
		obj.put("output", subObj);
		return obj.toString();
	}

	@SuppressWarnings("unchecked")
	public String getMembersOfGroup(long groupId)
	{
		String sqlGetMembers = "select user_name from mapusergroup where group_id = " + groupId + ";";
		ResultSet resGetMembers = this.query(sqlGetMembers);
		JSONObject obj = new JSONObject();
		obj.put("type", "RES_GET_MEMBERS_OF_GROUP");
		JSONObject subObj = new JSONObject();
		JSONArray arr = new JSONArray();
		try
		{
			while (resGetMembers.next())
			{
				String member = resGetMembers.getString(1);
				JSONObject obj1 = new JSONObject();
				obj1.put("user_name", member);
				arr.add(obj1);
			}
		}
		catch (SQLException e)
		{
			System.out.println("GET MEMBERS OF GROUP ERROR");
			System.out.println(e.getMessage());
			return "";
		}
		subObj.put("members", arr);
		obj.put("output", subObj);
		return obj.toString();
	}
	
	public static void main(String args[]) {
		ChatData data = new ChatData("jdbc:mysql://localhost:3306/ChatData","otr", "otr"); 
		//ArrayList<String> lst = data.outGroup(2, "user_2");
		//ArrayList<String> lst = data.addMemberToGroup(5, "__luan__", "__tan__");
		//Iterator<String> itr = lst.iterator();
		/*while (itr.hasNext())
		{
			System.out.println(itr.next());
		}*/
		//data.deleteGroup(2);
		//String res = data.listFriend("__tan__");
		//System.out.println(res);
		//data.sendTextMessage("user_1", 11, "xkt xkt xkt");
		//data.sendTextMessage("user_2", 11, "hkt hkt hkt");
		String res = data.listOutsideFriend(5, "__luan__");
		System.out.println(res);
		//data.createGroup("ggg", "__luan__");
		//data.addMemberToGroup(5, "user_2");
		//data.deleteMessage("user_1", 71);
		System.out.println("finished");
	}
}