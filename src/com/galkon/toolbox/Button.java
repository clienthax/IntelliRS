package com.galkon.toolbox;

import com.galkon.Main;
import com.galkon.graphics.DrawingArea;
import com.galkon.graphics.RSImage;
import com.galkon.graphics.font.RealFont;
import com.galkon.toolbox.Toolbox.Alignment;
import com.galkon.toolbox.action.BoxComponentAction;

public class Button implements BoxComponent {

	/**
	 * Creates the new button.
	 * @param icon
	 * @param tooltip
	 * @param action
	 */
	public Button(String name, String[] tooltip, int icon, BoxComponentAction action) {
		this.name = name;
		this.tooltip = tooltip;
		this.icon = new RSImage("buttonicons/" + icon + ".png");
		this.action = action;
		this.loaded = false;
		create();
	}

	@Override
	public void draw() {
		if (parent == null) {
			return;
		}
		int offsetX = parent.getX();
		int offsetY = parent.getY();
		int x = getX() + offsetX;
		int y = getY() + offsetY;
		images[isSelected() ? 1 : (isHovered() ? 2 : 0)].drawARGBImage(x, y);
		if (icon != null) {
			icon.drawARGBImage(x + (getWidth() / 2) - (icon.myWidth / 2) + 1, y + (getHeight() / 2) - (icon.myHeight / 2) + 1);
		}
		if (isHovered()) {
			x = (getX() + offsetX) + getWidth() + 10;
			y = (getY() + offsetY) + 2;
			if (parent.getAlignment() == Toolbox.Alignment.HORIZONTAL) {
				x = (getX() + offsetX) + 2;
				y = (getY() + offsetY) + getHeight() + 10;
			}
			int width = 0;
			int height = tooltip.length * 13 + 5 + 15;
			RealFont font = Main.getInstance().arial[0];
			for (String s : tooltip) {
				if (font.getTextWidth(s) > width) {
					width = font.getTextWidth(s);
				}
			}
			width += 8;
			if (x + width > Main.getInstance().getCanvasWidth()) {
				x -= (parent.getAlignment() == Alignment.VERTICAL ? parent.getWidth() : 0) + width + 10;
			}
			if (y + height > Main.getInstance().getCanvasHeight()) {
				y -= parent.getHeight() + height + 10;
			}
			DrawingArea.drawRoundedRectangle(x, y, width, height, 0, 175, true, false);
			Main.getInstance().arial[1].drawString(name, x + 4, y + 13, 0xFFFFFF, true);
			for (int index = 0; index < tooltip.length; index++) {
				font.drawString(tooltip[index], x + 4, y + 12 + 15 + (index * 13), 0xFFFFFF, true);
			}
		}
	}

	/**
	 * Creates the button images.
	 */
	public void create() {
		try {
			images = new RSImage[3];
			images[0] = new RSImage("button.png");
			images[1] = new RSImage("button_clicked.png");
			images[2] = new RSImage("button_hovered.png");
			loaded = true;
		} catch (Exception e) {
			loaded = false;
			System.out.println("An error occurred while loading the component: " + this);
			e.printStackTrace();
		}
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		// TODO Get the width of the button.
		return 35;
	}

	@Override
	public int getHeight() {
		// TODO Get the height of the button.
		return 35;
	}

	@Override
	public String toString() {
		return "[BoxComponent:Button] name: " + name + ", width: " + getWidth() + ", height: " + getHeight();
	}

	/**
	 * Sets whether or not the button is selected (clicked).
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Returns whether or not the button is selected (clicked).
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets whether or not the button is hovered.
	 * @param hovered
	 */
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	/**
	 * Returns whether or not the button is hovered.
	 * @return
	 */
	public boolean isHovered() {
		return hovered;
	}

	@Override
	public void setParent(Toolbox parent) {
		this.parent = parent;
	}

	/**
	 * Gets the action for the button.
	 * @return
	 */
	public BoxComponentAction getAction() {
		return action;
	}

	public int x;
	public int y;
	public boolean selected;
	public boolean hovered;
	public boolean loaded;
	public RSImage[] images;
	public String name;
	public RSImage icon;
	public String[] tooltip;
	public BoxComponentAction action;
	public Toolbox parent;

}
