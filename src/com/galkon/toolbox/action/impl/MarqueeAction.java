package com.galkon.toolbox.action.impl;

import com.galkon.Main;
import com.galkon.listeners.ClickType;
import com.galkon.toolbox.action.BoxComponentAction;

public class MarqueeAction implements BoxComponentAction {

	@Override
	public void perform(Main main) {
		if (main.getClickType() == ClickType.LEFT_DRAG) {
			Main.selectedId = -1;
			int[] distances = main.calculateDragDistance();
			main.resetSelection();
			main.selectionX = main.clickX;
			main.selectionY = main.clickY;
			main.selectionWidth = distances[0];
			main.selectionHeight = distances[1];
		}
	}

}
