package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;
 
public class GetContent extends Api{
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		long idFile = Long.parseLong(((JSONObject)obj.get("input")).get("idFile").toString());
		String contentFile = data.getContent(idFile);
		ArrayList<String> listResult = new ArrayList<String>();
		listResult.add(contentFile);
 		return listResult;
	}
}