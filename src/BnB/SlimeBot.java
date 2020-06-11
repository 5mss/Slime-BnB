package bnb;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SlimeBot extends SlimeCharacter{
	Timer updateTimer;
	static int updateDelay = 100;
	Point searchDirections[];
	
	public SlimeBot(int X, int Y, int i, SlimeType t, GameFrame f) {
		super(X, Y, i, t, f);
		isPlayer = false;
		
		ActionListener updateListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();			
			}
		};
		searchDirections = new Point[4];
		searchDirections[0] = new Point(1, 0); // FRONT
		searchDirections[1] = new Point(-1, 0); // BACK
		searchDirections[2] = new Point(0, -1); // LEFT
		searchDirections[3] = new Point(0, 1); // RIGHT
		
		updateTimer = new Timer(updateDelay, updateListener);
	}
	
	public void init() {
		isWalking = true;
		updateTimer.start();
	}
	
	public void pause() {
		super.pause();
		updateTimer.stop();
	}
	
	public void resume() {
		super.resume();
		updateTimer.start();
	}

	public boolean checkBomb(Direction d) {
		int nextRow = celly + searchDirections[d.ordinal()].x;
		int nextCol = cellx + searchDirections[d.ordinal()].y;
		if(nextRow < 0 || nextRow >= frame.rows || nextCol < 0 || nextCol >= frame.cols)
			return true;
		for(Bomb b:frame.bombList) {
			if(b.row == nextRow && Math.abs(b.col - nextCol) <= b.power) // bomb in the row
				return false;
			else if(b.col == nextCol && Math.abs(b.row - nextRow) <= b.power) // bomb in the column
				return false;
		}
		return true;
	}
	
	public void update() {
		// DFS for each goal
		int i, j;
		int r, c;
		Direction intendedDirection;
		
		
		// avoid being stuck
//		if(checkCollision(direction, 0)) {
//			int ran = (int)(Math.random() * 4);
//			intendedDirection = Direction.values()[ran];
//			if(checkBomb(intendedDirection)){
//						turn(intendedDirection);
//						return;
//			}
//		}
		
		//use life needle 
		if(status == Status.WEAK && lifeNeedle > 0) {
			useLifeNeedle();	
		}

		// kill players
		if(activeBomb < maxBomb) {
			for(i=0;i<frame.players;i++) {
				SlimeCharacter slime = frame.slimes[i];
				if(((slime.celly == celly && Math.abs(slime.cellx - cellx) <= power)
					|| (slime.cellx == cellx && Math.abs(slime.celly - celly) <= power))	
						&& status == Status.ALIVE) {
					placeBomb();	
				}
			}
		}

		//open chests
		if(activeBomb < maxBomb) {
			for(i=0;i<4;i++) {
				r = celly + searchDirections[i].x;
				c = cellx + searchDirections[i].y;
				if(r < 0 || r >= frame.rows || c < 0 || c >= frame.cols)
					continue;
				if(status == Status.ALIVE && frame.chests[r][c] != null) {
					placeBomb();	
				}
			}
		}


		// first priority: only walk on tracks
		
		// if(x % size != 5 || y % size != 5){ // not at cell center
		// 	if(x % size == 5) {
		// 		if(checkCollision(direction, 0)) {
		// 			if(direction == Direction.FRONT && checkBomb(Direction.BACK)) {
		// 				turn(Direction.BACK);
		// 				return;
		// 			}
		// 			else if(direction == Direction.BACK && checkBomb(Direction.FRONT)){
		// 				turn(Direction.FRONT);
		// 				return;
		// 			}
		// 		}
		// 	}
		// 	else if(y % size == 5) {
		// 		if(checkCollision(direction, 0)) {
		// 			if(direction == Direction.LEFT && checkBomb(Direction.RIGHT)) {
		// 				turn(Direction.RIGHT);
		// 				return;
		// 			}
		// 			else if(direction == Direction.RIGHT && checkBomb(Direction.LEFT)) {
		// 				turn(Direction.LEFT);
		// 				return;
		// 			}
		// 		}
		// 	}
		// 	return;
		// }
		
		// second priority: avoid bomb 
		for(Bomb b:frame.bombList) {
			// bomb in the row
			if(b.row == celly && Math.abs(b.col - cellx) <= b.power) {		
				if(b.row * size + size / 2 >= y + slimeSize / 2) {
					if(!checkCollision(Direction.BACK, 0)) {
						turn(Direction.BACK);
						return;
					}
					if(!checkCollision(Direction.FRONT, 0)) {
						turn(Direction.FRONT);
						return;
					}
				}
				else {
					if(!checkCollision(Direction.FRONT, 0)) {
						turn(Direction.FRONT);
						return;
					}
					if(!checkCollision(Direction.BACK, 0)) {
						turn(Direction.BACK);
						return;
					}
				}
				if(b.col * size + size / 2 >= x + slimeSize / 2) {
					if(!checkCollision(Direction.LEFT, 0)) {
						turn(Direction.LEFT);
						return;
					}
					if(!checkCollision(Direction.RIGHT, 0)) {
						turn(Direction.RIGHT);
						return;
					}			
				}
				else {
					if(!checkCollision(Direction.RIGHT, 0)) {
						turn(Direction.RIGHT);
						return;
					}
					if(!checkCollision(Direction.LEFT, 0)) {
						turn(Direction.LEFT);
						return;
					}
				}
				return;
			}
			//bomb in the column
			if(b.col == cellx && Math.abs(b.row - celly) <= b.power) {
				if(b.col * size + size / 2 >= x + slimeSize / 2) {
					if(!checkCollision(Direction.LEFT, 0)) {
						turn(Direction.LEFT);
						return; 
					}
					if(!checkCollision(Direction.RIGHT, 0)) {
						turn(Direction.RIGHT);
						return;
					}	
				}
				else {
					if(!checkCollision(Direction.RIGHT, 0)) {
						turn(Direction.RIGHT);
						return;
					}
					if(!checkCollision(Direction.LEFT, 0)) {
						turn(Direction.LEFT);
						return; 
					}
				}
				if(b.row * size + size / 2 >= y + slimeSize / 2) {
					if(!checkCollision(Direction.BACK, 0)) {
						turn(Direction.BACK);
						return;
					}
					if(!checkCollision(Direction.FRONT, 0)) {
						turn(Direction.FRONT);
						return;
					}			
				}
				else {
					if(!checkCollision(Direction.FRONT, 0)) {
						turn(Direction.FRONT);
						return;
					}
					if(!checkCollision(Direction.BACK, 0)) {
						turn(Direction.BACK);
						return;
					}
				}
				return;
			}			
		}
		
		
		// third priority: pick up items
		for(i=0;i<4;i++) { //for each direction
			j = 1;
			while(true) {
				r = celly + searchDirections[i].x * j;
				c = cellx + searchDirections[i].y * j;
				if(r < 0 || r >= frame.rows || c < 0 || c >= frame.cols)
					break;
				if(frame.blocks[r][c])
					break;
				if(frame.items[r][c] != Item.NONE) {
					intendedDirection = Direction.values()[i];
					if(checkBomb(intendedDirection) && !checkCollision(intendedDirection, 0)){
						turn(intendedDirection);
						return;
					}
				}
				j++;
			}
		}
		
		
		//forth priority: find chests
		for(i=0;i<4;i++) { //for each direction
			j = 1;
			while(true) {
				r = celly + searchDirections[i].x * j;
				c = cellx + searchDirections[i].y * j;
				if(r < 0 || r >= frame.rows || c < 0 || c >= frame.cols)
					break;
				if(frame.chests[r][c] != null) {
					intendedDirection = Direction.values()[i];
					if(checkBomb(intendedDirection) && !checkCollision(intendedDirection, 0)){
						turn(intendedDirection);
						return;
					}
				}
				else if(frame.blocks[r][c])
					break;
				j++;
			}
		}

		// otherwise, random walk
		if(Math.random() < 0.5) {
			int ran = (int)(Math.random() * 4);
			intendedDirection = Direction.values()[ran];
			if(checkBomb(intendedDirection)){
						turn(intendedDirection);
						return;
			}
		}
		
	}

}
