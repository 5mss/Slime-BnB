package bnb;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SlimeCharacter {
	protected int x, y, cellx, celly, size;
	protected SlimeType type;
	protected int index;
	public GameFrame frame;
	
	protected Direction direction;
	protected int speed;
	protected Status status;
	protected boolean isWalking;
	
	public static int propertyCount = 3;
	protected static int baseSpeed = 10;
	protected static int deltaSpeed = 2;
	protected static int MAX_BOMB = 10;
	protected static int MAX_POWER = 10;
	protected static int MAX_SPEED = 5;
	protected static int delay = 100; // in milliseconds
	protected static int weakLength = 5000; // in milliseconds
	protected static int weakTimer = 0;
	protected int maxBomb;
	protected int activeBomb;
	protected int power;
	protected int speedFactor;
	protected int lifeNeedle;

	
	
	// slime label
	public JLabel slimeLabel;
	public ImageIcon frontIcon, backIcon, leftIcon, rightIcon;
	
	// info panel
	private ImageIcon avatarIcon, bombIcon, powerIcon, speedIcon;
	public JPanel infoPanel;
	public JLabel bombNumber, powerNumber, speedNumber;
	public JLabel slimeAvatar;
	public JLabel nameTag;
	public JLabel bombImage, powerImage, speedImage;
	
	public SlimeCharacter(int X, int Y, int i, SlimeType t, GameFrame f) {
		//initialize variables
		x = X;
		y = Y;
		type = t;
		index = i;
		frame = f;
		size = frame.cellSize;
		cellx = x / size;
		celly = y / frame.cellSize;
		
		direction = Direction.FRONT;
		speed = baseSpeed;
		status = Status.ALIVE;
		isWalking = false;
		
		maxBomb = 1;
		activeBomb = 0;
		power = 1;
		speedFactor = 1;
		lifeNeedle = 0;
		
		//get slime type
		String slimeType;
		String name;
		switch (type) {
		case GREEN: {
			slimeType = "green_slime";
			name = "Green Slime";
			break;
		}
		case PINK: {
			slimeType = "pink_slime";
			name = "Pink Slime";
			break;
		}
		case BLUE: {
			slimeType = "blue_slime";
			name = "Blue Slime";
			break;
		}
		case HAT: {
			slimeType = "hat_slime";
			name = "Hat Slime";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
		
		//initialize imageicons
		String dir = "/res/characters/";
		frontIcon = new ImageIcon(this.getClass().getResource(dir + slimeType + "/" + slimeType + "_front.gif"));
		backIcon = new ImageIcon(this.getClass().getResource(dir + slimeType + "/" + slimeType + "_back.gif"));
		leftIcon = new ImageIcon(this.getClass().getResource(dir + slimeType + "/" + slimeType + "_left.gif"));
		rightIcon = new ImageIcon(this.getClass().getResource(dir + slimeType + "/" + slimeType + "_right.gif"));
		avatarIcon = new ImageIcon(this.getClass().getResource(dir + slimeType + "/" + slimeType + "_avatar.png"));
		avatarIcon.setImage(avatarIcon.getImage().getScaledInstance(size, size,Image.SCALE_DEFAULT ));
		frontIcon.setImage(frontIcon.getImage().getScaledInstance(size, size,Image.SCALE_DEFAULT ));
		backIcon.setImage(backIcon.getImage().getScaledInstance(size, size,Image.SCALE_DEFAULT ));
		leftIcon.setImage(leftIcon.getImage().getScaledInstance(size, size,Image.SCALE_DEFAULT ));
		rightIcon.setImage(rightIcon.getImage().getScaledInstance(size, size,Image.SCALE_DEFAULT ));

		//initialize GUI 
		Font textFont = new Font("Chalkduster", Font.PLAIN, 20);
		UIManager.put("Label.font", textFont);
		slimeLabel = new JLabel(frontIcon);
		slimeLabel.setBounds(x, y, size, size);	
		slimeAvatar = new JLabel(avatarIcon);
		nameTag = new JLabel(name);
		initInfoPanel();
	}
	
	private void initInfoPanel() {
		infoPanel = new JPanel();
		infoPanel.setOpaque(false);
		infoPanel.setLayout(new FlowLayout());
		int s = size / 3;

		//initialize imageicons
		bombIcon = new ImageIcon(this.getClass().getResource("/res/info/bomb.png"));
		powerIcon = new ImageIcon(this.getClass().getResource("/res/info/power.png"));
		speedIcon = new ImageIcon(this.getClass().getResource("/res/info/speed.png"));
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

	public void move() {
		int nextx, nexty, nextCellx, nextCelly;
		boolean inBounds;
		
		// get next position 
		switch (direction) {
		case FRONT: {
			nextx = x;
			nexty = y + speed;
			nextCellx = nextx / 60;
			nextCelly = nexty / 60;
			inBounds = (nexty < frame.mapHeight - size);
			break;
		}
		case BACK: {
			nextx = x;
			nexty = y - speed;
			nextCellx = nextx / 60;
			nextCelly = nexty / 60;
			inBounds = (nexty > 0);
			break;
		}
		case LEFT: {
			nextx = x - speed;
			nexty = y;
			nextCellx = nextx / 60;
			nextCelly = nexty / 60;
			inBounds = (nextx > 0);
			break;
		}
		case RIGHT: {
			nextx = x - speed;
			nexty = y;
			nextCellx = nextx / 60;
			nextCelly = nexty / 60;
			inBounds = (nextx < frame.mapWidth - size);
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + direction);
		}
		
		// if collision detection passed 
		if(inBounds && !frame.blocks[nextCelly][nextCellx]) {
			slimeLabel.setBounds(nextx, nexty, size, size);
			x = nextx;
			y = nexty;
			cellx = nextCellx;
			celly = nextCelly;
			
			// pick up items
			if(frame.items[celly][cellx] != Item.NONE) {
				
			}
			
			
		}
	}
	
	public void die() {
		
	}
	
	public void weakened() {
		
	}
	
	public void pause() {
		
	}
	
	public void useLifeNeedle() {
		
	}
	
	
}

enum Status {
	ALIVE, WEAK, DEAD
}

enum Direction {
	FRONT, BACK, LEFT, RIGHT
}
