
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class CumulLaneScores  {

	private int[][] cumulScores;
	private int bowlIndex;

	public CumulLaneScores(int bowlVal) {
		bowlIndex = bowlVal;
	}

	public void writeScoreToFile(String nickName, ScoreView scoreWin) {
		try {
			Date date = Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("hh:mm dd/mm/yyyy");
			String strDate = dateFormat.format(date);
			ScoreHistoryFile.addScore(nickName, strDate, Integer.toString(cumulScores[bowlIndex][9]), scoreWin);
		} catch (Exception e) {
			System.err.println("Exception in addScore. " + e);
		}
	}

	public void setBowlIndex(int val) {
		bowlIndex = val;
	}

	public void reset(int partySize) {
		cumulScores = new int[partySize][10];
	}

	public int getFinalScore() {
		return cumulScores[bowlIndex][9];
	}

	public int [][] getCumulScores() {
		return cumulScores;
	}

	public void setCumulScores(int[][] cumScore) {
		cumulScores = cumScore;
	}

	public int getScore( Bowler Cur, int frame, int ball, int[] curScore) {
		int totalScore = 0;
		for (int i = 0; i != 10; i++){
			cumulScores[bowlIndex][i] = 0;
		}
		int current = 2*(frame - 1)+ball-1;
		//Iterate through each ball until the current one.
		for (int i = 0; i != current+2; i++){
			//Spare:
			if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
				//This ball was a the second of a spare.
				//Also, we're not on the current ball.
				//Add the next ball to the ith one in cumul.
				cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];
			} else if( i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
				//This ball is the first ball, and was a strike.
				//If we can get 2 balls after it, good add them to cumul.
				if (curScore[i+2] != -1 && (curScore[i+3] != -1 || curScore[i+4] != -1)) {
						//Ok, got it.
						handleTwoStrikeBalls(curScore, i);
				} else {
					break;
				}
			} else {
				//We're dealing with a normal throw, add it and be on our way.
				handleNormalThrow(curScore, i);
			}
		}
		return totalScore;
	}

	private void handleNormalThrow(int[] curScore, int i) {
		if(i < 18){
			if(i % 2 ==0 ) {
				if (i == 0 && curScore[i] != -2) {
					cumulScores[bowlIndex][i / 2] += curScore[i];
				} else {
					//add his last frame's cumul to this ball, make it this frame's cumul.
					int add = (curScore[i] != -2) ? curScore[i] : 0;
					cumulScores[bowlIndex][i / 2] += cumulScores[bowlIndex][i / 2 - 1] + add;
				}
			} else if(curScore[i] != -1 && i > 2 && curScore[i] != -2)
				cumulScores[bowlIndex][i/2] += curScore[i];
		}
		else if ((i/2 == 9 || i/2 == 10) && curScore[i] != -2){
			cumulScores[bowlIndex][9] += curScore[i];
		}
		if (i == 18){
			cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
		}
	}

	private void handleTwoStrikeBalls(int[] curScore, int i) {
		//Add up the strike.
		//Add the next two balls to the current cumulscore.
		cumulScores[bowlIndex][i/2] += 10;
		if(curScore[i+1] != -1) {
			cumulScores[bowlIndex][i/2] += curScore[i+1] + cumulScores[bowlIndex][(i/2)-1];
			if (curScore[i+2] != -1 && curScore[i+2] != -2){
				cumulScores[bowlIndex][(i/2)] += curScore[i+2];
			} else if(curScore[i+3] != -2){
				cumulScores[bowlIndex][(i/2)] += curScore[i+3];
			}
		} else {
			int add = (i/2 > 0) ? cumulScores[bowlIndex][(i/2)-1] : 0;
			cumulScores[bowlIndex][i/2] += curScore[i+2] + add;
			int id = 4;
			if (curScore[i+3] != -1 &&  curScore[i+3] != -2){
				id = 3;
			}
			cumulScores[bowlIndex][(i/2)] += curScore[i+id];
		}
	}

}
