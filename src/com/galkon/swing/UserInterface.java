package com.galkon.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.net.URI;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.galkon.ActionHandler;
import com.galkon.Main;
import com.galkon.Settings;
import com.galkon.constants.Constants;
import com.galkon.rsinterface.RSInterface;
import com.galkon.swing.impl.CellRenderer;
import com.galkon.util.SwingUtils;


public class UserInterface extends Main implements ActionListener, TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static JFrame frame;
	public static JInternalFrame viewport;
	public static JInternalFrame treePane;
	public static JDesktopPane desktop;
	public JCheckBox displayGrid;
	public JCheckBox displayData;
	public JCheckBox displayHover;
	public JCheckBox forceEnabled;
	public static JTree tree;
	public static JScrollPane treeScroll;
	public static DefaultTreeModel treeModel;

	public UserInterface() {
		try {
			UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
	        JFrame.setDefaultLookAndFeelDecorated(true);
	        JDialog.setDefaultLookAndFeelDecorated(true);
			build();
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void build() {
		try {
			int width = 800;
			int height = 600;
			frame = new JFrame(Constants.NAME);
			desktop = new JDesktopPane();
			desktop.setBackground(new Color(28, 28, 28));
			viewport = new JInternalFrame("Viewport", false, false, false, false);
			viewport.setLayout(new BorderLayout());
			viewport.add(this);
			viewport.setVisible(true);
			viewport.setSize(765 + 8, 503 + 34);
			desktop.add(viewport);
			frame.getContentPane().add(desktop, BorderLayout.CENTER);
			buildMenuBar();
			frame.setMinimumSize(new Dimension(width, height));
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
			frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(true);
			frame.setVisible(true);
			frame.requestFocus();
			frame.toFront();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		String[] items = { "Open Interface", "Save", "-", "Import", "Export->", "-", "About", "Exit" };
		String[] exportItems = { "Current", "Selected" };
		JMenu file = SwingUtils.buildMenu("File", items, exportItems);
		items = new String[]{ "Interface Archive->", "Media Archive->", "-", "Rebuild" };
		String[] interfaceItems = { "Recompile<interfaces>", "Import<interfaces>", "Export<interfaces>" };
		String[] mediaItems = { "Recompile<media>", "Import<media>", "Export<media>" };
		JMenu cache = SwingUtils.buildMenu("Cache", items, interfaceItems, mediaItems);
		displayGrid = new JCheckBox("Show grid");
		displayGrid.addActionListener(SwingUtils.actionListener);
		displayGrid.setSelected(Settings.displayGrid);
		displayData = new JCheckBox("Show data");
		displayData.addActionListener(SwingUtils.actionListener);
		displayData.setSelected(Settings.displayData);
		displayHover = new JCheckBox("Hover highlight");
		displayHover.addActionListener(SwingUtils.actionListener);
		displayHover.setSelected(Settings.displayHover);
		forceEnabled = new JCheckBox("Force enabled");
		forceEnabled.addActionListener(SwingUtils.actionListener);
		forceEnabled.setSelected(Settings.forceEnabled);
		menuBar.add(file);
		menuBar.add(cache);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(displayGrid);
		menuBar.add(displayData);
		menuBar.add(displayHover);
		menuBar.add(forceEnabled);
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
	}

	public DefaultMutableTreeNode getTreeList() {
		DefaultMutableTreeNode parent = new DefaultMutableTreeNode(Integer.toString(currentId) + " - " + getType(getInterface().id, getInterface().type), true);
		treeModel = new DefaultTreeModel(parent);
		if (getInterface() != null) {
			if (getInterface().children != null) {
				for (int index = 0; index < getInterface().children.size(); index++) {
					RSInterface rsi_1 = RSInterface.getInterface(getInterface().children.get(index));
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(Integer.toString(getInterface().children.get(index)) + " - " + getType(rsi_1.id, rsi_1.type));
					treeModel.insertNodeInto(child, parent, index);
					if (rsi_1.children != null) {
						for (int childIndex = 0; childIndex < rsi_1.children.size(); childIndex++) {
							RSInterface rsi_2 = RSInterface.getInterface(rsi_1.children.get(childIndex));
							DefaultMutableTreeNode child2 = new DefaultMutableTreeNode(Integer.toString(rsi_1.children.get(childIndex)) + " - " + getType(rsi_2.id, rsi_2.type));
							treeModel.insertNodeInto(child2, child, childIndex);
						}
					}
				}
			}
		}
		return parent;
	}

	public void createTree() {
		tree = new JTree();
		tree.setModel(treeModel);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new CellRenderer());
	}

	public void rebuildTreeList() {
		if (tree != null && treePane != null) {
			tree = null;
		}
		treePane.remove(treeScroll);
		getTreeList();
		createTree();
		treeScroll = new JScrollPane(tree);
		treePane.add(treeScroll);
		try {
			treePane.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		treePane.setLocation(treePane.getLocation());
	}

	public void buildTreePane() {
		if (tree != null && treePane != null) {
			rebuildTreeList();
			return;
		}
		try {
			getTreeList();
			createTree();
			treeScroll = new JScrollPane(tree);
			treePane = new JInternalFrame("Children", false, false, false, false);
			treePane.setVisible(true);
			treePane.setSize(225, viewport.getHeight());
			treePane.setLocation(viewport.getWidth(), 0);
			treePane.add(treeScroll);
			desktop.add(treePane);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void openURL(String url) {
		Desktop d = Desktop.getDesktop();
		try {
			d.browse(new URI(url)); 	
		} catch (Exception e) {
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand().toLowerCase();
		try {
			if(cmd != null) {
				if (cmd.equals("open interface")) {
					selectInterface(ActionHandler.openInterface());
				}
				if (cmd.equals("show grid")) {
					Settings.displayGrid = displayGrid.isSelected();
					Settings.write();
				}
				if (cmd.equals("show data")) {
					Settings.displayData = displayData.isSelected();
					Settings.write();
				}
				if (cmd.equals("hover highlight")) {
					Settings.displayHover = displayHover.isSelected();
					Settings.write();
				}
				if (cmd.equals("force enabled")) {
					Settings.forceEnabled = forceEnabled.isSelected();
					Settings.write();
				}
				if (cmd.equals("save")) {
					Main.getInstance().updateArchive(Main.interfaces);
				}
			}
		} catch (Exception e) {
		}
	}

	public int treeClick = -1;

	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		TreePath[] paths = evt.getPaths();
		for (int index = 0; index < paths.length; index++) {
			if (evt.isAddedPath(index)) {
				String id = paths[index].getLastPathComponent().toString().split(" ")[0];
				selectedId = Integer.parseInt(id);
				break;
			}
		}
	}
}