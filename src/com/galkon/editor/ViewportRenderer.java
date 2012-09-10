package com.galkon.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.galkon.Main;
import com.galkon.Settings;
import com.galkon.constants.Constants;
import com.galkon.graphics.DrawingArea;
import com.galkon.graphics.RSImage;
import com.galkon.rsinterface.RSInterface;


public class ViewportRenderer {

	public Main instance;

	public ViewportRenderer(Main instance) {
		this.instance = instance;
	}
	
	/**
	 * Displays the interface.
	 */
	public void render() {
		if (instance == null) {
			return;
		}
		DrawingArea.drawFilledAlphaPixels(0, 0, instance.getCanvasWidth(), instance.getCanvasHeight(), Constants.BACKGROUND_COLOR, 256);
		if (Main.currentId != -1 && Main.getInterface() != null) {
			instance.drawInterface(Main.getInterface(), 0, 0, 0);
			if (Main.hoverId != -1 && Settings.displayHover) {
				DrawingArea.drawFilledAlphaPixels(Main.getX(Main.getInterface(), Main.getHovered()), Main.getY(Main.getInterface(), Main.getHovered()), Main.getHovered().width, Main.getHovered().height, 0xffffff, 50);
				DrawingArea.drawUnfilledPixels(Main.getX(Main.getInterface(), Main.getHovered()), Main.getY(Main.getInterface(), Main.getHovered()), Main.getHovered().width, Main.getHovered().height, 0xffffff);
			}
			if (Main.selectedId != -1) {
				Main.getInstance().childActions[5] = Main.getSelected().locked ? "Unlock" : "Lock";
				int x = -1;
				int y = -1;
				RSInterface child = null;
				if (Main.getInterface().children != null) {
					for (int index = 0; index < Main.getInterface().children.size(); index++) {
						if (Main.getInterface().children.get(index) == Main.selectedId) {
							child = RSInterface.getInterface(Main.getInterface().children.get(index));
							x = Main.getInterface().childX.get(index) + child.drawOffsetX;
							y = Main.getInterface().childY.get(index) + child.drawOffsetY;
							break;
						} else {
							if (RSInterface.getInterface(Main.getInterface().children.get(index)).children != null) {
								child = RSInterface.getInterface(Main.getInterface().children.get(index));
								for (int childIndex = 0; childIndex < child.children.size(); childIndex++) {
									if (child.children.get(childIndex) == Main.selectedId) {
										child =RSInterface.getInterface(child.children.get(childIndex));
										x = Main.getInterface().childX.get(index) + child.childX.get(childIndex) + child.drawOffsetX;
										y = Main.getInterface().childY.get(index) + child.childY.get(childIndex) + child.drawOffsetY;
										break;
									}
								}
							}
						}
					}
				}
				if (child != null) {
					int color = 0xff00ff;
					int alpha = 50;
					DrawingArea.drawFilledAlphaPixels(x, y, child.width, child.height, color, alpha);
					DrawingArea.drawUnfilledPixels(x, y, child.width, child.height, color);
				}
			}
		}
		showLockedChildren();
		drawSelectionArea();
		if (Main.currentId != -1 && Main.getInterface() != null) {
			drawSliders();
			if (instance.menuOpen) {
				instance.drawMenu(0, 0);
			}
		}
		if (Settings.displayData) {
			drawDataPane();
		}
		while (instance.strategy == null) {
			instance.strategy = instance.getBufferStrategy();
		}
		Graphics2D graphics = (Graphics2D) instance.strategy.getDrawGraphics();
		instance.imageProducer.drawGraphics(0, 0, graphics);
		drawGrid(graphics);
		debugMouse(graphics);
		if (instance.dull) {
			graphics.setColor(new Color(0, 0, 0, 200));
			graphics.fillRect(0, 0, instance.getCanvasWidth(), instance.getCanvasHeight());
		}
		graphics.dispose();
		instance.strategy.show();
	}

	/**
	 * Draws the multiple selection area.
	 */
	public void drawSelectionArea() {
		if (instance.multipleSelected()) {
			if (instance.selectionWidth < 0) {
				instance.selectionWidth *= -1;
				instance.selectionX -= instance.selectionWidth;
			}
			if (instance.selectionHeight < 0) {
				instance.selectionHeight *= -1;
				instance.selectionY -= instance.selectionHeight;
			}
			if (Main.movingSelection) {
				instance.glow(0);
			} else {
				instance.alpha[0] = 125;
			}
			DrawingArea.drawUnfilledAlphaPixels(instance.selectionX, instance.selectionY, instance.selectionWidth, instance.selectionHeight, 0x00FFFF, instance.alpha[0]);
			DrawingArea.drawFilledAlphaPixels(instance.selectionX, instance.selectionY, instance.selectionWidth, instance.selectionHeight, 0x00FFFF, instance.alpha[0]);
			RSInterface parent = Main.getInterface();
			for (RSInterface child : instance.getSelectedChildren()) {
				//RSInterface child = getSelectedChildren()[index];
				if (child != null) {
					DrawingArea.drawFilledAlphaPixels(Main.getX(parent, child), Main.getY(parent, child), child.width, child.height, 0, 125);
				}
			}
		}
	}

	/**
	 * Displays the mouse location and mouse information.
	 * @param g
	 */
	public void debugMouse(Graphics g) {
		if (Constants.DEBUG_MOUSE) {
			if (instance.mouseX != -1 && instance.mouseY != -1) {
				g.setColor(new Color(0, 255, 255));
				int x = instance.mouseX;
				int y = instance.mouseY;
				g.drawLine(x, 0, x, instance.getCanvasHeight());
				g.drawLine(0, y, instance.getCanvasWidth(), y);
			}
		}
	}

	/**
	 * Draws the grid sliders.
	 */
	public void drawSliders() {
		if (!Settings.displayGrid) {
			return;
		}
		int background = 0x151515;
		int bar = 0x666666;
		int thickness = 8;
		int start = 150;
		if (instance.mouseInRegion(0, instance.getCanvasWidth() - thickness, instance.getCanvasHeight() - thickness, instance.getCanvasHeight())) {
			if (instance.alpha[2] < 50) {
				instance.alpha[2] += 5;
			}
		} else {
			if (instance.alpha[2] > 0) {
				instance.alpha[2] -= 5;
			}
		}
		if (instance.mouseInRegion(instance.getCanvasWidth() - thickness, instance.getCanvasWidth(), 0, instance.getCanvasHeight() - thickness)) {
			if (instance.alpha[3] < 50) {
				instance.alpha[3] += 5;
			}
		} else {
			if (instance.alpha[3] > 0) {
				instance.alpha[3] -= 5;
			}
		}
		DrawingArea.drawRoundedRectangle(0, instance.getCanvasHeight() - thickness, instance.getCanvasWidth() - thickness, thickness, background, start + instance.alpha[2], true, false);
		DrawingArea.drawRoundedRectangle(instance.getCanvasWidth() - thickness, 0, thickness, instance.getCanvasHeight() - thickness, background, start + instance.alpha[3], true, false);
		DrawingArea.drawRoundedRectangle(Main.horizontalPos - (instance.getSliderWidth() / 2), instance.getCanvasHeight() - thickness, instance.getSliderWidth(), thickness, bar, start + (instance.alpha[2] * 2), true, false);
		DrawingArea.drawRoundedRectangle(instance.getCanvasWidth() - thickness, Main.verticalPos - (instance.getSliderHeight() / 2), thickness, instance.getSliderHeight(), bar, start + (instance.alpha[3] * 2), true, false);
	}

	/**
	 * Displays the grid.
	 */
	public void drawGrid(Graphics g) {
		if (!Settings.displayGrid) {
			return;
		}
		g.setColor(new Color(255, 255, 255, 15));
		int width = instance.getCanvasWidth();
		int height = instance.getCanvasHeight();
		int horizontalCount = width / (Main.horizontalScale != 0 ? Main.horizontalScale : 1);
		int verticalCount = height / (Main.verticalScale != 0 ? Main.verticalScale : 1);
		for (int index = 0, x = 0; index < horizontalCount + 1; index++, x += Main.horizontalScale) {
			g.drawLine(x, 0, x, height);
		}
		for (int index = 0, y = 0; index < verticalCount + 1; index++, y += Main.verticalScale) {
			g.drawLine(0, y, width, y);
		}
	}

	/**
	 * Draws the data pane.
	 */
	public void drawDataPane() {
		String[] names = { "instance.currentId:", "instance.selectedId:", "selectedX:", "selectedY:", "locked:", "hoverId:" };
		Object[] values = { Main.currentId, Main.selectedId, Main.getSelectedX(), Main.getSelectedY(), Main.getSelected() != null ? Main.getSelected().locked : false, Main.hoverId };
		int width = 80;
		int height = (names.length * 12) + 2;
		int alpha = 100;
		int x = 5;
		int y = instance.getCanvasHeight() - (height + 5);
		for (int index = 0; index < names.length; index++) {
			if (instance.small == null) {
				return;
			}
			if (instance.small.getTextWidth(names[index] + " " + values[index]) > width) {
				width = instance.small.getTextWidth(names[index] + " " + values[index]);
			}
		}
		width += 5;
		DrawingArea.drawRoundedRectangle(x, y, width, height, 0, alpha, true, true);
		DrawingArea.drawRoundedRectangle(x, y, width, height, 0xFFFFFF, alpha + 50, false, true);
		for (int index = 0; index < names.length; index++, y += 11) {
			instance.arial[0].drawString(names[index] + " " + values[index], x + 5, y + 12, instance.arialColor, true);
		}
	}

	/**
	 * Displays the children on the interface that are locked.
	 */
	public void showLockedChildren() {
		if (Main.getInterface() == null || Main.getInterface().children == null) {
			return;
		}
		for (int index = 0; index < Main.getInterface().children.size(); index++) {
			RSInterface child = RSInterface.getInterface(Main.getInterface().children.get(index));
			if (child.locked) {
				DrawingArea.drawFilledAlphaPixels(Main.getX(Main.getInterface(), child), Main.getY(Main.getInterface(), child), child.width, child.height, 0, 150);
				DrawingArea.drawUnfilledPixels(Main.getX(Main.getInterface(), child), Main.getY(Main.getInterface(), child), child.width, child.height, 0);
				RSImage lock = new RSImage("lock.png");
				lock.drawCenteredARGBImage(Main.getX(Main.getInterface(), child) + (child.width / 2), Main.getY(Main.getInterface(), child) + (child.height / 2));
			}
		}
	}

}
