package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class CreateGroup extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String group_name = ((JSONObject)obj.get("input")).get("group_name").toString();
		String creator = ((JSONObject)obj.get("input")).get("creator").toString();
 		String res = data.createGroup(group_name, creator);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add(res);
 		return listResult;
	}
}