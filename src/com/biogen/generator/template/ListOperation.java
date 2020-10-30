package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;
import com.biogen.utils.Debug;

/**
 * This class implements the &lt;list/&gt; tag from a template.  This element creates a List output of its child data elements. This would typically be used for only 
 * one row of data.  It creates each child element on a separate line.
 * <!-- code-readable version
 * <list name=”” {row=”” }{col=”” }{headerTextFormat=”” }{textFormat=”” }{headerMerge=”” }{dataMerge=”” }{filter=”” }/>
 * 
 * Attribute		Description
 * name				The name of this list.  This will be displayed in the execution log
 * row				If supported by the Output, the row to move to before starting the list
 * col				If supported by the Output file, the column to move to before starting the list
 * headerTextFormat	The TextFormat to use for header columns in this workbook.  This can be overridden by child elements.
 * textFormat		The TextFormat to use for data columns in this workbook.  This can be overridden by child elements.
 * headerMerge		If supported by the output file, the number of columns to merge with this one.for the header.
 * dataMerge		If supported by the output file, the number of columns to merge with this one for the data.
 * filter			For this element to be processed the value must be either in the list of filters in the .properties file or there must be 
 *					no filter list.  This allows you to skip parts of a template via the .properties file.
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;list name=”” {row=”” }{col=”” }{headerTextFormat=”” }{textFormat=”” }{headerMerge=”” }{dataMerge=”” }{filter=”” }/&gt;</code>
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>name</td><td>The name of this list.  This will be displayed in the execution log</td></tr>
 *<tr class="rowColor"><td>row</td><td>If supported by the {@link com.biogen.generator.io.output.Output Output}, the row to move to before starting the list</td></tr>
 *<tr class="altColor"><td>col</td><td>If supported by the {@link com.biogen.generator.io.output.Output Output} file, the column to move to before starting the list</td></tr>
 *<tr class="rowColor"><td>headerTextFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for header columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="altColor"><td>textFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for data columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="rowColor"><td>headerMerge</td><td>If supported by the output file, the number of columns to merge with this one.for the header.</td></tr>
 *<tr class="altColor"><td>dataMerge</td><td>If supported by the output file, the number of columns to merge with this one for the data.</td></tr>
 *<tr class="rowColor"><td>filter</td><td>For this element to be processed the value must be either in the list of filters in the .properties file or there must be 
 *no filter list.  This allows you to skip parts of a template via the .properties file.</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class ListOperation extends Operation {

	/**
	 * Constructor
	 */
	public ListOperation() {
	}

	@Override
	void loopChildOperation(Output output) {
		Debug.print("Processing List:" + getProperty("name") + "...");
		putSharedProperty("headerTextFormat", "HEADER_LIST");
		moveIfNeeded(output);
		
		for (Operation op: getOperationList()) {
			if (app.hasFilter(op.getProperty("filter"))) {
				putSharedProperty("merge", getProperty("headerMerge"));
				if (!"false".equalsIgnoreCase(getProperty("header"))) {
					op.executeOperation("Header", output);
				}
				putSharedProperty("merge", getProperty("dataMerge"));
				op.executeOperation("Data", output);
				if (!"N".equals(getProperty("newLine")))
					output.nextRow();
			}
			putSharedProperty("merge", null);
		}
		
		Debug.println("Done");
	}
	
	@Override
	void loopChildOperation(String function, Output output) {
		loopChildOperation(output);
	}
	
	@Override
	public String getOperationType() {
		return "list";
	}
	
}
