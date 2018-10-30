package bkmessprotocol;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import database.ChatData;

public class ListFriend extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user = ((JSONObject)obj.get("input")).get("user_name").toString();
 		String res = data.listFriend(user);
 		ArrayList<String> lst = new ArrayList<String>();
 		lst.add(res);
 		return lst;
	}
}