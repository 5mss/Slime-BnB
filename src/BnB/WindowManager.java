package bnb;

import java.awt.event.*;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.sound.sampled.*;

public class WindowManager {
	StartFrame startFrame;
	OptionFrame optionFrame;
	CharacterFrame characterFrame;
	GameFrame gameFrame;
	int width, height;
	boolean soundOn, musicOn;
	AudioInputStream music, click, win, loose, explosion;
	Clip musicClip, clickClip, winClip, looseClip, explosionClip;
	Timer tiemr;
	
	public WindowManager(int w, int h) {
		width = w;
		height = h;
		soundOn = true;
		musicOn = true;
		startFrame = new StartFrame(w, h, this);
		startFrame.init();
		startFrame.setVisible(true);
		setStartFrameButtons();	
		prepareClips();
	}
	
	private void prepareClips() {
		try {
			music = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("audios/bgm.wav"));
			click = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("audios/click.wav"));
			win = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("audios/win.wav"));
			loose = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("audios/loose.wav"));
			explosion = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("audios/explosion.wav"));
			musicClip = AudioSystem.getClip();
			musicClip.open(music);
			musicClip.loop(Clip.LOOP_CONTINUOUSLY);
			musicClip.start();
			clickClip = AudioSystem.getClip();
			clickClip.open(click);
			winClip = AudioSystem.getClip();
			winClip.open(win);
			looseClip = AudioSystem.getClip();
			looseClip.open(loose);
			explosionClip = AudioSystem.getClip();
			explosionClip.open(explosion);	
			
		} catch(Exception e) {e.printStackTrace();}
	}

	private void playClick() {
		if(soundOn) {
			clickClip.stop();
			clickClip.setFramePosition(0);
			clickClip.start();
		}
	}

	private void playWin() {
		if(soundOn) {
			winClip.stop();
			winClip.setFramePosition(0);
			winClip.start();
		}
	}

	private void playLoose() {
		if(soundOn) {
			looseClip.stop();
			looseClip.setFramePosition(0);
			looseClip.start();
		}
	}

	public void playExplosion() {
		if(soundOn) {
			explosionClip.stop();
			explosionClip.setFramePosition(0);
			explosionClip.start();
		}
	}
	
	private void setStartFrameButtons() {
		startFrame.startButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				playClick();
				startFrame.setVisible(false);
				optionFrame = new OptionFrame(width, height, startFrame);
				optionFrame.init();
				optionFrame.setVisible(true);
				setOptionFrameButtons();
			}
		});

		startFrame.musicButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				playClick();
				if(musicOn) {
					musicOn = false;
					startFrame.musicButton.setIcon(startFrame.musicOff);
					musicClip.stop();
				}
				else {
					musicOn = true;
					startFrame.musicButton.setIcon(startFrame.musicOn);
					musicClip.loop(Clip.LOOP_CONTINUOUSLY);
					musicClip.start();
				}
			}
		});

		startFrame.soundButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				playClick();
				if(soundOn) {
					soundOn = false;
					startFrame.soundButton.setIcon(startFrame.soundOff);
				}
				else {
					soundOn = true;
					startFrame.soundButton.setIcon(startFrame.soundOn);
				}
			}
		});

	}
	
	private void setOptionFrameButtons() {
		optionFrame.backButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				playClick();
				optionFrame.setVisible(false);
				startFrame.setVisible(true);
			}
		});
		
		optionFrame.nextButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				playClick();
				optionFrame.setVisible(false);
				characterFrame = new CharacterFrame(optionFrame);
				characterFrame.init();
				characterFrame.setVisible(true);
				setCharacterFrameButtons();
			}
		});
	}
	
	private void setCharacterFrameButtons() {
		characterFrame.backButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				playClick();
				characterFrame.setVisible(false);
				optionFrame.setVisible(true);
			}
		});
		
		characterFrame.startButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				playClick();
				characterFrame.setVisible(false);
				gameFrame = new GameFrame(optionFrame, characterFrame, 13, 15, 60);
				gameFrame.init();
				gameFrame.setVisible(true);
				gameFrame.clearInputMap();
				int i;
				for(i=0;i<gameFrame.slimeCount;i++)
					gameFrame.slimes[i].init();
				setGameFrameButtons();
			}
		});
	}
	
	private void setGameFrameButtons() {
		gameFrame.mainMenuButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				playClick();
				gameFrame.pause();
				String options[] = {"Cancel", "Yes"};
				int op = JOptionPane.showOptionDialog(gameFrame, "Exit to main menu?", "Comfirm Exit", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Cancel");
				if(op == 1) {
					gameFrame.setVisible(false);
					startFrame.setVisible(true);
				}
				else if(op == 0)
					gameFrame.resume();
			}
		});
		
		gameFrame.pauseButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				playClick();
				gameFrame.pause();
				String options[] = {"Resume"};
				int op = JOptionPane.showOptionDialog(gameFrame, "Paused.", "Pause", 
						JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Resume");
				if(op == 0)
					gameFrame.resume();
				
			}
		});
	}
	
	public void checkGameStatus() {
		int i;
		boolean LOOSE = true, WIN = true;

		// for pvp
		if(gameFrame.players == 2 && gameFrame.enemies == 0) {
			if(gameFrame.slimes[0].status == Status.DEAD) {
				playWin();
				gameFrame.pause();
				String options[] = {"Main Menu"};
				int op = JOptionPane.showOptionDialog(gameFrame, "P2 wins!", "Game Over", 
						JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Main Menu");
				if(op == 0) {
					gameFrame.setVisible(false);
					startFrame.setVisible(true);
				}
				return;
			}
			else if(gameFrame.slimes[1].status == Status.DEAD) {
				playWin();
				gameFrame.pause();
				String options[] = {"Main Menu"};
				int op = JOptionPane.showOptionDialog(gameFrame, "P1 wins!", "Game Over", 
						JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Main Menu");
				if(op == 0) {
					gameFrame.setVisible(false);
					startFrame.setVisible(true);
				}
			}
			return;
		}

		for(i=0;i<gameFrame.players;i++) {
			if(gameFrame.slimes[i].status != Status.DEAD) {
				LOOSE = false;
				break;
			}
		}
		if(LOOSE) {
			playLoose();
			gameFrame.pause();
			String options[] = {"Main Menu"};
			int op = JOptionPane.showOptionDialog(gameFrame, "You loose!", "Game Over", 
					JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Main Menu");
			if(op == 0) {
				gameFrame.setVisible(false);
				startFrame.setVisible(true);
			}	
		}
		
		for(i=gameFrame.players;i<gameFrame.slimeCount;i++) {
			if(gameFrame.slimes[i].status != Status.DEAD) {
				WIN = false;
				break;
			}
		}
		if(WIN) {
			playWin();
			gameFrame.pause();
			String options[] = {"Main Menu"};
			int op = JOptionPane.showOptionDialog(gameFrame, "You win!", "Game Over", 
					JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Main Menu");
			if(op == 0) {
				gameFrame.setVisible(false);
				startFrame.setVisible(true);
			}	
		}
	}

	public void timeOut() {
		playLoose();
		gameFrame.pause();
		String options[] = {"Main Menu"};
			int op = JOptionPane.showOptionDialog(gameFrame, "Time out!", "Game Over", 
					JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Main Menu");
			if(op == 0) {
				gameFrame.setVisible(false);
				startFrame.setVisible(true);
			}
	}
	
	public static void main(String[] args) {
		WindowManager w = new WindowManager(1200, 800);
	}
}


