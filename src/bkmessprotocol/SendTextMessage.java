package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class SendTextMessage extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String sender = ((JSONObject)obj.get("input")).get("sender").toString();
 		long idReceiver = Long.parseLong(((JSONObject)obj.get("input")).get("idReceiver").toString());
 		String content = ((JSONObject)obj.get("input")).get("content").toString();
 		return data.sendTextMessage(sender, idReceiver, content);
	}
}