package BnB;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class StartFrame extends JFrame{
	public int width;
	public int height;
	public int buttonCount;
	public JButton startButton;
	public JButton musicButton;
	public JButton soundButton;
	private ImageIcon soundOn;
	private ImageIcon soundOff;
	private ImageIcon musicOn;
	private ImageIcon musicOff;
	
	public StartFrame() {
		super("Slime BnB");
	}
	public void init(int w, int h) {
		// initialize member variables
		width = w;
		height = h;
		buttonCount = 2;
		setSize(width, height + 10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		JPanel contentPane = (JPanel)getContentPane();
		
		// set background image
		ImageIcon bg = new ImageIcon(this.getClass().getResource("/res/start_frame/mainBackground.gif"));
		bg.setImage(bg.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT ));
		JLabel bgLabel = new JLabel(bg);
		bgLabel.setSize(width, height);
		getLayeredPane().add(bgLabel, new Integer(Integer.MIN_VALUE));
		contentPane.setOpaque(false);
	    contentPane.setLayout(new BorderLayout());
	    contentPane.setSize(width, height);
	    
	    // set title label
	    JLabel titleLabel = new JLabel("     S  l  i  m  e           B    n    B     ");
	    Font titleFont = new Font("Phosphate", Font.BOLD, 100);
	    titleLabel.setForeground(new Color(0.686f, 0.678f, 0.631f));
	    titleLabel.setFont(titleFont);
	    contentPane.add(titleLabel, "North");
	    
	    // set buttons panel
	    JPanel buttonPanel = getButtonPanel();
	    contentPane.add(buttonPanel, "East");

		
	}
	
	private JPanel getButtonPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(buttonCount, 1));
		p.setOpaque(false);
		
		Font buttonFont = new Font("Chalkduster", Font.BOLD, 60);
		Color buttonTextColor = new Color(1f, 1f, 1f);
		UIManager.put("Button.font", buttonFont);
		UIManager.put("Button.foreground", buttonTextColor);
		startButton = new JButton("START   ");
		startButton.setBorderPainted(false);
		startButton.setFocusPainted(false);
		startButton.setContentAreaFilled(false);
		
		JPanel soundPanel = getSoundPanel();
		
		p.add(startButton);
		p.add(soundPanel);
		
		return p;
	}
	
	private JPanel getSoundPanel() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.setOpaque(false);
		int buttonWidth = 100;
		int buttonHeight = 100;
		
		//set Image icons
		soundOn = new ImageIcon(this.getClass().getResource("/res/start_frame/sound_on.png"));
		soundOff = new ImageIcon(this.getClass().getResource("/res/start_frame/sound_off.png"));
		musicOn = new ImageIcon(this.getClass().getResource("/res/start_frame/music_on.png"));
		musicOff = new ImageIcon(this.getClass().getResource("/res/start_frame/music_off.png"));
		soundOn.setImage(soundOn.getImage().getScaledInstance(buttonWidth, buttonHeight,Image.SCALE_DEFAULT ));
		soundOff.setImage(soundOff.getImage().getScaledInstance(buttonWidth, buttonHeight,Image.SCALE_DEFAULT ));
		musicOn.setImage(musicOn.getImage().getScaledInstance(buttonWidth, buttonHeight,Image.SCALE_DEFAULT ));
		musicOff.setImage(musicOff.getImage().getScaledInstance(buttonWidth, buttonHeight,Image.SCALE_DEFAULT ));
		soundButton = new JButton(soundOn);
		musicButton = new JButton(musicOn);
		musicButton.setBorderPainted(false);
		musicButton.setFocusPainted(false);
		musicButton.setContentAreaFilled(false);
		soundButton.setBorderPainted(false);
		soundButton.setFocusPainted(false);
		soundButton.setContentAreaFilled(false);
		p.add(musicButton);
		p.add(soundButton);
		
		return p;
	}
	
	public void setSoundIcon(boolean b) {
		if(b)
			soundButton.setIcon(soundOn);
		else
			soundButton.setIcon(soundOff);
	}
	
	public void setMusicIcon(boolean b) {
		if(b)
			musicButton.setIcon(musicOn);
		else
			musicButton.setIcon(musicOff);
	}
	

	
	
	public static void main(String[] args) {
		StartFrame f = new StartFrame();
		f.init(1200, 675);
		f.setVisible(true);
	}

}
