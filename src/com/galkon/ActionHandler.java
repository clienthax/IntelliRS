package com.galkon;

import javax.swing.JOptionPane;

import com.galkon.graphics.RSImage;
import com.galkon.rsinterface.RSInterface;
import com.galkon.swing.UserInterface;
import com.galkon.swing.edit.ImagePane;
import com.galkon.swing.edit.TextPane;


public class ActionHandler {

	public static Main main;

	public static int openInterface() {
		return Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter an interface id:", "Open Interface", JOptionPane.INFORMATION_MESSAGE));
	}

	public static int getSelectedIndex() {
		if (Main.selectedId != -1) {
			RSInterface rsi = Main.getInterface();
			if (rsi.children != null) {
				for (int index = 0; index < rsi.children.size(); index++) {
					if (rsi.children.get(index) == Main.selectedId) {
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
					rsi.childX.set(getSelectedIndex(), rsi.childX.get(getSelectedIndex()) + x);
				}
			}
		}
	}

	public static void moveSelectedY(int y) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childY.set(getSelectedIndex(), rsi.childY.get(getSelectedIndex()) + y);
				}
			}
		}
	}
	
	public static void setSelectedX(int x) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childX.set(getSelectedIndex(), x);
				}
			}
		}
	}

	public static void setSelectedY(int y) {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi != null) {
				if (rsi.children != null) {
					rsi.childY.set(getSelectedIndex(), y);
				}
			}
		}
	}

	public static void removeSelected() {
		if (Main.selectedId != -1 && !Main.getSelected().locked) {
			RSInterface rsi = Main.getInterface();
			if (rsi.children != null) {
				int index = getSelectedIndex();
				rsi.children.remove(index);
				rsi.childX.remove(index);
				rsi.childY.remove(index);
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
				int id = rsi.children.get(selected);
				int x = rsi.childX.get(selected);
				int y = rsi.childY.get(selected);
				rsi.children.remove(selected);
				rsi.children.add(index, id);
				rsi.childX.remove(selected);
				rsi.childX.add(index, x);
				rsi.childY.remove(selected);
				rsi.childY.add(index, y);
				//rsi.children = ArrayUtils.setZOrder(rsi.children, selected, index);
				////rsi.childX = ArrayUtils.setZOrder(rsi.childX, selected, index);
				//rsi.childY = ArrayUtils.setZOrder(rsi.childY, selected, index);
			}
			UserInterface.ui.rebuildTreeList();
		}
	}

	public static void toggleLock() {
		if (Main.selectedId != -1) {
			Main.getSelected().locked = !Main.getSelected().locked;
			Main.getUI().rebuildTreeList();
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
				Main.getInstance().selectInterface(rsi.id);
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
			rsi.disabledSprite = new RSImage(Main.media, rsi.disabledSpriteArchive, rsi.disabledSpriteId);
			rsi.width = rsi.disabledSprite.myWidth;
			rsi.height = rsi.disabledSprite.myHeight;
		} else {
			rsi.enabledSprite = new RSImage(Main.media, rsi.enabledSpriteArchive, rsi.enabledSpriteId);
			rsi.width = rsi.enabledSprite.myWidth;
			rsi.height = rsi.enabledSprite.myHeight;
		}
	}

	public static void lockSelectedChildren(boolean lock) {
		RSInterface[] children = Main.getInstance().getSelectedChildren();
		for (RSInterface child : children) {
			child.locked = lock;
		}
		Main.selectionLocked = lock;
	}

}