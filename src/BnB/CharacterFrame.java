package bnb;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import utils.*;

public class CharacterFrame extends JFrame{
	public int width;
	public int height;
	public int iconWidth;
	public int iconHeight;
	public int players;
	public Theme theme;
	public int bombType;
	public SlimeType characterType[];
	public JButton backButton;
	public JButton startButton;
	private ImageLabel bgLabel;
	private ImageIcon bgIcon;
	private ImageIcon slimeGreenIcon, slimePinkIcon, slimeBlueIcon, slimeHatIcon;
	private JLabel name[];
	private JRadioButton bomb0, bomb1, bomb2;
	private JRadioButton slimeGreen0, slimePink0, slimeBlue0, slimeHat0;
	private JRadioButton slimeGreen1, slimePink1, slimeBlue1, slimeHat1;
	private JRadioButton player1Buttons[];
	private JLabel slimeLabels1[];
	private ButtonGroup slimeGroup1;

	public CharacterFrame(int w, int h) {
		super("Slime BnB");
//		width = w;
//		height = h;
//		iconWidth = w/12;
//		iconHeight = w/12;
//		players = 1;
//		theme = Theme.NEON;
//		bombType = 0;
//		characterType = new SlimeType[1];
//		name = new JLabel[1];
//		characterType[0] = SlimeType.GREEN;
//		name[0] = new JLabel("Green Slime");
		
		width = w;
		height = h;
		iconWidth = w/12;
		iconHeight = w/12;
		players = 2;
		theme = Theme.NEON;
		bombType = 0;
		characterType = new SlimeType[players];
		name = new JLabel[players];
		characterType[0] = SlimeType.GREEN;
		name[0] = new JLabel("Green Slime");
		characterType[1] = SlimeType.PINK;
		name[1] = new JLabel("Pink Slime");
	}
	public CharacterFrame(OptionFrame f) {
		super("Slime BnB");
		width = f.width;
		height = f.height;
		iconWidth = width/12;
		iconHeight = width/12;
		players = f.players;
		theme = f.theme;
		bombType = 0;
		characterType = new SlimeType[players];
		name = new JLabel[players];
		characterType[0] = SlimeType.GREEN;
		name[0] = new JLabel("Green Slime");
		if(players == 2) {
			characterType[1] = SlimeType.PINK;
			name[1] = new JLabel("Pink Slime"); 
		}
	}
	
	public void init() {
		setSize(width, height + 10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		// set background
		JPanel contentPane = (JPanel)getContentPane();
		switch (theme) {
		case NEON: {	
			bgIcon = new ImageIcon(this.getClass().getResource("/res/themes/neon/theme_image.jpg"));
			bgIcon.setImage(bgIcon.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
			break;
		}
		case LEGO: {	
			bgIcon = new ImageIcon(this.getClass().getResource("/res/themes/lego/theme_image.jpg"));
			bgIcon.setImage(bgIcon.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
			break;
		}
		case PSYCHEDELIC: {	
			bgIcon = new ImageIcon(this.getClass().getResource("/res/themes/psychedelic/theme_image.jpg"));
			bgIcon.setImage(bgIcon.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + theme);
		}

		// get character icons
		slimeGreenIcon = new ImageIcon(this.getClass().getResource("/res/characters/green_slime/green_slime_front.gif"));
		slimePinkIcon = new ImageIcon(this.getClass().getResource("/res/characters/pink_slime/pink_slime_front.gif"));
		slimeBlueIcon = new ImageIcon(this.getClass().getResource("/res/characters/blue_slime/blue_slime_front.gif"));
		slimeHatIcon = new ImageIcon(this.getClass().getResource("/res/characters/hat_slime/hat_slime_front.gif"));
		slimeGreenIcon.setImage(slimeGreenIcon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));
		slimePinkIcon.setImage(slimePinkIcon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));
		slimeBlueIcon.setImage(slimeBlueIcon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));
		slimeHatIcon.setImage(slimeHatIcon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));


		bgLabel = new ImageLabel(bgIcon);
		bgLabel.setAlpha(0.5f);
		bgLabel.setSize(width, height);
		getLayeredPane().add(bgLabel, new Integer(Integer.MIN_VALUE));
		
		contentPane.setOpaque(false);
	    contentPane.setLayout(new BorderLayout());
	    contentPane.setSize(width, height);
		
		// set title
	    JLabel titleLabel = new JLabel("     S  l  i  m  e           B    n    B     ");
	    Font titleFont = new Font("Phosphate", Font.BOLD, 100);
	    titleLabel.setForeground(new Color(0.343f, 0.339f, 0.315f));
	    titleLabel.setFont(titleFont);
	    contentPane.add(titleLabel, "North");
	    
	    // set option panel
	    JPanel optionPanel = getOptionPanel();
	    contentPane.add(optionPanel, "Center");
	    
	 // set button panel
	    JPanel buttonPanel = getButtonPanel();
	    contentPane.add(buttonPanel, "South");    
	}

	
	private JPanel getButtonPanel() {
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new FlowLayout());
		
		Font font = new Font("Phosphate", Font.PLAIN, 45);
		Color color = new Color(0.343f, 0.339f, 0.315f);
		UIManager.put("Button.font", font);
		UIManager.put("Button.foreground", color);
		
		backButton = new JButton("Back");
		startButton = new JButton("Start");
		JLabel blank = new JLabel("                  ");
		p.add(backButton);
		p.add(blank);
		p.add(startButton);
		
		return p;
	}
	
	private JPanel getOptionPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(players + 1, 1));
		p.setOpaque(false);
		
		JPanel bombPanel = getBombPanel();
		p.add(bombPanel);
		JPanel characterPanel0 = getCharacterPanel0();
		p.add(characterPanel0);
		if(players == 2) {
			JPanel characterPanel1 = getCharacterPanel1();
			p.add(characterPanel1);
		}
		return p;		
	}
	
	private JPanel getBombPanel() {
		int i;
		JPanel p = new JPanel();
		p.setOpaque(false);
		FlowLayout flowlayout = new FlowLayout();
        flowlayout.setAlignment(FlowLayout.LEFT);
		p.setLayout(flowlayout);
		
		Font textFont = new Font("Chalkduster", Font.PLAIN, 40);
		UIManager.put("Label.font", textFont);
		
		JLabel bombLabel = new JLabel("     Choose Bomb   ");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new FlowLayout());
		BombButtonHandler handler = new BombButtonHandler();
		ImageIcon bomb0Icon = new ImageIcon(this.getClass().getResource("/res/bombs/b0.gif"));
		ImageIcon bomb1Icon = new ImageIcon(this.getClass().getResource("/res/bombs/b1.gif"));
		ImageIcon bomb2Icon = new ImageIcon(this.getClass().getResource("/res/bombs/b2.gif"));	
		bomb0Icon.setImage(bomb0Icon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));
		bomb1Icon.setImage(bomb1Icon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));
		bomb2Icon.setImage(bomb2Icon.getImage().getScaledInstance(iconWidth, iconHeight,Image.SCALE_DEFAULT ));
		ButtonGroup bombGroup = new ButtonGroup();
		bomb0 = new JRadioButton("");
		bomb1 = new JRadioButton("");
		bomb2 = new JRadioButton("");
		JLabel bombLabels[] = new JLabel[3];
		JRadioButton bombs[] = {bomb0, bomb1, bomb2};
		ImageIcon bombIcons[] = {bomb0Icon, bomb1Icon, bomb2Icon};
		for(i=0;i<3;i++) {
			bombs[i].addItemListener(handler);
			bombGroup.add(bombs[i]);
			buttonPanel.add(bombs[i]);
			bombLabels[i] = new JLabel(bombIcons[i]);
			buttonPanel.add(bombLabels[i]);
		}
		bombGroup.setSelected(bomb0.getModel(), true);

		p.add(bombLabel);
		p.add(buttonPanel);
		
		return p;
	}
	
	private JPanel getCharacterPanel0() {
		int i;
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridLayout(2, 1));
		
		Font textFont = new Font("Chalkduster", Font.PLAIN, 40);
		UIManager.put("Label.font", textFont);
		
		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		FlowLayout flowlayout = new FlowLayout();
        flowlayout.setAlignment(FlowLayout.LEFT);
		textPanel.setLayout(flowlayout);
		JLabel characterLabel = new JLabel("     Choose P1 Character     ");
		name[0] = new JLabel("Green Slime");
		textPanel.add(characterLabel);
		textPanel.add(name[0]);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new FlowLayout());
		
		CharacterButtonHandler0 handler = new CharacterButtonHandler0();
		slimeGreen0 = new JRadioButton("");
		slimePink0 = new JRadioButton("");
		slimeBlue0 = new JRadioButton("");
		slimeHat0 = new JRadioButton("");
		JRadioButton slimes[] = {slimeGreen0, slimePink0, slimeBlue0, slimeHat0};
				
		ButtonGroup slimeGroup = new ButtonGroup();
		JLabel slimeLabels[] = new JLabel[4];
		ImageIcon slimeIcons[] = {slimeGreenIcon, slimePinkIcon, slimeBlueIcon, slimeHatIcon};
		for(i=0;i<4;i++) {
			slimes[i].addItemListener(handler);
			slimeGroup.add(slimes[i]);
			buttonPanel.add(slimes[i]);
			slimeLabels[i] = new JLabel(slimeIcons[i]);
			buttonPanel.add(slimeLabels[i]);
		}
		slimeGroup.setSelected(slimeGreen0.getModel(), true);

		p.add(textPanel);
		p.add(buttonPanel);
		
		return p;
	}

	private JPanel getCharacterPanel1() {
		int i;
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridLayout(2, 1));
		
		Font textFont = new Font("Chalkduster", Font.PLAIN, 40);
		UIManager.put("Label.font", textFont);
		
		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		FlowLayout flowlayout = new FlowLayout();
        flowlayout.setAlignment(FlowLayout.LEFT);
		textPanel.setLayout(flowlayout);
		JLabel characterLabel = new JLabel("     Choose P2 Character     ");
		name[1] = new JLabel("Pink Slime");
		textPanel.add(characterLabel);
		textPanel.add(name[1]);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new FlowLayout());
		
		CharacterButtonHandler1 handler = new CharacterButtonHandler1();
		slimeGreen1 = new JRadioButton("");
		slimePink1 = new JRadioButton("");
		slimeBlue1 = new JRadioButton("");
		slimeHat1 = new JRadioButton("");
		JRadioButton slimes[] = {slimeGreen1, slimePink1, slimeBlue1, slimeHat1};
		player1Buttons = slimes;
		
		slimeGroup1 = new ButtonGroup();
		slimeLabels1 = new JLabel[4];
		ImageIcon slimeIcons[] = {slimeGreenIcon, slimePinkIcon, slimeBlueIcon, slimeHatIcon};
		for(i=0;i<4;i++) {
			slimes[i].addItemListener(handler);
			slimeGroup1.add(slimes[i]);
			buttonPanel.add(slimes[i]);
			slimeLabels1[i] = new JLabel(slimeIcons[i]);
			buttonPanel.add(slimeLabels1[i]);
		}
		slimeGroup1.setSelected(slimePink1.getModel(), true);
		slimeGreen1.setVisible(false);
		slimeLabels1[0].setVisible(false);


		p.add(textPanel);
		p.add(buttonPanel);
		
		return p;
	}
	
	private class BombButtonHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == bomb0)
			bombType = 0;
		else if(e.getSource() == bomb1)
			bombType = 1;	
		else if(e.getSource() == bomb2)
			bombType = 2;
		}		
	}

	private class CharacterButtonHandler0 implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			int i;
			boolean setP1 = players == 2 && player1Buttons != null;
			if(setP1) {
				for(i=0;i<4;i++) {
					player1Buttons[i].setVisible(true);
					slimeLabels1[i].setVisible(true);
				}		
			}
			if(e.getSource() == slimeGreen0) {
				characterType[0] = SlimeType.GREEN;
				if(setP1) {
					slimeGreen1.setVisible(false);
					slimeLabels1[0].setVisible(false);
					slimeGroup1.setSelected(slimePink1.getModel(), true);
				}
				name[0].setText("Green Slime");
			}
			else if(e.getSource() == slimePink0) {
				characterType[0] = SlimeType.PINK;
				if(setP1) {
					slimePink1.setVisible(false);
					slimeLabels1[1].setVisible(false);
					slimeGroup1.setSelected(slimeGreen1.getModel(), true);
				}
				name[0].setText("Pink Slime");
			}
			else if(e.getSource() == slimeBlue0) {
				characterType[0] = SlimeType.BLUE;
				if(setP1) {
					slimeBlue1.setVisible(false);
					slimeLabels1[2].setVisible(false);
					slimeGroup1.setSelected(slimeGreen1.getModel(), true);
				}
				name[0].setText("Blue Slime");
			}
			else if(e.getSource() == slimeHat0) {
				characterType[0] = SlimeType.HAT;
				if(setP1) {
					slimeHat1.setVisible(false);
					slimeLabels1[3].setVisible(false);
					slimeGroup1.setSelected(slimeGreen1.getModel(), true);
				}
				name[0].setText("Hat Slime");
			}
		}		
	}

	private class CharacterButtonHandler1 implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == slimeGreen1) {
				characterType[1] = SlimeType.GREEN;
				name[1].setText("Green Slime");
			}
			else if(e.getSource() == slimePink1) {
				characterType[1] = SlimeType.PINK;
				name[1].setText("Pink Slime");
			}
			else if(e.getSource() == slimeBlue1) {
				characterType[1] = SlimeType.BLUE;
				name[1].setText("Blue Slime");
			}
			else if(e.getSource() == slimeHat1) {
				characterType[1] = SlimeType.HAT;
				name[1].setText("Hat Slime");
			}
		}		
	}

	
	public static void main(String[] args) {
		CharacterFrame f = new CharacterFrame(1200, 720);
		f.init();
		f.setVisible(true);
	}
}

enum SlimeType {
	GREEN, PINK, BLUE, HAT
}
