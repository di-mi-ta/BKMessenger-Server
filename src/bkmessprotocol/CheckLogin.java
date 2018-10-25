package bkmessprotocol;
import org.json.simple.JSONObject;
import database.ChatData;
import java.util.ArrayList;
import server.ClientThread;

public class CheckLogin extends Api{
	private int indexThread = -1;
	private final ClientThread[] clientThreads;
	
	public CheckLogin(int indexThread, ClientThread[] threads) {
		 this.clientThreads = threads;
		 this.indexThread = indexThread;
	}
	
	public ArrayList<String> processMessage(JSONObject obj, ChatData data) {
		String user_name = ((JSONObject)obj.get("input")).get("user_name").toString();
 		String password = ((JSONObject)obj.get("input")).get("password").toString();
 		String IpAddr = ((JSONObject)obj.get("input")).get("IpAddr").toString();
 		String res = data.checkLogin(user_name, password, IpAddr);
 		this.clientThreads[this.indexThread].setOwner(user_name);
 		ArrayList<String> listResult = new ArrayList<String>();
 		listResult.add(res);
 		return listResult;
	}
}




