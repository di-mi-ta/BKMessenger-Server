	package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class DeleteMessage extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
 		int idMess = Integer.parseInt(((JSONObject)obj.get("input")).get("idMess").toString());
 		data.deleteMessage(user_name, idMess);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add("DELETE MESSAGE");
 		return listResult;
	}
}