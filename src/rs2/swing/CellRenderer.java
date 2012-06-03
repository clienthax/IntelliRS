package rs2.swing;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import rs2.Main;
import rs2.constants.Constants;

@SuppressWarnings("serial")
public class CellRenderer extends DefaultTreeCellRenderer {

	public Icon lock = new ImageIcon(Constants.getImageDirectory() + "leaf.gif");

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (leaf && isLocked(value)) {
			setIcon(lock);
		}
		return this;
	}

	protected boolean isLocked(Object value) {
		int id = Integer.parseInt(value.toString().split(" ")[0]);
		return Main.getInterface(id).locked;
	}

}