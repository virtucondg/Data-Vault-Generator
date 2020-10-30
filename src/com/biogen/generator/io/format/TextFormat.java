package com.biogen.generator.io.format;

import java.util.HashMap;

import com.biogen.generator.template.Operation;

/**
 * This class contains the data for formatting text.  If a target allows, this class contains font, size, bold, italic, and color values.
 * @author drothste
 *
 */
public class TextFormat {
	private String name;
	private int size;
	private boolean bold;
	private byte underline;
	private String underlineName;
	private boolean italic;
	private String foreColor;
	private String backColor;
	private String alignment;
	private String verticalAlignment;
	private String border;
	private boolean wrap;
	
	private HashMap<String, String> keys = new HashMap<String, String>();
	
	/**
	 * Static initializer
	 */
	{
		keys.put("UNDERLINE_", "0");
		keys.put("UNDERLINE_U_NONE", "0");
		keys.put("UNDERLINE_U_SINGLE", "1");
		keys.put("UNDERLINE_U_DOUBLE", "2");
		keys.put("UNDERLINE_U_SINGLE_ACCOUNTING", "33");
		keys.put("UNDERLINE_U_DOUBLE_ACCOUNTING", "34");
		keys.put("HORIZONTAL_", "LEFT");
		keys.put("HORIZONTAL_CENTER", "CENTER");
		keys.put("HORIZONTAL_LEFT", "LEFT");
		keys.put("HORIZONTAL_RIGHT", "RIGHT");
		keys.put("HORIZONTAL_GENERAL", "GENERAL");
		keys.put("HORIZONTAL_FILL", "FILL");
		keys.put("HORIZONTAL_JUSTIFY", "JUSTIFY");
		keys.put("VERTICAL_", "CENTER");
		keys.put("VERTICAL_CENTER", "CENTER");
		keys.put("VERTICAL_TOP", "TOP");
		keys.put("VERTICAL_BOTTOM", "BOTTOM");
		keys.put("VERTICAL_JUSTIFY", "JUSTIFY");
		keys.put("BORDER_", "NONE");
		keys.put("BORDER_NONE", "NONE");
		keys.put("BORDER_THIN", "THIN");
		keys.put("BORDER_THICK", "THICK");
		keys.put("BORDER_MEDIUM", "MEDUIM");
		keys.put("BORDER_DASHED", "DASHED");
		keys.put("BORDER_DOTTED", "DOTTED");
		keys.put("BORDER_DASH_DOT", "DASH_DOT");
		keys.put("BORDER_MEDIUM_DASHED", "MEDIUM_DASHED");
		keys.put("COLOR_BLACK", "BLACK");
		keys.put("COLOR_WHITE", "WHITE");
		keys.put("COLOR_RED", "RED");
		keys.put("COLOR_BRIGHT_GREEN", "BRIGHT_GREEN");
		keys.put("COLOR_BLUE", "BLUE");
		keys.put("COLOR_YELLOW", "YELLOW");
		keys.put("COLOR_PINK", "PINK");
		keys.put("COLOR_TURQUOISE", "TURQUOISE");
		keys.put("COLOR_DARK_RED", "DARK_RED");
		keys.put("COLOR_GREEN", "GREEN");
		keys.put("COLOR_DARK_BLUE", "DARK_BLUE");
		keys.put("COLOR_DARK_YELLOW", "DARK_YELLOW");
		keys.put("COLOR_VIOLET", "VIOLET");
		keys.put("COLOR_TEAL", "TEAL");
		keys.put("COLOR_GREY_25_PERCENT", "GREY_25_PERCENT");
		keys.put("COLOR_GREY_50_PERCENT", "GREY_50_PERCENT");
		keys.put("COLOR_CORNFLOWER_BLUE", "CORNFLOWER_BLUE");
		keys.put("COLOR_MAROON", "MAROON");
		keys.put("COLOR_LEMON_CHIFFON", "LEMON_CHIFFON");
		keys.put("COLOR_LIGHT_TURQUOISE1", "LIGHT_TURQUOISE1");
		keys.put("COLOR_ORCHID", "ORCHID");
		keys.put("COLOR_CORAL", "CORAL");
		keys.put("COLOR_ROYAL_BLUE", "ROYAL_BLUE");
		keys.put("COLOR_LIGHT_CORNFLOWER_BLUE", "LIGHT_CORNFLOWER_BLUE");
		keys.put("COLOR_SKY_BLUE", "SKY_BLUE");
		keys.put("COLOR_LIGHT_TURQUOISE", "LIGHT_TURQUOISE");
		keys.put("COLOR_LIGHT_GREEN", "LIGHT_GREEN");
		keys.put("COLOR_LIGHT_YELLOW", "LIGHT_YELLOW");
		keys.put("COLOR_PALE_BLUE", "PALE_BLUE");
		keys.put("COLOR_ROSE", "ROSE");
		keys.put("COLOR_LAVENDER", "LAVENDER");
		keys.put("COLOR_TAN", "TAN");
		keys.put("COLOR_LIGHT_BLUE", "LIGHT_BLUE");
		keys.put("COLOR_AQUA", "AQUA");
		keys.put("COLOR_LIME", "LIME");
		keys.put("COLOR_GOLD", "GOLD");
		keys.put("COLOR_LIGHT_ORANGE", "LIGHT_ORANGE");
		keys.put("COLOR_ORANGE", "ORANGE");
		keys.put("COLOR_BLUE_GREY", "BLUE_GREY");
		keys.put("COLOR_GREY_40_PERCENT", "GREY_40_PERCENT");
		keys.put("COLOR_DARK_TEAL", "DARK_TEAL");
		keys.put("COLOR_SEA_GREEN", "SEA_GREEN");
		keys.put("COLOR_DARK_GREEN", "DARK_GREEN");
		keys.put("COLOR_OLIVE_GREEN", "OLIVE_GREEN");
		keys.put("COLOR_BROWN", "BROWN");
		keys.put("COLOR_PLUM", "PLUM");
		keys.put("COLOR_INDIGO", "INDIGO");
		keys.put("COLOR_GREY_80_PERCENT", "GREY_80_PERCENT");
	}
	
	/** 
	 * Constructor
	 * @param operation the {@link Operation} to get the formatting values from
	 */
	public TextFormat(Operation operation) {
		this.name = operation.getProperty("name");
		this.size = operation.getPropertyAsInt("size");
		if (-1==this.size)
			this.size = 10;
		
		this.bold = testString(operation.getProperty("bold"));
		this.italic = testString(operation.getProperty("italic"));
		if (null==(this.underlineName = keys.get("UNDERLINE_" + operation.getProperty("underline")))) {
			this.underlineName = "NONE";
			this.underline = 0;
		} else  
			this.underline = Byte.valueOf(this.underlineName);
		
		if (null==(this.foreColor = keys.get("COLOR_" + operation.getProperty("color")))) this.foreColor = "BLACK";
		if (null==(this.backColor = keys.get("COLOR_" + operation.getProperty("backColor")))) this.backColor = "WHITE";
		if (null==(this.alignment = keys.get("HORIZONTAL_" + operation.getProperty("alignment")))) this.alignment = "LEFT";
		if (null==(this.verticalAlignment = keys.get("VERTICAL_" + operation.getProperty("verticalAlignment")))) this.verticalAlignment = "CENTER";
		if (null==(this.border = keys.get("BORDER_" + operation.getProperty("border")))) this.border = "NONE";
		this.wrap = testString(operation.getProperty("wrap"));
	}

	/**
	 * Convert a Yes/No/Y/N value to boolean
	 * @param value the value to test
	 * @return <code>TRUE</code> if the value is "Y" or "YES"; <code>FALSE</code> otherwise
	 */
	private boolean testString(String value) {
		return (null==value ? false: value.substring(0, 1).equalsIgnoreCase("Y"));
	}
	
	/** 
	 * Get the name of this TextFormat
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the font size
	 * @return the font size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Is this format bold?
	 * @return <code>TRUE</code> if bold; <code>FALSE</code> otherwise
	 */
	public boolean isBold() {
		return this.bold;
	}
	
	/**
	 * Is this format italic?
	 * @return <code>TRUE</code> if italic; <code>FALSE</code> otherwise
	 */
	public boolean isItalic() {
		return this.italic;
	}
	
	/** 
	 * Is the text wrappable?
	 * @return <code>TRUE</code> if wrappable; <code>FALSE</code> otherwise
	 */
	public boolean isWrap() {
		return this.wrap;
	}
	
	/**
	 * Get the underline type
	 * @return the underline type
	 */
	public String getUnderline() {
		return this.underlineName;
	}
	
	/**
	 * Get the underline value
	 * @return the underline value
	 */
	public byte getUnderlineValue() {
		return this.underline;
	}
	
	/**
	 * Get the foreground color
	 * @return the foreground color
	 */
	public String getForeColor() {
		return this.foreColor;
	}
	
	/**
	 * Get the background color
	 * @return the background color
	 */
	public String getBackColor() {
		return this.backColor;
	}
	
	/** 
	 * Get the horizontal alignment
	 * @return the horizontal alignment
	 */
	public String getAlignment() {
		return this.alignment;
	}
	
	/**
	 * Get the vertical alignment
	 * @return the vertical alignment
	 */
	public String getVerticalAlignment() {
		return this.verticalAlignment;
	}
	
	/**
	 * Get the border style
	 * @return the border style
	 */
	public String getBorder() {
		return this.border;
	}

	/**
	 * Get the token related to a given formatting name
	 * @param name the formatting name
	 * @return the key token
	 */
	public String getKey(String name) {
		return keys.get(name.toUpperCase());
	}
	
}
	
