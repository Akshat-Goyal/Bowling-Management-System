/* AddPartyView.java
 *
 *  Version:
 * 		 $Id$
 * 
 *  Revisions:
 * 		$Log: AddPartyView.java,v $
 * 		Revision 1.7  2003/02/20 02:05:53  ???
 * 		Fixed addPatron so that duplicates won't be created.
 * 		
 * 		Revision 1.6  2003/02/09 20:52:46  ???
 * 		Added comments.
 * 		
 * 		Revision 1.5  2003/02/02 17:42:09  ???
 * 		Made updates to migrate to observer model.
 * 		
 * 		Revision 1.4  2003/02/02 16:29:52  ???
 * 		Added ControlDeskEvent and ControlDeskObserver. Updated Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of ControlDesk.
 * 		
 * 
 */

/**
 * Class for GUI components need to add a party
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;
import java.text.*;

/**
 * Constructor for GUI used to Show LeaderBoard and Player Panel
 *  
 */

public class ScoreView implements ActionListener, ListSelectionListener {

	private JFrame win;
	private JButton allScore, maxScore, minScore;
	private JList playerList, boardList;
	private Vector playerScore, leaderBoard;

	private ControlDeskView controlDeskView;

	private String selectedNick, selectedButton;

	public ScoreView(ControlDeskView controlDeskView) {

		this.controlDeskView = controlDeskView;
		updateLanesAboutScoreWin();

		win = new JFrame("Scores");
		win.getContentPane().setLayout(new BorderLayout());
		((JPanel) win.getContentPane()).setOpaque(false);

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new GridLayout(1, 3));

		// Player Panel
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new FlowLayout());
		playerPanel.setBorder(new TitledBorder("Player Panel"));

		playerScore = new Vector();
		Vector empty = new Vector();
		empty.add("(Empty)");

		playerList = new JList(empty);
		playerList.setFixedCellWidth(200);
		playerList.setVisibleRowCount(8);
		JScrollPane partyPane = new JScrollPane(playerList);
		partyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		playerPanel.add(partyPane);

		// Leader Board Database
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new FlowLayout());
		boardPanel.setBorder(new TitledBorder("Leader Board"));

		leaderBoard = getLeaderBoard();
		boardList = new JList(leaderBoard);
		boardList.setVisibleRowCount(8);
		boardList.setFixedCellWidth(120);
		JScrollPane scorePane = new JScrollPane(boardList);
		scorePane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		boardList.addListSelectionListener(this);
		boardPanel.add(scorePane);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1));

		Insets buttonMargin = new Insets(4, 4, 4, 4);

		allScore = new JButton("All Scores");
		JPanel allScorePanel = new JPanel();
		allScorePanel.setLayout(new FlowLayout());
		allScore.addActionListener(this);
		allScorePanel.add(allScore);

		maxScore = new JButton("Max Score");
		JPanel maxScorePanel = new JPanel();
		maxScorePanel.setLayout(new FlowLayout());
		maxScore.addActionListener(this);
		maxScorePanel.add(maxScore);

		minScore = new JButton("Min Score");
		JPanel minScorePanel = new JPanel();
		minScorePanel.setLayout(new FlowLayout());
		minScore.addActionListener(this);
		minScorePanel.add(minScore);

		buttonPanel.add(allScorePanel);
		buttonPanel.add(maxScorePanel);
		buttonPanel.add(minScorePanel);

		// Clean up main panel
		colPanel.add(playerPanel);
		colPanel.add(boardPanel);
		colPanel.add(buttonPanel);

		win.getContentPane().add("Center", colPanel);

		win.pack();

		// Center Window on Screen
		Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
		win.setLocation(
			((screenSize.width) / 2) - ((win.getSize().width) / 2),
			((screenSize.height) / 2) - ((win.getSize().height) / 2));
		win.show();

	}

	public void updateLanesAboutScoreWin(){
		HashSet Lanes = controlDeskView.getControlDesk().getLanes();
		Lanes.forEach((v) -> {
			Lane lane = (Lane) v;
			lane.updateScoreWin(this);
		});
	}

	public Vector getLeaderBoard() {
		Vector scoreBoard;
		try {
			scoreBoard = new Vector(ScoreHistoryFile.getScores());
		} catch (Exception err) {
			System.err.println("File Error");
			scoreBoard = new Vector();
		}
		return scoreBoard;
	}

	public Vector getPlayerScore(String nickName) {
		Vector nickScore = new Vector();
		try {
			Vector scoredb = new Vector(ScoreHistoryFile.getScores(nickName));
			Collections.sort(scoredb, new SortbyScore());
			scoredb.forEach((v) -> {
				Score score = (Score) v;
				nickScore.add(new String( score.getNickName() + "  " + score.getDate() + "  " + score.getScore() ));
			});
		} catch (Exception err) {
			System.err.println("File Error");
		}
		return nickScore;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(allScore)) {
			if (selectedNick != null) {
				selectedButton = "allScore";
				playerScore = getPlayerScore(selectedNick.split(":  ")[0]);
				playerList.setListData(playerScore);
			}
		}
		if (e.getSource().equals(minScore)) {
			if (selectedNick != null) {
				selectedButton = "minScore";
				Vector scoredb = getPlayerScore(selectedNick.split(":  ")[0]);
				playerScore = new Vector();
				if(!scoredb.isEmpty()){
					playerScore.add(scoredb.lastElement());
				}
				playerList.setListData(playerScore);
			}
		}
		if (e.getSource().equals(maxScore)) {
			if (selectedNick != null) {
				selectedButton = "maxScore";
				Vector scoredb = getPlayerScore(selectedNick.split(":  ")[0]);
				playerScore = new Vector();
				if(!scoredb.isEmpty()){
					playerScore.add(scoredb.get(0));
				}
				playerList.setListData(playerScore);
			}
		}
	}

/**
 * Handler for List actions
 * @param e the ListActionEvent that triggered the handler
 */

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(boardList)) {
			selectedNick =
				((String) ((JList) e.getSource()).getSelectedValue());
		}
	}


/**
 * Called by ??? to notify Score View to update Leader Board and Player Panel
 */

	public void updateScoreView() {
		String nickName = selectedNick;
		if (selectedNick != null) {
			Vector scoredb = getPlayerScore(selectedNick.split(":  ")[0]);
			playerScore = new Vector();
			if(!scoredb.isEmpty() && selectedButton == "maxScore"){
				playerScore.add(scoredb.get(0));
			}
			else if(!scoredb.isEmpty() && selectedButton == "minScore"){
				playerScore.add(scoredb.lastElement());
			}
			else{
				playerScore = scoredb;
			}
			playerList.setListData(playerScore);
		}
		leaderBoard = getLeaderBoard();
		boardList.setListData(leaderBoard);
		selectedNick = nickName;
	}

}
