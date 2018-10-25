package bkmessprotocol;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import database.ChatData;

public class GetMessages extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
 		long idReceiver = Long.parseLong(((JSONObject)obj.get("input")).get("idReceiver").toString());
 		String res = data.getMessages(user_name, idReceiver);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add(res);
 		return listResult;
	}
}