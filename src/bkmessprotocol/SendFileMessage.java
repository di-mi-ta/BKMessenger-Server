package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class SendFileMessage extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String sender = ((JSONObject)obj.get("input")).get("sender").toString();
 		long idReceiver = Long.parseLong(((JSONObject)obj.get("input")).get("idReceiver").toString());
 		String type = ((JSONObject)obj.get("input")).get("type").toString();
 		String content = ((JSONObject)obj.get("input")).get("content").toString();
 		String nameFile = ((JSONObject)obj.get("input")).get("nameFile").toString();
 		return data.sendFileMessage(sender, idReceiver, type, content,nameFile);
	}
}