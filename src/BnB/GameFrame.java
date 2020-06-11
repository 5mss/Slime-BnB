package bnb;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.Timer;

import utils.*;

public class GameFrame extends JFrame{
	public WindowManager windowManager;
	
	int width, height, rows, cols;
	Theme theme;
	Difficulty difficulty;
	SlimeType characterType[];
	int players, enemies, bombType, slimeCount;
	SlimeCharacter slimes[];	//
	JLabel walls[][];
	JLabel chests[][];
	ImageLabel floors[][];
	boolean blocks[][];
	Bomb bombs[][];
	JLabel bombLabels[][];
	JLabel explosionLabels[][];
	int lightened[][];
	ArrayList<Bomb> bombList = new ArrayList<Bomb>();
	Item items[][];
	JLabel itemLabels[][];
	static int itemNumber = 4;
	static double itemProb = 0.5;
	static int spawnX[];
	static int spawnY[];
	int cellSize, mapWidth, mapHeight;
	
	ImageIcon bgIcon, wallIcon0, chestIcon0, floorIcon, bombIcon, explosionIcon, wallIcon1, chestIcon1;
	ImageIcon powerPotionIcon, bombPotionIcon, lifeNeedleIcon, shoeIcon;
	ImageIcon itemIcons[];
	JLabel slimeLabels[];
	ImageLabel bgLabel;
	JButton mainMenuButton, pauseButton, lifeNeedleButton1, lifeNeedleButton2;
	JLabel lifeNeedleNumber1, lifeNeedleNumber2;
	JPanel mapPanel;
	
	boolean pause;
	static int gameTime = 360;
	int timeLeft; // in seconds
	Timer timer;
	JLabel timeLabel;
	
	public GameFrame(int w, int h, int r, int c, int s) {  //for tests
		super("Slime BnB");
		width = w;
		height = h;
		rows = r;
		cols = c;
		theme = Theme.NEON;
		difficulty = Difficulty.NORMAL;
		bombType = 0;
		
//		characterType = new SlimeType[1];
//		characterType[0] = SlimeType.PINK;
//		players = 1;
//		enemies = 3;
//		slimeCount = players + enemies;	
//		
		
		characterType = new SlimeType[2];
		characterType[0] = SlimeType.GREEN;
		characterType[1] = SlimeType.PINK;
		players = 2;
		enemies = 2;
		slimeCount = players + enemies;	
		
		
		cellSize = s;
		timeLeft = gameTime;
		pause = false;
	}
	
	public GameFrame(OptionFrame op, CharacterFrame ch, int r, int c, int s) {
		super("Slime BnB");
		windowManager = ch.windowManager;
		width = op.width;
		height = op.height;
		rows = r;
		cols = c;
		theme = op.theme;
		difficulty = op.difficulty;
		bombType = ch.bombType;
		characterType = ch.characterType;
		players = op.players;
		enemies = op.enemies;
		slimeCount = players + enemies;
		cellSize = s;
		timeLeft = gameTime;
		pause = false;
	}
	
	public void init() {
		int i, j;
		mapWidth = cols * cellSize;
		mapHeight = rows * cellSize;
		ArrayList<SlimeType> slimeTypeList = new ArrayList<SlimeType>(4);
		slimeTypeList.add(SlimeType.GREEN);slimeTypeList.add(SlimeType.PINK);
		slimeTypeList.add(SlimeType.BLUE);slimeTypeList.add(SlimeType.HAT);
		
		// initialize arrays
		walls = new JLabel[rows][cols];
		chests = new JLabel[rows][cols];
		floors = new ImageLabel[rows][cols];
		blocks = new boolean[rows][cols];
		bombLabels = new JLabel[rows][cols];
		explosionLabels = new JLabel[rows][cols];
		lightened = new int[rows][cols];
		bombs = new Bomb[rows][cols];
		items = new Item[rows][cols];
		itemLabels = new JLabel[rows][cols];
		slimes = new SlimeCharacter[slimeCount];
		slimeLabels = new JLabel[slimeCount];
		spawnX = new int[4];
		spawnX[0] = cellSize + 5; spawnX[1] = cellSize * (cols - 2) + 5;
		spawnX[2] = cellSize + 5; spawnX[3] = cellSize * (cols - 2) + 5;
		spawnY = new int[4];
		spawnY[0] = cellSize + 5; spawnY[1] = cellSize * (rows - 2) + 5;
		spawnY[3] = cellSize + 5; spawnY[2] = cellSize * (rows - 2) + 5;
		
		
		for(i=0;i<rows;i++)
			for(j=0;j<cols;j++) {
				blocks[i][j] = false;
				items[i][j] = Item.NONE;
				bombs[i][j] = null;
				lightened[i][j] = 0;
				itemLabels[i][j] = null;
			}
		
		// initialize slimes
		for(i=0;i<players;i++) {
			slimes[i] = new SlimePlayer(spawnX[i], spawnY[i], i, characterType[i], this);
			slimeTypeList.remove(characterType[i]);
		}
		while(i < slimeCount) {
			slimes[i] = new SlimeBot(spawnX[i], spawnY[i], i, slimeTypeList.get(i - players), this);
			i++;
		}
		
		// initialize GUI
		setSize(width, height + 10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		// set background
		JPanel contentPane = (JPanel)getContentPane();
		String themeDir = "themes/";
		String themeFolder = theme.toString().toLowerCase();

		bgIcon = new ImageIcon(this.getClass().getClassLoader().getResource(themeDir + themeFolder + "/theme_image.jpg"));
		bgIcon.setImage(bgIcon.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
		bombIcon = new ImageIcon(this.getClass().getClassLoader().getResource("bombs/b" + String.valueOf(bombType) + ".gif"));
		bombIcon.setImage(bombIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		explosionIcon = new ImageIcon(this.getClass().getClassLoader().getResource("bombs/explosion.gif"));
		explosionIcon.setImage(explosionIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		wallIcon0 = new ImageIcon(this.getClass().getClassLoader().getResource(themeDir + themeFolder + "/wall0.png"));
		wallIcon0.setImage(wallIcon0.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		chestIcon0 = new ImageIcon(this.getClass().getClassLoader().getResource(themeDir + themeFolder + "/chest0.png"));
		chestIcon0.setImage(chestIcon0.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		wallIcon1 = new ImageIcon(this.getClass().getClassLoader().getResource(themeDir + themeFolder + "/wall1.png"));
		wallIcon1.setImage(wallIcon1.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		chestIcon1 = new ImageIcon(this.getClass().getClassLoader().getResource(themeDir + themeFolder + "/chest1.png"));
		chestIcon1.setImage(chestIcon1.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		floorIcon = new ImageIcon(this.getClass().getClassLoader().getResource(themeDir + themeFolder + "/floor.png"));
		floorIcon.setImage(floorIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		
		powerPotionIcon = new ImageIcon(this.getClass().getClassLoader().getResource("items_in_bubble/powerpotion.png"));
		powerPotionIcon.setImage(powerPotionIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		bombPotionIcon = new ImageIcon(this.getClass().getClassLoader().getResource("items_in_bubble/bombpotion.png"));
		bombPotionIcon.setImage(bombPotionIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		lifeNeedleIcon = new ImageIcon(this.getClass().getClassLoader().getResource("items_in_bubble/lifeneedle.png"));
		lifeNeedleIcon.setImage(lifeNeedleIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		shoeIcon = new ImageIcon(this.getClass().getClassLoader().getResource("items_in_bubble/shoe.png"));
		shoeIcon.setImage(shoeIcon.getImage().getScaledInstance(cellSize, cellSize,Image.SCALE_DEFAULT ));
		itemIcons = new ImageIcon[itemNumber];
		itemIcons[0] = powerPotionIcon; itemIcons[1] = bombPotionIcon; itemIcons[2] = lifeNeedleIcon; itemIcons[3] = shoeIcon;
		
		
		bgLabel = new ImageLabel(bgIcon);
		bgLabel.setAlpha(0.5f);
		bgLabel.setSize(width, height);
		getLayeredPane().add(bgLabel, new Integer(Integer.MIN_VALUE));
		
		contentPane.setOpaque(false);
	    contentPane.setLayout(null);
	    contentPane.setSize(width, height);
		
		// initialize map
		String diff = difficulty.toString().toLowerCase();
		String dir = "maps/";
		
		initializeWalls(dir + diff + "/walls");
		initializeChests(dir + diff + "/chests");
		
		mapPanel = getMapPanel();
		contentPane.add(mapPanel, "Center");
		contentPane.add(getInfoPanel(), "West");
		
	}
	
	private void initializeWalls(String path) {
		int i, j, hasWall;
		Scanner ipt = null;
		double ran;
		try {
		ipt = new Scanner(this.getClass().getClassLoader().getResource(path).openStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
		for(i=0;i<rows;i++)
			for(j=0;j<cols;j++) {
				hasWall = ipt.nextInt();
				if(hasWall == 1) {
					ran = Math.random();
					if(ran<0.5)
						walls[i][j] = new JLabel(wallIcon0);
					else
						walls[i][j] = new JLabel(wallIcon1);
					blocks[i][j] = true;
				}
				else {
					walls[i][j] = null;
				}			
			}
	}
	
	private void initializeChests(String path) {
		int i, j, hasChest;
		Scanner ipt = null;
		double ran;
		try {
		ipt = new Scanner(this.getClass().getClassLoader().getResource(path).openStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
		for(i=0;i<rows;i++)
			for(j=0;j<cols;j++) {
				hasChest = ipt.nextInt();
				if(hasChest == 1) {
					ran = Math.random();
					if(ran<0.5)
						chests[i][j] = new JLabel(chestIcon0);
					else
						chests[i][j] = new JLabel(chestIcon1);
					blocks[i][j] = true;
				}
				else {
					chests[i][j] = null;
				}			
			}
	}

	private JPanel getMapPanel() {
		JPanel p = new JPanel();
		p.setLayout(null);
		p.setBounds(0, 5, mapWidth, mapHeight);
		p.setOpaque(false);
		
		int i, j;
		
		for(i=0;i<rows;i++)
			for(j=0;j<cols;j++) {
				explosionLabels[i][j] = new JLabel(explosionIcon);
				explosionLabels[i][j].setVisible(false);
				explosionLabels[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
				bombLabels[i][j] = new JLabel(bombIcon);
				bombLabels[i][j].setVisible(false);
				bombLabels[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
				p.add(explosionLabels[i][j]);
				p.add(bombLabels[i][j]);
			}
		
		//place slimes
		for(i=0;i<enemies + players;i++) {
			p.add(slimes[i].weakBubbleLabel);
			p.add(slimes[i].slimeLabel);
		}
		
		//place labels
		for(i=0;i<rows;i++)
			for(j=0;j<cols;j++) {
				floors[i][j] = new ImageLabel(floorIcon);
				floors[i][j].setAlpha(0.60f);
				floors[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
				if(walls[i][j] != null) {
					walls[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
					p.add(walls[i][j]);
				}
				if(chests[i][j] != null) {
					chests[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
					p.add(chests[i][j]);
				}	
				p.add(floors[i][j]);
			}
		
		
		return p;
	}

	private JPanel getInfoPanel() {
		int i=0;
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setBounds(910, 5, 280, 790);
		p.setLayout(new BorderLayout());
		
		// time label and timer
		int min = timeLeft / 60;
		int sec = timeLeft % 60;
		Font timeFont = new Font("Chalkduster", Font.PLAIN, 40);
		String time = String.format("%02d:%02d", min, sec);
		timeLabel = new JLabel(time);
		timeLabel.setFont(timeFont);
		p.add(timeLabel, "North");
		ActionListener timerListener = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				timeLeft--;
				timeLabel.setText(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
				if(timeLeft <= 0)
					windowManager.timeOut();
			}
		};
		timer = new Timer(1000, timerListener);
		timer.start();
		
		// player info panel
		JPanel playerInfoPanel = new JPanel();
		playerInfoPanel.setLayout(new GridLayout((slimeCount), 1));
		playerInfoPanel.setOpaque(false);
		for(i=0;i<(slimeCount);i++) {
			playerInfoPanel.add(slimes[i].infoPanel);
		}
		p.add(playerInfoPanel, "Center");

		// button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setOpaque(false);
		
		JPanel itemPanel = new JPanel();
		itemPanel.setOpaque(false);
		itemPanel.setLayout(new FlowLayout());
		JPanel itemPanelPlayer1 = new JPanel();
		itemPanelPlayer1.setOpaque(false);
		itemPanelPlayer1.setLayout(new BorderLayout());
		JLabel itemLabel1 = new JLabel("P1 Items:");
		itemPanelPlayer1.add(itemLabel1, "North");
		JPanel itemButtonPanel1 = new JPanel();
		itemButtonPanel1.setOpaque(false);
		itemButtonPanel1.setLayout(new FlowLayout());
		itemPanelPlayer1.add(itemButtonPanel1, "Center");
		itemPanel.add(itemPanelPlayer1);

		JPanel itemButtons1[] = new JPanel[3];
		for(i=0;i<3;i++) {
			itemButtons1[i] = new JPanel();
			itemButtons1[i].setOpaque(false);
			itemButtons1[i].setLayout(new BorderLayout());
		}

		lifeNeedleNumber1 = new JLabel("0");
		slimes[0].lifeNeedleNumber = lifeNeedleNumber1;
		ImageIcon lifeNeedle = new ImageIcon(this.getClass().getClassLoader().getResource("items/lifeNeedle.png"));
		lifeNeedle.setImage(lifeNeedle.getImage().getScaledInstance(cellSize/2, cellSize/2,Image.SCALE_DEFAULT ));
		lifeNeedleButton1 = new JButton(lifeNeedle);
		lifeNeedleButton1.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				if(slimes[0].lifeNeedle > 0 && slimes[0].status == Status.WEAK)
					slimes[0].useLifeNeedle();		
			}
		});
		itemButtons1[0].add(lifeNeedleNumber1, "North");
		itemButtons1[0].add(lifeNeedleButton1, "Center");
		itemButtonPanel1.add(itemButtons1[0]);

		if(players == 2) {
			JPanel itemPanelPlayer2 = new JPanel();
			itemPanelPlayer2.setOpaque(false);
			itemPanelPlayer2.setLayout(new BorderLayout());
			JLabel itemLabel2 = new JLabel("P2 Items:");
			itemPanelPlayer2.add(itemLabel2, "North");
			JPanel itemButtonPanel2 = new JPanel();
			itemButtonPanel2.setOpaque(false);
			itemButtonPanel2.setLayout(new FlowLayout());
			itemPanelPlayer2.add(itemButtonPanel2, "Center");
			itemPanel.add(itemPanelPlayer2);

			JPanel itemButtons2[] = new JPanel[3];
			for(i=0;i<3;i++) {
				itemButtons2[i] = new JPanel();
				itemButtons2[i].setOpaque(false);
				itemButtons2[i].setLayout(new BorderLayout());
			}

			lifeNeedleNumber2 = new JLabel("0");
			slimes[1].lifeNeedleNumber = lifeNeedleNumber2;
			lifeNeedleButton2 = new JButton(lifeNeedle);
			lifeNeedleButton2.addActionListener(new ActionListener() {		
				public void actionPerformed(ActionEvent e) {
					if(slimes[1].lifeNeedle > 0)
						slimes[1].useLifeNeedle();		
				}
			});
			itemButtons2[0].add(lifeNeedleNumber2, "North");
			itemButtons2[0].add(lifeNeedleButton2, "Center");
			itemButtonPanel2.add(itemButtons2[0]);
		}


		

		JPanel menuPanel = new JPanel();
		menuPanel.setOpaque(false);
		mainMenuButton = new JButton("Main Menu");
		pauseButton = new JButton("Pause");
		Font buttonFont = new Font("Chalkduster", Font.PLAIN, 18);
		mainMenuButton.setFont(buttonFont);
		pauseButton.setFont(buttonFont);
		menuPanel.add(pauseButton);
		menuPanel.add(mainMenuButton);
		
		

		buttonPanel.add(itemPanel, "Center");
		buttonPanel.add(menuPanel, "South");
		p.add(buttonPanel, "South");
		
		
		return p;
	}

	public void openChest(int row, int col) {	
		if(Math.random() < 0.5) {
			int itemIndex;
			itemLabels[row][col] = chests[row][col];
			double ran = (itemNumber) * Math.random();
			itemIndex = (int)ran;	
			itemLabels[row][col].setIcon(itemIcons[itemIndex]);
			items[row][col] = Item.values()[itemIndex + 1];		
		}
		else
			chests[row][col].setVisible(false);
		chests[row][col] = null;
		synchronized(blocks){
			blocks[row][col] = false;
		}
	}
	
	public void pause() {
		int i;
		//puase slimes
		for(i=0;i<slimeCount;i++)
			slimes[i].pause();
		
		//puase bombs
		for(Bomb b:bombList) {
			b.pause();
		}

		timer.stop();
	}
	
	public void resume() {
		int i;
		//resume slimes
		for(i=0;i<slimeCount;i++)
			slimes[i].resume();
		
		//resume bombs
		for(Bomb b:bombList) {
			b.resume();
		}
		
		timer.start();
	}
	
	public void clearInputMap() {
		mainMenuButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		pauseButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		lifeNeedleButton1.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		if(lifeNeedleButton2 != null)
			lifeNeedleButton2.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
	}
	
//	public static void main(String[] args) {
//		GameFrame f = new GameFrame(1200, 800, 13, 15, 60);
//		f.init();
//		f.clearInputMap();
//		f.setVisible(true);
//		int i;
//		for(i=0;i<f.slimeCount;i++)
//			f.slimes[i].init();
////		f.openChest(0, 1);
//	}
	
	
}

enum Item {
	NONE, POWERPOTION, BOMBPOTION, LIFENEEDLE, SHOE
}
