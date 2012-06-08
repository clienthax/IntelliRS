package rs2.listeners.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import rs2.ActionHandler;
import rs2.Main;
import rs2.Settings;
import rs2.rsinterface.Export;
import rs2.rsinterface.Import;

public class MenuActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand().toLowerCase();
		if (cmd != null) {
			if (cmd.equals("open interface")) {
				Main.getInstance().selectInterface(ActionHandler.openInterface());
			}
			if (cmd.equals("save")) {
				Main.getInstance().updateArchive(Main.interfaces);
			}
			if (cmd.equals("import")) {
				new Import();
			}
			if (cmd.equals("current")) {
				new Export(Main.getInterface());
			}
			if (cmd.equals("selected")) {
				new Export(Main.getSelected());
			}
			if (cmd.contains("recompile")) {
				if (cmd.contains("interfaces")) {
					Main.getInstance().updateArchive(Main.interfaces);
				}
				if (cmd.contains("media")) {
					Main.getInstance().updateArchive(Main.media);
				}
			}
			if (cmd.contains("import")) {
				if (cmd.contains("interfaces")) {
					Main.getInstance().importArchive(Main.interfaces);
				}
				if (cmd.contains("media")) {
					Main.getInstance().importArchive(Main.media);
				}
			}
			if (cmd.contains("export")) {
				if (cmd.contains("interfaces")) {
					Main.getInstance().dumpArchive(Main.interfaces);
				}
				if (cmd.contains("media")) {
					Main.getInstance().dumpArchive(Main.media);
				}
			}
			if (cmd.equals("show grid")) {
				Settings.displayGrid = Main.getUI().displayGrid.isSelected();
				Settings.write();
			}
			if (cmd.equals("show data")) {
				Settings.displayData =  Main.getUI().displayData.isSelected();
				Settings.write();
			}
			if (cmd.equals("hover highlight")) {
				Settings.displayHover =  Main.getUI().displayHover.isSelected();
				Settings.write();
			}
			if (cmd.equals("force enabled")) {
				Settings.forceEnabled =  Main.getUI().forceEnabled.isSelected();
				Settings.write();
			}
			
		}
	}

}
