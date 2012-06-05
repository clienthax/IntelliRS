package rs2.swing.edit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import rs2.ActionHandler;
import rs2.Main;
import rs2.editor.RSInterface;
import rs2.graphics.RSImage;

@SuppressWarnings("serial")
public class ImagePane extends JFrame implements ActionListener, ItemListener {

	public ImagePane(RSInterface rsi) {
		this.rsi = rsi;
		setTitle("Image Editor - " + rsi.id);
		setLayout(null);
		buildPreviewPanel();
		addButtons();
		setSize(getContainerWidth(this), getContainerHeight(this));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
	}

	public int getContainerWidth(Component component) {
		Container container = null;
		if (component instanceof JFrame) {
			container = this.getContentPane();
		} else {
			container = (Container) component;
		}
		int width = 0;
		for (int index = 0; index < container.getComponentCount(); index++) {
			int offset = (container.getComponent(index).getX() + container.getComponent(index).getWidth());
			if (offset > width) {
				width = offset;
			}
		}
		if (component == preview) {
			width += preview.getInsets().left + preview.getInsets().right;
		}
		return width + (component == this ? getFrameInsets().left + getFrameInsets().right : 0);
	}

	public int getContainerHeight(Component component) {
		Container container = null;
		if (component instanceof JFrame) {
			container = this.getContentPane();
		} else {
			container = (Container) component;
		}
		int height = 0;
		for (int index = 0; index < container.getComponentCount(); index++) {
			int offset = (container.getComponent(index).getY() + container.getComponent(index).getHeight());
			if (offset > height) {
				height = offset;
			}
		}
		if (component == preview) {
			height += preview.getInsets().top + preview.getInsets().bottom;
		}
		return height + (component == this ? getFrameInsets().top + getFrameInsets().bottom : 0);
	}

	public Insets getFrameInsets() {
		return new Insets(30, 4, 4, 4);
	}

	public Dimension getOffset(Component component) {
		return new Dimension(component.getX() + component.getWidth(), component.getY() + component.getHeight());
	}

	public int getTextWidth(Component component, String str) {
		int width = 50;
		Font font = component.getFont();
		if (font != null) {
			FontMetrics metrics = Main.getInstance().getMetrics(font);
			if (str != null && metrics != null) {
				width = metrics.stringWidth(str);
			}
		}
		width += 16;
		return width;
	}

	public void buildPreviewPanel() {
		if (rsi == null) {
			return;
		}
		preview = new JPanel();
		preview.setBorder(new TitledBorder("Preview"));
		preview.setLayout(null);
		addDisabledComponents(preview);
		preview.setBounds(0, 0, getContainerWidth(preview), getContainerHeight(preview));
		add(preview);
	}

	public void addButtons() {
		if (rebuilding) {
			remove(save);
		}
		int width = 100;
		int height = 20;
		int centerX = getContainerWidth(this) / 2;
		int y = getContainerHeight(this) - (getFrameInsets().top + getFrameInsets().bottom);
		save = new JButton("Save");
		save.addActionListener(this);
		save.setBounds(centerX - (width / 2), y, width, height);
		add(save);
	}

	public void addDisabledComponents(JPanel parent) {
		int startX = 12;
		int startY = 20;
		int x = startX;
		int y = startY;
		int archiveIndex = Main.getInstance().mediaArchive.getIndexForName(rsi.disabledSpriteName + ".dat");
		int selectedIndex = rsi.disabledSpriteId;
		RSImage[] images = Main.getInstance().mediaArchive.archiveToRSImages(rsi.disabledSpriteName + ".dat");
		/* The disabled sprite archive dropdown box */
		disabledArchive = new JComboBox();
		disabledArchive.addItemListener(this);
		String[] list = Main.getInstance().mediaArchive.getArchiveList();
		for (String item : list) {
			disabledArchive.addItem(item.replace(".dat", ""));
		}
		disabledArchive.setSelectedIndex(archiveIndex);
		disabledArchive.setBounds(x, y, 120, 20);
		parent.add(disabledArchive);
		/* The disabled sprite index dropdown box */
		disabledIndex = new JComboBox();
		disabledIndex.addItemListener(this);
		for (int index = 0; index < images.length; index++) {
			disabledIndex.addItem(index);
		}
		disabledIndex.setSelectedIndex(selectedIndex);
		disabledIndex.setBounds((int) getOffset(disabledArchive).getWidth() + 5, y, 50, 20);
		parent.add(disabledIndex);
		/* The disabled sprite image label */
		disabledLabel = new JLabel(new ImageIcon(images[selectedIndex].getImage()));
		disabledLabel.setBounds(x, (int) getOffset(disabledArchive).getHeight() + 5, images[selectedIndex].myWidth, images[selectedIndex].myHeight);
		parent.add(disabledLabel);
	}

	public void rebuildPane() {
		rebuilding = true;
		if (preview != null) {
			preview.setSize(getContainerWidth(preview), getContainerHeight(preview));
			preview.repaint();
		}
		if (save != null) {
			addButtons();
		}
		if (this != null) {
			this.setSize(getContainerWidth(this), getContainerHeight(this));
			this.repaint();
		}
		rebuilding = false;
	}

	public void save() {
		if (rsi == null) {
			return;
		}
		if (!rsi.disabledSpriteName.equalsIgnoreCase(disabledArchive.getSelectedItem().toString())) {
			rsi.disabledSpriteName = disabledArchive.getSelectedItem().toString();
			rsi.disabledSpriteId = disabledIndex.getSelectedIndex();
			ActionHandler.updateSprite(rsi, true);
		}
		if (rsi.disabledSpriteId != disabledIndex.getSelectedIndex()) {
			rsi.disabledSpriteName = disabledArchive.getSelectedItem().toString();
			rsi.disabledSpriteId = disabledIndex.getSelectedIndex();
			ActionHandler.updateSprite(rsi, true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand().toLowerCase();
		if (cmd != null) {
			if (cmd.equals("save")) {
				save();
				dispose();
			}
			if (cmd.equals("cancel")) {
				dispose();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getSource();
		if (source == disabledArchive) {
			if (disabledLabel != null) {
				Container parent = disabledLabel.getParent();
				String archive = disabledArchive.getSelectedItem().toString();
				int selected = disabledIndex.getSelectedIndex();
				//rebuild the index combobox
				disabledIndex.removeAllItems();
				int indexCount = Main.getInstance().mediaArchive.archiveToRSImages(archive + ".dat").length;
				for (int index = 0; index < indexCount; index++) {
					disabledIndex.addItem(index);
				}
				disabledIndex.setSelectedIndex(0);
				disabledIndex.repaint();
				//rebuilding the image label
				parent.remove(disabledLabel);
				RSImage image = new RSImage(Main.media, archive, selected);
				disabledLabel = new JLabel(new ImageIcon(image.getImage()));
				disabledLabel.setBounds(12, (int) getOffset(disabledArchive).getHeight() + 5, image.myWidth, image.myHeight);
				parent.add(disabledLabel);
				parent.repaint();
				rebuildPane();
			}
		}
		if (source == disabledIndex) {
			if (disabledLabel != null) {
				Container parent = disabledLabel.getParent();
				String archive = disabledArchive.getSelectedItem().toString();
				int selected = disabledIndex.getSelectedIndex();
				//rebuild the image label
				parent.remove(disabledLabel);
				RSImage image = new RSImage(Main.media, archive, selected);
				disabledLabel = new JLabel(new ImageIcon(image.getImage()));
				disabledLabel.setBounds(12, (int) getOffset(disabledArchive).getHeight() + 5, image.myWidth, image.myHeight);
				parent.add(disabledLabel);
				parent.repaint();
				rebuildPane();
			}
		}
	}

	public boolean rebuilding = false;
	public RSInterface rsi;
	public JPanel preview;
	public JLabel disabledLabel;
	public JComboBox disabledArchive;
	public JComboBox disabledIndex;
	public JButton save;
	public JButton cancel;

}