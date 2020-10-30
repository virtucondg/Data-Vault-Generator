package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;
import com.biogen.utils.Debug;

/**
 * This class implements the &lt;grid/&gt; tag from a template.  This element creates a grid output of its child data elements, putting each child element in a separate column, 
 * with new rows for each looped dataset.
 * <p>
 * <!-- code-readable version
 * <grid name=”” {headerTextFormat=”” }{textFormat=”” }{objects=”” }{merge=”” }{filter=”” }{header=”” }{validation=”” }/>
 * 
 * Attribute		Description
 * name				The name of this grid.  This will be displayed in the execution log
 * headerTextFormat	The TextFormat to use for header columns in this sheet.  This can be overridden by child elements.
 * textFormat		The TextFormat to use for data columns in this sheet.  This can be overridden by child elements
 * objects			Specifies the type of object to loop over and create a new sheet for each instance.  If you wanted a separate sheet for each 
 * 					business data object and you specified objects=“BusinessDataObject” then a new sheet would be created for each.  If you are creating 
 * 					multiple instances of sheets then the name should have something to make each unique (like the object name).
 * merge			If supported by the output file system the number of columns to merge with this one.
 * filter			For this element to be processed the value must be either in the list of filters in the .properties file or there must be no
 * 					filter list.  This allows you to skip parts of a template via the .properties file.
 * header			If the header should be printed.  If more than one grid is on a sheet/file only the first grid should print the header.  
 * 					Values are [<b>TRUE</b>, FALSE].
 * validation		Perform not null validation on this parameter.  If the result is NULL ignore the row.  This can be a Tokenized String,
 * 
 * HTML-formatted text below:
 * -->
 *<code>&lt;grid name=”” {headerTextFormat=”” }{textFormat=”” }{objects=”” }{merge=”” }{filter=”” }{header=”” }{validation=”” }/&gt;</code>
 * 
 * </div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="rowColor"><td>name</td><td>The name of this grid.  This will be displayed in the execution log</td></tr>
 *<tr class="altColor"><td>headerTextFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat} to use for header columns in this sheet.  This can be overridden by child elements.</td></tr>
 *<tr class="rowColor"><td>textFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat} to use for data columns in this sheet.  This can be overridden by child elements.</td></tr>
 *<tr class="altColor"><td>objects</td><td>Specifies the type of object to loop over and create a new sheet for each instance.  If you wanted a separate sheet for each 
 *business data object and you specified objects=“BusinessDataObject” then a new sheet would be created for each.  If you are creating multiple instances of sheets then
 * the name should have something to make each unique (like the object name).</td></tr>
 *<tr class="rowColor"><td>merge</td><td>If supported by the output file system the number of columns to merge with this one.</td></tr>
 *<tr class="altColor"><td>filter</td><td>For this element to be processed the value must be either in the list of filters in the .properties file or there must be no
 * filter list.  This allows you to skip parts of a template via the .properties file.</td></tr>
 *<tr class="rowColor"><td>header</td><td>If the header should be printed.  If more than one grid is on a sheet/file only the first grid should print the header.  
 *Values are [<b>TRUE</b>, FALSE].</td></tr>
 *<tr class="altColor"><td>validation</td><td>Perform not null validation on this parameter.  If the result is NULL ignore the row.  This can be a Tokenized String,</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class GridOperation extends Operation {

	/**
	 * Constructor
	 */
	public GridOperation() {
	}

	@Override
	void executeObjectPreLoop(Output output) {
		moveIfNeeded(output);
		if (!"false".equalsIgnoreCase(getProperty("header"))) {
			putSharedProperty("merge", getProperty("headerMerge"));
			loopChildOperation("Header", output);
			putSharedProperty("merge", null);
		}
	}
		
	@Override
	void loopChildOperation(Output output) {
		putSharedProperty("merge", getProperty("dataMerge"));
		loopChildOperation("Data", output);
		putSharedProperty("merge", null);
	}
		
	@Override
	void loopChildOperation(String function, Output output) {
		Debug.print("Processing Grid (" + function + "):" + getProperty("name") + "...");
		
		for (Operation op: getOperationList())
			if (app.hasFilter(op.getProperty("filter")))
					op.executeOperation(function, output);	

		output.nextRow();
		
		Debug.println("Done");
	}
		
	@Override
	public void executeOperation(String function, Output output) {
		if (function.equalsIgnoreCase("LEGEND")) {
			for (Operation op: getOperationList()) {
				op.executeOperation("name", output);
				op.executeOperation("requirement", output);
				op.executeOperation("definition", output);
				output.nextRow();
			}
			
			output.nextRow();
		} else {
			loopChildOperation(function, output);
		}
	}

	@Override
	public String getOperationType() {
		return "grid";
	}
	
}
