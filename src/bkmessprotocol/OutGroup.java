package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class OutGroup extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
		long group_id = Long.parseLong(((JSONObject)obj.get("input")).get("group_id").toString());
 		return data.outGroup(group_id, user_name);
	}
}