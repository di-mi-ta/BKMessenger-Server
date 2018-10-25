package bkmessprotocol;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import database.ChatData;

public abstract class Api {
	public abstract ArrayList<String> processMessage(JSONObject obj, ChatData data);
}
