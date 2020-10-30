package com.biogen.generator;

import java.util.ArrayList;

import com.biogen.generator.ertools.erstudio.*;
import com.biogen.generator.io.*;
import com.biogen.generator.template.*;
import com.biogen.utils.Debug;
import com.biogen.utils.Utility;

public class ERSTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Debug.enable();
		
		DM1FileParser ersp = new DM1FileParser();
		Diagram diag = null;
		try {
			diag = ersp.parse("C:\\Users\\drothste\\Documents\\ERStudio Data Architect 2016\\Test.dm1");
			ersp.dump("C:\\Users\\drothste\\Documents\\ERStudio Data Architect 2016\\Test.dm1");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
//		Output output = new ExcelOutput();
		Application app = Application.getInstance();
		app.setCurrentObject();
		
//		app.addFilter("doEntity");
//		app.addFilter("doAttribute");
//		app.addFilter("doIndex");
//		app.addFilter("doRelationship");
//		app.addFilter("doLineageBatch");
//		app.addFilter("doLineage");
		app.addFilter("doEDC");
//		app.addFilter("doSTTM");
//		app.addFilter("doMetadata");
		app.setPropertyAsString("DIRECTORY", "C:\\Projects\\Candle\\DV Documents\\International Data Vault\\Modeling\\Lineage\\");

		Utility.loadLanguage("PLSQL");
		TemplateXMLParser tp = new TemplateXMLParser();
//		Operation operation = tp.parse("C:\\Users\\drothste\\eclipse-workspace\\Data Vault Generator\\src\\com\\biogen\\generator\\template\\templates\\Export Metadata.xml");
		TemplateOperation operation = tp.parse("C:\\Users\\drothste\\eclipse-workspace\\Data Vault Generator\\src\\com\\biogen\\generator\\template\\templates\\Export EDC.xml");
//		Operation operation = tp.parse("C:\\Users\\drothste\\eclipse-workspace\\Data Vault Generator\\src\\com\\biogen\\generator\\template\\templates\\Build Workflows and STTMs.xml");
		
//		System.out.println(operation.dumpXML());
//
//		System.out.println(ERStudioLibrary.getApp().getProperty("ERSFullVersion"));
		Diagram diagram = ERStudioLibrary.getActiveDiagram(); //.openDiagram("C:\\Users\\drothste\\Documents\\ERStudio Data Architect 2016\\Coordinated Care System.dm1");
		diagram.setCurrentObject();
		diagram.dump("Filename");
		Model model = new Model(diagram.getPropertyAsComponent("ActiveModel"));
		model.setCurrentObject();
		model.dump("Model.name");
		SubModel subModel = model.getActiveSubModel();
		subModel.setCurrentObject();
		subModel.dump("Submodel.Name");

		ArrayList<ModelingObject> objList = new ArrayList<ModelingObject>();
//	 	model.getChild("BUSINESSDATAOBJECT", "Patient Therapy").dump("LogicalName", "\t");
		if (1 == 0 ) {
			for (ModelingObject ent : model.getChildren("Entity")) 
	//			if (ent.getPropertyAsString("Owner").contains("DH_LAND"))
	//			if (ent.getName().contains("DI ") && !ent.getName().contains("TOUCH") && !ent.getName().contains("ZN"))
				if (ent.getName().startsWith("DI ") || ent.getName().startsWith("BI "))
	//			if (ent.getName().contains("DI RDM P") || ent.getName().contains("BI RDM"))
					objList.add(ent);
			
				operation.putCurrentObjectsList("ENTITY", objList);
		}

		operation.executeOperation();

		Dictionary entDict = (Dictionary)diagram.getChild("EnterpriseDataDictionary", "Commercial Data Vault Dictionary");
		entDict.dump("Dictionary.Name");
	}
}
