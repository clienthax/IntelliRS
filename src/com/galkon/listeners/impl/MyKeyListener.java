package com.galkon.listeners.impl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.galkon.ActionHandler;
import com.galkon.Main;


public class MyKeyListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		boolean control = event.isControlDown();
		switch (key) {
			case KeyEvent.VK_S:
				if (control) {
					Main.getInstance().updateArchive(Main.interfaces);
				}
				break;
			case KeyEvent.VK_O:
				if (control) {
					Main.getInstance().selectInterface(ActionHandler.openInterface());
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
			case KeyEvent.VK_L:
				if (control){
					ActionHandler.toggleLock();
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
