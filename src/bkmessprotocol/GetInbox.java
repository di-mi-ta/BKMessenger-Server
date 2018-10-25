package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class GetInbox extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
 		String res = data.getInbox(user_name);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add(res);
 		return listResult;
	}
}