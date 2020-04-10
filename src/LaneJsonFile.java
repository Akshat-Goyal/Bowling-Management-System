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

	/**
	 * returns name of all party along with the time when game was paused
	 */
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

	/**
	 * returns json object of a team
	 * @param name is the team name which is nick name of first member of team
	 * @param time is the pause time
	 */
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

	/**
	 * returns list of all bowlers of the team
	 * @param name is the name of team
	 * @param time is the pause time
	 */
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

	/**
	 * delets the party from Lane.json
	 * @param name is the name of the party
	 * @param time is the game's pause time
	 */
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

	/**
	 * converts int[] to json array
	 */
	public static JSONArray intArrayToJson (int[] elements) {
		JSONArray arr = new JSONArray();
		for (int i = 0; i < elements.length; i++) {
			arr.add(elements[i]);
		}
		return arr;
	}

	/**
	 * converts int[][] to json array
	 */
	public static JSONArray int2DArrayToJson (int[][] elements) {
		JSONArray arr = new JSONArray();
		for (int i = 0; i < elements.length; i++) {
			arr.add(intArrayToJson(elements[i]));
		}
		return arr;
	}

	/**
	 * converts json array to int[]
	 */
	public static int[] parse1DArray(JSONArray array) {
		int[] arr = new int[array.size()];
		for(int i = 0; i < array.size(); i++) {
			arr[i] = Math.toIntExact((long)(array.get(i)));
		}
		return arr;
	}

	/**
	 * converts json array to int[][]
	 */
	public static int[][] parse2DArray(JSONArray array) {
		int[][] arr = new int[array.size()][];
		for(int i = 0; i < array.size(); i++) {
			JSONArray newArray = (JSONArray)array.get(i);
			arr[i] = parse1DArray(newArray);
		}
		return arr;
	}

}
