package com.galkon.toolbox;

import com.galkon.graphics.DrawingArea;

public class Separator implements BoxComponent {

	@Override
	public void draw() {
		if (parent == null) {
			return;
		}
		x += parent.getX();
		y += parent.getY();
		if (parent.getAlignment() == Toolbox.Alignment.VERTICAL) {
			DrawingArea.drawHorizontalLine(getX(), getY(), getWidth(), 0x8D8D8D);
			DrawingArea.drawHorizontalLine(getX() + 1, getY() + 1, getWidth() - 2 , 0xE4E4E4);
		} else {
			DrawingArea.drawVerticalLine(getX(), getY(), getHeight(), 0x8D8D8D);
			DrawingArea.drawVerticalLine(getX() + 1, getY() + 1, getHeight() - 2 , 0xE4E4E4);
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
		return parent.getAlignment() == Toolbox.Alignment.HORIZONTAL ? 2 : 35;
	}

	@Override
	public int getHeight() {
		return parent.getAlignment() == Toolbox.Alignment.HORIZONTAL ? 35 : 2;
	}

	@Override
	public void setParent(Toolbox parent) {
		this.parent = parent;
	}

	public int x;
	public int y;
	public Toolbox parent;

}
