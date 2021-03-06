package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import model.AudioManager;
import model.GameBoardModel;
import model.PieceFactory;

// This class is dedicated to controlling the game flow
public class Controller {
	
	// The fall timer that controls the speed at which pieces
	// move down the screen. Speed is set in the listener for
	// the start button, so there is no need to provide a 
	// default value here
	static Timer fallTimer = new Timer(0, new ActionListener() {

		public void actionPerformed(ActionEvent e) {

			// If the piece can be lowered, lower it
			if (GameFrame.gameBoardPanel.currentPiece.canMove(1, 0))
				GameFrame.gameBoardPanel.lowerPiece();
			
			// Piece cannot be lowered, so must add it to the log of
			// current pieces on the board
			else {
				
				// Obtain a list of all complete lines (if any) that
				// result from permanently adding this piece to the board
				List<Integer> completeLines = GameBoardModel.addPiece(GameFrame.gameBoardPanel.currentPiece);
				
				if (!completeLines.isEmpty()) {
					
					// TODO I need to fix the flashing task
					// Execute a new flashing rows task for these complete lines
					// Thread flash = new Thread(GUI.gameBoardPanel.new FlashRowsTask(completeLines));
					// GameFrame.THREAD_EXECUTOR.execute(flash);

					GameBoardModel.removeCompleteLines(completeLines);
					AudioManager.playClearLineSound(completeLines.size());
					GameFrame.scorePanel.refreshScoreInfo();
					
					// Update game grid display to reflect new configuration
					// after removing lines
					updateGameGrid(completeLines);

					if (GameBoardModel.justLeveled) processLevelUp();
					
				}
				
				// Move the conveyor belt along to set the new pieces for both piece panels
				moveConveyorBelt();
				
				// If next piece can't emerge, it's game over
				if (!GameFrame.gameBoardPanel.currentPiece.canEmerge())
					processGameOver();
				
				else {
					GameFrame.gameBoardPanel.paintCurrentAndGhost();
					GameFrame.nextPiecePanel.clear();
					GameFrame.nextPiecePanel.paintCurrentPiece();
				}

			}
			
		}
		
	});
	
	// Moves the factory conveyor belt along 1 piece, setting
	// the game board panel's piece to the first piece in line and
	// the next piece panel's to the one directly following it.
	// Package-private so it can be accessed by the start button listener
	// to set the initial pieces on game load
	static void moveConveyorBelt() {

		GameFrame.gameBoardPanel.currentPiece = PieceFactory.receiveNextPiece();
		GameFrame.nextPiecePanel.currentPiece = PieceFactory.peekAtNextPiece();
		
	}
	
	// Repaints the rows according to the new piece configuration after
	// removing lines
	private static void updateGameGrid(List<Integer> removedLines) {
		
		// Should only have to paint upwards from the bottom removed line
		for (int line = removedLines.get(0); line >= 0; line--)
			GameFrame.gameBoardPanel.paintRow(line);

	}
	
	// For if removing lines causes a level up
	private static void processLevelUp() {
		
		if (GameBoardModel.getLevel() == 11)
			processGameComplete();
		
		else {
			
			fallTimer.setDelay(GameBoardModel.getTimerDelay());
			GameFrame.scorePanel.flashLevelLabel();
			
		}
		
		GameBoardModel.justLeveled = false;
		
	}
	
	// For if removing lines causes game complete
	private static void processGameComplete() {
		
		fallTimer.stop();
		
		GameFrame.scorePanel.flashWinMessage();
		GameFrame.gameBoardPanel.disablePieceMovementInput();
		GameFrame.gameBoardPanel.startClearAnimation();
		
		AudioManager.playVictoryFanfare();
		AudioManager.resetSoundtrackFramePositions();
		
		// Disable all buttons. Start button is re-enabled after
		// game complete animation finishes
		GameFrame.menuPanel.disablePauseButton();
		GameFrame.menuPanel.disableResumeButton();
		GameFrame.menuPanel.disableGiveUpButton();
		
		// After game is competed, player should be able to re-select
		// difficulty if they decide to start a new game
		GameFrame.settingsPanel.disableCbxListeners();
		GameFrame.settingsPanel.enableDifficultyList();
		
	}
	
	// What happens when the next piece can't emerge. Package-private so
	// it can be accessed by the game over listener
	static void processGameOver() {

		fallTimer.stop();
		
		// Change audio over to game over sound
		AudioManager.stopCurrentSoundtrack();
		AudioManager.playGameOverSound();
		
		AudioManager.resetSoundtrackFramePositions();
		
		GameFrame.scorePanel.flashGameOverMessage();
		GameFrame.gameBoardPanel.startSpiralAnimation();
		
		GameFrame.gameBoardPanel.disablePieceMovementInput();
		
		// Disable the pause, resume, and give up buttons
		// They will re-enable once the next game starts
		GameFrame.menuPanel.disablePauseButton();
		GameFrame.menuPanel.disableResumeButton();
		GameFrame.menuPanel.disableGiveUpButton();
		
		GameFrame.settingsPanel.disableCbxListeners();
		GameFrame.settingsPanel.enableDifficultyList();
		
	}
	
}
