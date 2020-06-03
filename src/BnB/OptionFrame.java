package bnb;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import utils.*;

public class OptionFrame extends JFrame{
	int width;
	int height;
	int players;
	int enemies;
	Theme theme;
	Difficulty difficulty;
	JButton backButton;
	JButton nextButton;
	private ImageLabel bgLabel;
	private JRadioButton players1, players2;
	private JRadioButton enemies0, enemies1, enemies2, enemies3;
	private JRadioButton themeNeon, themeLego, themePsychedelic;
	private JRadioButton diffEasy, diffNormal, diffHard;
	private int rbCount;
	private ImageIcon bgNeon, bgLego, bgPsychedelic;
	private ButtonGroup playersGroup, enemiesGroup;
	
	
	public OptionFrame(int w, int h) {
		super("Slime BnB");
		width = w;
		height = h;
		players = 1;
		enemies = 3;
		rbCount = 12;
		theme = Theme.NEON;
		difficulty = Difficulty.EASY;
	}
	
	public void init() {
		// set window
		setSize(width, height + 10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		// set background image
		JPanel contentPane = (JPanel)getContentPane();
		bgNeon = new ImageIcon(this.getClass().getResource("/res/themes/neon/theme_image.jpg"));
		bgNeon.setImage(bgNeon.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
		bgLego = new ImageIcon(this.getClass().getResource("/res/themes/lego/theme_image.jpg"));
		bgLego.setImage(bgLego.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
		bgPsychedelic = new ImageIcon(this.getClass().getResource("/res/themes/psychedelic/theme_image.jpg"));
		bgPsychedelic.setImage(bgPsychedelic.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
		
		bgLabel = new ImageLabel(bgNeon);
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
	
	private JPanel getOptionPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(4, 1));
		p.setOpaque(false);
		
		Font textFont = new Font("Chalkduster", Font.PLAIN, 40);
		UIManager.put("Label.font", textFont);
		UIManager.put("RadioButton.font", textFont);
		FlowLayout layout = new FlowLayout();

		// enemies panel
		JLabel enemiesLabel = new JLabel("Enemies ");
		EnemiesButtonHandler enemiesButtonHandler = new EnemiesButtonHandler();
		enemies1 = new JRadioButton("1");
		enemies2 = new JRadioButton("2");
		enemies0 = new JRadioButton("0");
		enemies3 = new JRadioButton("3");
		enemies1.addItemListener(enemiesButtonHandler);
		enemies2.addItemListener(enemiesButtonHandler);
		enemies0.addItemListener(enemiesButtonHandler);
		enemies3.addItemListener(enemiesButtonHandler);
		enemiesGroup = new ButtonGroup();
		enemiesGroup.add(enemies1);
		enemiesGroup.add(enemies2);
		enemiesGroup.add(enemies0);
		enemiesGroup.add(enemies3);
		enemiesGroup.setSelected(enemies3.getModel(), true);
		JPanel enemiesPanel = new JPanel();
		enemiesPanel.setLayout(layout);
		enemiesPanel.add(enemiesLabel);
		enemiesPanel.add(enemies3);
		enemiesPanel.add(enemies2);
		enemiesPanel.add(enemies1);
		enemiesPanel.add(enemies0);
		enemies0.setVisible(false);

		// players panel
		JLabel playersLabel = new JLabel("Players ");
		PlayersButtonHandler playersButtonHandler = new PlayersButtonHandler();
		players1 = new JRadioButton("1");
		players2 = new JRadioButton("2");
		players1.addItemListener(playersButtonHandler);
		players2.addItemListener(playersButtonHandler);
		playersGroup = new ButtonGroup();
		playersGroup.add(players1);
		playersGroup.add(players2);
		playersGroup.setSelected(players1.getModel(), true);
		JPanel playersPanel = new JPanel();
		playersPanel.setLayout(layout);
		playersPanel.add(playersLabel);
		playersPanel.add(players1);
		playersPanel.add(players2);
		
		// theme panel
		JLabel themeLabel = new JLabel("Theme ");
		ThemeButtonHandler themeButtonHandler = new ThemeButtonHandler();
		themeNeon = new JRadioButton("Neon");
		themeLego = new JRadioButton("Lego");
		themePsychedelic = new JRadioButton("Psychedelic");
		themeNeon.addItemListener(themeButtonHandler);
		themeLego.addItemListener(themeButtonHandler);
		themePsychedelic.addItemListener(themeButtonHandler);
		ButtonGroup themeGroup = new ButtonGroup();
		themeGroup.add(themeNeon);
		themeGroup.add(themeLego);
		themeGroup.add(themePsychedelic);
		themeGroup.setSelected(themeNeon.getModel(), true);
		JPanel themePanel = new JPanel();
		themePanel.setLayout(layout);
		themePanel.add(themeLabel);
		themePanel.add(themeNeon);
		themePanel.add(themeLego);		
		themePanel.add(themePsychedelic);	

		// difficulty panel
		JLabel difficultyLabel = new JLabel("Difficulty ");
		DifficultyButtonHandler difficultyButtonHandler = new DifficultyButtonHandler();
		diffEasy = new JRadioButton("Easy");
		diffNormal = new JRadioButton("Normal");
		diffHard = new JRadioButton("Hard");
		diffEasy.addItemListener(difficultyButtonHandler);
		diffNormal.addItemListener(difficultyButtonHandler);
		diffHard.addItemListener(difficultyButtonHandler);
		ButtonGroup difficultyGroup = new ButtonGroup();
		difficultyGroup.add(diffEasy);
		difficultyGroup.add(diffNormal);
		difficultyGroup.add(diffHard);
		difficultyGroup.setSelected(diffEasy.getModel(), true);
		JPanel difficultyPanel = new JPanel();
		difficultyPanel.setLayout(layout);
		difficultyPanel.add(difficultyLabel);
		difficultyPanel.add(diffEasy);
		difficultyPanel.add(diffNormal);
		difficultyPanel.add(diffHard);	

		
		playersPanel.setOpaque(false);
		enemiesPanel.setOpaque(false);
		themePanel.setOpaque(false);
		difficultyPanel.setOpaque(false);
		p.add(playersPanel);
		p.add(enemiesPanel);
		p.add(themePanel);
		p.add(difficultyPanel);	
		
		return p;
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
		nextButton = new JButton("Next");
		JLabel blank = new JLabel("                  ");
		p.add(backButton);
		p.add(blank);
		p.add(nextButton);
		
		return p;
	}
	
	private class PlayersButtonHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == players1){
				players = 1;
				enemies0.setVisible(false);
				enemies3.setVisible(true);
				enemiesGroup.setSelected(enemies3.getModel(), true);
			}
			else if(e.getSource() == players2) {
				players = 2;	
				enemies0.setVisible(true);
				enemies3.setVisible(false);
				enemiesGroup.setSelected(enemies2.getModel(), true);
			}
		}
		
	}
	
	private class EnemiesButtonHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == enemies0)
				enemies = 0;
			else if(e.getSource() == enemies1)
				enemies = 1;	
			else if(e.getSource() == enemies2)
				enemies = 2;
			else if(e.getSource() == enemies3)
				enemies = 3;
		}
		
	}

	private class ThemeButtonHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == themeNeon) {
				theme = Theme.NEON;
				bgLabel.setIcon(bgNeon);
			}
			else if(e.getSource() == themeLego) {
				theme = Theme.LEGO;	
				bgLabel.setIcon(bgLego);
			}
			else if(e.getSource() == themePsychedelic) {
				theme = Theme.PSYCHEDELIC;
				bgLabel.setIcon(bgPsychedelic);
			}
		}
	}
	
	private class DifficultyButtonHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == diffEasy)
				difficulty = Difficulty.EASY;
			else if(e.getSource() == diffNormal)
				difficulty = Difficulty.NORMAL;	
			else if(e.getSource() == diffHard)
				difficulty = Difficulty.HARD;
		}
	}

	public static void main(String[] args) {
		OptionFrame f = new OptionFrame(1200, 800);
		f.init();
		f.setVisible(true);
	}

}

enum Theme {
	NEON, LEGO, PSYCHEDELIC
}

enum Difficulty {
	EASY, NORMAL, HARD
}