package rs2.swing.edit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import rs2.Main;
import rs2.rsinterface.RSInterface;
import rs2.util.Utils;

@SuppressWarnings("serial")
public class TextPane extends JFrame implements ActionListener, ItemListener {

	public TextPane(RSInterface rsi) {
		this.rsi = rsi;
		this.fontSize = rsi.fontId;
		setTitle("Text Editor - " + rsi.id);
		setLayout(null);
		displayData();
		setVisible(true);
		setResizable(false);
		Insets insets = new Insets(30, 5, 5, 5);
		setSize(paneWidth + insets.left + insets.right, paneHeight + insets.top + insets.bottom);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
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

	public int paneWidth = 400;
	public int paneHeight = 300;

	public void displayData() {
		if (rsi != null) {
			//text (e.g. disabledText)
			JPanel text = new JPanel();
			text.setBorder(new TitledBorder("Text"));
			text.setLayout(null);
			disabledText = new JTextField(rsi.disabledText);
			enabledText = new JTextField(rsi.enabledText);
			JLabel disabled = new JLabel("Disabled text:");
			JLabel enabled = new JLabel("Enabled text:");
			int x = 12;
			int y = 17;
			disabled.setBounds(x, y, 75, 20);
			text.add(disabled);
			disabledText.setBounds(disabled.getX() + disabled.getWidth(), y, 100, 20);
			text.add(disabledText);
			y += 25;
			enabled.setBounds(x, y, 75, 20);
			text.add(enabled);
			enabledText.setBounds(enabled.getX() + enabled.getWidth(), y, 100, 20);
			text.add(enabledText);
			text.setBounds(0, 0, disabled.getWidth() + disabledText.getWidth() + x * 2, disabled.getHeight() + enabled.getHeight() + 34);
			add(text);

			//colors (e.g. disabled color and enabled color)
			JPanel colors = new JPanel();
			colors.setBorder(new TitledBorder("Colors"));
			colors.setLayout(null);
			int labelSize = 20;
			x = 12;
			y = 20;
			String[] labels = { "Disabled color", "Enabled color", "Disabled hover color", "Enabled hover color" };
			int[] color = { rsi.disabledColor, rsi.enabledColor, rsi.disabledHoverColor, rsi.enabledHoverColor };
			for (int index = 0; index < labels.length; index++) {
				if (index == 1 || index == 3) {
					y = 20 + 25;
				} else {
					y = 20;
				}
				if (index > 1) {
					x = 12 + labelSize + 3 + colorButtons[0].getWidth() + 10;
				}
				colorLabels[index] = new JLabel();
				colorLabels[index].setOpaque(true);
				colorLabels[index].setBackground(Utils.getColor(color[index]));
				colorLabels[index].setBounds(x, y, labelSize, labelSize);
				colors.add(colorLabels[index]);
				colorButtons[index] = new JButton(labels[index]);
				colorButtons[index].addActionListener(this);
				colorButtons[index].setBounds(x + colorLabels[index].getWidth() + 3, colorLabels[index].getY(), getTextWidth(colorButtons[index], colorButtons[index].getText()), labelSize);
				colors.add(colorButtons[index]);
			}
			int componentWidth = (labelSize * 2) + (colorButtons[0].getWidth() + colorButtons[2].getWidth() + 3 + 3) + 10;
			int componentHeight = (labelSize * 2);
			colors.setBounds(0, text.getHeight(), (12 * 2) + componentWidth, (20 * 2 - 3) + componentHeight);
			add(colors);

			//other (e.g. centered and shadowed)
			x = 12;
			y = 20;
			JPanel other = new JPanel();
			other.setBorder(new TitledBorder("Other"));
			other.setLayout(null);

			String[] sizes = { "Small", "Regular", "Bold", "Script" };
			font = new JComboBox<String>();
			for (String name : sizes) {
				font.addItem(name);
			}
			font.setSelectedIndex(rsi.fontId);
			font.addItemListener(this);
			font.setBounds(x, y, 75, 20);

			String[] shadow = { "Unshadowed", "Shadowed" };
			shadowed = new JComboBox<String>();
			for (String name : shadow) {
				shadowed.addItem(name);
			}
			shadowed.setSelectedIndex(rsi.shadowed ? 1 : 0);
			shadowed.addItemListener(this);
			shadowed.setBounds(x, y + 25, 100, 20);

			String[] items = { "Uncentered", "Centered" };
			centered = new JComboBox<String>();
			for (String name : items) {
				centered.addItem(name);
			}
			centered.setSelectedIndex(rsi.centered ? 1 : 0);
			centered.addItemListener(this);
			centered.setBounds(x + font.getWidth() + 5, y, 95, 20);


			other.add(font);
			other.add(centered);
			other.add(shadowed);

			other.setBounds(text.getX() + text.getWidth() + 5, 0, text.getWidth(), text.getHeight());
			add(other);
			
			save = new JButton("Save");
			cancel = new JButton("Cancel");
			save.addActionListener(this);
			cancel.addActionListener(this);
		
			int saveY = colors.getY() + colors.getHeight() + 5;
			paneWidth = (text.getX() + text.getWidth()) * 2 + 10;
			paneHeight = saveY + 20 + 5;
			int width = 100;
			int space = 10;
			int centerX = (paneWidth / 2);
			int saveX = centerX - (width) - (space / 2);
			int cancelX = centerX + (space / 2);
			save.setBounds(saveX, saveY, 100, 20);
			cancel.setBounds(cancelX, saveY, 100, 20);
			add(save);
			add(cancel);
		}
	}

	public void showColorChooser(boolean enabled, boolean hoverColor) {
		if (rsi == null) {
			return;
		}
		String type = "";
		int hex = 0;
		if (enabled) {
			type = "Enabled" + (hoverColor ? " hover" : "");
			hex = hoverColor ? rsi.enabledHoverColor : rsi.enabledColor;
		} else {
			type = "Disabled" + (hoverColor ? " hover" : "");
			hex = hoverColor ? rsi.disabledHoverColor : rsi.disabledColor;
		}
		Color oldColor = Utils.getColor(hex);
		Color color = JColorChooser.showDialog(this, type + " color", oldColor);
		if (color == null) {
			color = oldColor;
		} else {
			if (!hoverColor) {
				if (enabled) {
					enabledColor = color;
					colorLabels[1].setBackground(color);
				} else {
					disabledColor = color;
					colorLabels[0].setBackground(color);
				}
			} else {
				if (enabled) {
					enabledHoverColor = color;
					colorLabels[3].setBackground(color);
				} else {
					disabledHoverColor = color;
					colorLabels[2].setBackground(color);
				}
			}
		}
	}

	public void save() {
		if (rsi == null) {
			return;
		}
		if (disabledText != null && disabledText.getText() != null) {
			rsi.disabledText =  disabledText.getText();
		}
		if (enabledText != null && enabledText.getText() != null) {
			rsi.enabledText =  enabledText.getText();
		}
		if (disabledColor != null) {
			rsi.disabledColor = Utils.getHex(disabledColor);
		}
		if (enabledColor != null) {
			rsi.enabledColor = Utils.getHex(enabledColor);
		}
		if (disabledHoverColor != null) {
			rsi.disabledHoverColor = Utils.getHex(disabledHoverColor);
		}
		if (enabledHoverColor != null) {
			rsi.enabledHoverColor = Utils.getHex(enabledHoverColor);
		}
		if (rsi.fontId != font.getSelectedIndex()) {
			int id = font.getSelectedIndex();
			rsi.fontId = id;
			rsi.font = rsi.fonts[id];
			rsi.height = rsi.fonts[id].getTextHeight(rsi.shadowed);
		}
		rsi.shadowed = shadowed.getSelectedIndex() == 1;
		rsi.centered = centered.getSelectedIndex() == 1;
		rsi.width = rsi.font.getTextWidth(rsi.disabledText);
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
			if (cmd.equals("disabled color")) {
				showColorChooser(false, false);
			}
			if (cmd.equals("enabled color")) {
				showColorChooser(true, false);
			}
			if (cmd.equals("disabled hover color")) {
				showColorChooser(false, true);
			}
			if (cmd.equals("enabled hover color")) {
				showColorChooser(true, true);
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getSource() == font) {
			fontSize = font.getSelectedIndex();
		}
	}

	public int fontSize;
	public JTextField disabledText;
	public JTextField enabledText;
	public Color disabledColor;
	public Color enabledColor;
	public Color disabledHoverColor;
	public Color enabledHoverColor;
	public RSInterface rsi;
	public JButton[] colorButtons = new JButton[4];
	public JLabel[] colorLabels = new JLabel[4];
	public JButton save;
	public JButton cancel;
	public JComboBox<String> font;
	public JComboBox<String> centered;
	public JComboBox<String> shadowed;

}