package rs2.swing.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.LinkedList;

import javax.swing.JPanel;

public class RelativeLayout implements LayoutManager {

	public int vgap, hgap;
	private int minWidth = 0, minHeight = 0;
	private int preferredWidth = 0, preferredHeight = 0;
	private boolean sizeUnknown = true;

	public RelativeLayout() {
		this.vgap = 5;
		this.hgap = 5;
		resetComponents();
	}

	public RelativeLayout(int gap) {
		this.vgap = gap;
		this.hgap = gap;
		resetComponents();
	}

	public RelativeLayout(int vgap, int hgap) {
		this.vgap = vgap;
		this.hgap = hgap;
		resetComponents();
	}

	public void resetComponents() {
		this.defaultComponents = new LinkedList<Component>();
		this.bottomComponents = new LinkedList<Component>();
		this.bottomCenter = new LinkedList<Component>();
	}

	private void setSizes(Container parent) {
		int count = parent.getComponentCount();
		Insets insets = parent.getInsets();
		if (parent instanceof JPanel) {
			insets = new Insets(0, 0, 0, 0);
		}
		int insetWidth = 0;
		int insetHeight = 0;
		if (insets != null) {
			insetWidth = insets.left + insets.right;
			insetHeight = insets.top + insets.bottom;
		}
		Dimension dimension = null;
		preferredWidth = 0;
		preferredHeight = 0;
		minWidth = 0;
		minHeight = 0;
		for (int index = 0; index < count; index++) {
			Component component = parent.getComponent(index);
			if (component != null && component.isVisible()) {
				dimension = component.getPreferredSize();
				if (dimension == null) {
					dimension = component.getMinimumSize();
				}
				if (preferredWidth < component.getX() + dimension.width + hgap) {
					preferredWidth = component.getX() + dimension.width + hgap;
				}
				if (preferredHeight < component.getY() + dimension.height + vgap) {
					preferredHeight = component.getY() + dimension.height + vgap;
				}
			}
		}
		preferredWidth += hgap;
		preferredWidth += insetWidth;
		preferredHeight += vgap;
		preferredHeight += insetHeight;
		minHeight = preferredHeight;
		minWidth = preferredWidth;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension dimension = new Dimension(0, 0);
		setSizes(parent);
		Insets insets = parent.getInsets();
		dimension.width = preferredWidth + insets.left + insets.right;
		dimension.height = preferredHeight + insets.top + insets.bottom;
		sizeUnknown = false;
		return dimension;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension dimension = new Dimension(0, 0);
		Insets insets = parent.getInsets();
		dimension.width = minWidth + insets.left + insets.right;
		dimension.height = minHeight + insets.top + insets.bottom;
		sizeUnknown = false;
		return dimension;
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		int components = parent.getComponentCount();
		int previousWidth = 0;
		int previousX = 0, previousY = 0;
		int x = insets.left + hgap;
		int y = insets.top + vgap;
		if (sizeUnknown) {
			setSizes(parent);
		}
		for (int index = 0; index < components; index++) {
			Component component = parent.getComponent(index);
			if (component.isVisible()) {
				if (!defaultComponents.isEmpty() && defaultComponents.contains(component)) {
					x = previousX + previousWidth + hgap;
					y = previousY > y ? previousY : y;
				} else if (!bottomComponents.isEmpty() && bottomComponents.contains(component)) {
					x = getBottomX(parent);
					y = getBottomY(parent);
				} else if (!bottomCenter.isEmpty() && bottomCenter.contains(component)) {
					arrange(parent);
					x = getBottomCenterX(parent, component);
					y = getBottomY(parent);
				}
				Dimension dimension = component.getPreferredSize();
				component.setBounds(x, y, dimension.width, dimension.height);
				previousX = component.getX();
				previousY = component.getY();
				previousWidth = dimension.width;
			}
		}
	}

	@Override
	public void addLayoutComponent(String string, Component component) {
		if (component == null) {
			return;
		}
		if (string == null) {
			if (!defaultComponents.contains(component)) {
				defaultComponents.add(component);
			}
			return;
		}
		if (string.equalsIgnoreCase(DEFAULT)) {
			if (!defaultComponents.contains(component)) {
				defaultComponents.add(component);
			}
		} else if (string.equalsIgnoreCase(TOP)) {
		} else if (string.equalsIgnoreCase(BOTTOM)) {
			if (!bottomComponents.contains(component)) {
				bottomComponents.add(component);
			}
		} else if (string.equalsIgnoreCase(BOTTOM_CENTER)) {
			if (!bottomCenter.contains(component)) {
				bottomCenter.add(component);
			}
		} else if (string.equalsIgnoreCase(ABOVE)) {

		} else if (string.equalsIgnoreCase(BELOW)) {

		} else if (string.equalsIgnoreCase(LEFT)) {

		} else if (string.equalsIgnoreCase(LEFT_OF)) {

		} else if (string.equalsIgnoreCase(RIGHT)) {

		} else if (string.equalsIgnoreCase(RIGHT_OF)) {

		} else if (string.equalsIgnoreCase(CENTER)) {

		} else if (string.equalsIgnoreCase(CENTER_X)) {

		} else if (string.equalsIgnoreCase(CENTER_Y)) {

		} else {
			defaultComponents.add(component);
		}
	}

	@Override
	public void removeLayoutComponent(Component component) {
		if (defaultComponents.contains(component)) {
			defaultComponents.remove(component);
		}
		if (bottomComponents.contains(component)) {
			bottomComponents.remove(component);
		}
		if (bottomCenter.contains(component)) {
			bottomCenter.remove(component);
		}
	}

	public void arrange(Container parent) {
		for (int index = 0; index < parent.getComponentCount(); index++) {
			Component component = parent.getComponent(index);
			if (component != null && bottomCenter.contains(component)) {
				component.setLocation(getBottomCenterX(parent, component), component.getY());
				parent.repaint();
			}
		}
	}

	public int getBottomCenterX(Container parent, Component component) {
		LinkedList<Component> components = new LinkedList<Component>();
		LinkedList<Integer> locations = new LinkedList<Integer>();
		for (int index = 0; index < parent.getComponentCount(); index++) {
			components.add(parent.getComponent(index));
		}
		int centerX = parent.getWidth() / 2;
		int width = getWidth(bottomCenter);
		int x = centerX - (width / 2);
		for (Component c : bottomCenter) {
			int index = bottomCenter.indexOf(c);
			if (index > 0) {
				x += getDimensions(bottomCenter.get(index - 1)).width;
				x += hgap;
			}
			locations.add(index, x);
		}
		if (bottomCenter.contains(component)) {
			return locations.get(bottomCenter.indexOf(component));
		}
		return x;
	}

	public int getBottomX(Container parent) {
		if (bottomComponents.size() == 1) {
			return hgap;
		}
		int x = 0;
		Component[] components = parent.getComponents();
		for (int index = 0; index < components.length; index++) {
			Component component = components[index];
			if (bottomComponents.contains(component)) {
				if (x < component.getX() + component.getWidth()) {
					x = component.getX() + component.getWidth();
				}
			}
		}
		return x + hgap;
	}

	public int getBottomY(Container parent) {
		int y = 0;
		Component[] components = parent.getComponents();
		for (int index = 0; index < components.length; index++) {
			Component component = components[index];
			if (!bottomComponents.contains(component) && !bottomCenter.contains(component)) {
				if (y < component.getY() + getDimensions(component).height) {
					y = component.getY() + getDimensions(component).height;
				}
			}
		}
		return y + vgap;
	}

	public int getWidth(LinkedList<Component> list) {
		if (list == null) {
			return 0;
		}
		if (list.size() == 1) {
			return getDimensions(list.get(0)).width;
		}
		int width = 0;
		for (Component c : list) {
			if (list.contains(c)) {
				width += getDimensions(c).width;
			}
		}
		width += (list.size() - 1) * hgap;
		return width;
	}

	public Dimension getDimensions(Component component) {
		if (component == null) {
			return null;
		}
		Dimension dimension = component.getPreferredSize();
		if (dimension == null) {
			dimension = component.getMinimumSize();
		}
		return dimension;
	}

	private LinkedList<Component> defaultComponents, bottomComponents, bottomCenter;

	/**
	 * To the right of the previous component.
	 */
	public static final String DEFAULT = "default";

	/**
	 * The top row of components.
	 */
	public static final String TOP = "top";

	/**
	 * The bottom row of components.
	 */
	public static final String BOTTOM = "bottom";
	public static final String BOTTOM_CENTER = "bottom center";
	public static final String ABOVE = "above";
	public static final String BELOW = "below";
	public static final String LEFT = "left";
	public static final String LEFT_OF = "left of";
	public static final String RIGHT = "right";
	public static final String RIGHT_OF = "right of";
	public static final String CENTER = "center"; 
	public static final String CENTER_X = "center x";
	public static final String CENTER_Y = "center y";

}