package com.galkon.toolbox;


public abstract interface BoxComponent {

	/**
	 * Draws the box component.
	 */
	public abstract void draw();

	/**
	 * Sets the x position of the component.
	 * @param x
	 */
	public abstract void setX(int x);

	/**
	 * Sets the y position of the component.
	 * @param y
	 */
	public abstract void setY(int y);

	/**
	 * Gets the x position of the component.
	 * @return
	 */
	public abstract int getX();

	/**
	 * Gets the y position of the component.
	 * @return
	 */
	public abstract int getY();

	/**
	 * Gets the width of the box component.
	 * @return
	 */
	public abstract int getWidth();

	/**
	 * Gets the height of the box component.
	 * @return
	 */
	public abstract int getHeight();

	/**
	 * Turns the box component variables into a string.
	 * @return
	 */
	public abstract String toString();

	/**
	 * Sets the parent of the component.
	 * @param toolbox
	 */
	public abstract void setParent(Toolbox toolbox);

}
