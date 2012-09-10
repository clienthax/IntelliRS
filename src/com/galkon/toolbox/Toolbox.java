package com.galkon.toolbox;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.galkon.Main;
import com.galkon.graphics.DrawingArea;
import com.galkon.graphics.RSImage;
import com.galkon.listeners.ClickType;

public class Toolbox {

	/**
	 * Draws the toolbox.
	 */
	public void draw() {
		DrawingArea.drawFilledPixels(getX(), getY(), getWidth(), getHeight(), 0xD3D3D3);
		DrawingArea.drawUnfilledPixels(getX(), getY() + 10, getWidth(), getHeight() - 10, 0x737373);
		DrawingArea.drawVerticalGradient(getX(), getY(), getWidth(), 10, 0x353535, 0x1c1c1c);
		align.drawARGBImage(getX() + 2, getY() + 2, alignAlpha);
		int x = getAlignment() == Alignment.HORIZONTAL ? 5 : 0;
		int y = 15;
		for (BoxComponent component : components) {
			if (getAlignment() == Alignment.VERTICAL) {
				x = (getWidth() / 2) - (component.getWidth() / 2);
			}
			component.setX(x);
			component.setY(y);
			component.draw();
			if (getAlignment() == Alignment.VERTICAL) {
				y += component.getHeight() + 3;
			} else {
				x += component.getWidth() + 3;
			}
		}
	}

	/**
	 * Processes the toolbox input.
	 */
	public void processInput(Main main) {
		if (main.mouseInRegion(getX(), getX() + getWidth(), getY(), getY() + 10)) {
			if (main.clickType == ClickType.LEFT_DRAG) {
				dragging = true;
			} else {
				dragging = false;
			}
		}
		if (dragging && main.clickType != ClickType.RELEASED) {
			x = main.mouseX - (getWidth() / 2);
			y = main.mouseY - 5;
			checkPosition();
		}
		if (main.mouseInRegion(getX() + 2, getX() + 2 + align.myWidth, getY() + 2, getY() + 2 + align.myHeight)) {
			alignAlpha += alignAlpha < 255 ? 20 : 0;
			if (alignAlpha > 255) {
				alignAlpha = 255;
			}
		} else {
			alignAlpha -= alignAlpha > 75 ? 20 : 0;
			if (alignAlpha < 75) {
				alignAlpha = 75;
			}
		}
		if (main.clickType == ClickType.LEFT_CLICK && main.clickInRegion(getX() + 2, getX() + 2 + align.myWidth, getY() + 2, getY() + 2 + align.myHeight)) {
			switchAlignment();
			main.resetClick();
		}
		if (main.mouseInRegion(getX(), getX() + getWidth(), getY(), getY() + getHeight())) {
			for (BoxComponent component : components) {
				if (main.mouseInRegion(getX() + component.getX(), getX() + component.getX() + component.getWidth(), getY() + component.getY(), getY() + component.getY() + component.getHeight())) {
					if (component instanceof Button) {
						((Button) component).setHovered(true);
					}
				} else {
					if (component instanceof Button) {
						((Button) component).setHovered(false);
					}
				}
				if (main.clickType == ClickType.LEFT_CLICK) {
					if (main.clickInRegion(getX() + component.getX(), getX() + component.getX() + component.getWidth(), getY() + component.getY(), getY() + component.getY() + component.getHeight())) {
						if (component instanceof Button) {
							clickButton(main, component);
						}
					}
				}
			}
		} else {
			exit();
		}
	}

	public void checkPosition() {
		if (x < 0) {
			x = 0;
		} else if (x + getWidth() > Main.getInstance().getCanvasWidth()) {
			x = Main.getInstance().getCanvasWidth() - getWidth();
		}
		if (y < 0) {
			y = 0;
		} else if (y + getHeight() > Main.getInstance().getCanvasHeight()) {
			y = Main.getInstance().getCanvasHeight() - getHeight();
		}
	}

	/**
	 * Clicks the specified button.
	 * @param main
	 * @param button
	 */
	public void clickButton(Main main, BoxComponent component) {
		for (BoxComponent c : components) {
			if (c instanceof Button) {
				if (component == c) {
					((Button) c).setSelected(true);
					main.toolAction = ((Button) c).getAction();
				} else {
					((Button) c).setSelected(false);
				}
			}
		}
	}

	/**
	 * Sets the box to default mode for when the mouse exits.
	 */
	public void exit() {
		for (BoxComponent c : components) {
			if (c instanceof Button) {
				((Button) c).setHovered(false);
			}
		}
	}

	/**
	 * Initializes the toolbox.
	 */
	public Toolbox() {
		components = new ArrayList<BoxComponent>();
		align = new RSImage("alignment.png");
		alignAlpha = 75;
	}

	/**
	 * Adds a component to the toolbox.
	 * @param comp
	 */
	public void add(BoxComponent comp) {
		components.add(comp);
		comp.setParent(this);
	}

	/**
	 * Adds a separator in the menu.
	 */
	public void addSeparator() {
		add(new Separator());
	}

	/**
	 * Removes a component frmo the toolbox.
	 * @param comp
	 */
	public void remove(BoxComponent comp) {
		components.remove(comp);
	}

	/**
	 * Removes a component from the specified index.
	 * @param index
	 */
	public void removeAt(int index) {
		components.remove(index);
	}

	/**
	 * Gets the list of components.
	 * @return
	 */
	public ArrayList<BoxComponent> getComponents() {
		return components;
	}

	/**
	 * The list of components.
	 */
	public ArrayList<BoxComponent> components;

	/**
	 * Switches the alignment of the toolbox.
	 */
	public void switchAlignment() {
		if (getAlignment() == Alignment.VERTICAL) {
			setAlignment(Alignment.HORIZONTAL);
		} else {
			setAlignment(Alignment.VERTICAL);
		}
		checkPosition();
	}

	/**
	 * Sets the alignment.
	 * @param alignment
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * Gets the alignment.
	 * @return
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * The alignment of the toolbox.
	 */
	public Alignment alignment;

	/**
	 * The types of alignment the box can have.
	 * @author Galkon
	 */
	public enum Alignment {
		VERTICAL, HORIZONTAL
	}

	/**
	 * Repositions the toolbox.
	 * @param x
	 * @param y
	 */
	public void reposition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the x value for the toolbox.
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y value for the toolbox.
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the width of the toolbox.
	 * @return
	 */
	public int getWidth() {
		int width = this.width;
		if (!components.isEmpty() && getAlignment() == Alignment.HORIZONTAL) {
			for (BoxComponent comp : components) {
				if (comp.getX() + comp.getWidth() > width) {
					width = comp.getX() + comp.getWidth() + 5;
				}
			}
			width = 278;
		}
		return width;
	}

	/**
	 * Gets the height of the toolbox.
	 * @return
	 */
	public int getHeight() {
		int height = this.height;
		if (!components.isEmpty() && getAlignment() == Alignment.VERTICAL) {
			for (BoxComponent c : components) {
				if (c.getY() + c.getHeight() > height) {
					height = c.getY() + c.getHeight();
				}
			}
			height = 283;
		}
		return height + 5;
	}

	/**
	 * Gets the bounds of the toolbox.
	 * @return
	 */
	public Rectangle getBounds() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public int x = 4;
	public int y = 4;
	public int width = 47;
	public int height = 50;
	public RSImage align;
	public int alignAlpha;
	public boolean dragging = false;

}
