package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;
import com.biogen.utils.Utility;

/**
 * This class implements the &lt;data/&gt; tag of a template.  This element outputs a data element.
 * <!-- code-readable version
 * <data property=”” name=”” {wrap=”” }{headerTextFormat=””  {textFormat=”” }{maxLength=”” }{requirement=”” }{definition=”” }{format=}{merge=”” }{filter=”” }/>
 * 
 * Attribute		Description
 * property			The property value.  This can be a Tokenized String.
 * name				The name of this data point.  This will be used in the header of the grid.
 * wrap				Should the data in this column be wrapped? Values are [Y, N]
 * headerTextFormat	The TextFormat to use for header columns in this workbook.  This can be overridden by child elements.
 * textFormat		The TextFormat to use for data columns in this workbook.  This can be overridden by child elements.
 * maxLength		The maximum length of the property value.,  If the length exceeds it textFormat will override to DATA_ERROR (red text).
 * requirement		This will be displayed in the “Required” column of the Legend, if output.
 * definition		This will be displayed in the “Definition” column of the Legend, if output.
 * format			Should this data be output differently than a string?  Values are [BOOLEAN]
 * merge			If supported by the Output, the number of columns to merge with this one.
 * filter			For this element to be processed the value must be either in the list of filters in the .properties file or there must be no 
 * 					filter list.  This allows you to skip parts of a template via the .properties file.
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;data property=”” name=”” {wrap=”” }{headerTextFormat=””  {textFormat=”” }{maxLength=”” }{requirement=”” }{definition=”” }{format=}{merge=”” }{filter=”” }/&gt;</code>
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>property</td><td>The property value.  This can be a Tokenized String.</td></tr>
 *<tr class="rowColor"><td>name</td><td>The name of this data point.  This will be used in the header of the grid.</td></tr>
 *<tr class="altColor"><td>wrap</td><td>Should the data in this column be wrapped? Values are [Y, N]</td></tr>
 *<tr class="rowColor"><td>headerTextFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for header columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="altColor"><td>textFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for data columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="rowColor"><td>maxLength</td><td>The maximum length of the property value.,  If the length exceeds it textFormat will override to DATA_ERROR (red text).</td></tr>
 *<tr class="altColor"><td>requirement</td><td>This will be displayed in the “Required” column of the Legend, if output.</td></tr>
 *<tr class="rowColor"><td>definition</td><td>This will be displayed in the “Definition” column of the Legend, if output.</td></tr>
 *<tr class="altColor"><td>format</td><td>Should this data be output differently than a string?  Values are [BOOLEAN]</td></tr>
 *<tr class="rowColor"><td>merge</td><td>If supported by the {@link com.biogen.generator.io.output.Output Output}, the number of columns to merge with this one.</td></tr>
 *<tr class="altColor"><td>filter</td><td>For this element to be processed the value must be either in the list of filters in the .properties file or there must be no 
 *filter list.  This allows you to skip parts of a template via the .properties file.</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class DataOperation extends Operation {
private String tf = null;
private String htf = null;
private String rtf = null;
private String format = null;
private int maxLength = -1;
private int mergeCol = -1;
private boolean wrap = false;

	/**
	 * Constructor
	 */
	public DataOperation() {
	}

	@Override
	public void executeOperation(Output output) {
	}

	private void init() {
		rtf = "BODY_WRAP";

		if (null==(tf = getProperty("textFormat"))) 
			if (null==(tf = textFormat))
				tf = "BODY";		

		if (null==(htf = getProperty("headerTextFormat"))) 
			if (null==(htf = headerTextFormat))
				htf = "HEADER";		
		
		format = getProperty("format");
		maxLength = getPropertyAsInt("maxLength");
		wrap = "Y".equalsIgnoreCase(getProperty("wrap"));
}
	
	@Override
	public void executeOperation(String function, Output output) {
		String data = null;
		mergeCol = getPropertyAsInt("merge");

		if (null==tf) init();
		
		String hyperlink = getHyperlink();
		
		moveIfNeeded(output);

		switch(function.toUpperCase()) {
		case "HEADER" :
			if (!"false".equalsIgnoreCase(getProperty("header"))) {
				data = Utility.textToFormattedText(getProperty("name"), true);
				output.print(data, null, htf, hyperlink);
			}
			break;
		case "DATA" :
			data = getTokenProperty("property");
			if ((-1==maxLength) || (null==data)) {
				if (wrap)
					output.print(data, format, rtf, hyperlink);					
				else
					output.print(data, format, tf, hyperlink);
			} else {
				if ((-1==maxLength)||(data.length() <= maxLength)) {
					if (wrap)
						output.print(data, format, rtf, hyperlink);					
					else
						output.print(data, format, tf, hyperlink);
				}
				else
					output.print(data, format, "DATA_ERROR", hyperlink);
			}
			
			break;
		case "NAME" :
			data = Utility.textToFormattedText(getProperty("name"), false);
			output.print(data, null, tf, null);
			break;
		default:
			data = getProperty(function);
			output.print(data, format, tf, hyperlink);
		}

		if (mergeCol > 0) output.merge(mergeCol);
		output.nextColumn();
	}

	@Override
	public String getOperationType() {
		return "column";
	}
	
}
