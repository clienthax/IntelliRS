package rs2.rsinterface;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import rs2.constants.Constants;
import rs2.io.JagexBuffer;
import rs2.swing.impl.RSIFileFilter;
import rs2.util.DataUtils;
import rs2.util.SwingUtils;

public class Import {

	public Import() {
		location = SwingUtils.getFilePath("Select Import File", Constants.getExportDirectory(), false, new RSIFileFilter());
		importFile();
	}

	public void importFile() {
		if (location == null) {
			return;
		}
		try {
			JagexBuffer buffer = new JagexBuffer(DataUtils.readFile(location));
			main = Utilities.readHeaderChunk(buffer);
			Utilities.readTypeChunk(main, buffer);
			int children = buffer.getShort();
			if (children != 0) {
				for (int index = 0; index < children; index++) {
					RSInterface child = Utilities.readHeaderChunk(buffer);
					Utilities.readTypeChunk(child, buffer);
					int subchildren = buffer.getShort();
					if (subchildren != 0) {
						for (int sub = 0; sub < subchildren; sub++) {
							RSInterface subchild = Utilities.readHeaderChunk(buffer);
							Utilities.readTypeChunk(subchild, buffer);
						}
						int subsubchildren = buffer.getShort();
						if (subsubchildren != 0) {
							for (int sub = 0; sub < subchildren; sub++) {
								RSInterface subchild = Utilities.readHeaderChunk(buffer);
								Utilities.readTypeChunk(subchild, buffer);
							}
						}
					}
				}
			}
			JOptionPane.showMessageDialog(null, "Interface " + main.id + " was imported successfully.");
		} catch (Exception e) {
			log("An error occurred while importing the interface.");
			e.printStackTrace();
		}
	}

	public void log(Object o) {
		String prefix = main == null ? "[Import]" : "[Import-" + main.id + "]";
		System.out.println(prefix + ": " + o);
	}

	public String location = null;
	public RSInterface main;
	public LinkedList<RSInterface> children;
	public LinkedList<RSInterface> layers;
	public HashMap<RSInterface, RSInterface[]> subchildren;
	public HashMap<RSInterface, RSInterface[]> subsubchildren;

}