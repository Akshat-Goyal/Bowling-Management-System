
/* $Id$
 *
 * Revisions:
 *   $Log: Lane.java,v $
 *   Revision 1.52  2003/02/20 20:27:45  ???
 *   Fouls disables.
 *
 *   Revision 1.51  2003/02/20 20:01:32  ???
 *   Added things.
 *
 *   Revision 1.50  2003/02/20 19:53:52  ???
 *   Added foul support.  Still need to update laneview and test this.
 *
 *   Revision 1.49  2003/02/20 11:18:22  ???
 *   Works beautifully.
 *
 *   Revision 1.48  2003/02/20 04:10:58  ???
 *   Score reporting code should be good.
 *
 *   Revision 1.47  2003/02/17 00:25:28  ???
 *   Added disbale controls for View objects.
 *
 *   Revision 1.46  2003/02/17 00:20:47  ???
 *   fix for event when game ends
 *
 *   Revision 1.43  2003/02/17 00:09:42  ???
 *   fix for event when game ends
 *
 *   Revision 1.42  2003/02/17 00:03:34  ???
 *   Bug fixed
 *
 *   Revision 1.41  2003/02/16 23:59:49  ???
 *   Reporting of sorts.
 *
 *   Revision 1.40  2003/02/16 23:44:33  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.39  2003/02/16 23:43:08  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.38  2003/02/16 23:41:05  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.37  2003/02/16 23:00:26  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.36  2003/02/16 21:31:04  ???
 *   Score logging.
 *
 *   Revision 1.35  2003/02/09 21:38:00  ???
 *   Added lots of comments
 *
 *   Revision 1.34  2003/02/06 00:27:46  ???
 *   Fixed a race condition
 *
 *   Revision 1.33  2003/02/05 11:16:34  ???
 *   Boom-Shacka-Lacka!!!
 *
 *   Revision 1.32  2003/02/05 01:15:19  ???
 *   Real close now.  Honest.
 *
 *   Revision 1.31  2003/02/04 22:02:04  ???
 *   Still not quite working...
 *
 *   Revision 1.30  2003/02/04 13:33:04  ???
 *   Lane may very well work now.
 *
 *   Revision 1.29  2003/02/02 23:57:27  ???
 *   fix on pinsetter hack
 *
 *   Revision 1.28  2003/02/02 23:49:48  ???
 *   Pinsetter generates an event when all pins are reset
 *
 *   Revision 1.27  2003/02/02 23:26:32  ???
 *   ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 *
 *   Revision 1.26  2003/02/02 23:11:42  ???
 *   parties can now play more than 1 game on a lane, and lanes are properly released after games
 *
 *   Revision 1.25  2003/02/02 22:52:19  ???
 *   Lane compiles
 *
 *   Revision 1.24  2003/02/02 22:50:10  ???
 *   Lane compiles
 *
 *   Revision 1.23  2003/02/02 22:47:34  ???
 *   More observering.
 *
 *   Revision 1.22  2003/02/02 22:15:40  ???
 *   Add accessor for pinsetter.
 *
 *   Revision 1.21  2003/02/02 21:59:20  ???
 *   added conditions for the party choosing to play another game
 *
 *   Revision 1.20  2003/02/02 21:51:54  ???
 *   LaneEvent may very well be observer method.
 *
 *   Revision 1.19  2003/02/02 20:28:59  ???
 *   fixed sleep thread bug in lane
 *
 *   Revision 1.18  2003/02/02 18:18:51  ???
 *   more changes. just need to fix scoring.
 *
 *   Revision 1.17  2003/02/02 17:47:02  ???
 *   Things are pretty close to working now...
 *
 *   Revision 1.16  2003/01/30 22:09:32  ???
 *   Worked on scoring.
 *
 *   Revision 1.15  2003/01/30 21:45:08  ???
 *   Fixed speling of received in Lane.
 *
 *   Revision 1.14  2003/01/30 21:29:30  ???
 *   Fixed some MVC stuff
 *
 *   Revision 1.13  2003/01/30 03:45:26  ???
 *   *** empty log message ***
 *
 *   Revision 1.12  2003/01/26 23:16:10  ???
 *   Improved thread handeling in lane/controldesk
 *
 *   Revision 1.11  2003/01/26 22:34:44  ???
 *   Total rewrite of lane and pinsetter for R2's observer model
 *   Added Lane/Pinsetter Observer
 *   Rewrite of scoring algorythm in lane
 *
 *   Revision 1.10  2003/01/26 20:44:05  ???
 *   small changes
 *
 * 
 */

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Lane extends Thread implements PinsetterObserver {	
	private Party party;
	private ScoreView scoreWin = null;
	private Pinsetter setter;
	private HashMap scores;
	private Vector subscribers;

	private boolean gameIsHalted;

	private boolean partyAssigned;
	private boolean gameFinished;
	private Iterator bowlerIterator;
	private int ball;
	private int bowlIndex;
	private int frameNumber;
	private boolean isReady;
	private boolean tenthFrameStrike;

	private int[] curScores;
	private CumulLaneScores currentCumulScores;
	private boolean canThrowAgain;
	private String pauseTime;
	private int[][] finalScores;
	private int gameNumber;
	
	private Bowler currentThrower;			// = the thrower who just took a throw

	/** Lane()
	 * 
	 * Constructs a new lane and starts its thread
	 * 
	 * @pre none
	 * @post a new lane has been created and its thered is executing
	 */
	public Lane() { 
		setter = new Pinsetter();
		scores = new HashMap<Bowler, int[]>();
		subscribers = new Vector();

		gameIsHalted = false;
		partyAssigned = false;
		isReady = true;

		gameNumber = 0;

		setter.subscribe( this );

		currentCumulScores = new CumulLaneScores(bowlIndex);
		
		this.start();
	}

	/** run()
	 * 
	 * entry point for execution of this lane 
	 */
	public void run() {
		
		while (true) {
			if (partyAssigned) {
				if (!gameFinished) {    // we have a party on this lane,
					// so next bower can take a throw

					while (gameIsHalted) {
						try {
							sleep(10);
						} catch (Exception e) {
							break;
						}
					}

					if (bowlerIterator.hasNext()) {
						currentThrower = (Bowler) bowlerIterator.next();

						canThrowAgain = true;
						tenthFrameStrike = false;
						ball = 0;
						while (canThrowAgain) {
							setter.ballThrown();        // simulate the thrower's ball hiting
							ball++;
						}

						if (frameNumber == 9) {
							finalScores[bowlIndex][gameNumber] = currentCumulScores.getFinalScore();
							currentCumulScores.writeScoreToFile(currentThrower.getNick(), scoreWin);
						}

						setter.reset();
						bowlIndex++;
						currentCumulScores.setBowlIndex(bowlIndex);

					} else {
						frameNumber++;
						resetBowlerIterator();
						bowlIndex = 0;
						currentCumulScores.setBowlIndex(bowlIndex);
						if (frameNumber > 9) {
							gameFinished = true;
							gameNumber++;
						}
					}
				} else {
					EndGamePrompt egp = new EndGamePrompt(((Bowler) party.getMembers().get(0)).getNickName() + "'s Party");
					int result = egp.getResult();
					egp.destroy();
					egp = null;

					System.out.println("result was: " + result);

					if (result == 1) {
						// yes, want to play again
						resetScores();
						resetBowlerIterator();
					} else if (result == 2) {
						// no, dont want to play another game
						endGame();
					}
				}
			}

			try {
				sleep(10);
			} catch (Exception e) {
				break;
			}
		}
	}

	private void endGame() {
		EndGameReport egr = new EndGameReport(((Bowler) party.getMembers().get(0)).getNickName() + "'s Party", party);
		Vector printVector = egr.getResult();
		Vector members = party.getMembers();

		party = null;
		partyAssigned = false;

		publish();

		for(int i=0; i<members.size(); i++) {
			Bowler thisBowler = (Bowler) members.get(i);
			ScoreReport sr = new ScoreReport(thisBowler, finalScores[i], gameNumber);
			sr.sendEmail(thisBowler.getEmail());
			for (Object o : printVector) {
				if (thisBowler.getNick().equals((String) o)) {
					System.out.println("Printing " + thisBowler.getNick());
					sr.sendPrintout();
				}
			}
		}
	}

	/** update ScoreWin to current Score View recently opened
	 * @param scoreView current scoreWin
	 */
	public void updateScoreWin(ScoreView scoreView){
		scoreWin = scoreView;
	}

	/** recievePinsetterEvent()
	 * 
	 * recieves the thrown event from the pinsetter
	 *
	 * @pre none
	 * @post the event has been acted upon if desiered
	 * 
	 * @param pe 		The pinsetter event that has been received.
	 */
	public void receivePinsetterEvent(PinsetterEvent pe) {
		
			if (pe.pinsDownOnThisThrow() >=  0) {			// this is a real throw
				markScore(currentThrower, frameNumber + 1, pe.getThrowNumber(), pe.pinsDownOnThisThrow());
	
				// next logic handles the ?: what conditions dont allow them another throw?
				// handle the case of 10th frame first
				if (frameNumber == 9) {
					if (pe.totalPinsDown() == 10) {
						setter.resetPins();
						if(pe.getThrowNumber() == 1) {
							tenthFrameStrike = true;
						}
					}
				
					if ((pe.totalPinsDown() != 10) && (pe.getThrowNumber() == 2 && !tenthFrameStrike)) {
						canThrowAgain = false;
					}

					if (pe.getThrowNumber() == 3) {
						canThrowAgain = false;
					}
				} else { // its not the 10th frame
			
					if (pe.pinsDownOnThisThrow() == 10) {		// threw a strike
						canThrowAgain = false;
					} else if (pe.getThrowNumber() == 2) {
						canThrowAgain = false;
					}

				}
			}  //  this is not a real throw, probably a reset

	}

	/** resetBowlerIterator()
	 * 
	 * sets the current bower iterator back to the first bowler
	 * 
	 * @pre the party as been assigned
	 * @post the iterator points to the first bowler in the party
	 */
	private void resetBowlerIterator() {
		bowlerIterator = (party.getMembers()).iterator();
	}

	/** resetScores()
	 * 
	 * resets the scoring mechanism, must be called before scoring starts
	 * 
	 * @pre the party has been assigned
	 * @post scoring system is initialized
	 */
	private void resetScores() {
		Iterator bowlIt = (party.getMembers()).iterator();

		while ( bowlIt.hasNext() ) {
			int[] toPut = new int[25];
			for ( int i = 0; i != 25; i++){
				toPut[i] = -1;
			}
			scores.put( bowlIt.next(), toPut );
		}
		
		gameFinished = false;
		frameNumber = 0;
	}
		
	/** assignParty()
	 * 
	 * assigns a party to this lane
	 * 
	 * @pre none
	 * @post the party has been assigned to the lane
	 * 
	 * @param theParty		Party to be assigned
	 */
	public void assignParty( Party theParty ) {
		party = theParty;
		resetBowlerIterator();
		partyAssigned = true;
		
		curScores = new int[party.getMembers().size()];
		currentCumulScores.reset(party.getMembers().size());
		finalScores = new int[party.getMembers().size()][128]; //Hardcoding a max of 128 games, bite me.
		gameNumber = 0;
		
		resetScores();
	}

	/** markScore()
	 *
	 * Method that marks a bowlers score on the board.
	 * 
	 * @param Cur		The current bowler
	 * @param frame	The frame that bowler is on
	 * @param ball		The ball the bowler is on
	 * @param score	The bowler's score 
	 */
	private void markScore( Bowler Cur, int frame, int ball, int score ){
		int[] curScore = new int[25];
		int index =  ( (frame - 1) * 2 + ball);

		for(Object b: scores.keySet()) {
			Bowler bowler = (Bowler) b;
			if(bowler.equals(Cur)){
				curScore = (int[]) scores.get(b);
				break;
			}
		}

		// same thing as above 'for loop' but this line is giving file error
		// curScore = (int[]) scores.get(Cur);

		curScore[ index - 1] = score;
		scores.put(Cur, curScore);
		currentCumulScores.getScore(Cur, frame, ball, (int[]) scores.get(Cur));
		publish();
	}

	/** getScore()
	 *
	 * Method that calculates a bowlers score
	 * 
	 * @param Cur		The bowler that is currently up
	 * @param frame	The frame the current bowler is on
	 * 
	 * @return			The bowlers total score
	 */

	/** isPartyAssigned()
	 * 
	 * checks if a party is assigned to this lane
	 * 
	 * @return true if party assigned, false otherwise
	 */
	public boolean isPartyAssigned() {
		return partyAssigned;
	}
	
	/** isGameFinished
	 * 
	 * @return true if the game is done, false otherwise
	 */
	public boolean isGameFinished() {
		return gameFinished;
	}

	/** subscribe
	 * 
	 * Method that will add a subscriber
	 * 
//	 * @param subscribe	Observer that is to be added
	 */

	public void subscribe( LaneObserver adding ) {
		subscribers.add( adding );
	}

	/** unsubscribe
	 * 
	 * Method that unsubscribes an observer from this object
	 * 
	 * @param removing	The observer to be removed
	 */
	
	public void unsubscribe( LaneObserver removing ) {
		subscribers.remove( removing );
	}

	/** publish
	 *
	 * publish();Method that publishes event to subscribers by getting by fetching event
	 *
	 */

	private void publish() {
		LaneEvent event = new LaneEvent(party, bowlIndex, currentThrower, currentCumulScores.getCumulScores(), scores, frameNumber+1, curScores, ball, gameIsHalted);
		if( subscribers.size() > 0 ) {
			Iterator eventIterator = subscribers.iterator();

			while ( eventIterator.hasNext() ) {
				( (LaneObserver) eventIterator.next()).receiveLaneEvent( event );
			}
		}
	}

	/**
	 * Accessor to get this Lane's pinsetter
	 * 
	 * @return		A reference to this lane's pinsetter
	 */

	public Pinsetter getPinsetter() {
		return setter;	
	}

	/**
	 * returns false to assignLane in controlDesk if old party is assigned this lane
	 */
	public boolean getIsReady() { return isReady; }

	/**
	 * updates all the states of lane to resume the previous game
	 */
	public void updateStates(JSONObject obj) {
		try{
			isReady = false;
			System.out.println("ok... assigning this party");
			ArrayList<String> partyList = (ArrayList<String>) obj.get("party");
			JSONArray scoreArray = (JSONArray) obj.get("scores");
			Vector partyVector = new Vector();
			scores = new HashMap();
			for(int i = 0; i < partyList.size(); i++) {
				String s = partyList.get(i);
				partyVector.add(BowlerFile.getBowlerInfo(s));
				scores.put(BowlerFile.getBowlerInfo(s), LaneJsonFile.parse1DArray((JSONArray) scoreArray.get(i)));
			}
			party = new Party(partyVector);
			party.setOld(true);
			currentThrower = BowlerFile.getBowlerInfo((String) obj.get("currentThrower"));
			resetBowlerIterator();
			while(bowlerIterator.hasNext()) {
				Bowler bowler = (Bowler) bowlerIterator.next();
				if(bowler.equals(currentThrower)) break;
			}
			bowlIndex = Math.toIntExact((long)obj.get("bowlIndex"));
			currentCumulScores = new CumulLaneScores(bowlIndex);
			currentCumulScores.setCumulScores(LaneJsonFile.parse2DArray((JSONArray) obj.get("cumulScores")));
			curScores = LaneJsonFile.parse1DArray((JSONArray) obj.get("curScores"));
			finalScores = LaneJsonFile.parse2DArray((JSONArray) obj.get("finalScores"));
			ball = Math.toIntExact((long)obj.get("ball"));
			pauseTime = (String) obj.get("pauseTime");
			tenthFrameStrike = (Boolean) obj.get("tenthFrameStrike");
			gameIsHalted = (Boolean) obj.get("gameIsHalted");
			canThrowAgain = (Boolean) obj.get("canThrowAgain");
			frameNumber = Math.toIntExact((long)obj.get("frameNumber"));
			gameNumber = Math.toIntExact((long)obj.get("gameNumber"));
			partyAssigned = true;
			isReady = true;
			unPauseGame();
		} catch (Exception e) {
			System.err.println("File Error");
		}

	}

	/**
	 * collects all data of lane and add it to jsonObject
	 */
	public JSONObject collectData() {
		JSONObject LaneData = new JSONObject();
		JSONArray partyData = new JSONArray();
		JSONArray scoreData = new JSONArray();
		party.getMembers().forEach((v) -> {
			Bowler bowler = (Bowler) v;
			scoreData.add(LaneJsonFile.intArrayToJson((int[]) scores.get(bowler)));
			partyData.add(bowler.getNickName());
		});
		LaneData.put("party", partyData);
		LaneData.put("scores", scoreData);
		LaneData.put("currentThrower", currentThrower.getNickName());
		LaneData.put("curScores", LaneJsonFile.intArrayToJson(curScores));
		LaneData.put("cumulScores", LaneJsonFile.int2DArrayToJson(currentCumulScores.getCumulScores()));
		LaneData.put("finalScores", LaneJsonFile.int2DArrayToJson(finalScores));
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm dd/mm/yyyy");
		String pauseTime = dateFormat.format(date);
		this.pauseTime = pauseTime;
		LaneData.put("pauseTime", pauseTime);
		LaneData.put("bowlIndex", bowlIndex);
		LaneData.put("ball", ball);
		LaneData.put("tenthFrameStrike", tenthFrameStrike);
		LaneData.put("gameIsHalted", gameIsHalted);
		LaneData.put("canThrowAgain", canThrowAgain);
		LaneData.put("frameNumber", frameNumber);
		LaneData.put("gameNumber", gameNumber);

		return LaneData;
	}

	/**
	 * Pause the execution of this game
	 */
	public void pauseGame() {
		gameIsHalted = true;
		publish();
		JSONObject LaneData = collectData();
		JSONArray laneArr = LaneJsonFile.loadData();
		laneArr.add(LaneData);
		LaneJsonFile.dumpData(laneArr);
	}
	
	/**
	 * Resume the execution of this game
	 */
	public void unPauseGame() {
		try{
			LaneJsonFile.delParty(((Bowler) party.getMembers().get(0)).getNickName(), pauseTime);
		} catch (Exception e) {
			System.err.println("File Error");
		}
		gameIsHalted = false;
		publish();
	}

}
