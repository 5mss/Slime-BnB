package bnb;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SlimePlayer extends SlimeCharacter{
	private final static String PRESSED = "pressed ";
	private final static String RELEASED = "released ";

	private int pressedKeyNum;
	private boolean keyPressed[];

	public SlimePlayer(int X, int Y, int i, SlimeType t, GameFrame f) {
		super(X, Y, i, t, f);
		// TODO Auto-generated constructor stub
		isPlayer = true;
		pressedKeyNum = 0;
		keyPressed = new boolean[4];
		keyPressed[0] = false; keyPressed[1] = false; keyPressed[2] = false; keyPressed[3] = false;
//		addMotionSupport();
	}
	
	public void init() {
		addMotionSupport();
	}

	public void addAction(String name, Direction d)
	{
		ActionMap actionMap = slimeLabel.getActionMap();
		InputMap inputMap = slimeLabel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		// for key pressed action
		MotionAction pressedAction = new MotionAction(name, true, d);
		String pressedKey = PRESSED + name;
		KeyStroke pressedKeyStroke = KeyStroke.getKeyStroke(pressedKey);	
		inputMap.put(pressedKeyStroke, pressedKey);
		actionMap.put(pressedKey, pressedAction);

		// for key released action
		MotionAction releasedAction = new MotionAction(name, false, d);
		String releasedKey = RELEASED + name;
		KeyStroke releasedKeyStroke = KeyStroke.getKeyStroke(releasedKey);
		inputMap.put(releasedKeyStroke, releasedKey);
		actionMap.put(releasedKey, releasedAction);
	}
	
	private void computePressedKeyNum() {
		int i;
		pressedKeyNum = 0;
		for(i=0;i<4;i++) {
			if(keyPressed[i])
				pressedKeyNum ++;
		}
	}
	
	private Direction getCurrentDirection() {
		int i;
		for(i=0;i<4;i++) {
			if(keyPressed[i])
				return Direction.values()[i];
		}
		
		return direction;
	}

	private class MotionAction extends AbstractAction implements ActionListener
	{
		boolean pressed;
		Direction actionDirection;
		public MotionAction(String name, boolean p, Direction d) {
			super(name);
			pressed = p;
			actionDirection = d;
		}

		public void actionPerformed(ActionEvent e) {
			//handel key event
			if(status != Status.DEAD) {
				if(actionDirection != Direction.NONE) {
					if(pressed) {
						if(direction != actionDirection)
							turn(actionDirection);
						
						keyPressed[actionDirection.ordinal()] = true;
						computePressedKeyNum();
						isWalking = true;
					}
					else {
						Direction d = getCurrentDirection();
						if(direction != d)
							turn(d);		
						keyPressed[actionDirection.ordinal()] = false;
						computePressedKeyNum();
						if(pressedKeyNum == 0)
							isWalking = false;
					}
				}
				else if(status == Status.ALIVE && actionDirection == Direction.NONE && pressed)
					placeBomb();
			}
		}
	}

	
	public void addMotionSupport()
	{
		// MotionWithKeyBindings motion = new MotionWithKeyBindings(component);
		if(frame.players == 1 || (frame.players == 2 && index == 1))
			addAction("SPACE", Direction.NONE);
		else
			addAction("ENTER", Direction.NONE);
		
		if(index == 0) {
			addAction("LEFT", Direction.LEFT);
			addAction("RIGHT", Direction.RIGHT);
			addAction("UP", Direction.BACK);
			addAction("DOWN", Direction.FRONT);
		}

		else{
			addAction("A", Direction.LEFT);
			addAction("D", Direction.RIGHT);
			addAction("W", Direction.BACK);
			addAction("S", Direction.FRONT);
		}

	}	

}
