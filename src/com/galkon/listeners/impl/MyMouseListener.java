package com.galkon.listeners.impl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import com.galkon.Main;
import com.galkon.listeners.ClickType;


public class MyMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	public Main instance = null;

	public MyMouseListener(Main instance) {
		this.instance = instance;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
		setType(ClickType.EXITED);
		updateMouse();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (clickType == ClickType.LEFT_CLICK) {
			//return;
		}
		idleTime = 0;
		clickX = e.getX();
		clickY = e.getY();
		long oldTime = clickTime;
		clickTime = System.currentTimeMillis();
		if (e.isControlDown()) {
			setType(isRightButton(e) ? ClickType.CTRL_RIGHT : ClickType.CTRL_LEFT);
		} else {
			if (clickTime - oldTime < 250) {
				setType(ClickType.DOUBLE);
			} else {
				setType(e.isMetaDown() ? ClickType.RIGHT_CLICK : ClickType.LEFT_CLICK);
			}
		}
		updateMouse();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		idleTime = 0;
		clickX = -1;
		clickY = -1;
		setType(ClickType.RELEASED);
		updateMouse();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		idleTime = 0;
		mouseX = e.getX();
		mouseY = e.getY();
		if (e.isControlDown() && !isRightButton(e)) {
			setType(ClickType.CTRL_DRAG);
		} else {
			setType(isRightButton(e) ? ClickType.RIGHT_DRAG : ClickType.LEFT_DRAG);
		}
		updateMouse();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		idleTime = 0;
		mouseX = e.getX();
		mouseY = e.getY();
		setType(ClickType.MOVED);
		updateMouse();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		if (e.isControlDown()) {
			instance.zoom -= rotation > 0 ? 10 : -10;
			Main.scaledX = 0;
			Main.scaledY = 0;
		}
	}


	/**
	 * Is the MouseEvent from a right click?
	 * @param e
	 * @return
	 */
	public boolean isRightButton(MouseEvent e) {
		return SwingUtilities.isRightMouseButton(e);
	}

	public void updateMouse() {
		instance.updateMouse(mouseX, mouseY, clickX, clickY, idleTime, clickTime, clickType);
	}

	public ClickType clickType;

	public void setType(ClickType clickType) {
		this.clickType = clickType;
	}

	public int mouseX;
	public int mouseY;
	public int clickX;
	public int clickY;
	public int idleTime;
	public long clickTime;
}
