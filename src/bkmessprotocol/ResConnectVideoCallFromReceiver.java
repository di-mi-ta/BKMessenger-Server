package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public class ResConnectVideoCallFromReceiver extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String sender = ((JSONObject)obj.get("input")).get("sender").toString();
		String confirm = ((JSONObject)obj.get("input")).get("confirm").toString();
 		long idReceiver = Long.parseLong(((JSONObject)obj.get("input")).get("idReceiver").toString());
 		return data.resConfirmVideoCall(sender, idReceiver, confirm);
	}
}