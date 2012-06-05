package rs2.listeners.impl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import rs2.ActionHandler;
import rs2.Main;
import rs2.editor.RSInterface;

public class MyKeyListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		boolean control = event.isControlDown();
		switch (key) {
			case KeyEvent.VK_S:
				if (control) {
					RSInterface.save();
				}
				break;
			case KeyEvent.VK_E:
			case KeyEvent.VK_ALT:
				if (control && key == KeyEvent.VK_E) {
					ActionHandler.edit(Main.getSelected());
				} else {
					ActionHandler.edit(Main.getSelected());
				}
				break;
			case KeyEvent.VK_TAB:
				Main.selectNextChild();
				break;
			case KeyEvent.VK_LEFT:
				ActionHandler.moveSelectedX(-1);
				break;
			case KeyEvent.VK_RIGHT:
				ActionHandler.moveSelectedX(1);
				break;
			case KeyEvent.VK_UP:
				ActionHandler.moveSelectedY(-1);
				break;
			case KeyEvent.VK_DOWN:
				ActionHandler.moveSelectedY(1);
				break;
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_EQUALS:
				if (control && Main.getInstance().zoom < 250) {
					Main.getInstance().zoom += 10;
				}
				break;
			case KeyEvent.VK_MINUS:
				if (control && Main.getInstance().zoom > 100) {
					Main.getInstance().zoom -= 10;
					Main.scaledX = 0;
					Main.scaledY = 0;
				}
				break;
			case KeyEvent.VK_L:
				if (control){
					if (Main.getSelected() != null) {
						Main.getSelected().locked = !Main.getSelected().locked;
					}
				}
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {	
	}

}
