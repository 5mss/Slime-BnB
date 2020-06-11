package bnb;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

import javax.swing.*;
import utils.*;

public class Bomb {
	GameFrame frame;
	SlimeCharacter slime;
	int power;
	int row, col;
	static int lifeSpan = 3000;
	static int delay = 100;
	int t;
	int s;
	
	
	ArrayList<Point> explodedCellList;
	Timer timer;

	public Bomb(SlimeCharacter x) {
		slime = x;
		frame = x.frame;
		power = x.power;
		row = x.celly;
		col = x.cellx;
		
		s = frame.cellSize;

		frame.bombs[row][col] = this;
		frame.bombList.add(this);
//		synchronized(frame.blocks){
			frame.blocks[row][col] = true;
//		}
		
		//place bombLabel
		frame.bombLabels[row][col].setVisible(true);
		
		//set timer
		ActionListener bombTimerListener = new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				t -= delay;
				if(t <= 0)
					explode();			
			}
		};
		
		timer = new Timer(delay, bombTimerListener);
		t = lifeSpan;
		timer.start();
	}

	public void explode() {
		pause();
		int i, j, rowStart, rowEnd, colStart, colEnd;	
		
		//play sound
//		Thread playSoundThread = new Thread() {
//			public void run() {
//				try {
//					Clip clip= AudioSystem.getClip();
//					clip.open(frame.windowManager.explosion);
//					clip.start();
//				} catch(Exception e) {e.printStackTrace();}
//			}
//		};
//		playSoundThread.start();
		
		frame.windowManager.playExplosion();
		frame.bombs[row][col] = null;
		frame.bombList.remove(this);
		synchronized(frame.blocks){
			frame.blocks[row][col] = false;
		}
		frame.bombLabels[row][col].setVisible(false);
		
		slime.activeBomb -= 1;
		
		explodedCellList = new ArrayList<Point>();
		
		//center
		explodedCellList.add(new Point(row, col));
		
		// forward
		for(i=1;i<=power;i++) {
			//edge
			if(row + i >= frame.rows)
				break;
			// wall
			if(frame.walls[row + i][col] != null)
				break;
			explodedCellList.add(new Point(row + i, col));
			// chest
			if(frame.chests[row + i][col] != null) {
				frame.openChest(row + i, col);
				break;
			}
			//bomb
			if(frame.bombs[row + i][col] != null)
				frame.bombs[row + i][col].explode();	
		}
		rowEnd = row + i - 1;


		// backward
		for(i=1;i<=power;i++) {
			//edge
			if(row - i < 0)
				break;
			// wall
			if(frame.walls[row - i][col] != null)
				break;
			explodedCellList.add(new Point(row - i, col));
			// chest
			if(frame.chests[row - i][col] != null) {
				frame.openChest(row - i, col);
				break;
			}
			//bomb
			if(frame.bombs[row - i][col] != null)
				frame.bombs[row - i][col].explode();	
		}
		if(i > 1) {
			for(j=0;j<frame.slimeCount;j++) {
				if(frame.slimes[j].status == Status.ALIVE && slimeInRange(j, row - i + 1, row - 1, col, col)) {
					frame.slimes[j].weakened();
				}
			}
		}
		rowStart = row - i + 1;

		//left
		for(i=1;i<=power;i++) {
			//edge
			if(col - i < 0)
				break;
			// wall
			if(frame.walls[row][col - i] != null)
				break;
			explodedCellList.add(new Point(row, col - i));
			// chest
			if(frame.chests[row][col - i] != null) {
				frame.openChest(row, col - i);
				break;
			}	
			//bomb
			if(frame.bombs[row][col - i] != null)
				frame.bombs[row][col - i].explode();	
		}
		colStart = col - i + 1;

		//right
		for(i=1;i<=power;i++) {
			//edge
			if(col + i >= frame.cols)
				break;
			
			// wall
			if(frame.walls[row][col + i] != null)
				break;
			explodedCellList.add(new Point(row, col + i));		
			// chest
			if(frame.chests[row][col + i] != null) {
				frame.openChest(row, col + i);
				break;
			}
			//bomb
			if(frame.bombs[row][col + i] != null)
				frame.bombs[row][col + i].explode();	
		}
		colEnd = col + i - 1;

		for(j=0;j<frame.slimeCount;j++) {
//			if(frame.slimes[j].status == Status.ALIVE && (slimeInRange(j, rowStart, rowEnd, col, col)
//				|| slimeInRange(j, row, row, colStart, colEnd)))
			if(frame.slimes[j].status == Status.ALIVE && ((frame.slimes[j].cellx == col && 
					rowStart <= frame.slimes[j].celly && frame.slimes[j].celly <= rowEnd) || 
					(frame.slimes[j].celly == row && colStart <= frame.slimes[j].cellx &&
					frame.slimes[j].cellx <= colEnd)))
				frame.slimes[j].weakened();
		}

		//play animation
		Thread threadPlayExplosion = new Thread() {
			public void run() {
				Clip clip = null;
				try {
					AudioInputStream explosion = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("audios/explosion.wav"));
					clip= AudioSystem.getClip();
					clip.open(explosion);
					clip.start();
				} catch(Exception e) {e.printStackTrace();}
				
				synchronized(frame.lightened) {
					for(Point p:explodedCellList) {
						frame.lightened[p.x][p.y] += 1;
						synchronized (frame.explosionLabels) {
							frame.explosionLabels[p.x][p.y].setVisible(true);
						}
					}
				}
				try {sleep(500);}catch (Exception e) {e.printStackTrace();}
				synchronized (frame.lightened) {	
					for(Point p:explodedCellList) {
						frame.lightened[p.x][p.y] -= 1;
						if(frame.lightened[p.x][p.y] == 0) {
							synchronized (frame.explosionLabels) {
								frame.explosionLabels[p.x][p.y].setVisible(false); 
							}
						}
					}
				}
				try {sleep(1000);}catch (Exception e) {e.printStackTrace();}
				if(clip != null) {
					clip.stop();
					clip.close();
				}
			}
		};
		threadPlayExplosion.start();
	}
	
	private boolean slimeInRange(int i, int rowStart, int rowEnd, int colStart, int colEnd) {
		int x = frame.slimes[i].x, y = frame.slimes[i].y;
		return (rowStart * s - s / 2 < y) && (y < rowEnd * s + s / 2) && (colStart * s - s / 2) < x && x <= (colEnd * s + s / 2);
	}

	public void pause() {
		timer.stop();
	}
	
	public void resume() {
		timer.start();
	}
	
	
}
