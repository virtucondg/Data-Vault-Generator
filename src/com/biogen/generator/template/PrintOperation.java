package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;

/**
 * This class implements the &lt;print/&gt; tag from a template.  This element outputs a permanent value.
 * <!-- code-readable version
 * <print text=”” {hyperlink=”” }{row=”” }{col=”” }{textFormat=”” }{newLine=””}{merge=”” }{filter=”” }/>
 * 
 * Attribute		Description
 * text				The text to output in this cell.
 * hyperlink		If supported by the output file, you can create a hyperlink behind the text.
 * row				If supported by the output file, the row to move to before outputting text
 * column			If supported by the output file, the column to move to before outputting text
 * textFormat		The TextFormat to use for data columns in this sheet.  This can be overridden by child elements.
 * newLine			If the parser should move to the next row after printing.  Values are [Y, N]
 * merge			If supported by the output file, the number of columns to merge with this one.
 * filter			For this element to be processed the value must be either in the list of filters in the .properties file or there must be 
 *					no filter list.  This allows you to skip parts of a template via the .properties file.
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;print text=”” {hyperlink=”” }{row=”” }{col=”” }{textFormat=”” }{newLine=””}{merge=”” }{filter=”” }/&gt;</code>
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>text</td><td>The text to output in this cell.</td></tr>
 *<tr class="rowColor"><td>hyperlink</td><td>If supported by the output file, you can create a hyperlink behind the text.</td></tr>
 *<tr class="altColor"><td>row</td><td>If supported by the output file, the row to move to before outputting text</td></tr>
 *<tr class="rowColor"><td>column</td><td>If supported by the output file, the column to move to before outputting text</td></tr>
 *<tr class="altColor"><td>textFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for data columns in this sheet.  This can be overridden by child elements.</td></tr>
 *<tr class="rowColor"><td>newLine</td><td>If the parser should move to the next row after printing.  Values are [Y, N]</td></tr>
 *<tr class="altColor"><td>merge</td><td>If supported by the output file, the number of columns to merge with this one.</td></tr>
 *<tr class="rowColor"><td>filter</td><td>For this element to be processed the value must be either in the list of filters in the .properties file or there must be 
 *no filter list.  This allows you to skip parts of a template via the .properties file.</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class PrintOperation extends Operation {

	/**
	 * Constructor
	 */
	public PrintOperation() {
	}

	@Override
	public void executeOperation(Output output) {
		String data = getProperty("text");
		String newLine = getProperty("newLine");
		String tf = getProperty("textFormat");
		int mergeCol = getPropertyAsInt("merge");
		String hyperlink = getHyperlink();
		moveIfNeeded(output);
		
		output.print(data, getProperty("format"), (null==tf ? textFormat : tf), hyperlink);
		if (0 < mergeCol) output.merge(mergeCol);

		if (!"N".equalsIgnoreCase(newLine))
			output.nextRow();
		else
			output.nextColumn();
	}

	@Override
	public String getOperationType() {
		return "print";
	}

	@Override
	public void executeOperation(String function, Output output) {
		executeOperation(output);
	}

}
