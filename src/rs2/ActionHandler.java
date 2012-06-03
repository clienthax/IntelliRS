package rs2;

import rs2.graphics.RSImage;
import rs2.swing.UserInterface;
import rs2.swing.edit.ImagePane;
import rs2.swing.edit.TextPane;
import rs2.util.ArrayUtils;

public class ActionHandler {

	public static Main main;

	public static int getSelectedIndex() {
		if (Main.selectedId != -1) {
			RSInterface rsi = Main.getInterface();
			if (rsi.children != null) {
				for (int index = 0; index < rsi.children.length; index++) {
					if (rsi.children[index] == Main.selectedId) {
						return index;
					}/* else {
						RSInterface child = RSInterface.cache[rsi.children[index]];
						if (child.children != null) {
							for (int index2 = 0; index2 < child.children.length; index2++) {
								if (child.children[index2] == Main.selectedId) {
									Main.parentId = child.children[index2];
									Main.depth = 2;
									return index;
								}
							}
						}
					}*/
				}
			}
		}
		return -1;
	}

	public static void moveSelectedX(int x) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childX[getSelectedIndex()] += x;
				}
			}
		}
	}

	public static void moveSelectedY(int y) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childY[getSelectedIndex()] += y;
				}
			}
		}
	}
	
	public static void setSelectedX(int x) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childX[getSelectedIndex()] = x;
				}
			}
		}
	}

	public static void setSelectedY(int y) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childY[getSelectedIndex()] = y;
				}
			}
		}
	}

	public static void removeSelected() {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi.children != null) {
				int index = getSelectedIndex();
				rsi.children = ArrayUtils.remove(rsi.children, index);
				rsi.childX = ArrayUtils.remove(rsi.childX, index);
				rsi.childY = ArrayUtils.remove(rsi.childY, index);
			}
			UserInterface.ui.rebuildTreeList();
		}
	}

	public static void setZIndex(int index) {
		if (getSelectedIndex() == index) {
			return;
		}
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi.children != null) {
				int selected = getSelectedIndex();
				rsi.children = ArrayUtils.setZOrder(rsi.children, selected, index);
				rsi.childX = ArrayUtils.setZOrder(rsi.childX, selected, index);
				rsi.childY = ArrayUtils.setZOrder(rsi.childY, selected, index);
			}
			UserInterface.ui.rebuildTreeList();
		}
	}

	public static void lock() {
		if (Main.selectedId != -1) {
			Main.getSelected().locked = true;
		}
	}

	public static void unlock() {
		if (Main.selectedId != -1) {
			Main.getSelected().locked = false;
		}
	}

	public static void edit(RSInterface rsi) {
		if (rsi == null) {
			return;
		}
		if (rsi.locked) {
			return;
		}
		switch (rsi.type) {
			case 0:
				Main.selectInterface(rsi.id);
				break;
			case 4:
				//setText(rsi, JOptionPane.showInputDialog(null, "Enter new text:", rsi.disabledText));
				new TextPane(rsi);
				break;
			case 5:
				new ImagePane(rsi);
				break;
		}
	}

	public static void updateSprite(RSInterface rsi, boolean disabled) {
		if (disabled) {
			rsi.disabledSprite = new RSImage(Main.media, rsi.disabledSpriteName, rsi.disabledSpriteId);
			rsi.width = rsi.disabledSprite.myWidth;
			rsi.height = rsi.disabledSprite.myHeight;
		} else {
			rsi.enabledSprite = new RSImage(Main.media, rsi.enabledSpriteName, rsi.enabledSpriteId);
			rsi.width = rsi.enabledSprite.myWidth;
			rsi.height = rsi.enabledSprite.myHeight;
		}
	}

}
