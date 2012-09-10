package com.galkon.swing.edit;

import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import com.galkon.ActionHandler;
import com.galkon.Main;
import com.galkon.graphics.RSImage;
import com.galkon.rsinterface.RSInterface;
import com.galkon.swing.impl.RelativeLayout;


public class ImagePane implements ActionListener, ItemListener {

	public int gap = 5;

	public ImagePane(RSInterface rsi) {
		this.rsi = rsi;
		frame = new JFrame("Editing: " + rsi.id);
		frame.setLayout(new RelativeLayout(0));
		buildPreviewPanel(null);
		addButtons();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.toFront();
	}

	public void buildPreviewPanel(String name) {
		if (rsi == null) {
			return;
		}
		if (preview == null) {
			preview = new JPanel(new BorderLayout());
			preview.setBorder(new TitledBorder("Preview"));
		}
		if (disabledPanel == null) {
			disabledPanel = new JPanel(new SpringLayout());
		}
		addPanel(disabledPanel, name);
		if (rsi.enabledSprite != null) {
			if (enabledPanel == null) {
				enabledPanel = new JPanel(new SpringLayout());
			}
			addPanel(enabledPanel, name);
		} else {
			JButton button = new JButton("Set Enabled");
			button.addActionListener(this);
			
		}
		if (!rebuilding) {
			frame.add(preview, RelativeLayout.DEFAULT);
		}
	}

	public void addPanel(Container parent, String name) {
		boolean disabled = parent == disabledPanel;
		JComboBox archiveBox = disabled ? disabledArchive : enabledArchive;
		JComboBox indexBox = disabled ? disabledIndex : enabledIndex;
		JLabel imageLabel = disabled ? disabledLabel : enabledLabel;
		String archiveName = disabled ? "disabled archive" : "enabled archive";
		String indexName = disabled ? "disabled index" : "enabled index";
		String archive;
		int selected;
		RSImage image;
		if (rebuilding) {
			archive = archiveBox.getSelectedItem().toString();
			selected = name.equals(archiveName) ? 0 : indexBox.getSelectedIndex();
			image = new RSImage(Main.media, archive, selected);
		} else {
			archive = disabled ? rsi.disabledSpriteArchive : rsi.enabledSpriteArchive;
			selected = disabled ? rsi.disabledSpriteId : rsi.enabledSpriteId;
			image = disabled ? rsi.disabledSprite : rsi.enabledSprite;
		}
		int count = Main.getInstance().mediaArchive.archiveToRSImages(archive + ".dat").length;
		String[] list = Main.getInstance().mediaArchive.getArchiveList();
		
		if (archiveBox == null) {
			archiveBox = new JComboBox();
			archiveBox.setName(archiveName);
			for (String item : list) {
				archiveBox.addItem(item.replace(".dat", ""));
			}
			archiveBox.setSelectedItem(archive);
			archiveBox.addItemListener(this);
		}

		if (indexBox == null) {
			indexBox = new JComboBox();
			indexBox.setName(indexName);
			for (int index = 0; index < count; index++) {
				indexBox.addItem(index);
			}
			indexBox.setSelectedItem(selected);
			indexBox.addItemListener(this);
		}
		if (indexBox != null && name != null && name.equals(archiveName) && rebuilding) {
			indexBox.removeAllItems();
			for (int index = 0; index < count; index++) {
				indexBox.addItem(index);
			}
			indexBox.setSelectedItem(selected);
		}

		ImageIcon labelImage = new ImageIcon(image.getImage());
		if (imageLabel != null && rebuilding) {
			labelImage.getImage().flush();
			imageLabel.setIcon(labelImage);
		} else {
			imageLabel = new JLabel(labelImage);
		}

		archiveBox.setMaximumSize(archiveBox.getMinimumSize());
		indexBox.setMaximumSize(indexBox.getMinimumSize());
		SpringLayout layout = (SpringLayout) parent.getLayout();
		if (!rebuilding) {
			layout.putConstraint(SpringLayout.NORTH, parent, gap, SpringLayout.NORTH, frame);
			layout.putConstraint(SpringLayout.WEST, parent, gap, SpringLayout.WEST, frame);
			layout.putConstraint(SpringLayout.WEST, archiveBox, gap, SpringLayout.WEST, parent);
			layout.putConstraint(SpringLayout.NORTH, archiveBox, gap, SpringLayout.NORTH, parent);
			parent.add(archiveBox);
			layout.putConstraint(SpringLayout.WEST, indexBox, gap, SpringLayout.EAST, archiveBox);
			layout.putConstraint(SpringLayout.NORTH, indexBox, 0, SpringLayout.NORTH, archiveBox);
			parent.add(indexBox);
			layout.putConstraint(SpringLayout.WEST, imageLabel, gap, SpringLayout.WEST, parent);
			layout.putConstraint(SpringLayout.NORTH, imageLabel, gap, SpringLayout.SOUTH, archiveBox);
			parent.add(imageLabel);
			layout.putConstraint(SpringLayout.EAST, parent, gap, SpringLayout.EAST, indexBox);
			layout.putConstraint(SpringLayout.SOUTH, parent, gap, SpringLayout.SOUTH, imageLabel);
		} else {
			int indexPos = indexBox.getX() + indexBox.getWidth();
			int labelPos = imageLabel.getX() + image.myWidth;
			layout.putConstraint(SpringLayout.EAST, parent, gap, SpringLayout.EAST, indexPos > labelPos ? indexBox : imageLabel);
		}
		if (!rebuilding) {
			preview.add(parent, parent == disabledPanel ? BorderLayout.NORTH : BorderLayout.SOUTH);
		}
		if (disabled) {
			disabledArchive = archiveBox;
			disabledIndex = indexBox;
			disabledLabel = imageLabel;
		} else {
			enabledArchive = archiveBox;
			enabledIndex = indexBox;
			enabledLabel = imageLabel;
		}
	}

	public void addButtons() {
		if (save == null) {
			save = new JButton("Save");
			save.addActionListener(this);
		}
		if (cancel == null) {
			cancel = new JButton("Cancel");
			cancel.addActionListener(this);
		}
		if (!rebuilding) {
			buttonPanel = new JPanel();
		} else {
			frame.remove(buttonPanel);
		}
		if (!rebuilding) {
			buttonPanel.add(save);
			buttonPanel.add(cancel);
		}
		frame.add(buttonPanel, RelativeLayout.BOTTOM_CENTER);
	}

	public void rebuildPane(String name) {
		if (rebuilding) {
			return;
		}
		rebuilding = true;
		buildPreviewPanel(name);
		addButtons();
		frame.setVisible(true);
		frame.pack();
		rebuilding = false;
	}

	public void save() {
		if (rsi == null) {
			return;
		}
		if (!rsi.disabledSpriteArchive.equalsIgnoreCase(disabledArchive.getSelectedItem().toString())) {
			rsi.disabledSpriteArchive = disabledArchive.getSelectedItem().toString();
			rsi.disabledSpriteId = disabledIndex.getSelectedIndex();
			ActionHandler.updateSprite(rsi, true);
		}
		if (rsi.disabledSpriteId != disabledIndex.getSelectedIndex()) {
			rsi.disabledSpriteArchive = disabledArchive.getSelectedItem().toString();
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
				frame.dispose();
			}
			if (cmd.equals("cancel")) {
				frame.dispose();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getSource();
		if (source instanceof JComboBox) {
			String name = ((JComboBox) source).getName();
			rebuildPane(name);
		}
	}

	public boolean rebuilding = false;
	public RSInterface rsi = null;
	public JFrame frame = null;
	public JPanel preview = null, buttonPanel = null, disabledPanel = null, enabledPanel = null;
	public JLabel disabledLabel = null, enabledLabel = null;
	public JComboBox disabledArchive = null, enabledArchive = null;
	public JComboBox disabledIndex = null, enabledIndex = null;
	public JButton save = null;
	public JButton cancel = null;

}