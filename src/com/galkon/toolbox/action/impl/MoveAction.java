package com.galkon.toolbox.action.impl;

import com.galkon.ActionHandler;
import com.galkon.Main;
import com.galkon.listeners.ClickType;
import com.galkon.rsinterface.RSInterface;
import com.galkon.toolbox.action.BoxComponentAction;

public class MoveAction implements BoxComponentAction {

	@Override
	public void perform(Main main) {
		if (main.clickType == ClickType.LEFT_DRAG || main.selectionX != -1) {
			if (main.selectionX != -1) {
				if (main.mouseInRegion(main.selectionX, main.selectionX + main.selectionWidth, main.selectionY, main.selectionY + main.selectionHeight)) {
					//TODO Selection moving
				}
			} else {
				RSInterface selected = Main.getSelected();
				if (selected != null) {
					ActionHandler.setSelectedX(main.mouseX - main.getAreaX() - (selected.width / 2));
					ActionHandler.setSelectedY(main.mouseY - main.getAreaY() - (selected.height / 2));
				}
			}
		} else {
			new SelectAction().perform(main);
		}
	}

}
