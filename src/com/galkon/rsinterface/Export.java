package com.galkon.rsinterface;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.galkon.constants.Constants;
import com.galkon.io.RSByteArrayOutputStream;
import com.galkon.util.DataUtils;


public class Export {

	public Export(RSInterface main) {
		if (main == null) {
			JOptionPane.showMessageDialog(null, "This is not a valid interface!");
			return;
		}
		this.main = main;
		setChildren();
		if (layers != null && !layers.isEmpty()) {
			setSubChildren();
		}
		if (subchildren != null && !subchildren.isEmpty()) {
			setSubSubChildren();
		}
		export();
	}

	public void setChildren() {
		if (main == null) {
			return;
		}
		if (main.hasChildren()) {
			children = new LinkedList<RSInterface>();
			layers = new LinkedList<RSInterface>();
			for (int index = 0; index < main.children.size(); index++) {
				RSInterface child = RSInterface.getInterface(main.children.get(index));
				if (child.hasChildren()) {
					layers.add(child);
				}
				children.add(child);
			}
		}
	}

	public void setSubChildren() {
		if (layers == null) {
			return;
		}
		subchildren = new HashMap<RSInterface, RSInterface[]>();
		for (RSInterface layer : layers) {
			if (layer.hasChildren()) {
				RSInterface[] children = new RSInterface[layer.children.size()];
				for (int index = 0; index < children.length; index++) {
					children[index] = RSInterface.getInterface(layer.children.get(index));
				}
				subchildren.put(layer, children);
			}
		}
	}
	public void setSubSubChildren() {
		if (subchildren == null) {
			return;
		}
		subsubchildren = new HashMap<RSInterface, RSInterface[]>();
		for (RSInterface layer : subchildren.keySet()) {
			if (layer.hasChildren()) {
				RSInterface[] children = new RSInterface[layer.children.size()];
				for (int index = 0; index < children.length; index++) {
					children[index] = RSInterface.getInterface(layer.children.get(index));
				}
				subsubchildren.put(layer, children);
			}
		}
	}

	public void export() {
		if (main == null) {
			return;
		}
		File directory = new File(Constants.getExportDirectory());
		if (!directory.exists()) {
			directory.mkdir();
		}
		try {
			RSByteArrayOutputStream buffer = new RSByteArrayOutputStream();
			buffer.write(DataChunk.getHeaderChunk(main));
			buffer.write(DataChunk.getTypeChunk(main));
			if (children != null && !children.isEmpty()) {
				buffer.putShort(children.size());
				for (RSInterface child : children) {
					buffer.write(DataChunk.getHeaderChunk(child));
					buffer.write(DataChunk.getTypeChunk(child));
					if (layers.contains(child)) {
						buffer.putShort(subchildren.get(child).length);
						for (RSInterface layer : subchildren.get(child)) {
							buffer.write(DataChunk.getHeaderChunk(layer));
							buffer.write(DataChunk.getTypeChunk(layer));
							if (subsubchildren.containsKey(layer)) {
								buffer.putShort(subsubchildren.get(layer).length);
								for (RSInterface layerchild : subsubchildren.get(layer)) {
									buffer.write(DataChunk.getHeaderChunk(layerchild));
									buffer.write(DataChunk.getTypeChunk(layerchild));
								}
							} else {
								buffer.putShort(0);
							}
						}
					} else {
						buffer.putShort(0);
					}
				}
			} else {
				buffer.putShort(0);
			}
			buffer.close();
			byte[] data = buffer.toByteArray();
			DataUtils.writeFile(Constants.getExportDirectory() + main.id + Constants.INTERFACE_FILE_EXTENSION, data);
			JOptionPane.showMessageDialog(null, "Interface " + main.id + " was exported successfully.");
		} catch (Exception e) {
			System.out.println("[Export-" + main.id + "]: An error occurred while exporting the interface.");
			e.printStackTrace();
		}
	}

	public void log(Object o) {
		String prefix = main == null ? "[Export]" : "[Export-" + main.id + "]";
		System.out.println(prefix + ": " + o);
	}

	public RSInterface main;
	public LinkedList<RSInterface> children;
	public LinkedList<RSInterface> layers;
	public HashMap<RSInterface, RSInterface[]> subchildren;
	public HashMap<RSInterface, RSInterface[]> subsubchildren;

}