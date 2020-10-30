package com.biogen.generator.template;

import com.biogen.generator.io.output.Output;

/**
 * This class implements the &lt;template/&gt; tag from the template.  This element is the parent element defining a template.
 * <!-- code-readable version
 * <template name=”” mode=”” fileType=””/>
 * 
 * Attribute		Description
 * name				The name of this template.  This will be displayed in the execution log
 * mode				The type of template.  Values are [Input, Output]
 * fileType			The type of file to process. Values are [CSV, Excel]
 * 
 * HTML-formatted text below:
 * -->
 * <p>
 * <code>&lt;template name=”” mode=”” fileType=””/&gt;</code>
 *</div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Valid Attributes</h3>
 *<table class="attributeSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Listing table, listing attributes, and an explanation">
 *<tr><th>Attribute</th><th>Description</th></tr>
 *<tr class="altColor"><td>name</td><td>The name of this template.  This will be displayed in the execution log</td></tr>
 *<tr class="rowColor"><td>mode</td><td>The type of template.  Values are [Input, Output]</td></tr>
 *<tr class="altColor"><td>fileType</td><td>The type of file to process. Values are [CSV, Excel]</td></tr>
 *</table></li></ul>
 *<div class="summary">
 * 
 * @author drothste
 *
 */
public class TemplateOperation extends Operation {
	private Output output;
	static final private String[][] DEFAULT_TEXT_FORMATS = new String[][] 
			{{"name=DATA_ERROR", "size=10", "border=THIN", "color=RED", "backColor=WHITE", "alignment=LEFT", "verticalAlignment=CENTER"}, 
			 {"name=BODY", "size=10", "border=THIN", "color=BLACK", "backColor=WHITE", "alignment=LEFT", "verticalAlignment=CENTER"}, 
			 {"name=BODY_HYPERLINK", "size=10", "border=THIN", "color=PURPLE", "backColor=WHITE", "alignment=LEFT", "underline=U_SINGLE", "verticalAlignment=CENTER"}, 
			 {"name=BODY_WRAP", "size=10", "border=THIN", "color=BLACK", "backColor=WHITE", "alignment=LEFT", "verticalAlignment=CENTER", "wrap=Y"}, 
			 {"name=HEADER", "size=10", "border=THIN", "bold=Y", "color=BLACK", "backColor=GREY_25_PERCENT", "alignment=CENTER", "verticalAlignment=BOTTOM", "wrap=Y"}, 
			 {"name=HEADER_LIST", "size=10", "border=THIN", "bold=Y", "color=BLACK", "backColor=GREY_25_PERCENT", "alignment=RIGHT", "verticalAlignment=BOTTOM", "wrap=Y"}};

	/** 
	 * Constructor
	 * @param ignored used to select this empty constructor
	 */
	public TemplateOperation(boolean ignored) {
		super();
	}
	
	/**
	 * Constructor
	 */
	public TemplateOperation() {
		super();

		for (int i = DEFAULT_TEXT_FORMATS.length; i-- > 0; ) {
			TextFormatOperation tf = new TextFormatOperation();
			tf.loadProperties(DEFAULT_TEXT_FORMATS[i]);
			addOperation(tf);
		}
	}

	@Override
	public String getOperationType() {
		return "template";
	}

	/**
	 * Set the {@link com.biogen.generator.io.output.Output Output} this template will execute on.
	 * @param output the {@link Output} for this template
	 */
	public void setOutput(Output output) {
		this.output = output;
	}
	
	/**
	 * Get the {@link com.biogen.generator.io.output.Output Output} for this template to execute on.
	 * @return the {@link com.biogen.generator.io.output.Output Output} for this template
	 */
	public Output getOutput() {
		return output;
	}
	
	/**
	 * Execute the template and all child {@link Operation}s
	 */
	public void executeOperation() {
		super.executeOperation(output);
	}
}
