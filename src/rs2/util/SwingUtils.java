package rs2.util;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import rs2.listeners.impl.MenuActionListener;

public class SwingUtils {

	public static ActionListener actionListener = new MenuActionListener();

	public static JMenu buildMenu(String name, String[] items, String[] ...strings) {
		int subIndex = 0;
		JMenu menu = new JMenu(name);
		for (String itemName : items) {
			if (itemName.equals("-")) {
				menu.addSeparator();
			} else {
				String action = itemName;
				if (itemName.contains("<")) {
					action = itemName.substring(itemName.indexOf("<") + 1, itemName.indexOf(">"));
					itemName = itemName.substring(0, itemName.indexOf("<"));
					action += "-" + itemName;
				} 
				if (itemName.contains("->")) {
					itemName = itemName.replaceAll("->", "");
					if (strings.length >= subIndex + 1) {
						JMenu sub = buildMenu(itemName.replace("->", ""), strings[subIndex]);
						menu.add(sub);
					}
					subIndex++;
				} else {
					JMenuItem item = new JMenuItem(itemName);
					item.addActionListener(actionListener);
					item.setActionCommand(action);
					menu.add(item);
				}
			}
		}
		menu.getPopupMenu().setLightWeightPopupEnabled(false);
		return menu;
	}

	/**
	 * Creates a new JFileChooser and returns the selected path's location.
	 * @param title
	 * @param openTo
	 * @param chooseDirectory
	 * @param filter
	 * @return
	 */
	public static String getFilePath(String title, String openTo, boolean chooseDirectory, FileFilter filter) {
		String location = null;
		JFileChooser chooser = new JFileChooser(title);
		chooser.setCurrentDirectory(new File(openTo));
		chooser.setFileFilter(filter);
		chooser.setDialogTitle("Open Cache");
		if (chooseDirectory) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		} else {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
			location = chooser.getSelectedFile().getAbsolutePath();
		}
		return location;
	}

	public static int getContainerWidth(Container container) {
		int width = 0;
		for (int index = 0; index < container.getComponentCount(); index++) {
			int offset = (container.getComponent(index).getX() + container.getComponent(index).getWidth());
			if (offset > width) {
				width = offset;
			}
		}
		return width;
	}

	public static int getContainerHeight(Container container) {
		int height = 0;
		for (int index = 0; index < container.getComponentCount(); index++) {
			int offset = (container.getComponent(index).getY() + container.getComponent(index).getHeight());
			if (offset > height) {
				height = offset;
			}
		}
		return height;
	}

}
