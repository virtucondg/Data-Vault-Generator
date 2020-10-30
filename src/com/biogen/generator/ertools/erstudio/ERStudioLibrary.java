package com.biogen.generator.ertools.erstudio;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
/**
 * Utilities for working with ER/Studio
 * @author drothste
 *
 */
public class ERStudioLibrary {

	/** 
	 * the application ActiveX component 
	 */
	private static ActiveXComponent ers;
	
	static private void checkErs() {
		if (null==ers)
			ers = new ActiveXComponent(new Dispatch("ERStudio.Application"));
//			ers = new ActiveXComponent("ERStudio.Application");
	}

	/** 
	 * Get the application component
	 * @return the application ActiveX component
	 */
	static public ActiveXComponent getApp() {
		checkErs();
		return ers;
	}
	
	/**
	 * Open a diagram
	 * @param file the diagram to open
	 * @return the Diagram object
	 */
	static public Diagram openDiagram(String file) {
		ers = ERStudioLibrary.getApp();
		ers.invoke("OpenFile", new Variant(file));
		Diagram diagram = getActiveDiagram();
		
		return diagram;
	}

	/**
	 * Get the active diagram from the application
	 * @return the active Diagram
	 */
	static public Diagram getActiveDiagram() {
		ers = ERStudioLibrary.getApp();
		Diagram diagram = new Diagram(ers.getPropertyAsComponent("ActiveDiagram"));
		
		return diagram;
	}

}
