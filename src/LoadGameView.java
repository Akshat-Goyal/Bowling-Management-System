
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;
import java.text.*;

/**
 * Constructor for GUI used to Load Previous Games.
 *  
 */

public class LoadGameView implements ActionListener, ListSelectionListener {

	private JFrame win;
	private JButton bowlers, start;
	private JList bowlerList, partyList;
	private Vector bowlerdb, partydb;

	private ControlDeskView controlDeskView;

	private String selectedParty;

	public LoadGameView(ControlDeskView controlDeskView) {

		this.controlDeskView = controlDeskView;

		win = new JFrame("Load Game");
		win.getContentPane().setLayout(new BorderLayout());
		((JPanel) win.getContentPane()).setOpaque(false);

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new GridLayout(1, 3));

		// Bowler Panel
		JPanel bowlerPanel = new JPanel();
		bowlerPanel.setLayout(new FlowLayout());
		bowlerPanel.setBorder(new TitledBorder("Bowlers"));

		bowlerdb = new Vector();
		Vector empty = new Vector();
		empty.add("(Empty)");

		bowlerList = new JList(empty);
		bowlerList.setFixedCellWidth(120);
		bowlerList.setVisibleRowCount(6);
		JScrollPane bowlerPane = new JScrollPane(bowlerList);
		bowlerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		bowlerPanel.add(bowlerPane);

		// Party Database
		JPanel partyPanel = new JPanel();
		partyPanel.setLayout(new FlowLayout());
		partyPanel.setBorder(new TitledBorder("Party Database"));

		partydb = getPartydb();
		partyList = new JList(partydb);
		partyList.setVisibleRowCount(8);
		partyList.setFixedCellWidth(120);
		JScrollPane partyPane = new JScrollPane(partyList);
		partyPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		partyList.addListSelectionListener(this);
		partyPanel.add(partyPane);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 1));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 5, 5, 5));

		bowlers = new JButton("Bowlers");
		JPanel bowlersPanel = new JPanel();
		bowlersPanel.setLayout(new FlowLayout());
		bowlers.addActionListener(this);
		bowlersPanel.add(bowlers);

		start = new JButton("Resume");
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new FlowLayout());
		start.addActionListener(this);
		startPanel.add(start);

		buttonPanel.add(bowlersPanel);
		buttonPanel.add(startPanel);

		// Clean up main panel
		colPanel.add(bowlerPanel);
		colPanel.add(partyPanel);
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

	/**
	 * returns list of all party list saved in Lane.json file
	 */
	public Vector getPartydb() {
		Vector db;
		try {
			db = LaneJsonFile.getAllParty();
		} catch (Exception e) {
			System.err.println("File Error");
			db = new Vector();
		}
		return db;
	}

	/**
	 * returns all bowlers of a selected party
	 */
	public Vector getBowlerdb(String partyName) {
		Vector db;
		try {
			db = LaneJsonFile.getBowlers(partyName.split(":  ")[0], partyName.split(":  ")[1]);
		} catch (Exception e) {
			System.err.println("File Error");
			db = new Vector();
		}
		return db;
	}

	/**
	 * returns all bowlers of a selected party
	 */
	public JSONObject getPartyObj(String partyName) {
		JSONObject db;
		try {
			db = LaneJsonFile.getParty(partyName.split(":  ")[0], partyName.split(":  ")[1]);
		} catch (Exception e) {
			System.err.println("File Error");
			db = new JSONObject();
		}
		return db;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(bowlers)) {
			if (selectedParty != null) {
				bowlerdb = getBowlerdb(selectedParty);
				bowlerList.setListData(bowlerdb);
			}
		}
		if (e.getSource().equals(start)) {
			if (selectedParty != null) {
				for(Object l: controlDeskView.getControlDesk().getLanes()) {
					Lane lane = (Lane) l;
					if(lane.isPartyAssigned() == false) {
						lane.updateStates(getPartyObj(selectedParty));
						break;
					}
				}
				win.hide();
			}
		}
	}

/**
 * Handler for List actions
 * @param e the ListActionEvent that triggered the handler
 */

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(partyList)) {
			selectedParty =
				((String) ((JList) e.getSource()).getSelectedValue());
		}
	}

}
