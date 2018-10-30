package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class AddMemberToGroup extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
		long group_id = Long.parseLong(((JSONObject)obj.get("input")).get("group_id").toString());
 		data.addMemberToGroup(group_id, user_name);
 		ArrayList<String> lst = new ArrayList<String>();
 		return lst;
	}
}