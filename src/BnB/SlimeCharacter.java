package bnb;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SlimeCharacter {
	protected boolean isPlayer;
	protected int x, y, cellx, celly, size, slimeSize;
	protected SlimeType type;
	protected int index;
	public GameFrame frame;
	public WindowManager windowManager;
	Timer timer;
	
	protected Direction direction;
	protected int speed;
	protected Status status;
	protected boolean isWalking;
	protected boolean pause;
	
	public static int propertyCount = 3;
	protected static int speedList[] = {8, 10, 11, 12, 14, 15};
	protected static int MAX_BOMB = 10;
	protected static int MAX_POWER = 10;
	protected static int MAX_SPEED = 6;
	protected static int delay = 100; // in milliseconds
	protected static int weakLength = 5000; // in milliseconds
	protected static int weakTimer = 0;
	protected static int tol = 15;
	protected int maxBomb;
	protected int activeBomb;
	protected int power;
	protected int speedFactor;
	protected int lifeNeedle;

	int nextx, nexty, nextCellx, nextCelly; // for collision detection
	
	// slime label
	public JLabel slimeLabel, lifeNeedleNumber;
	public ImageIcon frontIcon, backIcon, leftIcon, rightIcon;
	
	// info panel
	private ImageIcon avatarIcon, deadAvatarIcon, bombIcon, powerIcon, speedIcon, weakBubbleIcon;
	public JPanel infoPanel;
	public JLabel bombNumber, powerNumber, speedNumber, weakBubbleLabel;
	public JLabel slimeAvatar;
	public JLabel nameTag;
	public JLabel bombImage, powerImage, speedImage;
	
	public SlimeCharacter(int X, int Y, int i, SlimeType t, GameFrame f) {
		//initialize variables
		windowManager = f.windowManager;
		x = X;
		y = Y;
		type = t;
		index = i;
		frame = f;
		size = frame.cellSize;
		slimeSize = size - 6;
		cellx = (x + slimeSize / 2) / size;
		celly = (y + slimeSize / 2) / size;
		
		maxBomb = 1;
		activeBomb = 0;
		power = 1;
		speedFactor = 1;
		lifeNeedle = 0;
		
		direction = Direction.FRONT;
		speed = speedList[speedFactor - 1];
		status = Status.ALIVE;
		isWalking = false;
		pause = false;
		
		//get slime type
		String slimeType;
		String name;
		switch (type) {
		case GREEN: {
			slimeType = "green_slime";
			name = "Jellyfish";
			break;
		}
		case PINK: {
			slimeType = "pink_slime";
			name = "Panas";
			break;
		}
		case BLUE: {
			slimeType = "blue_slime";
			name = "Waterball";
			break;
		}
		case HAT: {
			slimeType = "hat_slime";
			name = "Smurf";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
		
		//initialize imageicons
		String dir = "characters/";
		weakBubbleIcon = new ImageIcon(this.getClass().getClassLoader().getResource("items_in_bubble/weakBubble.png"));
		frontIcon = new ImageIcon(this.getClass().getClassLoader().getResource(dir + slimeType + "/" + slimeType + "_front.gif"));
		backIcon = new ImageIcon(this.getClass().getClassLoader().getResource(dir + slimeType + "/" + slimeType + "_back.gif"));
		leftIcon = new ImageIcon(this.getClass().getClassLoader().getResource(dir + slimeType + "/" + slimeType + "_left.gif"));
		rightIcon = new ImageIcon(this.getClass().getClassLoader().getResource(dir + slimeType + "/" + slimeType + "_right.gif"));
		avatarIcon = new ImageIcon(this.getClass().getClassLoader().getResource(dir + slimeType + "/" + slimeType + "_avatar.png"));
		deadAvatarIcon = new ImageIcon(this.getClass().getClassLoader().getResource(dir + slimeType + "/" + slimeType + "_avatar_dead.png"));
		weakBubbleIcon.setImage(weakBubbleIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));
		avatarIcon.setImage(avatarIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));
		deadAvatarIcon.setImage(deadAvatarIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));
		frontIcon.setImage(frontIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));
		backIcon.setImage(backIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));
		leftIcon.setImage(leftIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));
		rightIcon.setImage(rightIcon.getImage().getScaledInstance(slimeSize, slimeSize,Image.SCALE_DEFAULT ));

		//initialize GUI 
		Font textFont = new Font("Chalkduster", Font.PLAIN, 20);
		UIManager.put("Label.font", textFont);
		slimeLabel = new JLabel(frontIcon);
		slimeLabel.setBounds(x, y, slimeSize, slimeSize);	
		weakBubbleLabel = new JLabel(weakBubbleIcon);
		weakBubbleLabel.setBounds(x, y, slimeSize, slimeSize);
		weakBubbleLabel.setVisible(false);
		
		slimeAvatar = new JLabel(avatarIcon);
		nameTag = new JLabel(name);
		initInfoPanel();
		
		//set up timer
		ActionListener timerListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i;
				// check die
				if(status == Status.WEAK) {
					weakTimer -= delay;
					if(weakTimer <= 0) // weak time out
						die();
					else {
						for(i=0;i<frame.players + frame.enemies;i++) {
							if(i != index) {
								if(Math.abs(frame.slimes[i].x - x) <= 20 && Math.abs(frame.slimes[i].y - y) <= 20)
									die();
							}
						}
					}
				}
				// move()
				if(isWalking)
					move();				
			}
		};
		
		timer = new Timer(delay, timerListener);
		timer.start();
	}
	
	public void init() {}
	
	private void initInfoPanel() {
		infoPanel = new JPanel();
		infoPanel.setOpaque(false);
		infoPanel.setLayout(new FlowLayout());
		int s = size / 3;

		//initialize imageicons
		bombIcon = new ImageIcon(this.getClass().getClassLoader().getResource("info/bomb.png"));
		powerIcon = new ImageIcon(this.getClass().getClassLoader().getResource("info/power.png"));
		speedIcon = new ImageIcon(this.getClass().getClassLoader().getResource("info/speed.png"));
		bombIcon.setImage(bombIcon.getImage().getScaledInstance(s, s,Image.SCALE_DEFAULT ));
		powerIcon.setImage(powerIcon.getImage().getScaledInstance(s, s,Image.SCALE_DEFAULT ));
		speedIcon.setImage(speedIcon.getImage().getScaledInstance(s, s,Image.SCALE_DEFAULT ));
		bombImage = new JLabel(bombIcon);
		powerImage = new JLabel(powerIcon);
		speedImage = new JLabel(speedIcon);

		//player panel
		JPanel playerPanel = new JPanel();
		playerPanel.setOpaque(false);
		playerPanel.setLayout(new BorderLayout());
		playerPanel.add(slimeAvatar, "Center");
		playerPanel.add(nameTag, "South");

		//property panel
		JPanel propertyPanel = new JPanel();
		FlowLayout layout = new FlowLayout();
		propertyPanel.setOpaque(false);
		propertyPanel.setLayout(new GridLayout(4, 1));

		JPanel bombPanel = new JPanel();
		bombPanel.setOpaque(false);
		bombPanel.setLayout(layout);
		bombPanel.add(bombImage);
		bombNumber = new JLabel("1");
		bombPanel.add(bombNumber);
		propertyPanel.add(bombPanel);

		JPanel powerPanel = new JPanel();
		powerPanel.setOpaque(false);
		powerPanel.setLayout(layout);
		powerPanel.add(powerImage);
		powerNumber = new JLabel("1");
		powerPanel.add(powerNumber);
		propertyPanel.add(powerPanel);

		JPanel speedPanel = new JPanel();
		speedPanel.setOpaque(false);
		speedPanel.setLayout(layout);
		speedPanel.add(speedImage);
		speedNumber = new JLabel("1");
		speedPanel.add(speedNumber);
		propertyPanel.add(speedPanel);

		infoPanel.add(playerPanel);
		infoPanel.add(propertyPanel);
	}

	// actions
	public void turn(Direction d) {
		direction = d;
		switch (d) {
		case FRONT: {
			slimeLabel.setIcon(frontIcon);
			break;
		}
		case BACK: {
			slimeLabel.setIcon(backIcon);
			break;
		}
		case LEFT: {
			slimeLabel.setIcon(leftIcon);
			break;
		}
		case RIGHT: {
			slimeLabel.setIcon(rightIcon);                                                                                                                                                           
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + d);
		}
	}
	
	public boolean checkCollision(Direction d, int flag) { //for AIs
		boolean collision = false;
		int nx, ny, nCellx, nCelly;
		
		switch (d) {
		case FRONT: {
			nx = x;
			ny = y + speed;
			nCellx = (nx + slimeSize / 2) / size;
			nCelly = (ny + slimeSize / 2) / size;
			
			if(ny < frame.mapHeight - slimeSize) {
				int detectRow = (ny + slimeSize) / size;
				if((ny + slimeSize) % size != 0) {
					if(frame.blocks[detectRow][nCellx])
						collision = true;
					else {
						if(nx > nCellx * size + size - slimeSize && frame.blocks[detectRow][nCellx + 1]) {
							int delta = nx - nCellx * size - size + slimeSize;
							if(delta >= tol) 
								collision = true;
						}
						else if(nx < nCellx * size && frame.blocks[detectRow][nCellx - 1]) {
							int delta = nCellx * size - nx;
							if(delta >= tol) 
								collision = true;
						}
					}
				}
			}
			else 
				collision = true;
			break;
		}
		case BACK: {
			nx = x;
			ny = y - speed;
			nCellx = (nx + slimeSize / 2) / size;
			nCelly = (ny + slimeSize / 2) / size;
			
			if(ny > 0) {
				int detectRow = ny / size;
				if(ny % size != 0) {
					if(frame.blocks[detectRow][nCellx])
						collision = true;
					else {
						if(nx > nCellx * size + size - slimeSize && frame.blocks[detectRow][nCellx + 1]) {
							int delta = nx - nCellx * size - size + slimeSize;
							if(delta >= tol) 
								collision = true;
						}
						else if(nx < nCellx * size && frame.blocks[detectRow][nCellx - 1]) {
							int delta = nCellx * size - nx;
							if(delta >= tol) 
								collision = true;
						}
					}
				}
			}
			else 
				collision = true;
			break;
		}
		case LEFT: {
			nx = x - speed;
			ny = y;
			nCellx = (nx + slimeSize / 2) / size;
			nCelly = (ny + slimeSize / 2) / size;
			
			if(nx > 0) {
				int detectCol = nx / size;
				if(nx % size != 0) {
					if(frame.blocks[nCelly][detectCol])
						collision = true;
					else {
						if(ny > nCelly * size + size - slimeSize && frame.blocks[nCelly + 1][detectCol]) {
							int delta = ny - nCelly * size - size + slimeSize;
							if(delta >= tol) 
								collision = true;
						}
						else if(ny < nCelly * size && frame.blocks[nCelly - 1][detectCol]) {
							int delta = nCelly * size - ny;
							if(delta >= tol) 
								collision = true;
						}
					}
				}
			}
			else
				collision = true;
			break;
		}
		case RIGHT: { 
			nx = x + speed;
			ny = y;
			nCellx = (nx + slimeSize / 2) / size;
			nCelly = (ny + slimeSize / 2) / size;
			
			if(nx < frame.mapWidth - slimeSize) {
				int detectCol = (nx + slimeSize) / size;
				if((nx + slimeSize) % size != 0) {
					if(frame.blocks[nCelly][detectCol])
						collision = true;
					else {
						if(ny > nCelly * size + size - slimeSize && frame.blocks[nCelly + 1][detectCol]) {
							int delta = ny - nCelly * size - size + slimeSize;
							if(delta >= tol) 
								collision = true;
						}
						else if(ny < nCelly * size && frame.blocks[nCelly - 1][detectCol]) {
							int delta = nCelly * size - ny;
							if(delta >= tol) 
								collision = true;
						}
					}
				}
			}
			else
				collision = true;
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + direction);
		}
		
		return collision;
	}
	
	public boolean checkCollision(Direction d) {  // for players
		boolean collision = false;
		
		switch (d) {
		case FRONT: {
			nextx = x;
			nexty = y + speed;
			nextCellx = (nextx + slimeSize / 2) / size;
			nextCelly = (nexty + slimeSize / 2) / size;
			
			if(nexty < frame.mapHeight - slimeSize) {
				int detectRow = (nexty + slimeSize) / size;
				if((nexty + slimeSize) % size != 0) {
					if(frame.blocks[detectRow][nextCellx])
						collision = true;
					else {
						if(nextx > nextCellx * size + size - slimeSize && frame.blocks[detectRow][nextCellx + 1]) {
							int delta = nextx - nextCellx * size - size + slimeSize;
							if(delta < tol) 
								nextx = cellx * size + (size - slimeSize) / 2;
							else
								collision = true;
						}
						else if(nextx < nextCellx * size && frame.blocks[detectRow][nextCellx - 1]) {
							int delta = nextCellx * size - nextx;
							if(delta < tol) 
								nextx = cellx * size + (size - slimeSize) / 2;
							else
								collision = true;
						}
					}
				}
			}
			else 
				collision = true;
			break;
		}
		case BACK: {
			nextx = x;
			nexty = y - speed;
			nextCellx = (nextx + slimeSize / 2) / size;
			nextCelly = (nexty + slimeSize / 2) / size;
			
			if(nexty > 0) {
				int detectRow = nexty / size;
				if(nexty % size != 0) {
					if(frame.blocks[detectRow][nextCellx])
						collision = true;
					else {
						if(nextx > nextCellx * size + size - slimeSize && frame.blocks[detectRow][nextCellx + 1]) {
							int delta = nextx - nextCellx * size - size + slimeSize;
							if(delta < tol) 
								nextx = cellx * size + (size - slimeSize) / 2;
							else
								collision = true;
						}
						else if(nextx < nextCellx * size && frame.blocks[detectRow][nextCellx - 1]) {
							int delta = nextCellx * size - nextx;
							if(delta < tol) 
								nextx = cellx * size + (size - slimeSize) / 2;
							else
							collision = true;
						}
					}
				}
			}
			else 
				collision = true;
			break;
		}
		case LEFT: {
			nextx = x - speed;
			nexty = y;
			nextCellx = (nextx + slimeSize / 2) / size;
			nextCelly = (nexty + slimeSize / 2) / size;
			
			if(nextx > 0) {
				int detectCol = nextx / size;
				if(nextx % size != 0) {
					if(frame.blocks[nextCelly][detectCol])
						collision = true;
					else {
						if(nexty > nextCelly * size + size - slimeSize && frame.blocks[nextCelly + 1][detectCol]) {
							int delta = nexty - nextCelly * size - size + slimeSize;
							if(delta < tol) 
								nexty = celly * size + (size - slimeSize) / 2;
							else
								collision = true;
						}
						else if(nexty < nextCelly * size && frame.blocks[nextCelly - 1][detectCol]) {
							int delta = nextCelly * size - nexty;
							if(delta < tol) 
								nexty = celly * size + (size - slimeSize) / 2;
							else
								collision = true;
						}
					}
				}
			}
			else
				collision = true;
			break;
		}
		case RIGHT: { 
			nextx = x + speed;
			nexty = y;
			nextCellx = (nextx + slimeSize / 2) / size;
			nextCelly = (nexty + slimeSize / 2) / size;
			
			if(nextx < frame.mapWidth - slimeSize) {
				int detectCol = (nextx + slimeSize) / size;
				if((nextx + slimeSize) % size != 0) {
					if(frame.blocks[nextCelly][detectCol])
						collision = true;
					else {
						if(nexty > nextCelly * size + size - slimeSize && frame.blocks[nextCelly + 1][detectCol]) {
							int delta = nexty - nextCelly * size - size + slimeSize;
							if(delta < tol) 
								nexty = celly * size + (size - slimeSize) / 2;
							else
								collision = true;
						}
						else if(nexty < nextCelly * size && frame.blocks[nextCelly - 1][detectCol]) {
							int delta = nextCelly * size - nexty;
							if(delta < tol) 
								nexty = celly * size + (size - slimeSize) / 2;
							else
							collision = true;
						}
					}
				}
			}
			else
				collision = true;
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + direction);
		}
		
		return collision;
	}
	
	public void move() {
		boolean collision = checkCollision(direction);
		
		// if collision detection passed 
		if(!collision) {
			slimeLabel.setBounds(nextx, nexty, slimeSize, slimeSize);
			if(status == Status.WEAK)
				weakBubbleLabel.setBounds(nextx, nexty, slimeSize, slimeSize);
			x = nextx;
			y = nexty;
			cellx = nextCellx;
			celly = nextCelly;
			
			// pick up items
			if(frame.items[celly][cellx] != Item.NONE && status != Status.WEAK) {
				switch (frame.items[celly][cellx]) {
				case POWERPOTION: {
					if(power < MAX_POWER) {
						power += 1;
						powerNumber.setText(String.valueOf(power));
					}
					break;
				}
				case BOMBPOTION: {
					if(maxBomb < MAX_BOMB) {
						maxBomb += 1;
						bombNumber.setText(String.valueOf(maxBomb));
					}
					break;
				}
				case LIFENEEDLE: {
					lifeNeedle += 1;
					if(lifeNeedleNumber != null)
						lifeNeedleNumber.setText(String.valueOf(lifeNeedle));
					break;
				}
				case SHOE: {
					if(speedFactor < MAX_SPEED) {
						speedFactor += 1;
						speedNumber.setText(String.valueOf(speedFactor));
						speed = speedList[speedFactor - 1];
					}
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + frame.items[celly][cellx]);
				}
				
					frame.itemLabels[celly][cellx].setVisible(false);
					frame.itemLabels[celly][cellx] = null;
					frame.items[celly][cellx] = Item.NONE;
			}			
		}
	}
	
	public void die() {
		// pause timers
		pause();
		status = Status.DEAD;
		weakBubbleLabel.setVisible(false);
		slimeLabel.setVisible(false);
		slimeAvatar.setIcon(deadAvatarIcon);
		if(windowManager != null)
			windowManager.checkGameStatus();
	}
	
	public void weakened() {
		status = Status.WEAK;
		weakBubbleLabel.setBounds(x, y, slimeSize, slimeSize);
		speed /= 4;
		weakBubbleLabel.setVisible(true);
		weakTimer = weakLength;
	}
	
	public void pause() {
		pause = true;
		timer.stop();
	}
	
	public void resume() {
		pause = false;
		timer.start();
	}
	
	public void useLifeNeedle() {
		status = Status.ALIVE;
		weakBubbleLabel.setVisible(false);
		speed = speedList[speedFactor - 1];
		lifeNeedle -= 1;
		if(lifeNeedleNumber != null)
			lifeNeedleNumber.setText(String.valueOf(lifeNeedle));
	}
	
	public void placeBomb() {
		if(activeBomb < maxBomb && frame.bombs[celly][cellx] == null) {
			activeBomb += 1;
			 Thread t = new bombThread(this);
			 t.start();
			
//			Bomb bomb = new Bomb(this);
		}
	}
	
	protected class bombThread extends Thread {
		SlimeCharacter x;
		public bombThread(SlimeCharacter slimeCharacter) {
			super();
			x = slimeCharacter;
		}
		
		public void run() {
			Bomb bomb = new Bomb(x);
		}
	}
	
	
}

enum Status {
	ALIVE, WEAK, DEAD
}

enum Direction {
	FRONT, BACK, LEFT, RIGHT, NONE
}
