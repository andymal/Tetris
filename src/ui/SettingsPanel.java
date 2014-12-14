package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class SettingsPanel extends JPanel {
	
	// Public so it can be used to map int values to difficulty strings by other
	// GUI classes and the DBComm class
	public final static String[] DIFFICULTIES = {"Easy","Medium","Hard"};
	
	private JCheckBox ghostSquaresCbx = new JCheckBox("Ghost Squares", true);
	private JCheckBox soundEffectsCbx = new JCheckBox("Sound Effects", true);
	private JCheckBox saveScoreCbx = new JCheckBox("Save Scores", true);
	
	private JComboBox<String> difficultyList = new JComboBox<String>(DIFFICULTIES);
	
	private ItemListener ghostSquaresListener = new ItemListener() {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (ghostSquaresCbx.isSelected())
				GameFrame.gameBoardPanel.paintGhostPiece();
			else 
				GameFrame.gameBoardPanel.eraseGhostPiece();
			
			// In case ghost overlaps current piece
			GameFrame.gameBoardPanel.paintCurrentPiece();
			
		}
		
	};
	
	SettingsPanel() {
		
		setLayout(new GridLayout(4,1));
		setBorder(new TitledBorder("Settings"));
		
		for (JCheckBox x : new JCheckBox[]{ghostSquaresCbx, soundEffectsCbx, saveScoreCbx}) {
			add(x);
			x.setFocusable(false);
		}
		
		JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		diffPanel.add(new JLabel("Difficulty:  "));
		diffPanel.add(difficultyList);
		add(diffPanel);
		
	}
	
	public boolean ghostSquaresOn() { return ghostSquaresCbx.isSelected(); }
	public boolean effectsOn() { return soundEffectsCbx.isSelected(); }
	public boolean saveScoreOn() { return saveScoreCbx.isSelected(); }
	
	int getDifficulty() { return difficultyList.getSelectedIndex(); }
	
	void enableCbxListeners() {
		ghostSquaresCbx.addItemListener(ghostSquaresListener);
	}
	
	void disableCbxListeners() {
		ghostSquaresCbx.removeItemListener(ghostSquaresListener);
	}
	
	void enableDifficultyList() { difficultyList.setEnabled(true); }
	
	void disableDifficultyList() { difficultyList.setEnabled(false); }
		
}
