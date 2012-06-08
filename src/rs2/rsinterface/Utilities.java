package rs2.rsinterface;

import java.io.IOException;
import java.util.LinkedList;

import rs2.Main;
import rs2.cache.Archive;
import rs2.graphics.RSFont;
import rs2.graphics.RSImage;
import rs2.io.ExtendedByteArrayOutputStream;
import rs2.io.JagexBuffer;

public class Utilities {

	/**
	 * Gets the interface header data for the specified interface.
	 * @param rsi
	 * @return
	 */
	public static byte[] getHeaderChunk(RSInterface rsi) {
		if (rsi == null) {
			return null;
		}
		try {
			ExtendedByteArrayOutputStream buffer = new ExtendedByteArrayOutputStream();
			int parent = rsi.parentId;
			if (rsi.parentId != -1) {
				buffer.putShort(65535);
				buffer.putShort(parent);
				buffer.putShort(rsi.id);
			} else {
				buffer.putShort(rsi.id);
			}
			buffer.putByte(rsi.type);
			buffer.putByte(rsi.actionType);
			buffer.putShort(rsi.contentType);
			buffer.putShort(rsi.width);
			buffer.putShort(rsi.height);
			buffer.putByte(rsi.alpha);
			if (rsi.hoverId != -1) {
				buffer.putSpaceSaver(rsi.hoverId);
			} else {
				buffer.putByte(0);
			}
			int valueCompareTypeCount = 0;
			if (rsi.valueCompareType != null) {
				valueCompareTypeCount = rsi.valueCompareType.length;
			}
			buffer.putByte(valueCompareTypeCount);
			if (valueCompareTypeCount > 0) {
				for (int i = 0; i < valueCompareTypeCount; i++) {
					buffer.putByte(rsi.valueCompareType[i]);
					buffer.putShort(rsi.requiredValues[i]);
				}
			}
			int valueLength = 0;
			if (rsi.valueIndexArray != null) {
				valueLength = rsi.valueIndexArray.length;
			}
			buffer.putByte(valueLength);
			if (valueLength > 0) {
				for (int index = 0; index < valueLength; index++) {
					int total = rsi.valueIndexArray[index].length;
					buffer.putShort(total);
					for (int index2 = 0; index2 < total; index2++) {
						buffer.putShort(rsi.valueIndexArray[index][index2]);
					}
				}
			}
			buffer.close();
			return buffer.toByteArray();
		} catch (IOException e) {
			System.out.println("[Interface-" + rsi.id + "] An error occurred while writing the header chunk.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the interface type data for the specified interface.
	 * @param rsi
	 * @return
	 */
	public static byte[] getTypeChunk(RSInterface rsi) {
		if (rsi == null) {
			return null;
		}
		try {
			ExtendedByteArrayOutputStream buffer = new ExtendedByteArrayOutputStream();
			if (rsi.type == 0) {
				buffer.putShort(rsi.scrollMax);
				buffer.putByte(rsi.showInterface ? 1 : 0);
				buffer.putShort(rsi.children.size());
				for (int index = 0; index < rsi.children.size(); index++) {
					buffer.putShort(rsi.children.get(index));
					buffer.putShort(rsi.childX.get(index));
					buffer.putShort(rsi.childY.get(index));
				}
			} else if (rsi.type == 1) {
				buffer.putShort(0);
				buffer.putByte(0);
			} else if (rsi.type == 2) {
				buffer.putByte(rsi.itemsSwappable? 1 : 0);
				buffer.putByte(rsi.isInventoryInterface ? 1 : 0);
				buffer.putByte(rsi.usableItemInterface ? 1 : 0);
				buffer.putByte(rsi.deletesTargetSlot ? 1 : 0);
				buffer.putByte(rsi.invSpritePadX);
				buffer.putByte(rsi.invSpritePadY);
				for (int index = 0; index < 20; index++) {
					buffer.putByte(rsi.sprites[index] == null ? 0 : 1);
					if (rsi.sprites[index] != null) {
						buffer.putShort(rsi.spritesX[index]);
						buffer.putShort(rsi.spritesY[index]);
						buffer.putString(rsi.spriteNames[index] + "," + rsi.spriteIds[index]);
					}
				}
				for (int index = 0; index < 5; index++) {
					if (rsi.actions[index] != null) {
						buffer.putString(rsi.actions[index]);
					} else {
						buffer.putString("");
					}
				}
			} else if (rsi.type == 3) {
				buffer.putByte(rsi.filled ? 1 : 0);
			}
			if (rsi.type == 4 || rsi.type == 1) {
				buffer.putByte(rsi.centered ? 1 : 0);
				buffer.putByte(rsi.fontId);
				buffer.putByte(rsi.shadowed ? 1 : 0);
			}
			if (rsi.type == 4) {
				buffer.putString(rsi.disabledText);
				if (rsi.enabledText != null) {
					buffer.putString(rsi.enabledText);
				} else {
					buffer.putString("null");
				}
			}
			if (rsi.type == 1 || rsi.type == 3 || rsi.type == 4)
				buffer.putInt(rsi.disabledColor);
			if (rsi.type == 3 || rsi.type == 4) {
				buffer.putInt(rsi.enabledColor);
				buffer.putInt(rsi.disabledHoverColor);
				buffer.putInt(rsi.enabledHoverColor);
			}
			if (rsi.type == 5) {
				if (rsi.disabledSprite != null) {
					if (rsi.disabledSpriteArchive != null) {
						buffer.putString(rsi.disabledSpriteArchive + "," + rsi.disabledSpriteId);
					} else {
						buffer.putString("");
					}
				} else {
					buffer.putString("");
				}
				if (rsi.enabledSprite != null) {
					if (rsi.enabledSpriteArchive != null) {
						buffer.putString(rsi.enabledSpriteArchive + "," + rsi.enabledSpriteId);
					} else {
						buffer.putString("");
					}
				} else {
					buffer.putString("");
				}
			} else if (rsi.type == 6) {
				if (rsi.disabledMediaType != -1 && rsi.disabledMediaId > 0) {
					buffer.putSpaceSaver(rsi.disabledMediaId);
				} else {
					buffer.putByte(0);
				}
				if (rsi.enabledMediaType > 0) {
					buffer.putSpaceSaver(rsi.enabledMediaId);
				} else {
					buffer.putByte(0);
				}
				if (rsi.disabledAnimation > 0) {
					buffer.putSpaceSaver(rsi.disabledAnimation);
				} else {
					buffer.putByte(0);
				}
				if (rsi.enabledAnimation > 0) {
					buffer.putSpaceSaver(rsi.enabledAnimation);
				} else {
					buffer.putByte(0);
				}
				buffer.putShort(rsi.zoom);
				buffer.putShort(rsi.rotationX);
				buffer.putShort(rsi.rotationY);
			} else if (rsi.type == 7) {
				buffer.putByte(rsi.centered ? 1 : 0);
				buffer.putByte(rsi.fontId);
				buffer.putByte(rsi.shadowed ? 1 : 0);
				buffer.putInt(rsi.disabledColor);
				buffer.putShort(rsi.invSpritePadX);
				buffer.putShort(rsi.invSpritePadY);
				buffer.putByte(rsi.isInventoryInterface ? 1 : 0);
				for (int i = 0; i < 5; i++) {
					if (rsi.actions[i] != null) {
						buffer.putString(rsi.actions[i]);
					} else {
						buffer.putString("");
					}
				}
			}
			if (rsi.actionType == 2 || rsi.type == 2) {
				buffer.putString(rsi.selectedActionName);
				buffer.putString(rsi.spellName);
				buffer.putShort(rsi.spellUsableOn);
			}
			if (rsi.type == 8) {
				buffer.putString(rsi.disabledText);
			}
			if (rsi.actionType == 1 || rsi.actionType == 4 || rsi.actionType == 5 || rsi.actionType == 6) {
				buffer.putString(rsi.tooltip);
			}
			buffer.close();
			return buffer.toByteArray();
		} catch (IOException e) {
			System.out.println("[Interface-" + rsi.id + "] An error occurred while writing the type chunk.");
			e.printStackTrace();
		}
		return null;
	}

	public static RSInterface readHeaderChunk(JagexBuffer buffer) {
		try {
			int parentId = -1;
			int interfaceId = buffer.getUnsignedShort();
			if (interfaceId == 65535) {
				parentId = buffer.getUnsignedShort();
				interfaceId = buffer.getUnsignedShort();
			}
			RSInterface rsi = new RSInterface();
			RSInterface.cache.put(interfaceId, rsi);
			rsi.id = interfaceId;
			rsi.parentId = parentId;
			rsi.type = buffer.getUnsignedByte();
			rsi.actionType = buffer.getUnsignedByte();
			rsi.contentType = buffer.getUnsignedShort();
			rsi.width = buffer.getUnsignedShort();
			rsi.height = buffer.getUnsignedShort();
			rsi.alpha = (byte) buffer.getUnsignedByte();
			rsi.hoverId = buffer.getUnsignedByte();
			if (rsi.hoverId != 0) {
				rsi.hoverId = (rsi.hoverId - 1 << 8) + buffer.getUnsignedByte();
			} else {
				rsi.hoverId = -1;
			}
			int requiredmentIndex = buffer.getUnsignedByte();
			if (requiredmentIndex > 0) {
				rsi.valueCompareType = new int[requiredmentIndex];
				rsi.requiredValues = new int[requiredmentIndex];
				for (int index = 0; index < requiredmentIndex; index++) {
					rsi.valueCompareType[index] = buffer.getUnsignedByte();
					rsi.requiredValues[index] = buffer.getUnsignedShort();
				}
			}
			int valueType = buffer.getUnsignedByte();
			if (valueType > 0) {
				rsi.valueIndexArray = new int[valueType][];
				for (int valueIndex = 0; valueIndex < valueType; valueIndex++) {
					int size = buffer.getUnsignedShort();
					rsi.valueIndexArray[valueIndex] = new int[size];
					for (int nextIndex = 0; nextIndex < size; nextIndex++) {
						rsi.valueIndexArray[valueIndex][nextIndex] = buffer.getUnsignedShort();
					}
				}
			}
			return rsi;
		} catch (Exception e) {
			System.out.println("[Interface] An error occurred while reading the header chunk.");
			e.printStackTrace();
		}
		return null;
	}

	public static void readTypeChunk(RSInterface rsi, JagexBuffer buffer) {
		Archive media = Main.media;
		RSFont[] fonts = { Main.getInstance().small, Main.getInstance().regular, Main.getInstance().bold, Main.getInstance().fancy };
		if (rsi.type == 0) {
			rsi.scrollMax = buffer.getUnsignedShort();
			rsi.showInterface = buffer.getUnsignedByte() == 1;
			int totalChildren = buffer.getUnsignedShort();
			rsi.children = new LinkedList<Integer>();
			rsi.childX = new LinkedList<Integer>();
			rsi.childY = new LinkedList<Integer>();
			for (int index = 0; index < totalChildren; index++) {
				rsi.children.add(buffer.getUnsignedShort());
				rsi.childX.add(buffer.getShort());
				rsi.childY.add(buffer.getShort());
			}
		}
		if (rsi.type == 1) {
			buffer.getUnsignedShort();
			buffer.getUnsignedByte();
		}
		if (rsi.type == 2) {
			rsi.inventory = new int[rsi.width * rsi.height];
			rsi.inventoryAmount = new int[rsi.width * rsi.height];
			rsi.itemsSwappable = buffer.getUnsignedByte() == 1;
			rsi.isInventoryInterface = buffer.getUnsignedByte() == 1;
			rsi.usableItemInterface = buffer.getUnsignedByte() == 1;
			rsi.deletesTargetSlot = buffer.getUnsignedByte() == 1;
			rsi.invSpritePadX = buffer.getUnsignedByte();
			rsi.invSpritePadY = buffer.getUnsignedByte();
			rsi.spritesX = new int[20];
			rsi.spritesY = new int[20];
			rsi.sprites = new RSImage[20];
			rsi.spriteNames = new String[20];
			rsi.spriteIds = new int[20];
			for (int index = 0; index < 20; index++) {
				int dummy = buffer.getUnsignedByte();
				if (dummy == 1) {
					rsi.spritesX[index] = buffer.getShort();
					rsi.spritesY[index] = buffer.getShort();
					String spriteInfo = buffer.getString();
					if (media != null && spriteInfo.length() > 0) {
						int comma = spriteInfo.lastIndexOf(",");
						int id = Integer.parseInt(spriteInfo.substring(comma + 1));
						String name = spriteInfo.substring(0, comma);
						rsi.setSprites(index, name, id, media);
					}
				}
			}
			rsi.actions = new String[5];
			for (int index = 0; index < 5; index++) {
				rsi.actions[index] = buffer.getString();
				if (rsi.actions[index].length() == 0) {
					rsi.actions[index] = null;
				}
			}
		}
		if (rsi.type == 3) {
			rsi.filled = buffer.getUnsignedByte() == 1;
		}
		if (rsi.type == 4 || rsi.type == 1) {
			rsi.centered = buffer.getUnsignedByte() == 1;
			rsi.fontId = buffer.getUnsignedByte();
			if (fonts != null) {
				rsi.fonts = fonts;
				rsi.font = fonts[rsi.fontId];
			}
			rsi.shadowed = buffer.getUnsignedByte() == 1;
		}
		if (rsi.type == 4) {
			rsi.disabledText = buffer.getString();
			rsi.enabledText = buffer.getString();
		}
		if (rsi.type == 1 || rsi.type == 3 || rsi.type == 4) {
			rsi.disabledColor = buffer.getInt();
		}
		if (rsi.type == 3 || rsi.type == 4) {
			rsi.enabledColor = buffer.getInt();
			rsi.disabledHoverColor = buffer.getInt();
			rsi.enabledHoverColor = buffer.getInt();
		}
		if (rsi.type == 5) {
			String location = buffer.getString();
			if (media != null && location.length() > 0) {
				int comma = location.lastIndexOf(",");
				String name = location.substring(0, comma);
				int id = Integer.parseInt(location.substring(comma + 1));
				rsi.setDisabledSprite(name, id, media);
			}
			location = buffer.getString();
			if (media != null && location.length() > 0) {
				int comma = location.lastIndexOf(",");
				String name = location.substring(0, comma);
				int id = Integer.parseInt(location.substring(comma + 1));
				rsi.setEnabledSprite(name, id, media);
			}
		}
		if (rsi.type == 6) {
			int value = buffer.getUnsignedByte();
			if (value != 0) {
				rsi.disabledMediaType = 1;
				rsi.disabledMediaId = (value - 1 << 8) + buffer.getUnsignedByte();
			}
			value = buffer.getUnsignedByte();
			if (value != 0) {
				rsi.enabledMediaType = 1;
				rsi.enabledMediaId = (value - 1 << 8) + buffer.getUnsignedByte();
			}
			value = buffer.getUnsignedByte();
			if (value != 0) {
				rsi.disabledAnimation = (value - 1 << 8) + buffer.getUnsignedByte();
			} else {
				rsi.disabledAnimation = -1;
			}
			value = buffer.getUnsignedByte();
			if (value != 0) {
				rsi.enabledAnimation = (value - 1 << 8) + buffer.getUnsignedByte();
			} else {
				rsi.enabledAnimation = -1;
			}
			rsi.zoom = buffer.getUnsignedShort();
			rsi.rotationX = buffer.getUnsignedShort();
			rsi.rotationY = buffer.getUnsignedShort();
		}
		if (rsi.type == 7) {
			rsi.inventory = new int[rsi.width * rsi.height];
			rsi.inventoryAmount = new int[rsi.width * rsi.height];
			rsi.centered = buffer.getUnsignedByte() == 1;
			rsi.fontId = buffer.getUnsignedByte();
			if (fonts != null) {
				rsi.fonts = fonts;
				rsi.font = fonts[rsi.fontId];
			}
			rsi.shadowed = buffer.getUnsignedByte() == 1;
			rsi.disabledColor = buffer.getInt();
			rsi.invSpritePadX = buffer.getShort();
			rsi.invSpritePadY = buffer.getShort();
			rsi.isInventoryInterface = buffer.getUnsignedByte() == 1;
			rsi.actions = new String[5];
			for (int index = 0; index < 5; index++) {
				rsi.actions[index] = buffer.getString();
				if (rsi.actions[index].length() == 0) {
					rsi.actions[index] = null;
				}
			}
		}
		if (rsi.actionType == 2 || rsi.type == 2) {
			rsi.selectedActionName = buffer.getString();
			rsi.spellName = buffer.getString();
			rsi.spellUsableOn = buffer.getUnsignedShort();
		}
		if (rsi.type == 8) {
			rsi.disabledText = buffer.getString();
		}
		if (rsi.actionType == 1 || rsi.actionType == 4 || rsi.actionType == 5 || rsi.actionType == 6) {
			rsi.tooltip = buffer.getString();
			if (rsi.tooltip.length() == 0) {
				if (rsi.actionType == 1) {
					rsi.tooltip = "Ok";
				}
				if (rsi.actionType == 4) {
					rsi.tooltip = "Select";
				}
				if (rsi.actionType == 5) {
					rsi.tooltip = "Select";
				}
				if (rsi.actionType == 6) {
					rsi.tooltip = "Continue";
				}
			}
		}
	}

}