package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;

/**
 * This class implements the &lt;workbook/&gt; tag of a template.  This element is the parent element defining an Excel workbook.
 * <!-- code-readable version
 * <workbook name=”” {headerTextFormat=”” }{textFormat=”” }(objects=”” }{filter=”” }/>
 * 
 * Attribute		Description
 * name				The file name of this workbook.  This can be a Tokenized String,  This will be displayed in the execution log
 * headerTextFormat	The TextFormat to use for header columns in this workbook.  This can be overridden by child elements.
 * textFormat		The TextFormat to use for data columns in this workbook.  This can be overridden by child elements.
 * attribute		Specifies the type of object to loop over and create a new workbook for each instance.  If you wanted a separate workbook
 * 					for each business data object and you specified objects=“BusinessDataObject” then a new workbook would be created for each.  
 * 					If you are creating multiple instances of workbooks then the name should have something to make each unique (like the object name).
 * filter			For this element to be processed the value must be either in the list of filters in the .properties file or there must be no 
 *					filter list.  This allows you to skip parts of a template via the .properties file.
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;workbook name=”” {headerTextFormat=”” }{textFormat=”” }(objects=”” }{filter=”” }/&gt;</code>
 * 
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>name</td><td>The file name of this workbook.  This can be a Tokenized String,  This will be displayed in the execution log</td></tr>
 *<tr class="rowColor"><td>headerTextFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for header columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="altColor"><td>textFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for data columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="objects"><td>attribute</td><td>Specifies the type of object to loop over and create a new workbook for each instance.  If you wanted a separate workbook
 * for each business data object and you specified objects=“BusinessDataObject” then a new workbook would be created for each.  If you are creating multiple instances 
 * of workbook then the name should have something to make each unique (like the object name).</td></tr>
 *<tr class="altColor"><td>filter</td><td>For this element to be processed the value must be either in the list of filters in the .properties file or there must be no 
 *filter list.  This allows you to skip parts of a template via the <code>.properties</code> file.</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 */
public class WorkbookOperation extends Operation {

	/**
	 * Constructor
	 */
	public WorkbookOperation() {
		super();
	}

	@Override
	public String getOperationType() {
		return "workbook";
	}
	
	@Override
	void loopChildOperation(Output output) {
		String filename = app.getPropertyAsString("targetDirectory") + getTokenProperty("name");
		output.open(parse(filename));		
		super.loopChildOperation(output);
		output.close();		
	}

	@Override
	void loopChildOperation(String function, Output output) {
		loopChildOperation(output);
	}
		
	@Override
	public String getProperty(String key) {
		String value = super.getProperty(key);
		
		if (key.equals("name")) {
			return value.replaceAll(".dm1", "");
		} else
			return value;
	}
	
}
