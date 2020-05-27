package bnb;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import utils.*;

public class GameFrame extends JFrame{
	public int width;
	public int height;
	public int rows;
	public int cols;
	public Theme theme;
	public Difficulty difficulty;
	public int bombType;
	public SlimeType characterType[];
	public int players;
	public int enemies;
	public SlimeCharacter slimes[];	//
	private JLabel walls[][];
	private JLabel cases[][];
	private boolean blocks[][];
	private boolean bombs[][];
	private Item items[][];
	
	public GameFrame(int w, int h, int r, int c) {
		super("Slime BnB");
		width = w;
		height = h;
		rows = r;
		cols = c;
		theme = Theme.NEON;
		difficulty = Difficulty.EASY;
		bombType = 0;
		characterType = new SlimeType[1];
		characterType[0] = SlimeType.GREEN;
		players = 1;
		enemies = 3;	
	}
	
	public GameFrame(OptionFrame op, CharacterFrame ch, int r, int c) {
		super("Slime BnB");
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
	}
	
	public void init() {
		
	}
	
	
	
	
}

enum Item {
	NONE, POWERPOTION, BOMBPOTION, LIFENEEDLE
}
