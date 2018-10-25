package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class DeleteAllMessage extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String sender = ((JSONObject)obj.get("input")).get("sender").toString();
 		long receiver = Long.parseLong(((JSONObject)obj.get("input")).get("receiver").toString());
 		data.deleteAllMessages(sender, receiver);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add("DELETE ALL MESSAGE");
 		return listResult;
	}
}