package rs2.editor;

import rs2.ActionHandler;
import rs2.Main;
import rs2.Settings;
import rs2.listeners.ClickType;
import rs2.rsinterface.RSInterface;

public class InputListener {

	public Main instance;

	public InputListener(Main instance) {
		this.instance = instance;
	}

	/**
	 * Processes mouse input for the interface editor.
	 */
	public void process() {
		if (instance == null) {
			return;
		}
		if (Main.getInterface() != null) {
			/* Interface children clicking */
			if (!instance.menuOpen) {
				processChildClicking();
			}
			/* Grid adjustment */
			if (instance.clickInRegion(0, instance.getCanvasWidth() - instance.sliderThickness, instance.getCanvasHeight() - instance.sliderThickness, instance.getCanvasHeight())) {
				if (Settings.displayGrid) {
					if (instance.getClickType() == ClickType.LEFT_CLICK || instance.getClickType() == ClickType.LEFT_DRAG) {
						instance.adjustHorizontal(instance.mouseX);
					}
				}
			} else if (instance.clickInRegion(instance.getCanvasWidth() - instance.sliderThickness, instance.getCanvasWidth(), 0, instance.getCanvasHeight() - instance.sliderThickness)) {
				if (Settings.displayGrid) {
					if (instance.getClickType() == ClickType.LEFT_CLICK || instance.getClickType() == ClickType.LEFT_DRAG) {
						instance.adjustVertical(instance.mouseY);
					}
				}
			} else {
				instance.adjustingGrid = false;
			}
			/* Multi selection */
			if (!instance.adjustingGrid) {
				/* Selection rectangle dragging */
				if (instance.mouseInRegion(instance.selectionX, instance.selectionX + instance.selectionWidth, instance.selectionY, instance.selectionY + instance.selectionHeight) && Main.movingSelection) {
					return;
				} else {
					if (instance.getClickType() == ClickType.LEFT_DRAG) {
						int[] distances = instance.calculateDragDistance();
						instance.resetSelection();
						instance.selectionX = instance.clickX;
						instance.selectionY = instance.clickY;
						instance.selectionWidth = distances[0];
						instance.selectionHeight = distances[1];
					}
				}
			}
			/* Multi selection menu clicking */
			if (instance.multipleSelected()) {
				if (instance.getClickType() == ClickType.RIGHT_CLICK && instance.clickInRegion(instance.selectionX, instance.selectionX + instance.selectionWidth, instance.selectionY, instance.selectionY + instance.selectionHeight)) {
					instance.menuActions = 1;
					instance.determineMenuSize();
				}
				if (instance.menuOpen) {
					if (instance.getClickType() == ClickType.LEFT_CLICK) {
						int _clickX = instance.clickX;
						int _clickY = instance.clickY ;
						for(int action = 0; action < instance.getActions().length; action++) {
							int posY = instance.menuOffsetY + 31 + (instance.getActions().length - 1 - action) * 15;
							if(_clickX > instance.menuOffsetX && _clickX < instance.menuOffsetX + instance.menuWidth && _clickY > posY - 13 && _clickY < posY + 3) {
								instance.actionIndex = action;
								instance.perform(instance.actionIndex);
							}
						}
					}
				}
			}
			/* Right clicking menus and actions */
			if (Main.getSelected() != null) {
				if (instance.getClickType() == ClickType.RIGHT_CLICK && instance.clickInRegion(Main.getSelectedX(), Main.getSelectedX() + Main.getSelected().width, Main.getSelectedY(), Main.getSelectedY() + Main.getSelected().height)) {
					instance.menuActions = 0;
					instance.determineMenuSize();
				}
				if (instance.menuOpen) {
					if (instance.getClickType() == ClickType.LEFT_CLICK) {
						int _clickX = instance.clickX;
						int _clickY = instance.clickY ;
						for(int action = 0; action < instance.getActions().length; action++) {
							int posY = instance.menuOffsetY + 31 + (instance.getActions().length - 1 - action) * 15;
							if(_clickX > instance.menuOffsetX && _clickX < instance.menuOffsetX + instance.menuWidth && _clickY > posY - 13 && _clickY < posY + 3) {
								instance.actionIndex = action;
								instance.perform(instance.actionIndex);
							}
						}
					}
				}
			}
			if (Main.getSelected() != null) {
				if (instance.getClickType() == ClickType.RIGHT_CLICK && instance.clickInRegion(Main.getSelectedX(), Main.getSelectedX() + Main.getSelected().width, Main.getSelectedY(), Main.getSelectedY() + Main.getSelected().height)) {
					instance.menuActions = 0;
					instance.determineMenuSize();
				}
				if (instance.menuOpen) {
					if (instance.getClickType() == ClickType.LEFT_CLICK) {
						int _clickX = instance.clickX;
						int _clickY = instance.clickY ;
						for(int action = 0; action < instance.getActions().length; action++) {
							int posY = instance.menuOffsetY + 31 + (instance.getActions().length - 1 - action) * 15;
							if(_clickX > instance.menuOffsetX && _clickX < instance.menuOffsetX + instance.menuWidth && _clickY > posY - 13 && _clickY < posY + 3) {
								instance.actionIndex = action;
								instance.perform(instance.actionIndex);
							}
						}
					}
				}
			}
			/* Close menu when mouse leaves menu area */
			if (!instance.mouseInRegion(instance.menuOffsetX, instance.menuOffsetX + instance.menuWidth, instance.menuOffsetY, instance.menuOffsetY + instance.menuHeight)) {
				instance.menuOpen = false;
				instance.actionIndex = -1;
			}
		}
	}

	public void processChildClicking() {
		if (Main.movingSelection) {
			return;
		}
		RSInterface rsi = Main.getInterface();
		int offsetY = 0;
		int _mouseX = instance.mouseX;
		int _mouseY = instance.mouseY;
		int _clickX = instance.clickX;
		int _clickY = instance.clickY;
		if (rsi.type != 0 || rsi.children == null || rsi.showInterface) {
			return;
		}
		if (_mouseX < 0 || _mouseY < 0 || _mouseX > 0 + rsi.width || _mouseY > 0 + rsi.height) {
			return;
		}
		if (_clickX < 0 || _clickY < 0 || _clickX > 0 + rsi.width || _clickY > 0 + rsi.height) {
			//return;
		}
		Main.hoverId = -1;
		int childCount = rsi.children.size();
		for(int index = 0; index < childCount; index++) {
			int posX = rsi.childX.get(index);
			int posY = rsi.childY.get(index) - offsetY;
			RSInterface child = RSInterface.getInterface(rsi.children.get(index));
			posX += child.drawOffsetX;
			posY += child.drawOffsetY;
			if (instance.mouseInRegion(posX, posX + child.width, posY, posY + child.height)) {
				Main.hoverId = child.id;
				if (instance.getClickType() == ClickType.CTRL_DRAG && Main.selectedId != -1 && Main.selectedId != Main.currentId && instance.getScale() == 1) {
					ActionHandler.setSelectedX(_mouseX - (Main.getSelected().width / 2));
					ActionHandler.setSelectedY(_mouseY - (Main.getSelected().height / 2));
					return;
				}
			}
			if (instance.clickInRegion(posX, posX + child.width, posY, posY + child.height)) {
				if (instance.getClickType() == ClickType.CTRL_RIGHT) {
					Main.selectChild(index);
					instance.determineMenuSize();
					return;
				}
				if (instance.getClickType() == ClickType.CTRL_LEFT || instance.getClickType() == ClickType.DOUBLE) {
					Main.selectChild(index);
					return;
				}
			}
			boolean test = false;
			if (test && instance.getClickType() == ClickType.LEFT_CLICK && instance.clickInRegion(posX, posX + child.width, posY, posY + child.height)) {
				if(child.actionType == 1) {
					boolean flag = false;
					if(child.contentType != 0) {
						//TODO: Content type.
					}
					if(!flag) {
						instance.menuActionName[instance.menuActionRow] = child.tooltip;
						instance.menuActionID[instance.menuActionRow] = 315;
						instance.menuActionCmd3[instance.menuActionRow] = child.id;
						instance.menuActionRow++;
					}
				}
				if(child.actionType == 2) {
					String name = child.selectedActionName;
					if(name.indexOf(" ") != -1) {
						name = name.substring(0, name.indexOf(" "));
					}
					if (instance.menuActionRow < instance.menuActionName.length) {
						instance.menuActionName[instance.menuActionRow] = name + " @gre@" + child.spellName;
						instance.menuActionID[instance.menuActionRow] = 626;
						instance.menuActionCmd3[instance.menuActionRow] = child.id;
						instance.menuActionRow++;
					}
				}
				if(child.actionType == 3) {
					if (instance.menuActionRow < instance.menuActionName.length) {
						instance.menuActionName[instance.menuActionRow] = "Close";
						instance.menuActionID[instance.menuActionRow] = 200;
						instance.menuActionCmd3[instance.menuActionRow] = child.id;
						instance.menuActionRow++;
					}
				}
				if(child.actionType == 4) {
					if (instance.menuActionRow < instance.menuActionName.length) {
						instance.menuActionName[instance.menuActionRow] = child.tooltip;
						instance.menuActionID[instance.menuActionRow] = 169;
						instance.menuActionCmd3[instance.menuActionRow] = child.id;
						instance.menuActionRow++;
					}
				}
				if(child.actionType == 5) {
					if (instance.menuActionRow < instance.menuActionName.length) {
						instance.menuActionName[instance.menuActionRow] = child.tooltip;
						instance.menuActionID[instance.menuActionRow] = 646;
						instance.menuActionCmd3[instance.menuActionRow] = child.id;
						instance.menuActionRow++;
					}
				}
				if(child.actionType == 6 && !instance.aBoolean1149) {
					if (instance.menuActionRow < instance.menuActionName.length) {
						instance.menuActionName[instance.menuActionRow] = child.tooltip;
						instance.menuActionID[instance.menuActionRow] = 679;
						instance.menuActionCmd3[instance.menuActionRow] = child.id;
						instance.menuActionRow++;
					}
				}
				instance.doAction(instance.menuActionRow - 1);
			}
		}
	}

}
