package rs2;

import java.io.IOException;

import rs2.cache.Archive;
import rs2.graphics.RSFont;
import rs2.graphics.RSImage;
import rs2.io.ExtendedByteArrayOutputStream;
import rs2.io.JagexBuffer;
import rs2.node.MemCache;
import rs2.util.TextUtils;

public class RSInterface {

	/**
	 * Loads the interfaces from data.dat.
	 * @param interfaces
	 * @param media
	 * @param fonts
	 */
	public static void load(Archive interfaces, Archive media, RSFont fonts[]) {
		spriteNodes = new MemCache(50000);
		//byte[] data = rs2.util.DataUtils.readFile(Constants.getCacheDirectory() + "data");
		byte[] data = interfaces.getFile("data");
		JagexBuffer buffer = new JagexBuffer(data);
		int parentId = -1;
		int total = buffer.getUnsignedShort();
		cache = new RSInterface[total];
		while (buffer.offset < buffer.payload.length) {
			int interfaceId = buffer.getUnsignedShort();
			if (interfaceId == 65535) {
				parentId = buffer.getUnsignedShort();
				interfaceId = buffer.getUnsignedShort();
			}
			RSInterface rsi = cache[interfaceId] = new RSInterface();
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
			if (rsi.type == 0) {
				rsi.scrollMax = buffer.getUnsignedShort();
				rsi.showInterface = buffer.getUnsignedByte() == 1;
				int totalChildren = buffer.getUnsignedShort();
				rsi.children = new int[totalChildren];
				rsi.childX = new int[totalChildren];
				rsi.childY = new int[totalChildren];
				for (int index = 0; index < totalChildren; index++) {
					rsi.children[index] = buffer.getUnsignedShort();
					rsi.childX[index] = buffer.getShort();
					rsi.childY[index] = buffer.getShort();
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
		spriteNodes = null;
	}

	/**
	 * Writes the interface cache (data.dat).
	 */
	public static byte[] getData() {
		try {
			ExtendedByteArrayOutputStream buffer = new ExtendedByteArrayOutputStream();
			buffer.putShort(cache.length);
			for (RSInterface rsi : cache) {
				if (rsi == null) {
					continue;
				}
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
				if (rsi.type == 0) {
					buffer.putShort(rsi.scrollMax);
					buffer.putByte(rsi.showInterface ? 1 : 0);
					buffer.putShort(rsi.children.length);
					for (int i = 0; i < rsi.children.length; i++) {
						buffer.putShort(rsi.children[i]);
						buffer.putShort(rsi.childX[i]);
						buffer.putShort(rsi.childY[i]);
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
						if (rsi.disabledSpriteName != null) {
							buffer.putString(rsi.disabledSpriteName + "," + rsi.disabledSpriteId);
						} else {
							buffer.putString("");
						}
					} else {
						buffer.putString("");
					}
					if (rsi.enabledSprite != null) {
						if (rsi.enabledSpriteName != null) {
							buffer.putString(rsi.enabledSpriteName + "," + rsi.enabledSpriteId);
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
			}
			buffer.close();
			return buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static RSImage getSprite(String name, int id, Archive archive) {
		Main.getInstance().mediaArchive.addKnownArchive(name);
		long hash = (TextUtils.stringToLong(name) << 8) + (long) id;
		RSImage sprite = (RSImage) spriteNodes.get(hash);
		if (sprite != null) {
			return sprite;
		}
		try {
			sprite = new RSImage(archive, name, id);
			spriteNodes.put(sprite, hash);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public void setDisabledSprite(String name, int id, Archive archive) {
		disabledSpriteName = name;
		disabledSpriteId = id;
		disabledSprite = getSprite(name, id, archive);
	}

	public void setEnabledSprite(String name, int id, Archive archive) {
		enabledSpriteName = name;
		enabledSpriteId = id;
		enabledSprite = getSprite(name, id, archive);
	}

	public void setSprites(int index, String name, int id, Archive archive) {
		spriteNames[index] = name;
		spriteIds[index] = id;
		sprites[index] = getSprite(name, id, archive);
	}

	public RSInterface() {
	}

	public boolean locked = false;
	public RSImage disabledSprite;
	public int framesLeft;
	public RSImage sprites[];
	public static RSInterface cache[];
	public int requiredValues[];
	public int contentType;
	public int spritesX[];
	public int disabledHoverColor;
	public int actionType;
	public String spellName;
	public int enabledColor;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public boolean centered;
	public int scrollPosition;
	public String actions[];
	public int valueIndexArray[][];
	public boolean filled;
	public String enabledText;
	public int hoverId;
	public int invSpritePadX;
	public int disabledColor;
	public int disabledMediaType;
	public int disabledMediaId;
	public boolean deletesTargetSlot;
	public int parentId;
	public int spellUsableOn;
	protected static MemCache spriteNodes;
	public int enabledHoverColor;
	public int children[];
	public int childX[];
	public boolean usableItemInterface;
	public RSFont font;
	public int invSpritePadY;
	public int valueCompareType[];
	public int currentFrame;
	public int spritesY[];
	public String disabledText;
	public boolean isInventoryInterface;
	public int id;
	public int inventoryAmount[];
	public int inventory[];
	public byte alpha;
	public int enabledMediaType;
	public int enabledMediaId;
	public int disabledAnimation;
	public int enabledAnimation;
	public boolean itemsSwappable;
	public RSImage enabledSprite;
	public int scrollMax;
	public int type;
	public int drawOffsetX;
	public int drawOffsetY;
	public boolean showInterface;
	public int height;
	public boolean shadowed;
	public int zoom;
	public int rotationX;
	public int rotationY;
	public int childY[];
	public String disabledSpriteName;
	public int disabledSpriteId;
	public String enabledSpriteName;
	public int enabledSpriteId;
	public String[] spriteNames;
	public int[] spriteIds;
	public int fontId;
	public RSFont[] fonts;

}