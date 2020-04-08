/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */


import java.util.*;
import java.io.*;

class SortbyScore implements Comparator<String> {
	public int compare(String a, String b){
		int c = Integer.valueOf(a.split(":  ")[1]);
		int d = Integer.valueOf(b.split(":  ")[1]);
		return d-c;
	}
}

public class ScoreHistoryFile {

	private static String SCOREHISTORY_DAT = "SCOREHISTORY.DAT";

	public static void addScore(String nick, String date, String score)
		throws IOException, FileNotFoundException {

		String data = nick + "\t" + date + "\t" + score + "\n";

		RandomAccessFile out = new RandomAccessFile(SCOREHISTORY_DAT, "rw");
		out.skipBytes((int) out.length());
		out.writeBytes(data);
		out.close();
	}

	public static Vector getScores(String nick)
		throws IOException, FileNotFoundException {
		Vector scores = new Vector();

		BufferedReader in =
			new BufferedReader(new FileReader(SCOREHISTORY_DAT));
		String data;
		while ((data = in.readLine()) != null) {
			// File format is nick\tfname\te-mail
			String[] scoredata = data.split("\t");
			//"Nick: scoredata[0] Date: scoredata[1] Score: scoredata[2]
			if (nick.equals(scoredata[0])) {
				scores.add(new Score(scoredata[0], scoredata[1], scoredata[2]));
			}
		}
		return scores;
	}

	public static Vector getScores()
			throws IOException, FileNotFoundException {
		Vector scores = new Vector();

		BufferedReader in =
				new BufferedReader(new FileReader(SCOREHISTORY_DAT));
		String data;
		HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
		while ((data = in.readLine()) != null) {
			// File format is nick\tfname\te-mail
			String[] scoredata = data.split("\t");
			//"Nick: scoredata[0] Date: scoredata[1] Score: scoredata[2]
			if(scoreMap.containsKey(scoredata[0])){
					if(scoreMap.get(scoredata[0]) < Integer.valueOf(scoredata[2])){
						scoreMap.replace(scoredata[0], Integer.valueOf(scoredata[2]));
					}
			}
			else{
				scoreMap.put(scoredata[0], Integer.valueOf(scoredata[2]));
			}
		}
		scoreMap.forEach((k, v) -> scores.add(new String(k + ":  " + String.valueOf(v))));
		Collections.sort(scores, new SortbyScore());
		return scores;
	}

}
