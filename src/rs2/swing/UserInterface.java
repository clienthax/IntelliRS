package rs2.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import rs2.Main;
import rs2.RSInterface;
import rs2.Settings;
import rs2.constants.Constants;

public class UserInterface extends Main implements ActionListener, TreeSelectionListener, TreeModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static UserInterface ui;

	public static JMenuBar menuBar;
	public static JMenu fileMenu;
	public static JFrame frame;
	public static JInternalFrame viewport;
	public static JInternalFrame treePane;
	public static JDesktopPane desktop;
	public static JCheckBox displayGrid;
	public static JCheckBox displayData;
	public static JCheckBox displayHover;
	public static JCheckBox forceEnabled;
	public static JTree tree;
	public static JScrollPane treeScroll;
	public static DefaultTreeModel treeModel;

	public UserInterface() {
		super();
		try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
	        JFrame.setDefaultLookAndFeelDecorated(true);
	        JDialog.setDefaultLookAndFeelDecorated(true);
			build();
			ui = this;
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void build() {
		try {
			int width = 800;
			int height = 500;
			frame = new JFrame(Constants.NAME);
			desktop = new JDesktopPane();
			desktop.setBackground(new Color(28, 28, 28));
			viewport = new JInternalFrame("Viewport", false, false, false, false);
			viewport.setLayout(new BorderLayout());
			viewport.add(this);
			viewport.setVisible(true);
			viewport.setSize(appletWidth + 9, appletHeight + 33);
			viewport.setResizable(true);
			desktop.add(viewport);
			frame.getContentPane().add(desktop, BorderLayout.CENTER);
			initMenuBar();
			frame.setResizable(true);
			frame.setVisible(true);
			frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom + menuBar.getHeight());
			frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2);
			frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
			frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
			frame.requestFocus();
			frame.toFront();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultMutableTreeNode getTreeList() {
		DefaultMutableTreeNode parent = new DefaultMutableTreeNode(Integer.toString(currentId) + " - " + getType(getInterface().id, getInterface().type), true);
		treeModel = new DefaultTreeModel(parent);
		if (getInterface() != null) {
			if (getInterface().children != null) {
				for (int index = 0; index < getInterface().children.length; index++) {
					RSInterface rsi_1 = RSInterface.cache[getInterface().children[index]];
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(Integer.toString(getInterface().children[index]) + " - " + getType(rsi_1.id, rsi_1.type));
					treeModel.insertNodeInto(child, parent, index);
					if (rsi_1.children != null) {
						for (int childIndex = 0; childIndex < rsi_1.children.length; childIndex++) {
							RSInterface rsi_2 = RSInterface.cache[rsi_1.children[childIndex]];
							DefaultMutableTreeNode child2 = new DefaultMutableTreeNode(Integer.toString(rsi_1.children[childIndex]) + " - " + getType(rsi_2.id, rsi_2.type));
							treeModel.insertNodeInto(child2, child, childIndex);
							//child.add(child2);
						}
					}
					//parent.add(child);
				}
			}
		}
		return parent;
	}

	public void createTree() {
		tree = new JTree();
		tree.setModel(treeModel);
		treeModel.addTreeModelListener(this);
		//tree.addMouseListener(this);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//tree.setCellRenderer(new CellRenderer());
		tree.setCellRenderer(getCellRenderer());
	}

	public DefaultTreeCellRenderer getCellRenderer() {
		return new CellRenderer();
	}

	protected boolean isLocked(Object value) {
		int id = Integer.parseInt(value.toString().split(" ")[0]);
		Main.selectedId = id;
		return Main.getSelected().locked;
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

	public void initMenuBar() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		String[] fileMenuItems = { "Open Interface", "Save", "-", "Exit" };
		for(String name : fileMenuItems) {
			if (name.equals("-")) {
				fileMenu.addSeparator();
			} else {
				JMenuItem fileMenuItem = new JMenuItem(name);
				fileMenuItem.addActionListener(this);
				fileMenu.add(fileMenuItem);
			}
		}
		displayGrid = new JCheckBox("Show grid");
		displayGrid.addActionListener(this);
		displayGrid.setSelected(Settings.displayGrid);
		displayData = new JCheckBox("Show data");
		displayData.addActionListener(this);
		displayData.setSelected(Settings.displayData);
		displayHover = new JCheckBox("Hover highlight");
		displayHover.addActionListener(this);
		displayHover.setSelected(Settings.displayHover);
		forceEnabled = new JCheckBox("Force enabled");
		forceEnabled.addActionListener(this);
		forceEnabled.setSelected(Settings.forceEnabled);
		menuBar.add(fileMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(displayGrid);
		menuBar.add(displayData);
		menuBar.add(displayHover);
		menuBar.add(forceEnabled);
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
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
					selectInterface(Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter an interface id:", "Open Interface", JOptionPane.INFORMATION_MESSAGE)));
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
					RSInterface.save();
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

	@Override
	public void treeNodesChanged(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeNodesInserted(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}