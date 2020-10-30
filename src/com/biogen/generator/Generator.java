package com.biogen.generator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import com.biogen.generator.ertools.erstudio.*;
import com.biogen.generator.io.DM1FileParser;
import com.biogen.generator.io.TemplateXMLParser;
import com.biogen.generator.io.VaultExcelFileParser;
import com.biogen.generator.template.TemplateOperation;
import com.biogen.utils.*;
/**
 * This is the main generator class.  It requires one argument: the properties file.
 * 
 * @author drothste
 *
 */
public class Generator {

	private Application app = Application.getInstance(); 
	private Model model;
	
/**
 * Constructor
 * @param args array whose first element contains the name of the properties file to load.  Additional elements are in the form property=value
 * allowing overrides of parameters in the file as well as the ability to set other parameters.
 */
	public Generator(String[] args) {
		app.setCurrentObject();

		Debug.println(this.getClass().getSimpleName() +  " initializing properties from: " + args[0], true);

		app.loadProperties(args[0]);
		
		for (int i = 0; ++i < args.length; ) {
			String[] props = args[i].split("=");
			if (2==props.length) 
				app.setPropertyAsString(props[0], props[1]);	
		}
		Utility.loadLanguage(app.getPropertyAsString("language", "PLSQL"));

	}

	/**
	 * This method executes based on the parameter file loaded.
	 */
	@SuppressWarnings("unchecked")
	protected void generate() {
		TemplateXMLParser tp = new TemplateXMLParser();
		TemplateOperation operation = tp.parse(app.getPropertyAsString("template"));
		boolean isActiveFile = false;
		ArrayList<ModelingObject> selEntList = new ArrayList<ModelingObject>();
		ArrayList<ModelingObject> selViewList = new ArrayList<ModelingObject>();
		ArrayList<ModelingObject> selBusObjList = new ArrayList<ModelingObject>();

		Diagram diagram = null;
		SubModel subModel = null;
		String fileName; 
		
		fileName = app.getPropertyAsString("fileName");
		String loaderType = app.getPropertyAsString("loaderType", "DEFAULT");
		
		try {
			if ("DIRECT".equalsIgnoreCase(loaderType)) {
				fileName = app.getPropertyAsString("sourceDirectory") + fileName;
				diagram = new DM1FileParser().parse(fileName);
			} else if ("EXCEL".equals(loaderType)) {
				fileName = app.getPropertyAsString("sourceDirectory") + fileName;
				diagram = Diagram.nullObj;
				selEntList.addAll(new VaultExcelFileParser().parse(fileName));
			} else {
				if ("<<ACTIVE>>".equalsIgnoreCase(fileName)) {
					diagram = ERStudioLibrary.getActiveDiagram();
					isActiveFile = true;
				} else {
					fileName = app.getPropertyAsString("sourceDirectory") + fileName;
					diagram = ERStudioLibrary.openDiagram(app.getPropertyAsString("sourceDirectory") + fileName);
				}
			}
		} catch (IOException ex) {
			Debug.printError("Error loading file: " + fileName);
			Debug.printStackTrace(ex);
			
			System.exit(1);
		}
		
		if (null==diagram) {
			Debug.printError("Unable to ");
		}
		if (Diagram.nullObj != diagram) {
			diagram.setCurrentObject();
	
			model = new Model(diagram.getPropertyAsComponent("ActiveModel"));
			model.setCurrentObject();
	
			subModel = model.getActiveSubModel();
			subModel.setCurrentObject();
	
			if ("SELECTED".equalsIgnoreCase(app.getPropertyAsString("selection")) && isActiveFile) {
				ModelingObject e1;
				for (ModelingObject mob : subModel.getChildren("SelectedObject")) 
					switch (mob.getPropertyAsString("Type")) {
					case "1" : model.initChild("Entity");
							   e1 = model.getChild("Entity", mob.getProperty("ID"));
							   selEntList.add(e1);
							   break;
					case "16" : model.initChild("View");
							    e1 = model.getChild("View", mob.getProperty("ID"));
					   			selViewList.add(model.getChild("View", mob.getProperty("ID")));
					   			break;
					case "108" : model.initChild("BusinessDataObject");
								 e1 = model.getChild("BusinessDataObject", mob.getProperty("ID"));
					   			 selBusObjList.add(model.getChild("BusinessDataObject", mob.getProperty("ID")));
					   			 break;
				    default: 
					}
			}
			
			System.out.print(".");
			selectListFromString("SelectedBusinessDataObject", "BusinessDataObject", selBusObjList);
			System.out.print(".");
			selectListFromString("SelectedEntity", "Entity", selEntList);
			System.out.print(".");
			selectListFromString("SelectedView", "View", selViewList);
		}
		
		System.out.print(".");
		if ((0==selEntList.size()) && (0 < selBusObjList.size())) {
			ArrayList<ModelingObject> ae = (ArrayList<ModelingObject>) subModel.getChildren("ENTITY");
			System.out.print("!");
			for (ModelingObject bdo : selBusObjList)
				for (ModelingObject ent : ((BusinessDataObject)bdo).getReferencedObjects())
					if (ae.contains(ent))
							selEntList.add(ent);
		}
		
		if (Diagram.nullObj != diagram) {
			System.out.print(".");
			operation.putCurrentObjectsList("ENTITY", (0 < selEntList.size()) ? selEntList : subModel.getChildren("ENTITY"));
			System.out.print(".");
			operation.putCurrentObjectsList("VIEW", (0 < selViewList.size()) ? selViewList : subModel.getChildren("VIEW"));
			System.out.print(".");
			operation.putCurrentObjectsList("BUSINESSDATAOBJECT", (0 < selBusObjList.size()) ? selBusObjList : subModel.getChildren("BUSINESSDATAOBJECT"));
			System.out.println("Done");
		} else {
			System.out.print(".");
			operation.putCurrentObjectsList("ENTITY", (0 < selEntList.size()) ? selEntList : null);			
		}
		operation.executeOperation();
	}

	/**
	 * This procedure creates a list of objects based on a semicolon (";") separated list.
	 * @param key the list of objects to turn into a list, semicolon-separated
	 * @param entityType the type of entity the keys refer to (ex: ENTITY)
	 * @param list the list of objects found from the key string
	 */
	private void selectListFromString(String key, String entityType, ArrayList<ModelingObject> list) {
		for (String item : app.getPropertyAsString(key, "").split(";")) {
			ModelingObject mo = model.getChild(entityType, item.trim());
			if (null!=mo) 
				list.add(mo);
		}
	}
	
	/**
	 * The main method
	 * @param args the list of parameters passed into the class 
	 */
	public static void main(String[] args) {
		Debug.enable();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat jdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

		if (0==args.length) {
			Debug.printError("ER/Studio Generator: missing configuration properties file!");
			Debug.printError("usage:  Generator <folder>/<config file> [key=value]... ");
			Debug.printError("\tkey=value pairs to override properties in the configuration file");
			System.exit(0);
		}

		Debug.println("ER/Studio Generator: " + args[0] + " start time=" +jdf.format(date), true);
		
		Generator generator = new Generator(args);
		generator.generate();

		Date endDate = new Date(System.currentTimeMillis());
		Debug.println("ER/Studio Generator: complete; start time=" +jdf.format(date) + "; end time=" +jdf.format(endDate) + "; elapsed time=" + String.valueOf((endDate.getTime() - date.getTime()) / 1000) + " seconds.", true);
	}
}
