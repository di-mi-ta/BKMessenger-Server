package bkmessprotocol;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import database.ChatData;

public class ListOutsideFriend extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
		long group_id = Long.parseLong(((JSONObject)obj.get("input")).get("group_id").toString());
 		String res = data.listOutsideFriend(group_id, user_name);
 		ArrayList<String> lst = new ArrayList<String>();
 		lst.add(res);
 		return lst;
	}
}