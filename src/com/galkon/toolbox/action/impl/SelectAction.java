package com.galkon.toolbox.action.impl;

import com.galkon.Main;
import com.galkon.listeners.ClickType;
import com.galkon.toolbox.action.BoxComponentAction;

public class SelectAction implements BoxComponentAction {

	@Override
	public void perform(Main main) {
		if (main.clickType == ClickType.LEFT_CLICK) {
			Main.selectedId = Main.hoverId;
			main.resetSelection();
			main.resetClick();
		}
	}

}
