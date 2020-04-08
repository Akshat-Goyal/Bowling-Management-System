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
 * Constructor for GUI used to Add Parties to the waiting party queue.
 *  
 */

public class ScoreView implements ActionListener, ListSelectionListener {

	private JFrame win;
	private JButton selectPlayer;
	private JList partyList, allScores;
	private Vector party, scoredb;
	private Integer lock;

	private ControlDeskView controlDesk;

	private String selectedNick, selectedMember;

	public ScoreView(ControlDeskView controlDesk) {

		this.controlDesk = controlDesk;

		win = new JFrame("Scores");
		win.getContentPane().setLayout(new BorderLayout());
		((JPanel) win.getContentPane()).setOpaque(false);

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new GridLayout(1, 3));

		// Party Panel
		JPanel partyPanel = new JPanel();
		partyPanel.setLayout(new FlowLayout());
		partyPanel.setBorder(new TitledBorder("Player's Score"));

		party = new Vector();
		Vector empty = new Vector();
		empty.add("(Empty)");

		partyList = new JList(empty);
		partyList.setFixedCellWidth(200);
		partyList.setVisibleRowCount(5);
		partyList.addListSelectionListener(this);
		JScrollPane partyPane = new JScrollPane(partyList);
		// partyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		partyPanel.add(partyPane);

		// Bowler Database
		JPanel scorePanel = new JPanel();
		scorePanel.setLayout(new FlowLayout());
		scorePanel.setBorder(new TitledBorder("Leader Board"));

		try {
			scoredb = new Vector(ScoreHistoryFile.getScores());
		} catch (Exception e) {
			System.err.println("File Error");
			scoredb = new Vector();
		}
		allScores = new JList(scoredb);
		allScores.setVisibleRowCount(8);
		allScores.setFixedCellWidth(120);
		JScrollPane scorePane = new JScrollPane(allScores);
		scorePane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		allScores.addListSelectionListener(this);
		scorePanel.add(scorePane);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 1));

		Insets buttonMargin = new Insets(4, 4, 4, 4);

		selectPlayer = new JButton("Select");
		JPanel selectPlayerPanel = new JPanel();
		selectPlayerPanel.setLayout(new FlowLayout());
		selectPlayer.addActionListener(this);
		selectPlayerPanel.add(selectPlayer);

		buttonPanel.add(selectPlayerPanel);

		// Clean up main panel
		colPanel.add(partyPanel);
		colPanel.add(scorePanel);
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(selectPlayer)) {
			if (selectedNick != null) {
				try {
					Vector playerScore = new Vector(ScoreHistoryFile.getScores(selectedNick.split(":  ")[0]));
					party = new Vector();
					playerScore.forEach((v) -> party.add(v));
				} catch (Exception err) {
					System.err.println("File Error");
					party = new Vector();
				}
				partyList.setListData(party);
			}
		}
	}

/**
 * Handler for List actions
 * @param e the ListActionEvent that triggered the handler
 */

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(allScores)) {
			selectedNick =
				((String) ((JList) e.getSource()).getSelectedValue());
		}
		if (e.getSource().equals(partyList)) {
			selectedMember =
				((String) ((JList) e.getSource()).getSelectedValue());
		}
	}

/**
 * Accessor for Party
 */

	public Vector getNames() {
		return party;
	}

/**
 * Called by NewPatronView to notify AddPartyView to update
 * 
 * @param newPatron the NewPatronView that called this method
 */

//	public void updateNewPatron(NewPatronView newPatron) {
//		try {
//			Bowler checkBowler = BowlerFile.getBowlerInfo( newPatron.getNick() );
//			if ( checkBowler == null ) {
//				BowlerFile.putBowlerInfo(
//					newPatron.getNick(),
//					newPatron.getFull(),
//					newPatron.getEmail());
//				scoredb = new Vector(BowlerFile.getBowlers());
//				allScores.setListData(scoredb);
//				party.add(newPatron.getNick());
//				partyList.setListData(party);
//			} else {
//				System.err.println( "A Bowler with that name already exists." );
//			}
//		} catch (Exception e2) {
//			System.err.println("File I/O Error");
//		}
//	}

/**
 * Accessor for Party
 */

	public Vector getParty() {
		return party;
	}

}
