package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;

/**
 * This class implements the &lt;textFormat/&gt; tag from the template.  This element defines a text formatting for outputs that can use it.  
 * Note that not all options nor all parameters may be supported in every output type.,
 * <!-- code-readable version
 * <textFormat name=”” size="" bold="" border="" color="" backColor="" alignment="" verticalAlignment=""/>
 * 
 * Attribute			Description
 * name					The name of this text format.  This will be displayed in the execution log
 * size					The font size in points.
 * bold					Is the font bold? [Y, <b>N</b>]
 * border				Does the font use a border?  Values in Border
 * color				Font color. Values in Color
 * backCokor			Cell background color.  Values in Color
 * alignment			Horizontal alignment of text.  Values in Alignment
 * verticalAlignment	Vertical alignment of text.  Values in Vertical Alignment
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;textFormat name=”” size="" bold="" border="" color="" backColor="" alignment="" verticalAlignment=""/&gt;</code>
 *
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>name</td><td>The name of this text format.  This will be displayed in the execution log</td></tr>
 *<tr class="rowColor"><td>size</td><td>The font size in points.</td></tr>
 *<tr class="altColor"><td>bold</td><td>Is the font bold? [Y, <b>N</b>]</td></tr>
 *<tr class="rowColor"><td>border</td><td>Does the font use a border?  Values in Border </td></tr>
 *<tr class="altColor"><td>color</td><td>Font color. Values in Color</td></tr>
 *<tr class="rowColor"><td>backCokor</td><td>Cell background color.  Values in Color</td></tr>
 *<tr class="altColor"><td>alignment</td><td>Horizontal alignment of text.  Values in Alignment</td></tr>
 *<tr class="rowColor"><td>verticalAlignment</td><td>Vertical alignment of text.  Values in Vertical Alignment</td></tr>
 *</table></li></ul>
 *<p>
 *There are pre-defined text formats for DATA_ERROR, BODY, BODY_HYPERLINK, BODY_WRAP, HEADER and HEADER_LIST.
 * <p>
 * Border Values - [NONE, THIN, MEDIUM, DASHED, DOTTED, THICK, DOUBLE, HAIR, MEDIUM_DASHED, DASH_DOT, MEDIUM_DASH_DOT, DASH_DOT_DOT, MEDIUM_DASH_DOT_DOT, SLANTED_DASH_DOT]
 * <p>
 * Color Values - [WHITE, BLACK, RED, BRIGHT_GREEN, BLUE, YELLOW, PINK, TURQUIOSE, DARK_RED, GREEN, DARK_BLUE, DARK_YELLOW, VIOLET, TEAL, GREY_24_PERCENT, GERY_50_PERCENT, 
 * 			CORNLOWER_BLUE, MAROON, LEMON_CHIFFON, LIGHT_TURQUIOSE, ORCHID, CORAL, ROYAL_BLUE, LIGHT_CORNFLOWER_BLUE, SKY_BLUE, LIGHT_TURQUIOSE, LIGHT_GREEN, 
 * 			LIGHT_YELLOW, PALE_BLUE, ROSE, LAVENDER, TAN, LIGHT_BLUE, AQUA, LIME, GOLD, LIGHT_ORANGE, ORANGE, BLUE_GREY, GREY_40_PERCENT, DARK_TEAL, SEA_GREEN, 
 * 			DARK_GREEN, OLUVE_GREEN, BROWN, PLUM, INDIGO, GREY_80_PERCENT, AUTOMATIC]
 * <p>
 * Alignment Values - [GENERAL, LEFT, CENTER, RIGHT, FILL, JUSTIFY, CENTER_SELECTION, DISTRIBUTED]
 * <p>
 * Vertical Alignment Values - [TOP, CENTER, BOTTOM, JUSTIFY, DISTRIBUTED]
 *
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class TextFormatOperation extends Operation {

	/**
	 * Constructor
	 */
	public TextFormatOperation() {
		super();
	}

	@Override
	public String getOperationType() {
		return "textFormat";
	}
	
	@Override
	public void executeOperation(Output output) {
		output.createTextFormat(this);
	}
	
	@Override
	public void executeOperation(String function, Output output) {
		executeOperation(output);
	}
	
	@Override
	public boolean canHaveChildren() {
		return false;
	}
}
