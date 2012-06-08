package rs2.rsinterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import rs2.Main;
import rs2.cache.Archive;
import rs2.constants.Constants;
import rs2.graphics.RSFont;
import rs2.graphics.RSImage;
import rs2.io.ExtendedByteArrayOutputStream;
import rs2.io.JagexBuffer;
import rs2.node.MemCache;
import rs2.util.DataUtils;
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
		byte[] data = interfaces.getFile("data");
		JagexBuffer buffer = new JagexBuffer(data);
		/*int total = */buffer.getUnsignedShort();
		cache = new HashMap<Integer, RSInterface>();
		while (buffer.offset < buffer.payload.length) {
			RSInterface rsi = Utilities.readHeaderChunk(buffer);
			Utilities.readTypeChunk(rsi, buffer);
		}
		spriteNodes = null;
	}

	/**
	 * Writes the interface cache (data.dat).
	 */
	public static byte[] getData() {
		try {
			ExtendedByteArrayOutputStream buffer = new ExtendedByteArrayOutputStream();
			buffer.putShort(cache.size());
			for (int index = 0; index < cache.size(); index++) {
				RSInterface rsi = cache.get(index);
				if (rsi == null) {
					continue;
				}
				buffer.write(Utilities.getHeaderChunk(rsi));
				buffer.write(Utilities.getTypeChunk(rsi));
			}
			buffer.close();
			return buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a backup of the interface cache.
	 */
	public static void createBackup() {
		DataUtils.writeFile(Constants.getCacheDirectory() + "data.backup", getData());
	}

	/**
	 * Gets the backup of the interface cache.
	 * @return
	 */
	public static byte[] getBackup() {
		return DataUtils.readFile(Constants.getCacheDirectory() + "data.backup");
	}

	public static RSImage getSprite(String name, int id, Archive archive) {
		Main.getInstance().mediaArchive.addKnownArchive(name);
		long hash = (TextUtils.stringToLong(name) << 8) + (long) id;
		RSImage sprite = null;
		if (spriteNodes != null) {
			sprite = (RSImage) spriteNodes.get(hash);
			if (sprite != null) {
				return sprite;
			}
		}
		try {
			sprite = new RSImage(archive, name, id);
			if (spriteNodes != null) {
				spriteNodes.put(sprite, hash);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return sprite;
	}

	public void setDisabledSprite(String name, int id, Archive archive) {
		disabledSpriteArchive = name;
		disabledSpriteId = id;
		disabledSprite = getSprite(name, id, archive);
	}

	public void setEnabledSprite(String name, int id, Archive archive) {
		enabledSpriteArchive = name;
		enabledSpriteId = id;
		enabledSprite = getSprite(name, id, archive);
	}

	public void setSprites(int index, String name, int id, Archive archive) {
		spriteNames[index] = name;
		spriteIds[index] = id;
		sprites[index] = getSprite(name, id, archive);
	}

	public boolean hasChildren() {
		return children != null;
	}

	public static RSInterface getInterface(int id) {
		return cache.get(id);
	}

	public RSInterface() {
	}

	public boolean locked = false;
	public RSImage disabledSprite;
	public int framesLeft;
	public RSImage sprites[];
	public static HashMap<Integer, RSInterface> cache;
	public LinkedList<Integer> children;
	public LinkedList<Integer> childX;
	public LinkedList<Integer> childY;
	//public static RSInterface cache[];
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
	public String disabledSpriteArchive;
	public int disabledSpriteId;
	public String enabledSpriteArchive;
	public int enabledSpriteId;
	public String[] spriteNames;
	public int[] spriteIds;
	public int fontId;
	public RSFont[] fonts;

}