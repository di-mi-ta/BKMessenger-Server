package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class DeleteGroup extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		long group_id = Long.parseLong(((JSONObject)obj.get("input")).get("group_id").toString());
		data.deleteGroup(group_id);
		ArrayList<String> lst = new ArrayList<String>();
		lst.add("DELETE_GROUP");
		return lst;
	}
}