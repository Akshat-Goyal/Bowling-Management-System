/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.io.*;


public class LaneJsonFile {

	private static String LANEJSON = "./Lane.json";

	/**
	 * loads data from Lane.json file
	 */
	public static JSONArray loadData() {
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(LANEJSON)) {
			Object obj = jsonParser.parse(reader);
			return (JSONArray) obj;
		}
		catch (IOException | ParseException e){ }
		return (new JSONArray());
	}

	/**
	 * writes the lane data to Lane.json file
	 */
	public static void dumpData(JSONArray laneData) {
		try {
			FileWriter writer = new FileWriter(LANEJSON);
			writer.write(laneData.toJSONString());
			writer.close();
		}

		catch (IOException e) { }
	}

	public static Vector getAllParty()
		throws IOException, FileNotFoundException {
		JSONArray data = loadData();
		Vector partydb = new Vector();
		for(Object obj: data) {
			JSONObject object = (JSONObject) obj;
			ArrayList<String> party = (ArrayList<String>) object.get("party");
			String pauseTime = (String) object.get("pauseTime");
			partydb.add(new String(party.get(0) + ":  " + pauseTime));
		}
		return partydb;
	}

	public static JSONObject getParty(String name, String time)
			throws IOException, FileNotFoundException {
		JSONArray data = loadData();
		for(Object obj: data) {
			JSONObject object = (JSONObject) obj;
			ArrayList<String> party = (ArrayList<String>) object.get("party");
			String pauseTime = (String) object.get("pauseTime");
			if(party.get(0).equals(name) && pauseTime.equals(time)) {
				return object;
			}
		}
		return (new JSONObject());
	}

	public static Vector getBowlers(String name, String time)
			throws IOException, FileNotFoundException {
		JSONArray data = loadData();
		Vector bowlerdb = new Vector();
		for(Object obj: data) {
			JSONObject object = (JSONObject) obj;
			ArrayList<String> party = (ArrayList<String>) object.get("party");
			String pauseTime = (String) object.get("pauseTime");
			if(party.get(0).equals(name) && pauseTime.equals(time)) {
				for(String s: party) {
					bowlerdb.add(new String(s));
				}
				break;
			}
		}
		return bowlerdb;
	}

	public static void delParty(String name, String time)
			throws IOException, FileNotFoundException {
		JSONArray data = loadData();
		JSONArray newData = new JSONArray();
		for(Object obj: data) {
			JSONObject object = (JSONObject) obj;
			ArrayList<String> party = (ArrayList<String>) object.get("party");
			String pauseTime = (String) object.get("pauseTime");
			if(party.get(0).equals(name) && pauseTime.equals(time)) {
				continue;
			}
			newData.add(object);
		}
		dumpData(newData);
	}

}
