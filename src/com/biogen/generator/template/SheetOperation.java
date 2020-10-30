package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;

/**
 * This class implements the &lt;sheet/&gt; tag from a template.  This element icreates a new sheet in an Excel workbook.
 * <!-- code-readable version
 * <sheet name=”” title=”” {headerTextFormat=”” }{textFormat=”” }{objects=””}{sorted=”” }{distinct=”” }{filter=”” }/>
 * 
 * Attribute		Description
 * name				The name of this sheet.  This can be a Tokenized String,  This will be displayed in the execution log
 * title			The title of this sheet.  Will be used if a legend is output.</td></tr>
 * headerTextFormat	The TextFormat to use for header columns in this workbook.  This can be overridden by child elements.
 * textFormat		The TextFormat to use for data columns in this workbook.  This can be overridden by child elements.
 * objects			Specifies the type of object to loop over and create a new workbook for each instance.  If you wanted a separate 
 *					workbook for each business data object and you specified objects=“BusinessDataObject” then a new workbook would be 
 *					created for each.  If you are creating multiple instances of workbook then the name should have something to make 
 *					each unique (like the object name).
 * sorted			If this file should have its rows sorted then the semi-colon “;” delimited list of columns to sort by (Ex: “C;E;H;G”).  
 *					This will not remove duplicate rows and cannot be used with the “distinct” attribute
 * distinct			If this file should have its rows deduplicated then the delimited list of columns to sort by after deduplication
 * 					(ex; C;E;H;G”).  This attribute cannot be used with the “sorted” attribute
 * filter			For this element to be processed the value must be either in the list of filters in the .properties file or there 
 *					must be no filter list.  This allows you to skip parts of a template via the .properties file.
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;sheet name=”” title=”” {headerTextFormat=”” }{textFormat=”” }{objects=””}{sorted=”” }{distinct=”” }{filter=”” }/&gt;</code>
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>name</td><td>The name of this sheet.  This can be a Tokenized String,  This will be displayed in the execution log</td></tr>
 *<tr class="rowColor"><td>title</td><td>The title of this sheet.  Will be used if a legend is output.</td></tr>
 *<tr class="altColor"><td>headerTextFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for header columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="rowColor"><td>textFormat</td><td>The {@link com.biogen.generator.io.format.TextFormat TextFormat} to use for data columns in this workbook.  This can be overridden by child elements.</td></tr>
 *<tr class="altColor"><td>objects</td><td>Specifies the type of object to loop over and create a new workbook for each instance.  If you wanted a separate 
 *workbook for each business data object and you specified objects=“BusinessDataObject” then a new workbook would be created for each.  If you are creating multiple 
 *instances of workbook then the name should have something to make each unique (like the object name).</td></tr>
 *<tr class="rowColor"><td>sorted</td><td>If this file should have its rows sorted then the semi-colon “;” delimited list of columns to sort by (Ex: “C;E;H;G”).  
 *This will not remove duplicate rows and cannot be used with the “distinct” attribute</td></tr>
 *<tr class="altColor"><td>distinct</td><td>If this file should have its rows deduplicated then the delimited list of columns to sort by after deduplication
 * (ex; C;E;H;G”).  This attribute cannot be used with the “sorted” attribute</td></tr>
 *<tr class="rowColor"><td>filter</td><td>For this element to be processed the value must be either in the list of filters in the .properties file or there 
 *must be no filter list.  This allows you to skip parts of a template via the .properties file.</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class SheetOperation extends Operation {

	/** 
	 * Constructor
	 */
	public SheetOperation() {
		super();
	}

	@Override
	public String getOperationType() {
		return "sheet";
	}
	
	@Override
	void loopChildOperation(Output output) {
		pushOutputCodes(output, getProperty("distinct"), getProperty("sorted"));
		
		output.createSheet(parse(getTokenProperty("name")));
		super.loopChildOperation(output);	

		output.endSheet();
		
		popOutputCodes(output);
	}
	
	@Override
	void loopChildOperation(String function, Output output) {
		loopChildOperation(output);
	}
	
}
