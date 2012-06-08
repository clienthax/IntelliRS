package rs2.rsinterface.type;

import rs2.rsinterface.RSInterface;

public class TextType extends RSInterface {

	public final int DEFAULT_COLOR = 0xFFFFFF;

	public TextType(int parentId, String text) {
		new TextType(parentId, 0, 0, (byte) 0, -1, false, 1, false, text, DEFAULT_COLOR);
	}

	public TextType(int parentId, int actionType, int contentType, byte alpha, int hoverId, boolean centered, int fontId, boolean shadowed, String disabledText, int disabledColor) {
		this.parentId = parentId;
		this.type = 4;
		this.actionType = actionType;
		this.contentType = contentType;
		this.alpha = alpha;
		this.hoverId = hoverId;
		this.valueCompareType = null;
		this.requiredValues = null;
		this.valueIndexArray = null;
		this.centered = centered;
		this.fontId = fontId;
		this.shadowed = shadowed;
		this.disabledText = disabledText;
		this.disabledColor = disabledColor;
		this.enabledColor = 0;
		this.enabledHoverColor = 0;
		this.width = this.fonts[this.fontId].getTextWidth(disabledText);
		this.width = this.fonts[this.fontId].getTextHeight(this.shadowed);
		cache.put(cache.size(), this);
	}

}