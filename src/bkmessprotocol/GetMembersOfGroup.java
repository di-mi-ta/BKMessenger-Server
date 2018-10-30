
package bkmessprotocol;
import java.util.ArrayList;
 
import org.json.simple.JSONObject;
import database.ChatData;
 
public class GetMembersOfGroup extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		long group_id = Long.parseLong(((JSONObject)obj.get("input")).get("group_id").toString());
 		String res = data.getMembersOfGroup(group_id);
 		ArrayList<String> lst = new ArrayList<String>();
 		lst.add(res);
 		return lst;
	}
}