package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class GetRegistration extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
		String password = ((JSONObject)obj.get("input")).get("password").toString();
		String full_name = ((JSONObject)obj.get("input")).get("full_name").toString();
 		String res = data.getRegistration(user_name, password, full_name);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add(res);
 		return listResult;
	}
}