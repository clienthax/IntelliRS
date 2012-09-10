package com.galkon;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;

import javax.swing.JOptionPane;

import com.galkon.cache.Archive;
import com.galkon.cache.Cache;
import com.galkon.cache.CacheIndice;
import com.galkon.cache.media.MediaArchive;
import com.galkon.constants.Constants;
import com.galkon.editor.InputListener;
import com.galkon.editor.ViewportRenderer;
import com.galkon.graphics.DrawingArea;
import com.galkon.graphics.RSImage;
import com.galkon.graphics.RSImageProducer;
import com.galkon.graphics.font.RSFont;
import com.galkon.graphics.font.RealFont;
import com.galkon.listeners.ClickType;
import com.galkon.listeners.impl.MyKeyListener;
import com.galkon.listeners.impl.MyMouseListener;
import com.galkon.rsinterface.Export;
import com.galkon.rsinterface.RSInterface;
import com.galkon.swing.UserInterface;
import com.galkon.swing.impl.JAGFileFilter;
import com.galkon.toolbox.Button;
import com.galkon.toolbox.Separator;
import com.galkon.toolbox.Toolbox;
import com.galkon.toolbox.action.BoxComponentAction;
import com.galkon.toolbox.action.impl.MarqueeAction;
import com.galkon.toolbox.action.impl.MoveAction;
import com.galkon.toolbox.action.impl.SelectAction;
import com.galkon.util.DataUtils;
import com.galkon.util.SwingUtils;


@SuppressWarnings("serial")
public class Main extends Canvas implements Runnable {

	/**
	 * Recompiles the specified archive and rebuilds the cache.
	 */
	public void updateArchive(Archive archive) {
		dull = true;
		CacheIndice indice = cache.getIndice(0);
		try {
			if (archive == interfaces) {
				byte[] interfaceData = RSInterface.getData();
				archive.updateFile(archive.indexOf("data"), interfaceData);
				byte[] data = archive.recompile();
				indice.updateFile(3, data);
			}
			if (archive == media) {
				byte[] data = media.recompile();
				indice.updateFile(4, data);
			}
			cache.rebuildCache();
			JOptionPane.showMessageDialog(this, "Archive repacked successfully.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "An error occurred while repacking the archive.");
			e.printStackTrace();
		}
		dull = false;
	}

	/**
	 * Replaces the specified archive with read data and rebuilds the cache.
	 */
	public void importArchive(Archive archive) {
		dull = true;
		CacheIndice indice = cache.getIndice(0);
		try {
			byte[] data = null;
			String location = SwingUtils.getFilePath("Select Archive", Constants.getExportDirectory(), false, new JAGFileFilter());
			if (location != null) {
				data = DataUtils.readFile(location);
			}
			if (archive == interfaces) {
				if (data != null) {
					indice.updateFile(3, data);
				}
			}
			if (archive == media) {
				if (data != null) {
					indice.updateFile(4, data);
				}
			}
			if (data != null) {
				cache.rebuildCache();
				JOptionPane.showMessageDialog(this, "Archive replaced successfully.");
			} else {
				JOptionPane.showMessageDialog(this, "An error occurred while replacing the archive.");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "An error occurred while replacing the archive.");
			e.printStackTrace();
		}
		dull = false;
	}

	/**
	 * Dumps the specified archive to the export folder.
	 */
	public void dumpArchive(Archive archive) {
		dull = true;
		try {
			String name = null;
			if (archive == interfaces) {
				name = "interface";
			}
			if (archive == media) {
				name = "media";
			}
			DataUtils.writeFile(Constants.getExportDirectory() + name + ".jag", archive.recompile());
			JOptionPane.showMessageDialog(this, "Archive dumped successfully.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "An error occurred while dumping the archive.");
			e.printStackTrace();
		}
		dull = false;
	}

	/**
	 * Gets the width of the interface area.
	 * @return
	 */
	public int getAreaWidth() {
		int width = 512;
		if (getInterface() != null) {
			width = getInterface().width;
		}
		return width;
	}

	/**
	 * Gets the height of the interface area.
	 * @return
	 */
	public int getAreaHeight() {
		int height = 338;
		if (getInterface() != null) {
			height = getInterface().height;
		}
		return height;
	}

	/**
	 * Gets the x position of the interface area.
	 * @return
	 */
	public int getAreaX() {
		return (getCanvasWidth() / 2) - (getAreaWidth() / 2);
	}

	/**
	 * Gets the y position of the interface area.
	 * @return
	 */
	public int getAreaY() {
		return (getCanvasHeight() / 2) - (getAreaHeight() / 2);
	}

	/**
	 * Draws the multiple selection area.
	 */
	public void drawSelectionArea() {
		if (multipleSelected()) {
			if (selectionWidth < 0) {
				selectionWidth *= -1;
				selectionX -= selectionWidth;
			}
			if (selectionHeight < 0) {
				selectionHeight *= -1;
				selectionY -= selectionHeight;
			}
			if (Main.movingSelection) {
				glow(0);
			} else {
				alpha[0] = 125;
			}
			DrawingArea.drawUnfilledAlphaPixels(selectionX, selectionY, selectionWidth, selectionHeight, 0x00FFFF, alpha[0]);
			DrawingArea.drawFilledAlphaPixels(selectionX, selectionY, selectionWidth, selectionHeight, 0x00FFFF, alpha[0]);
			RSInterface parent = getInterface();
			for (RSInterface child : getSelectedChildren()) {
				//RSInterface child = getSelectedChildren()[index];
				if (child != null) {
					DrawingArea.drawFilledAlphaPixels(getAreaX() + getX(parent, child), getAreaY() + getY(parent, child), child.width, child.height, 0, 125);
				}
			}
		}
	}

	/**
	 * Displays the interface area.
	 */
	public void drawInterfaceArea() {
		drawDropShadow(new Rectangle(getAreaX(), getAreaY(), getAreaWidth(), getAreaHeight()));
		int width = 8;
		int height = 8;
		boolean swap = false;
		for (int y = 0; y < getAreaHeight(); y += height) {
			for (int x = 0; x < getAreaWidth(); x += width * 2) {
				if (getAreaX() + x + width > getAreaX() + getAreaWidth()) {
					width -= (getAreaX() + x + width) - (getAreaX() + getAreaWidth());
				}
				if (getAreaY() + y + height > getAreaY() + getAreaHeight()) {
					height -= (getAreaY() + y + height) - (getAreaY() + getAreaHeight());
				}
				DrawingArea.drawFilledPixels(x + getAreaX(), y + getAreaY(), width, height, swap ? 0xCCCCCC : 0xFFFFFF);
			}
			for (int x = width; x < getAreaWidth(); x += width * 2) {
				if (getAreaX() + x + width > getAreaX() + getAreaWidth()) {
					width -= (getAreaX() + x + width) - (getAreaX() + getAreaWidth());
				}
				if (getAreaY() + y + height > getAreaY() + getAreaHeight()) {
					height -= (getAreaY() + y + height) - (getAreaY() + getAreaHeight());
				}
				DrawingArea.drawFilledPixels(x + getAreaX(), y + getAreaY(), width, height, swap ? 0xFFFFFF : 0xCCCCCC);
			}
			swap = !swap;
		}
		if (getInterface() != null) {
			drawInterface(getInterface(), getAreaX(), getAreaY(), 0);
		}
	}

	/**
	 * Draws the interface panel.
	 */
	public void draw() {
		DrawingArea.drawFilledPixels(0, 0, getCanvasWidth(), getCanvasHeight(), 0x494949);
		drawInterfaceArea();
		if (toolbox != null) {
			toolbox.draw();
			drawDropShadow(toolbox.getBounds());
		}
		drawSelectionArea();
		//the graphics buffer
		while (strategy == null) {
			strategy = getBufferStrategy();
		}
		Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();
		imageProducer.drawGraphics(0, 0, graphics);
		if (dull) {
			graphics.setColor(new Color(0, 0, 0, 200));
			graphics.fillRect(0, 0, getCanvasWidth(), getCanvasHeight());
		}
		graphics.dispose();
		strategy.show();
	}

	/**
	 * Draws the drop shadow behind the toolbox.
	 */
	public void drawDropShadow(Rectangle bounds) {
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();
		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();
		dropShadow[0].drawARGBImage(x - 4, y - 4);
		dropShadow[1].drawARGBImage(x + width - 4, y - 4);
		dropShadow[2].drawARGBImage(x + width - 4, y + height - 4);
		dropShadow[3].drawARGBImage(x - 4, y + height - 4);
		for (int index = 0; index < width - 8; index++) {
			dropShadow[4].drawARGBImage(x + 4 + index, y - 4);
			dropShadow[6].drawARGBImage(x + 4 + index, y + height);
		}
		for (int index = 0; index < height - 8; index++) {
			dropShadow[7].drawARGBImage(x - 4, y + 4 + index);
			dropShadow[5].drawARGBImage(x + width, y + 4 + index);
		}
	}

	/**
	 * Builds the toolbox and it's components.
	 */
	public void buildToolbox() {
		toolbox = new Toolbox();
		toolbox.setAlignment(Toolbox.Alignment.VERTICAL);
		String[] types = { "Text", "Tooltip box", "Pixels" };
		String[][] tooltips = {
			{"Create a new line of text or", "edit existing text."},
			{"Create a yellow tooltip box", "that will show up when hovering", "over the area you select."},
			{"Create a rectangle of pixels", "that can be filled, unfilled, and", "transparent."}
		};
		for (int index = 0; index < types.length; index++) {
			Button button = new Button(types[index], tooltips[index], index, null);
			toolbox.add(button);
		}
		toolbox.addSeparator();
		String[] tools = { "Select", "Move", "Marquee", "Trash" };
		tooltips = new String[][]{
			{"Select the item you choose without", "moving or editing it."},
			{"Move the item you choose (double click", "to select, or just drag to move)."},
			{"Select a rectangular area of the", "current interface."},
			{"Delete the item you choose."}
		};
		BoxComponentAction[] actions = { new SelectAction(), new MoveAction(), new MarqueeAction(), null };
		for (int index = 0; index < tools.length; index++) {
			Button button = new Button(tools[index], tooltips[index], index + types.length, actions[index]);
			if (index == 0) {
				button.setSelected(true);
			}
			toolbox.add(button);
		}
	}

	/**
	 * Processes the program input (mouse, text, etc).
	 */
	public void processInput() {
		if (toolbox != null) {
			toolbox.processInput(this);
		}
		processChildInteraction();
	}

	public void processChildInteraction() {
		RSInterface rsi = getInterface();
		if (rsi == null) {
			return;
		}
		int offsetY = 0;
		int _mouseX = mouseX;
		int _mouseY = mouseY;
		int _clickX = clickX;
		int _clickY = clickY;
		if (rsi.type != 0 || rsi.children == null || rsi.showInterface) {
			return;
		}
		if (_mouseX < getAreaX() || _mouseY < getAreaY() || _mouseX > getAreaX() + rsi.width || _mouseY > getAreaY() + rsi.height) {
			return;
		}
		if (_clickX < getAreaX() || _clickY < getAreaY() || _clickX > getAreaX() + rsi.width || _clickY > getAreaY() + rsi.height) {
			//return;
		}
		hoverId = -1;
		int childCount = rsi.children.size();
		boolean busy = false;
		for(int index = childCount - 1; index > 0; index--) {
			int posX = rsi.childX.get(index) + getAreaX();
			int posY = rsi.childY.get(index) - offsetY + getAreaY();
			RSInterface child = RSInterface.getInterface(rsi.children.get(index));
			posX += child.drawOffsetX;
			posY += child.drawOffsetY;
			if (!busy && mouseInRegion(posX, posX + child.width, posY, posY + child.height)) {
				hoverId = child.id;
				busy = true;
			}
			if (mouseInRegion(posX, posX + child.width, posY, posY + child.height)) {
				if (toolAction != null) {
					toolAction.perform(this);
				}
				break;
			}
		}
	}

	/**
	 * Is an area being selected or already selected?
	 * @return
	 */
	public boolean multipleSelected() {
		if (selectionX != -1 && selectionY != -1) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the font metrics.
	 * @param font
	 * @return
	 */
	public FontMetrics getMetrics(Font font) {
		if (font == null) {
			return null;
		}
		return getFontMetrics(font);
	}

	/**
	 * Sets the next child as the selected interface.
	 */
	public static void selectNextChild() {
		RSInterface rsi = getInterface();
		if (rsi.children != null) {
			if (getSelectedIndex() + 1 < rsi.children.size()) {
				selectChild(getSelectedIndex() + 1);
			} else {
				selectChild(0);
			}
		}
	}

	/**
	 * Returns the text for the type of interface.
	 * @param id
	 * @param type
	 * @return
	 */
	public static String getType(int id, int type) {
		switch(type) {
			case 0:
				return "parent";
			case 1:
				return "";
			case 2:
				return "item group";
			case 3:
				return "pixels: " + RSInterface.getInterface(id).width + "x" + RSInterface.getInterface(id).height;
			case 4:
				return "text: " + RSInterface.getInterface(id).disabledText;
			case 5:
				return "image";
			case 6:
				return "model";
			case 7:
				return "media";
			case 8:
				return "tooltip";
		}
		return null;
	}

	/**
	 * Gets the current interface.
	 * @return
	 */
	public static RSInterface getInterface() {
		if (currentId == -1) {
			return null;
		}
		return RSInterface.getInterface(currentId);
	}

	/**
	 * Gets the interface of the selected id.
	 * @return
	 */
	public static RSInterface getSelected() {
		if (selectedId == -1) {
			return null;
		}
		return RSInterface.getInterface(selectedId);
	}

	/**
	 * Gets the interface for the hovered id.
	 * @return
	 */
	public static RSInterface getHovered() {
		if (hoverId == -1) {
			return null;
		}
		return RSInterface.getInterface(hoverId);
	}

	/**
	 * Gets the selected child's index in the parent's children.
	 * @return
	 */
	public static int getSelectedIndex() {
		if (getSelected() == null) {
			return -1;
		}
		RSInterface parent = getInterface();
		if (parent.children == null) {
			return -1;
		}
		for (int index = 0; index < parent.children.size(); index++) {
			if (parent.children.get(index) == getSelected().id) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Gets the selected child's x-position.
	 * @return
	 */
	public static int getSelectedX() {
		if (selectedId == -1) {
			return 0;
		}
		return getX(getInterface(), getSelected());
	}

	/**
	 * Gets the selected child's y-position.
	 * @return
	 */
	public static int getSelectedY() {
		if (selectedId == -1) {
			return 0;
		}
		return getY(getInterface(), getSelected());
	}

	/**
	 * Gets the x-position of a child on the parent interface.
	 * @param parent The parent interface.
	 * @param child The child to find the x-position for.
	 * @return
	 */
	public static int getX(RSInterface parent, RSInterface child) {
		if (parent == null || parent.children == null || child == null) {
			return -1;
		}
		for (int index = 0; index < parent.children.size(); index++) {
			if (parent.children.get(index) == child.id) {
				return parent.childX.get(index);
			}
		}
		return -1;
	}

	/**
	 * Gets the y-position of a child on the parent interface.
	 * @param parent The parent interface.
	 * @param child The child to find the y-position for.
	 * @return
	 */
	public static int getY(RSInterface parent, RSInterface child) {
		if (parent == null || parent.children == null || child == null) {
			return -1;
		}
		for (int index = 0; index < parent.children.size(); index++) {
			if (parent.children.get(index) == child.id) {
				return parent.childY.get(index);
			}
		}
		return -1;
	}

	/**
	 * Selects the specified child index.
	 * @param index
	 */
	public static void selectChild(int index) {
		RSInterface rsi = getInterface();
		if (rsi != null) {
			if (rsi.children != null) {
				if (index < rsi.children.size()) {
					selectedId = rsi.children.get(index);
				}
			}
		}
	}

	/**
	 * Selects the specified id as the current interface.
	 * @param id
	 */
	public void selectInterface(int id) {
		selectedId = -1;
		currentId = id;
		verticalPos = getCanvasHeight() / 2;
		horizontalPos = getCanvasWidth() / 2;
		verticalScale = 10;
		horizontalScale = 10;
		getUI().buildTreePane();
	}

	/**
	 * Gets the horizontal slider width.
	 * @return
	 */
	public int getSliderWidth() {
		return getCanvasWidth() / 5;
	}

	/**
	 * Gets the vertical slider height.
	 * @return
	 */
	public int getSliderHeight() {
		return getCanvasHeight() / 5;
	}

	/**
	 * Calculates the x and y distance the mouse has been dragged from the original click.
	 * @return
	 */
	public int[] calculateDragDistance() {
		int[] distances = new int[2];
		int startX = clickX;
		int startY = clickY;
		int currentX = mouseX;
		int currentY = mouseY;
		if (startX < currentX) {
			distances[0] = +(currentX - startX);
		}
		if (startX > currentX) {
			distances[0] = -(startX - currentX);
		}
		if (startY < currentY) {
			distances[1] = +(currentY - startY);
		}
		if (startY > currentY) {
			distances[1] = -(startY - currentY);
		}
		return distances;
	}

	/**
	 * Resets the multiple selection area.
	 */
	public void resetSelection() {
		selectionX = -1;
		selectionY = -1;
		selectionWidth = 0;
		selectionHeight = 0;
		selectionLocked = false;
		movingSelection = false;
	}

	/**
	 * Returns an RSInterface array of the selected children.
	 * @return
	 */
	public RSInterface[] getSelectedChildren() {
		RSInterface parent = getInterface();
		RSInterface[] children = new RSInterface[parent.children.size()];
		int count = 0;
		for (int index = 0; index < children.length; index++) {
			RSInterface child = RSInterface.getInterface(parent.children.get(index));
			int childX = getAreaX() + getX(parent, child);
			int childY = getAreaY() + getY(parent, child);
			int childWidth = child.width;
			int childHeight = child.height;
			if (inArea(getSelectionArea(), new Rectangle(childX, childY, childWidth, childHeight))) {
				children[count] = child;
				count++;
			}
		}
		RSInterface[] _children = new RSInterface[count];
		for (int index = 0; index < count; index++) {
			_children[index] = children[index];
		}
		return _children;
	}

	/**
	 * Gets the selection area as a rectange.
	 * @return
	 */
	public Rectangle getSelectionArea() {
		return new Rectangle(selectionX, selectionY, selectionWidth, selectionHeight);
	}

	/**
	 * Returns the selected child's area as a rectange.
	 * @return
	 */
	public Rectangle getSelectedArea() {
		return new Rectangle(getSelectedX(), getSelectedY(), getSelected().width, getSelected().height);
	}

	/**
	 * Checks to see if the specified rectangle is within a region.
	 * @param in
	 * @param area
	 * @return
	 */
	public boolean inArea(Rectangle in, Rectangle area) {
		if (area.getX() >= in.getX() && area.getX() + area.getWidth() <= in.getX() + in.getWidth()
		&& area.getY() >= in.getY() && area.getY() + area.getHeight() <= in.getY() + in.getHeight()) {
			return true;
		}
		return false;
	}

	/**
	 * Adjusts the horizontal grid scale.
	 * @param newX
	 */
	public void adjustHorizontal(int newX) {
		if (getScale() != 1) {
			return;
		}
		if (newX == horizontalPos) {
			return;
		}
		int startX = horizontalPos;
		if (newX < (getSliderWidth() / 2)) {
			horizontalPos = (getSliderWidth() / 2);
			return;
		}
		if (newX > getCanvasWidth() - (getSliderWidth() / 2) - 8) {
			horizontalPos = getCanvasWidth() - (getSliderWidth() / 2) - 8;
			return;
		}
		horizontalPos = newX;
		int percent = (int) (((double) (horizontalPos - (getSliderWidth() / 2)) / (double) (getCanvasWidth() - (getSliderWidth() / 2) - 8)) * 100D);
		percent = (percent / 10) * 2;
		boolean left = startX - newX > 0 ? true : false;
		if (left) {
			if (horizontalScale > 1) {
				horizontalScale = percent > 0 ? percent : 1;
			}
		} else {
			if (horizontalScale < 20) {
				horizontalScale = percent < 20 ? percent : 20;
			}
		}
		adjustingGrid = true;
	}

	/**
	 * Adjusts the vertical grid scale.
	 * @param newX
	 */
	public void adjustVertical(int newX) {
		if (getScale() != 1) {
			return;
		}
		if (newX == verticalPos) {
			return;
		}
		int startX = verticalPos;
		if (newX < (getSliderHeight() / 2)) {
			verticalPos = (getSliderHeight() / 2);
			return;
		}
		if (newX > getCanvasHeight() - (getSliderHeight() / 2) - 8) {
			verticalPos = getCanvasHeight() - (getSliderHeight() / 2) - 8;
			return;
		}
		verticalPos = newX;
		int percent = (int) (((double) (verticalPos - (getSliderHeight() / 2)) / (double) (getCanvasHeight() - (getSliderHeight() / 2) - 8)) * 100D);
		percent = (percent / 10) * 2;
		boolean left = startX - newX > 0 ? true : false;
		if (left) {
			if (verticalScale > 1) {
				verticalScale = percent > 0 ? percent : 1;
			}
		} else {
			if (verticalScale < 20) {
				verticalScale = percent < 20 ? percent : 20;
			}
		}
		adjustingGrid = true;
	}

	public String[] childActions = { "Remove", "Move down", "Move up", "Move to back", "Move to front", "Lock", "Export", "Edit" };
	private String[] selectionActions = { "Remove", "Move", "Lock", "Unselect" };

	public String[] getActions() {
		String[][] actions = { childActions, selectionActions };
		if (menuActions == 1) {
			selectionActions[2] = selectionLocked ? "Unlock" : "Lock";
		}
		return actions[menuActions];
	}

	public void perform(int action) {
		menuOpen = false;
		switch (menuActions) {
			case 0:
				switch (action) {
					case 0:
						ActionHandler.removeSelected();
						break;
					case 1:
						ActionHandler.setZIndex(getSelectedIndex() - 1);
						break;
					case 2:
						ActionHandler.setZIndex(getSelectedIndex() + 1);
						break;
					case 3:
						ActionHandler.setZIndex(0);
						break;
					case 4:
						ActionHandler.setZIndex(getInterface().children.size() - 1);
						break;
					case 5:
						ActionHandler.toggleLock();
						break;
					case 6:
						new Export(getSelected());
						break;
					case 7:
						ActionHandler.edit(getSelected());
						break;
				}
				break;
			case 1:
				switch (action) {
				case 1:
					movingSelection = !movingSelection;
					break;
				case 2:
					ActionHandler.lockSelectedChildren(selectionActions[2].equalsIgnoreCase("lock") ? true : false);
					break;
				case 3:
					resetSelection();
					break;
				}
				break;
		}
	}

	/**
	 * Handles the "glowing" alpha values.
	 * @param index
	 */
	public void glow(int index) {
		if (alpha[index] < maximum[index] && increasing[index]) {
			alpha[index] += rate[index];
		}
		if (alpha[index] > maximum[index]) {
			alpha[index] = maximum[index];
		}
		if (alpha[index] == maximum[index]) {
			increasing[index] = false;
		}
		if (alpha[index] > minimum[index] && !increasing[index]) {
			alpha[index] -= rate[index];
		}
		if (alpha[index] <= minimum[index]) {
			increasing[index] = true;
			alpha[index] = minimum[index];
		}
	}

	/**
	 * Draws a scrollbar.
	 * @param x
	 * @param y
	 * @param height
	 * @param scrollPosition
	 * @param scrollMax
	 */
	private void drawScrollbar(int x, int y, int height, int scrollPosition, int scrollMax) {
		int barHeight = ((height - 32) * height) / scrollMax;
		if(barHeight < 8) {
			barHeight = 8;
		}
		int offsetY = ((height - 32 - barHeight) * scrollPosition) / (scrollMax - height);
		scrollBar1.drawImage(x, y);
		scrollBar2.drawImage(x, (y + height) - 16);
		DrawingArea.drawFilledPixels(x, y + 16, 16, height - 32, scrollBackground);
		DrawingArea.drawFilledPixels(x, y + 16 + offsetY, 16, barHeight, scrollFill);
		DrawingArea.drawVerticalLine(x, y + 16 + offsetY, barHeight, scrollLight);
		DrawingArea.drawVerticalLine(x + 1, y + 16 + offsetY, barHeight, scrollLight);
		DrawingArea.drawHorizontalLine(x, y + 16 + offsetY, 16, scrollLight);
		DrawingArea.drawHorizontalLine(x, y + 17 + offsetY, 16, scrollLight);
		DrawingArea.drawVerticalLine(x + 15, y + 16 + offsetY, barHeight, scrollDark);
		DrawingArea.drawVerticalLine(x + 14, y + 17 + offsetY, barHeight - 1, scrollDark);
		DrawingArea.drawHorizontalLine(x, y + 15 + offsetY + barHeight, 16, scrollDark);
		DrawingArea.drawHorizontalLine(x + 1, y + 14 + offsetY + barHeight, 15, scrollDark);
	}

	/**
	 * Updates the progress bar.
	 * @param string
	 * @param percent
	 */
	public void updateProgress(String string, int percent) {
        for(float f = progress; f < (float)percent; f = (float)((double)f + 0.29999999999999999D)) {
            displayProgress(string, (int)f);
        }
        progress = percent;
    }

	/**
	 * Displays the progress bar.
	 * @param string
	 * @param percent
	 */
	public void displayProgress(String string, int percent) {
		while (strategy == null) {
			createBufferStrategy(2);
			strategy = getBufferStrategy();
		}
		java.awt.Graphics2D graphics = (java.awt.Graphics2D) strategy.getDrawGraphics();
		int centerX = getCanvasWidth() / 2;
		int centerY = getCanvasHeight() / 2;
		int width = 300;
		int height = 30;
		int alpha = 150;
		int x = centerX - (width / 2);
		int y = centerY - (height / 2);
		int loaded = (width * percent) / 100;
		imageProducer.initDrawingArea();
		DrawingArea.drawFilledPixels(0, 0, getCanvasWidth(), getCanvasHeight(), Constants.BACKGROUND_COLOR);
		DrawingArea.drawRoundedRectangle(x, y, width, height, 0, alpha / 2, true, true);
		DrawingArea.drawRoundedRectangle(x, y, loaded, height, 0, alpha / 2, true, true);
		DrawingArea.drawRoundedRectangle(x, y, width, height, Constants.TEXT_COLOR, alpha, false, true);
		arial[1].drawStringCenter(string, centerX, centerY + 5, 0xFFFFFF, true);
		imageProducer.drawGraphics(0, 0, graphics);
		graphics.dispose();
		strategy.show();
	}

	/**
	 * Returns the archive for the specified index.
	 * 1 - title
	 * 3 - interfaces
	 * 4 - media
	 * @param index
	 * @return
	 */
	private Archive getArchive(int index) {
		byte data[] = null;
		if(cache.getIndice(0) != null) {
			data = cache.getIndice(0).get(index);
		}
		if(data != null) {
			Archive archive = new Archive(data);
			return archive;
		}
		return null;
	}

	/**
	 * Does the specified action for the actionIndex.
	 * @param actionIndex
	 */
	public void doAction(int actionIndex) {
		if(actionIndex < 0) {
			return;
		}
		int id = menuActionCmd3[actionIndex];
		int action = menuActionID[actionIndex];
		if(action >= 2000) {
			action -= 2000;
		}
		if(action == 679 && !aBoolean1149) {
			aBoolean1149 = true;
		}
		if(action == 626) {
			RSInterface rsi = RSInterface.getInterface(id);
			String prefix = rsi.selectedActionName;
			if(prefix.indexOf(" ") != -1) {
				prefix = prefix.substring(0, prefix.indexOf(" "));
			}
			String suffix = rsi.selectedActionName;
			if(suffix.indexOf(" ") != -1) {
				suffix = suffix.substring(suffix.indexOf(" ") + 1);
			}
			return;
		}
		if(action == 646) {
			RSInterface rsi = RSInterface.getInterface(id);
			if(rsi.valueIndexArray != null && rsi.valueIndexArray[0][0] == 5) {
				int setting = rsi.valueIndexArray[0][1];
				if(variousSettings[setting] != rsi.requiredValues[0]) {
					variousSettings[setting] = rsi.requiredValues[0];
				}
			}
		}
		if(action == 169) {
			RSInterface rsi = RSInterface.getInterface(id);
			if(rsi.valueIndexArray != null && rsi.valueIndexArray[0][0] == 5) {
				int setting = rsi.valueIndexArray[0][1];
				variousSettings[setting] = 1 - variousSettings[setting];
			}
		}
	}

	private String interfaceIntToString(int val) {
		if(val < 0x3b9ac9ff) {
			return String.valueOf(val);
		} else {
			return "*";
		}
	}

	public void drawInterface(RSInterface rsi, int pos_x, int pos_y, int offsetY) {
		if(rsi.type != 0 || rsi.children == null) {
			return;
		}
		if(rsi.showInterface && anInt1026 != rsi.id && anInt1048 != rsi.id && anInt1039 != rsi.id) {
			return;
		}
		int startX = DrawingArea.startX;
		int startY = DrawingArea.startY;
		int endX = DrawingArea.endX;
		int endY = DrawingArea.endY;
		DrawingArea.setBounds(pos_x, pos_x + rsi.width, pos_y, pos_y + rsi.height);
		int children = rsi.children.size();
		for(int index = 0; index < children; index++) {
			int x = rsi.childX.get(index) + pos_x;
			int y = (rsi.childY.get(index) + pos_y) - offsetY;
			RSInterface child = RSInterface.getInterface(rsi.children.get(index));
			x += child.drawOffsetX;
			y += child.drawOffsetY;
			if(child.contentType > 0) {	
				//TODO: Content type.
			}
			if(child.type == 0) {
				if(child.scrollPosition > child.scrollMax - child.height)
					child.scrollPosition = child.scrollMax - child.height;
				if(child.scrollPosition < 0) {
					child.scrollPosition = 0;
				}
				drawInterface(child, x, y, child.scrollPosition);
				if(child.scrollMax > child.height) {
					drawScrollbar(x + child.width, y, child.height, child.scrollPosition, child.scrollMax);
				}
			} else {
				if(child.type != 1)
					if(child.type == 2) {
						int item_index = 0;
						for(int itemY = 0; itemY < child.height; itemY++) {
							for(int itemX = 0; itemX < child.width; itemX++) {
								int item_x = x + itemX * (32 + child.invSpritePadX);
								int item_y = y + itemY * (32 + child.invSpritePadY);
								if(item_index < 20) {
									item_x += child.spritesX[item_index];
									item_y += child.spritesY[item_index];
								}
								if(child.inventory[item_index] > 0) {
									if(item_x > DrawingArea.startX - 32 && item_x < DrawingArea.endX && item_y > DrawingArea.startY - 32 && item_y < DrawingArea.endY || activeInterfaceType != 0 && anInt1085 == item_index) {
										/*RSImage image = ItemDefinitions.getImage(itemId, child.inventoryAmount[item_index], color);
										if(image != null) {
											if(activeInterfaceType != 0 && anInt1085 == item_index && anInt1084 == child.id) {
												offset_x = mouseX - anInt1087;
												offset_y = mouseY - anInt1088;
												if(offset_x < 5 && offset_x > -5) {
													offset_x = 0;
												}
												if(offset_y < 5 && offset_y > -5) {
													offset_y = 0;
												}
												if(anInt989 < 5) {
													offset_x = 0;
													offset_y = 0;
												}
												image.drawImage(item_x + offset_x, item_y + offset_y, 128);
												if(item_y + offset_y < RSDrawingArea.startY && rsi.scrollPosition > 0) {
													int scrollPos = (anInt945 * (RSDrawingArea.startY - item_y - offset_y)) / 3;
													if(scrollPos > anInt945 * 10) {
														scrollPos = anInt945 * 10;
													}
													if(scrollPos > rsi.scrollPosition) {
														scrollPos = rsi.scrollPosition;
													}
													rsi.scrollPosition -= scrollPos;
													anInt1088 += scrollPos;
												}
												if(item_y + offset_y + 32 > RSDrawingArea.endY && rsi.scrollPosition < rsi.scrollMax - rsi.height) {
													int scrollPos = (anInt945 * ((item_y + offset_y + 32) - RSDrawingArea.endY)) / 3;
													if(scrollPos > anInt945 * 10) {
														scrollPos = anInt945 * 10;
													}
													if(scrollPos > rsi.scrollMax - rsi.height - rsi.scrollPosition) {
														scrollPos = rsi.scrollMax - rsi.height - rsi.scrollPosition;
													}
													rsi.scrollPosition += scrollPos;
													anInt1088 -= scrollPos;
												}
											} else {
												if(atInventoryInterfaceType != 0 && atInventoryIndex == item_index && atInventoryInterface == child.id) {
													image.drawImage(item_x, item_y, 128);
												} else {
													image.drawImage(item_x, item_y);
												}
											}
											if(image.maxWidth == 33 || child.inventoryAmount[item_index] != 1) {
												int amount = child.inventoryAmount[item_index];
												small.drawShadowedString(getAmountString(amount), item_x + offset_x, item_y + offset_y + 9, getAmountColor(amount), true);
											}
										}*/
									}
								} else
									if(child.sprites != null && item_index < 20) {
										RSImage image = child.sprites[item_index];
										if(image != null) {
											image.drawImage(item_x, item_y);
										}
									}
								item_index++;
							}
						}
					} else if(child.type == 3) {
						boolean hovered = false;
						if(anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id) {
							hovered = true;
						}
						int color;
						if(isEnabled(child)) {
							color = child.enabledColor;
							if(hovered && child.enabledHoverColor != 0) {
								color = child.enabledHoverColor;
							}
						} else {
							color = child.disabledColor;
							if(hovered && child.disabledHoverColor != 0) {
								color = child.disabledHoverColor;
							}
						}
						if(child.alpha == 0) {
							if(child.filled) {
								DrawingArea.drawFilledPixels(x, y, child.width, child.height, color);
							} else {
								DrawingArea.drawUnfilledPixels(x, y, child.width, child.height, color);
							}
						} else {
							if(child.filled) {
								DrawingArea.drawFilledAlphaPixels(x, y, child.width, child.height, color, 256 - (child.alpha & 0xff));
							} else {
								DrawingArea.drawUnfilledAlphaPixels(x, y, child.width, child.height, color, 256 - (child.alpha & 0xff));
								//RSDrawingArea.method338(y, child.height, 256 - (child.alpha & 0xff), color, child.width, x);
							}
						}
					} else if(child.type == 4) {
						RSFont[] fonts = { small, regular, bold, fancy };
						RSFont font = fonts[child.fontId];
						String text = child.disabledText;
						boolean hovered = hoverId == child.id;
						int color;
						if(isEnabled(child)) {
							color = child.enabledColor;
							if(hovered && child.enabledHoverColor != 0) {
								color = child.enabledHoverColor;
							}
							if(child.enabledText.length() > 0) {
								text = child.enabledText;
							}
						} else {
							color = child.disabledColor;
							if(hovered && child.disabledHoverColor != 0) {
								color = child.disabledHoverColor;
							}
						}
						if(child.actionType == 6 && aBoolean1149) {
							text = "Please wait...";
							color = child.disabledColor;
						}
						for(int textY = y + font.baseHeight; text.length() > 0; textY += font.baseHeight) {
							if(text.indexOf("%") != -1) {
								do {
									int valueIndex = text.indexOf("%1");
									if(valueIndex == -1)
										break;
									text = text.substring(0, valueIndex) + interfaceIntToString(extractValue(child, 0)) + text.substring(valueIndex + 2);
								} while(true);
								do {
									int valueIndex = text.indexOf("%2");
									if(valueIndex == -1)
										break;
									text = text.substring(0, valueIndex) + interfaceIntToString(extractValue(child, 1)) + text.substring(valueIndex + 2);
								} while(true);
								do {
									int valueIndex = text.indexOf("%3");
									if(valueIndex == -1)
										break;
									text = text.substring(0, valueIndex) + interfaceIntToString(extractValue(child, 2)) + text.substring(valueIndex + 2);
								} while(true);
								do {
									int valueIndex = text.indexOf("%4");
									if(valueIndex == -1)
										break;
									text = text.substring(0, valueIndex) + interfaceIntToString(extractValue(child, 3)) + text.substring(valueIndex + 2);
								} while(true);
								do {
									int valueIndex = text.indexOf("%5");
									if(valueIndex == -1)
										break;
									text = text.substring(0, valueIndex) + interfaceIntToString(extractValue(child, 4)) + text.substring(valueIndex + 2);
								} while(true);
							}
							int newLineIndex = text.indexOf("\\n");
							String finalText;
							if(newLineIndex != -1) {
								finalText = text.substring(0, newLineIndex);
								text = text.substring(newLineIndex + 2);
							} else {
								finalText = text;
								text = "";
							}
							if(child.centered) {
								font.drawCenteredString(finalText, x + child.width / 2, textY, color, child.shadowed);
							} else {
								font.drawShadowedString(finalText, x, textY, color, child.shadowed);
							}
						}
				} else if(child.type == 5) {
					RSImage sprite = null;
					if(isEnabled(child)) {
						sprite = child.enabledSprite;
					} else {
						sprite = child.disabledSprite;
					}
					if(sprite != null) {
						child.width = sprite.myWidth;
						child.height = sprite.myHeight;
						sprite.drawImage(x, y);
					}
				} else if(child.type == 6) {
					/*int k3 = Rasterizer.centerX;
					int j4 = Rasterizer.centerY;
					Rasterizer.centerX = x + child.width / 2;
					Rasterizer.centerY = y + child.height / 2;
					boolean enabled = isEnabled(child);
					int anim;
					if(enabled) {
						anim = child.enabledAnimation;
					} else {
						anim = child.disabledAnimation;
					}
					if(anim == -1) {
						//child.getAnimatedModel(-1, -1, enabled);
					}
					Rasterizer.centerX = k3;
					Rasterizer.centerY = j4;*/
				} else if(child.type == 7) {
					RSFont font = child.font;
					int k4 = 0;
					for(int j5 = 0; j5 < child.height; j5++) {
						for(int i6 = 0; i6 < child.width; i6++) {
							if(child.inventory[k4] > 0) {
								//ItemDefinitions itemDef = ItemDefinitions.getDefinition(child.inventory[k4] - 1);
								//String s2 = itemDef.name;
								//if(itemDef.stackable || child.inventoryAmount[k4] != 1)
								//	s2 = s2 + " x" + formatAmount(child.inventoryAmount[k4]);
								int i9 = x + i6 * (115 + child.invSpritePadX);
								int k9 = y + j5 * (12 + child.invSpritePadY);
								if(child.centered)
									font.drawCenteredString("", i9 + child.width / 2, k9, child.disabledColor, child.shadowed);
								else
									font.drawShadowedString("", i9, k9, child.disabledColor, child.shadowed);
							}
							k4++;
						}

					}
				} else if (child.type == 8 && hoverId == child.id) {
                    int boxWidth = 0;
					int boxHeight = 0;
					RSFont font = regular;
					for (String s1 = child.disabledText; s1.length() > 0;) {
						if (s1.indexOf("%") != -1) {
							do {
								int k7 = s1.indexOf("%1");
								if (k7 == -1)
									break;
								s1 = s1.substring(0, k7) + interfaceIntToString(extractValue(child, 0)) + s1.substring(k7 + 2);
							} while (true);
								do {
									int l7 = s1.indexOf("%2");
									if (l7 == -1)
										break;
									s1 = s1.substring(0, l7) + interfaceIntToString(extractValue(child, 1)) + s1.substring(l7 + 2);
								} while (true);
								do {
									int i8 = s1.indexOf("%3");
									if (i8 == -1)
										break;
									s1 = s1.substring(0, i8) + interfaceIntToString(extractValue(child, 2)) + s1.substring(i8 + 2);
								} while (true);
								do {
									int j8 = s1.indexOf("%4");
									if (j8 == -1)
										break;
									s1 = s1.substring(0, j8) + interfaceIntToString(extractValue(child, 3)) + s1.substring(j8 + 2);
								} while (true);
								do {
									int k8 = s1.indexOf("%5");
									if (k8 == -1)
										break;
									s1 = s1.substring(0, k8) + interfaceIntToString(extractValue(child, 4)) + s1.substring(k8 + 2);
								} while (true);
							}
							int l7 = s1.indexOf("\\n");
							String s4;
							if (l7 != -1) {
								s4 = s1.substring(0, l7);
								s1 = s1.substring(l7 + 2);
							} else {
								s4 = s1;
								s1 = "";
							}
							int j10 = font.getTextWidth(s4);
							if (j10 > boxWidth) {
								boxWidth = j10;
							}
							boxHeight += font.baseHeight + 1;
						}
						boxWidth += 6;
						boxHeight += 7;
						int xPos = (x + child.width) - 5 - boxWidth;
						int yPos = y + child.height + 5;
						if (xPos < x + 5)
							xPos = x + 5;
						if (xPos + boxWidth > pos_x + rsi.width)
							xPos = (pos_x + rsi.width) - boxWidth;
						if (yPos + boxHeight > offsetY + rsi.height)
							yPos = (y - boxHeight);
						DrawingArea.drawFilledPixels(xPos, yPos, boxWidth, boxHeight, 0xFFFFA0);
						DrawingArea.drawUnfilledPixels(xPos, yPos, boxWidth, boxHeight, 0);
						String s2 = child.disabledText;
						for (int j11 = yPos + font.baseHeight + 2; s2.length() > 0; j11 += font.baseHeight + 1) {
							if (s2.indexOf("%") != -1) {
								do {
									int k7 = s2.indexOf("%1");
									if (k7 == -1)
										break;
									s2 = s2.substring(0, k7) + interfaceIntToString(extractValue(child, 0)) + s2.substring(k7 + 2);
								} while (true);
								do {
									int l7 = s2.indexOf("%2");
									if (l7 == -1)
										break;
									s2 = s2.substring(0, l7) + interfaceIntToString(extractValue(child, 1)) + s2.substring(l7 + 2);
								} while (true);
								do {
									int i8 = s2.indexOf("%3");
									if (i8 == -1)
										break;
									s2 = s2.substring(0, i8) + interfaceIntToString(extractValue(child, 2)) + s2.substring(i8 + 2);
								} while (true);
								do {
									int j8 = s2.indexOf("%4");
									if (j8 == -1)
										break;
									s2 = s2.substring(0, j8) + interfaceIntToString(extractValue(child, 3)) + s2.substring(j8 + 2);
								} while (true);
								do {
									int k8 = s2.indexOf("%5");
									if (k8 == -1)
										break;
									s2 = s2.substring(0, k8) + interfaceIntToString(extractValue(child, 4)) + s2.substring(k8 + 2);
								} while (true);
							}
							int l11 = s2.indexOf("\\n");
							String s5;
							if (l11 != -1) {
								s5 = s2.substring(0, l11);
								s2 = s2.substring(l11 + 2);
							} else {
								s5 = s2;
								s2 = "";
							}
							if (child.centered) {
								font.drawCenteredString(s5, xPos + child.width / 2, yPos, 0, false);
							} else {
								if (s5.contains("\\r")) {
									String text = s5.substring(0, s5.indexOf("\\r"));
									String text2 = s5.substring(s5.indexOf("\\r") + 2);
									font.drawBasicString(text, xPos + 3, j11, 0);
									int rightX = boxWidth + xPos - font.getTextWidth(text2) - 2;
									font.drawBasicString(text2, rightX, j11, 0);
									System.out.println("Box: " + boxWidth + "");
								} else
									font.drawBasicString(s5, xPos + 3, j11, 0);
							}
						}
				}
			}
			if (selectedId == child.id) {
				DrawingArea.drawFilledAlphaPixels(x, y, child.width, child.height, 0xFF00FF, 50);
				DrawingArea.drawUnfilledPixels(x, y, child.width, child.height, 0xFF00FF);
			}
			if (hoverId == child.id && Settings.displayHover) {
				DrawingArea.drawFilledAlphaPixels(x, y, child.width, child.height, 0xFFFFFF, 50);
				DrawingArea.drawUnfilledPixels(x, y, child.width, child.height, 0xFFFFFF);
			}
		}
		DrawingArea.setBounds(startX, endX, startY, endY);
	}

	/**
	 * Draws the context menu.
	 * @param offsetX
	 * @param offsetY
	 */
	public void drawMenu(int offsetX, int offsetY) {
		int x = menuOffsetX - offsetX;
		int y = menuOffsetY - offsetY;
		int width = menuWidth;
		int height = menuHeight;
		DrawingArea.drawRoundedRectangle(x, y, width, height, 0, 220, true, false);
		DrawingArea.drawFilledAlphaPixels(x + 1, y + 1, width - 2, 16, 0x2C2C2C, 150);
		DrawingArea.drawRoundedRectangle(x, y, width, height, 0xffffff, 220, false, true);
		DrawingArea.drawHorizontalAlphaLine(x + 1, y + 17, width - 1, 0xFFFFFF, 220);
		arial[1].drawString("Choose Action", x + 3, y + 14, 0xFFFFFF, true);
		int _mouseX = mouseX - offsetX;
		int _mouseY = mouseY - offsetY;
		for(int action = 0; action < getActions().length; action++) {
			int posY = y + 31 + (getActions().length - 1 - action) * 15;
			int color = Constants.TEXT_COLOR;
			if(_mouseX > x && _mouseX < x + width && _mouseY > posY - 13 && _mouseY < posY + 3) {
				color = 0xFFFFFF;
				DrawingArea.drawFilledAlphaPixels(x + 2, posY - 13, menuWidth - 4, 16, 0x9F9F9F, 220);
			}
			arial[1].drawString(getActions()[action], x + 3, posY, color, true);
		}
	}

	/**
	 * Determines the context menu size.
	 */
	public void determineMenuSize() {
		int width = regular.getEffectTextWidth("Choose Action");
		for(int action = 0; action < getActions().length; action++) {
			int itemWwidth = bold.getEffectTextWidth(getActions()[action]);
			if(itemWwidth > width) {
				width = itemWwidth;
			}
		}
		width += 8;
		int height = 15 * getActions().length + 21;
		int startX =  0;
		int endX = getCanvasWidth();
		int startY = 0;
		int endY = getCanvasHeight();
		if(clickX > startX && clickY > startY && clickX < endX && clickY < endY) {
			int x = clickX - startX - width / 2;
			if(x + width > (endX - startX)) {
				x = (endX - startX) - width;
			}
			if(x < 0) {
				x = 0;
			}
			int y = clickY - startY;
			if(y + height > (endY - startY)) {
				y = (endY - startY) - height;
			}
			if(y < 0) {
				y = 0;
			}
			menuOpen = true;
			menuOffsetX = x;
			menuOffsetY = y;
			menuWidth = width;
			menuHeight = height;
		}
	}

	private int extractValue(RSInterface rsi, int valueIndex) {
		if(rsi.valueIndexArray == null || valueIndex >= rsi.valueIndexArray.length) {
			return -2;
		}
		try {
			int opcodes[] = rsi.valueIndexArray[valueIndex];
			int result = 0;
			int counter = 0;
			int type = 0;
			do {
				int opcode = opcodes[counter++];
				int value = 0;
				byte tempType = 0;
				switch (opcode) {
					case 0:
						return result;
					case 1:
						//returned = currentStats[valueArray[valuePointer++]];
						break;
					case 2:
						//returned = maxStats[valueArray[valuePointer++]];
						break;
					case 3:
						//returned = currentExp[valueArray[valuePointer++]];
						break;
					case 4:
					/*int k2 = valueArray[valuePointer++];
						if(k2 >= 0 && k2 < ItemDefinitions.totalItems && (!ItemDefinitions.getDefinition(k2).membersObject || isMembers)) {
							for(int j3 = 0; j3 < child.inventory.length; j3++) {
								if(child.inventory[j3] == k2 + 1) {
									returned += child.inventoryAmount[j3];
								}
							}
						}*/
						break;
					case 5:
						value = variousSettings[opcodes[counter++]];
						break;
					case 6:
						//returned = anIntArray1019[maxStats[valueArray[valuePointer++]] - 1];
						break;
					case 7:
						value = (variousSettings[opcodes[counter++]] * 100) / 46875;
						break;
					case 8:
						break;
					case 9:
						break;
					case 10:
						break;
					case 11:
						//returned = energy;
						break;
					case 12:
						//returned = weight;
						break;
					case 13:
						int i2 = variousSettings[opcodes[counter++]];
						int i3 = opcodes[counter++];
						value = (i2 & 1 << i3) == 0 ? 0 : 1;
						break;
					case 14:
						/*int j2 = valueArray[valuePointer++];
						VarBit varBit = VarBit.cache[j2];
						int l3 = varBit.anInt648;
						int i4 = varBit.anInt649;
						int j4 = varBit.anInt650;
						int k4 = anIntArray1232[j4 - i4];
						returned = variousSettings[l3] >> i4 & k4;*/
						break;
					case 15:
						tempType = 1;
						break;
					case 16:
						tempType = 2;
						break;
					case 17:
						tempType = 3;
						break;
					case 20:
						value = opcodes[counter++];
						break;

					default:
						break;
				}
				if(tempType == 0) {
					if(type == 0)
						result += value;
					if(type == 1)
						result -= value;
					if(type == 2 && value != 0)
						result /= value;
					if(type == 3)
						result *= value;
					type = 0;
				} else {
					type = tempType;
				}
			} while(true);
		} catch(Exception _ex) {
			return -1;
		}
	}


	private boolean isEnabled(RSInterface rsi) {
		if(rsi.valueCompareType == null) {
			return false;
		}
		if (Settings.forceEnabled) {
			return true;
		}
		for(int index = 0; index < rsi.valueCompareType.length; index++) {
			int value = extractValue(rsi, index);
			int required = rsi.requiredValues[index];
			if(rsi.valueCompareType[index] == 2) {
				if(value >= required) {
					return false;
				}
			} else {
				if(rsi.valueCompareType[index] == 3) {
					if(value <= required) {
						return false;
					}
				} else {
					if(rsi.valueCompareType[index] == 4) {
						if(value == required) {
							return false;
						}
					} else {
						if(value != required) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Is the mouse in the specified region?
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return
	 */
	public boolean mouseInRegion(int x1, int x2, int y1, int y2) {
		if (getScale() != 1) {
			x2 = (int) ((x2 - x1) * getScale());
			x1 *= getScale();
			x2 += x1;
			y2 = (int) ((y2 - y1) * getScale());
			y1 *= getScale();
			y2 += y1;
			x1 += Main.scaledX;
			x2 += Main.scaledX;
			y1 += Main.scaledY;
			y2 += Main.scaledY;
		}
		if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
			return true;
		}
		return false;
	}

	/**
	 * Is the click in the specified region?
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return
	 */
	public boolean clickInRegion(int x1, int x2, int y1, int y2) {
		/*if (getScale() != 1) {
			x2 = (int) ((x2 - x1) * getScale());
			x1 *= getScale();
			x2 += x1;
			y2 = (int) ((y2 - y1) * getScale());
			y1 *= getScale();
			y2 += y1;
			x1 += Main.scaledX;
			x2 += Main.scaledX;
			y1 += Main.scaledY;
			y2 += Main.scaledY;
		}*/
		//System.out.println(System.currentTimeMillis() - clickTime);
		boolean clicked = false;
		if (System.currentTimeMillis() - clickTime < 50) {
			if (clickX >= x1 && clickX <= x2 && clickY >= y1 && clickY <= y2) {
				return true;
			}
		} else {
			clickType = ClickType.RELEASED;
		}
		return false;
	}

	public void resetClick() {
		clickType = ClickType.RELEASED;
	}

	/**
	 * Gets the current zoom scale.
	 * @return
	 */
	public double getScale() {
		if (zoom < 100) {
			zoom = 100;
		}
		if (zoom > 250) {
			zoom = 250;
		}
		return (zoom / 100D);
	}

	/**
	 * Gets the canvas width.
	 * @return
	 */
	public int getCanvasWidth() {
		return getWidth() + 1;
	}

	/**
	 * Gets the canvas height.
	 * @return
	 */
	public int getCanvasHeight() {
		return getHeight() + 1;
	}

	/**
	 * The InputListener instance.
	 */
	public InputListener inputListener = null;

	/**
	 * The ViewportRenderer instance.
	 */
	public ViewportRenderer renderer = null;

	/**
	 * The UserInterface instance.
	 */
	public static UserInterface ui;

	/**
	 * Gets the UserInterface instance.
	 * @return
	 */
	public static UserInterface getUI() {
		return ui;
	}

	/**
	 * The Main instance.
	 */
	public static Main main;

	/**
	 * Gets this instance.
	 * @return
	 */
	public static Main getInstance() {
		return main;
	}

	/**
	 * Cleans up the program for quitting.
	 */
	public void cleanUpForQuit() {
		menuActionCmd3 = null;
		menuActionID = null;
		menuActionName = null;
		variousSettings = null;
		imageProducer = null;
		RSInterface.cache = null;
	}

	/**
	 * Starts the program.
	 */
	void startUp() {
		try {
			updateProgress("Unpacking archives...", 25);
			titleArchive = getArchive(1);
			small = new RSFont(false, "p11_full", titleArchive);
			regular = new RSFont(false, "p12_full", titleArchive);
			bold = new RSFont(false, "b12_full", titleArchive);
			fancy = new RSFont(true, "q8_full", titleArchive);
			interfaces = getArchive(3);
			if (interfaces == null) {
				System.out.println("Interface archive is null.");
				return;
			}
			media = getArchive(4);
			if (media == null) {
				System.out.println("Media archive is null.");
				return;
			}
			mediaArchive = new MediaArchive(media);
			updateProgress("Unpacking media...", 50);
			scrollBar1 = new RSImage(media, "scrollbar", 0);
			scrollBar2 = new RSImage(media, "scrollbar", 1);
			for (int index = 0; index < dropShadow.length; index++) {
				dropShadow[index] = new RSImage("dropshadow/shadow " + index + ".png");
			}
			updateProgress("Unpacking interfaces...", 75);
			RSFont fonts[] = { small, regular, bold, fancy };
			RSInterface.load(interfaces, media, fonts);
			RSInterface.createBackup();
			mediaArchive.updateKnown();
			updateProgress("Complete!", 100);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main method.
	 * @param args
	 */
	public static void main(String args[]) {
		Settings.checkDirectory();
		Settings.read();
		ui = new UserInterface();
	}

	/**
	 * The init method.
	 */
	public void init() {
		main = this;
		ActionHandler.main = main;
		startRunnable(this, 1);
	}

	public Main() {
		toolAction = new SelectAction();
		dropShadow = new RSImage[8];
		buildToolbox();
		inputListener = new InputListener(this);
		renderer = new ViewportRenderer(this);
		cache = new Cache();
		imageProducer = new RSImageProducer(765, 504, this);
		DrawingArea.setAllPixelsToZero();
		arial = new RealFont[]{ new RealFont(this, "Arial", 0, 10, true), new RealFont(this, "Arial", 0, 12, true), new RealFont(this, "Arial", 0, 14, true) };
		arialColor = 0xD8D8D8;;
		progress = 0.0F;
		menuOpen = false;
		scrollLight = 0x766654;
		scrollDark = 0x332d25;
		variousSettings = new int[2000];
		scrollBackground = 0x23201b;
		scrollFill = 0x4d4233;
		menuActionCmd3 = new int[500];
		menuActionID = new int[500];
		aBoolean1149 = false;
		menuActionName = new String[500];
		zoom = 100;
		scaledX = 0;
		scaledY = 0;
	}

	public BoxComponentAction toolAction;
	public RSImage[] dropShadow;
	public Toolbox toolbox;
	public static int horizontalScale = 10;
	public static int verticalScale = 10;
	public boolean adjustingGrid = false;
	public int sliderThickness = 8;
	public int menuActions = 0;
	public int actionIndex = -1;
	public static boolean selectionLocked = false;
	public static boolean movingSelection = false;
	public static int verticalPos;
	public static int horizontalPos;
	public int selectionX = -1;
	public int selectionY = -1;
	public int selectionWidth = 0;
	public int selectionHeight = 0;
	public MediaArchive mediaArchive;
	public static Cache cache;
	public int zoom;
	public static int scaledX;
	public static int scaledY;
	public static Archive media;
	public static Archive interfaces;
	public RealFont[] arial;
	public int arialColor;
	public static int currentId = -1;
	public static int selectedId = -1;
	public static int hoverId = -1;
	public int[] alpha = { 0, 0, 0, 0 };
	private boolean[] increasing = { false, false, false, false };
	private int[] minimum = { 75, 0, 0, 0 };
	private int[] maximum = { 200, 256, 256, 256 };
	private int[] rate = { 4, 8, 8, 8 };
	public boolean dull = false;

	public float progress;
	public int menuOffsetX;
	public int menuOffsetY;
	public int menuWidth;
	public int menuHeight;
	public boolean menuOpen;
	private final int scrollLight;
	private final int scrollDark;
	public int variousSettings[];
	private final int scrollBackground;
	private RSImage scrollBar1;
	private RSImage scrollBar2;
	private int anInt1026;
	private int anInt1039;
	private int anInt1048;
	private Archive titleArchive;
	private final int scrollFill;
	private int anInt1085;
	private int activeInterfaceType;
	static int anInt1089;
	public int[] menuActionCmd3;
	public int[] menuActionID;
	public RSImageProducer imageProducer;
	public int menuActionRow;
	public boolean aBoolean1149;
	public String[] menuActionName;
	public RSFont small;
	public RSFont regular;
	public RSFont bold;
	public RSFont fancy;
	public static int anInt1290;
    public int anInt1044;
    public int anInt1129;
    public int anInt1315;
    public int anInt1500;
    public int anInt1501;

	public void run() {
		MyMouseListener mouseListener = new MyMouseListener(getInstance());
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(mouseListener);
		addKeyListener(new MyKeyListener());
		displayProgress("Loading...", 0);
		startUp();
		try {
			do {
				long start = System.currentTimeMillis();
				if (inputListener != null) {
					//animTick++;
					//inputListener.process();
					processInput();
				}
				if (renderer != null) {
					draw();
					//renderer.render();
					//animTick = 0;
				}
				long done = System.currentTimeMillis() - start;
				if(20 - done >= 0) {
				    Thread.sleep(20 - done);
				} else {
					Thread.sleep(0);
				}
			} while(true);
		} catch(Exception e) {
		}
	}

	private void exit() {
		timeRunning = -2;
		cleanUpForQuit();
	}

	public void startRunnable(Runnable runnable, int priority) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(priority);
	}

	final void setDelayTime(int time) {
		delayTime = 1000 / time;
	}

	public final void start() {
		if(timeRunning >= 0) {
			timeRunning = 0;
		}
	}

	public final void stop() {
		if(timeRunning >= 0) {
			timeRunning = 4000 / delayTime;
		}
	}

	public final void destroy() {
		timeRunning = -1;
		try {
			Thread.sleep(5000L);
		} catch(Exception e) {
		}
		if(timeRunning == -1) {
			exit();
		}
	}

	public void updateMouse(int mouseX, int mouseY, int clickX, int clickY, int idleTime, long clickTime, ClickType clickType) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.clickX = clickX;
		this.clickY = clickY;
		this.idleTime = idleTime;
		this.clickTime = clickTime;
		this.clickType = clickType;
	}

	public ClickType clickType;

	public ClickType getClickType() {
		return clickType;
	}

	public int idleTime;
	public int mouseX;
	public int mouseY;
	public int clickX;
	public int clickY;
	public long clickTime;

	public String titleText = "";
	public static int hotKey = 508;
	private int timeRunning;
	private int delayTime;
	int minDelay;
	int fps;
	boolean shouldDebug;
	int myWidth;
	int myHeight;
	public Insets insets = new Insets(30, 5, 5, 5);
	public boolean isApplet;
	boolean awtFocus;
	long aLong29;
	protected final int keyArray[] = new int[128];
	protected final int charQueue[] = new int[128];
	protected int writeIndex;
	public static int anInt34;
	public BufferStrategy strategy;

}