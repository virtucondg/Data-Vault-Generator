package com.biogen.generator.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.biogen.generator.ertools.erstudio.Attribute;
import com.biogen.generator.ertools.erstudio.DataFlow;
import com.biogen.generator.ertools.erstudio.DataMovementColumnLink;
import com.biogen.generator.ertools.erstudio.Diagram;
import com.biogen.generator.ertools.erstudio.Entity;
import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.biogen.utils.Debug;
import com.jacob.com.Variant;

/** 
 * This class processes an ER/Studio .DM1 file.
 * @author drothste
 *
 */
public class VaultExcelFileParser {
	private String filename = null;
	private static HashMap<String, String> sheetTypes;
	private static HashMap<String, String[]> headers;
	private static HashMap<String, String[]> tables;
	private static HashMap<String, String[]> attributes;
	
	private Workbook workbook;
	
	static {
		sheetTypes = new HashMap<String, String>();
		sheetTypes.put("E", "Entity");
		sheetTypes.put("A", "Attribute");

		headers = new HashMap<String, String[]>();
		headers.put("Entity", new String[] {"GUID", "Owner", "EntityName", "TableName", "BusinessDataObject", 
											"Note", "Definition", "Workflow Name", "View Type", "Bridge Table List", "Manual Override"});

		headers.put("Attribute",  new String[] {"EntityGUID", "EntityOwner", "EntityName", "TableName", "SequenceNo",
												"GUID", "AttributeName", "LogicalRoleName", "ColumnName", "RoleName", "DomainName",
												"Usage", "TargetTable", // generator columns
												"DataType", "Length", "Scale", "$$", "Nullable", "DefaultText", 
												"PrimaryKey", "ForeignKey", "Note", "Definition", "Rate of Change", "Completeness", "SourceDirectTransformationLogic"});
		
		attributes = new HashMap<String, String[]>();
//		attributes.put("HKEY", new String[] {}); 
		attributes.put("TargetHKEY", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "$TgtAttributeName", "", "$TgtColumnName", "", "DV MD5 Key", "", "", "VARCHAR", "32", "", "", "N", "", "N", "N", "", "", "", "", "MD5:!$$RECORD_SOURCE$$"}); 
		attributes.put("RCTS", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Record Create Timestamp", "", "RECORD_CREATE_TS", "", "DV Create Timestamp", "", "", "TIMESTAMP/DATE", "", "", "", "N", "", "Y", "N", "", "", "", "", "!$$SESSION_START_TIME$$"}); 
		attributes.put("RecordSource", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Record Source", "", "RECORD_SOURCE", "", "DV Data Source", "", "", "VARCHAR", "255", "", "", "N", "", "N", "N", "", "", "", "", "!$$RECORD_SOURCE$$"});
		attributes.put("RETS", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Record End Timestamp", "", "RECORD_END_TS", "", "DV End Timestamp", "", "", "TIMESTAMP/DATE", "", "", "", "Y", "TO_DATE('31-DEC-9999','DD-MON-YYYY')", "N", "N", "", "Record End Timestamp", "", "", "'31-DEC-9999'"}); 
		attributes.put("RecordCurrentFlag", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Record Current Flag", "", "RECORD_CURRENT_FLG", "", "Flag", "", "", "CHAR", "1", "", "", "N", "", "N", "N", "", "Record Current Flag", "", "", "'Y'"});
		attributes.put("ValidDataFlag", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Valid Data Flag", "", "VALID_DATA_FLG", "", "Flag", "", "", "CHAR", "1", "", "", "Y", "", "N", "N", "", "Record Valid Flag", "", "", "'Y'"}); 
		attributes.put("RecordErrorCode", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Record Error Code", "", "ERROR_CODE", "", "", "", "", "VARCHAR", "25", "", "", "Y", "", "N", "N", "", "Record Error Code", "", "", ""}); 
		attributes.put("RowHashKey", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "Row Hash Key", "", "ROW_HASH_KEY", "", "DV MD5 Key", "", "", "VARCHAR", "32", "", "", "N", "", "N", "N", "", "Row Hash Key", "", "", "MD5"}); 
		attributes.put("ETLName", new String[] {"", "$Owner", "$EntityName", "$TableName", "#", "", "ETL Process Name", "", "ETL_PROCESS_NAME", "", "DV ETL Process Name", "", "", "VARCHAR", "255", "", "", "N", "", "N", "N", "", "", "", "", "!$$ETL_PROCESS_NAME$$"});

		tables = new HashMap<String, String[]>();
		tables.put("Satellite", new String[] {"RCTS", "RecordSource", "RETS", "RecordCurrentFlag", "ValidDataFlag", "RecordErrorCode", "RowHashKey", "ETLName"});	
		tables.put("Hub", new String[] {"RecordSource", "RCTS", "ETLName"});	
		tables.put("Link", new String[] {"RecordSource", "RCTS", "ValidDataFlag", "RecordErrorCode", "ETLName"});
		
	}

	/**
	 * Constructor
	 */
	public VaultExcelFileParser() {
	}

	/**
	 * Constructor
	 * @param filename the file to parse
	 */
	public VaultExcelFileParser(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Parse the .DM1 file
	 */
	public void parseVaultModel() {
		if (null!=this.filename)
			try {
				parse(this.filename);
			} catch (IOException ex) {
				Debug.printStackTrace(ex);
			}
	}

	/**
	 * Parse the excel file into a {@link Entity}/{@link Attribute} structure
	 * @param filename the file to parse
	 * @return a {@link Diagram} with all child objects
	 * @throws IOException if the file cannot be processed
	 */
	public ArrayList<Entity> parse(String filename) throws IOException {
		RawDataObject obj = null;
		HashMap<String, RawDataObject> objects = new HashMap<String, RawDataObject>();
		DataFormatter dataFormatter = new DataFormatter();
		HashMap<String, Integer> curCols = new HashMap<String, Integer>();
		
		Debug.println("Direct loading file: " + filename, true);
		
		try {
			workbook = WorkbookFactory.create(new File(filename));

			for (Sheet sheet : workbook) {
				StringBuilder sb = new StringBuilder().append(sheet.getSheetName());
				
	            String sheetType = sheetTypes.get(sb.reverse().toString().substring(0, 1));
	            String[] header = headers.get(sheetType);
	            
	            if (null != sheetType) {
		            obj = new RawDataObject(sheetType);
	            	objects.put(sheetType, obj);
	            	
		            boolean skipRow = true;
		            for (Row row : sheet) {
		            	if (skipRow) {
		            		skipRow = false;
		            	} else {
			            	HashMap<String, String> props = new HashMap<String, String>();
			            	props.put("RawType", "EXCEL");
			            	
			            	int colPos = 0;
			            	for (Cell cell : row) {
			            		props.put(header[colPos++], dataFormatter.formatCellValue(cell));
			            		
			            		if (colPos >= header.length) break;
			            	}
				            
				            obj.setPropertyMap(props);
		            	}
		            }
	            }
			}
			
			workbook.close();
			workbook = null;
		} catch (Exception ex) {
	    	Debug.printStackTrace(ex);
	    }
	    finally {
	    	if(null != workbook) 
	    		workbook.close();
	    }
		
		for (RawDataObject os : objects.values())
			Debug.println("\t\t" + os.getName() + "=" + String.valueOf(os.getPropertiesCount()));
		
		ArrayList<Entity> entities = Entity.create(null, objects, null);
		
        String[] header = headers.get("Attribute");
		obj = objects.get("Attribute");
		
		CollectionLoader<? extends ModelingObject> cl;
		HashMap<String, String> entityKeys = new HashMap<String, String>();
		
		for (Entity entity : entities) {
			String[] columns = tables.get(entity.getPropertyAsString("EntityName").substring(entity.getPropertyAsString("EntityName").lastIndexOf(" ") + 1));
			
			if (null != columns) {
				obj.clear();
				cl = entity.getChildCollection("ATTRIBUTE");
				
				for (ModelingObject attribute : cl.getList()) {
					if ("Y".equals(attribute.getPropertyAsString("PrimaryKey"))) {
						entityKeys.put(entity.getPropertyAsString("EntityName"), attribute.getPropertyAsString("AttributeName"));
						if (attribute.getPropertyAsString("ColumnName").endsWith("HKEY"))
							attribute.setPropertyAsString("SourceDirectTransformationLogic", "MD5:$$RECORD_SOURCE$$");
						
					}
				}
				
				int seq = 1;
				for (String col : columns) {
	            	HashMap<String, String> props = new HashMap<String, String>();
	            	props.put("RawType", "EXCEL");
	            	
	            	int colPos = 0;
	            	for (String attr : attributes.get(col)) {
	            		if (attr.startsWith("!"))
	            			attr = attr.substring(1);
	            		else if (attr.contains("$"))
	            			attr = entity.getPropertyAsString(attr.substring(1));
	            		else if (attr.equals("#"))
	            			attr = String.valueOf(++seq);
	            		
	            		props.put(header[colPos++], attr);
	            	}	            	
					if (0 < colPos) {
						obj.setPropertyMap(props);
					}
	            }
				
				if (1 < obj.getPropertiesCount()) 
					cl.addAll(Attribute.create(entity, objects, entity.getPropertyAsString("Owner") + "|" + entity.getPropertyAsString("EntityName")));

				curCols.put(entity.getPropertyAsString("EntityName"), new Integer(seq));
			}				
		}
		
		RawDataObject dfObj = new RawDataObject("Model");
		objects.put("Model", dfObj);
		obj = new RawDataObject("Transformation");
		objects.put("Transformation", obj);
		String dfs = new String();
    	HashMap<String, String> props = new HashMap<String, String>();
		
		for (Entity entity : entities) {
			if (0 < entity.getPropertyAsString("Workflow Name").length()) {
            	for (String workflow : entity.getPropertyAsString("Workflow Name").split(";")) {
            		String[] wf = workflow.split(":");
        
            		if (!dfs.contains(wf[0])) {
            			dfs = dfs + wf[0] + ";";
            			
            			props.put("RawType", "EXCEL");
            			props.put("ModelType", "8");
            			props.put("DiagramID", "1");
            			props.put("ModelName", wf[0]);
            			props.put("Model_ID", wf[0]);
            			
            			dfObj.setPropertyMap(props);
            			props = new HashMap<String, String>();
            		}
            		
	            	props.put("RawType", "EXCEL");
            		props.put("DataFlow", wf[0]);
            		props.put("Diagram_ID", "1");
            		props.put("Model_ID", wf[0]);
            		props.put("Name", wf[1]);
        			props.put("WorkflowName", workflow);
            		props.put("GUID", "");
            		props.put("Source Delta Filter Override", "LASTMODIFIEDDATE");
            		props.put("Source Delete Filter Override", "ISDELETED = 1");
            		props.put("Archive Data", "No");
            		props.put("Data Load Frequency", "Multiple/Day");
            		props.put("Job Failure Recovery", "Restart Entire Load");
            		props.put("Source File Format", "Salesforce Cloud");
            		props.put("Drop Indexes on Load", "No");
            		props.put("Source Delete Filter Override", "ISDELETED = 1");
            		props.put("Transformation_ID", wf[1]);

                	obj.setPropertyMap(props);
            		props = new HashMap<String, String>();
            	}            	
			}
		}
		
		DataFlow.create(null, objects, "1");

		RawDataObject dmclObj = new RawDataObject("DataMovementColumnLink");
		obj = objects.get("Attribute");
		obj.clear();
		
		String[] attrCols = headers.get("Attribute");
		String[] tgtKeyValues = attributes.get("TargetHKEY");
		HashMap<String, HashMap<String, String>> userKeys = new HashMap<String, HashMap<String, String>>();
		
		for (Entity entity : entities) {
			if (0 < entity.getPropertyAsString("GUID").length())
				for (ModelingObject attribute : entity.getChildren("Attribute")) {
					String tgtTables = attribute.getPropertyAsString("TargetTable");
					String baseTable = tgtTables.split(";")[0];
					String attrType = attribute.getPropertyAsString("AttributeType");
					String[] usage = attribute.getPropertyAsString("usage").split(":");
					
					String nullable = "NULL";
					
					for (String tgtTable : tgtTables.split(";")) {
						ModelingObject tgtEnt = Entity.find(new Variant(tgtTable));
						
						if (null != tgtEnt) {
							int maxCol = curCols.get(tgtEnt.getPropertyAsString("EntityName"));
							String[] workflow = tgtEnt.getPropertyAsString("Workflow Name").split(":");
							
							if (0 < usage.length) // & tgtTable.equals(baseTable))
								for (String token : usage[0].toUpperCase().split(";"))
									switch (token.toUpperCase()) {
									case "LINK":
										
									case "KEY":
									props = new HashMap<String, String>();
									props.put("RawType", "EXCEL");
									props.put("SOURCEENTITYGUID", entity.getPropertyAsString("GUID"));
									props.put("SOURCEENTITYOwner", entity.getPropertyAsString("Owner"));
									props.put("SOURCEENTITYEntityName", entity.getPropertyAsString("EntityName"));
									props.put("SrcTrgtTableName", entity.getPropertyAsString("TableName"));
									props.put("SrcTrgtColumnName", attribute.getPropertyAsString("ColumnName"));
									props.put("SOURCEATTRIBUTEGUID", attribute.getPropertyAsString("GUID"));
									props.put("SOURCEATTRIBUTEAttributeName", attribute.getPropertyAsString("AttributeName"));
									props.put("Owner", tgtEnt.getPropertyAsString("Owner"));
									props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
									props.put("AttributeName", entityKeys.get(tgtEnt.getPropertyAsString("EntityName")));
									props.put("BatchName", workflow[0]);
									props.put("WorkflowName", workflow[1]);
									props.put("QualifiedName", workflow[0] + ":" + workflow[1]);
									
									nullable = "NOT NULL";
									
									dmclObj.setPropertyMap(props);
	
									break;
									case "FK":
										if (baseTable.equals(tgtTable)) {
											props = new HashMap<String, String>();
											for (int i = attrCols.length; i-- > 0; ) 
												props.put(attrCols[i], tgtKeyValues[i]);
							            	props.put("RawType", "EXCEL");
											props.put("SequenceNo", String.valueOf(++maxCol));
											props.put("EntityOwner", tgtEnt.getPropertyAsString("Owner"));
											props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
											props.put("TableName", tgtEnt.getPropertyAsString("TableName"));
											props.put("AttributeName", usage[1]);
											props.put("ColumnName", usage[2]);
											props.put("SourceDirectTransformationLogic", "MD5:$$RECORD_SOURCE$$");
											props.put("SourceDirectTransformationDescription", "");
		
											obj.setPropertyMap(props);		
											
											props = new HashMap<String, String>();
											props.put("RawType", "EXCEL");
											props.put("SOURCEENTITYGUID", entity.getPropertyAsString("GUID"));
											props.put("SOURCEENTITYOwner", entity.getPropertyAsString("Owner"));
											props.put("SOURCEENTITYEntityName", entity.getPropertyAsString("EntityName"));
											props.put("SrcTrgtTableName", entity.getPropertyAsString("TableName"));
											props.put("SrcTrgtColumnName", attribute.getPropertyAsString("ColumnName"));
											props.put("SOURCEATTRIBUTEGUID", attribute.getPropertyAsString("GUID"));
											props.put("SOURCEATTRIBUTEAttributeName", attribute.getPropertyAsString("AttributeName"));
											props.put("Owner", tgtEnt.getPropertyAsString("Owner"));
											props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
											props.put("AttributeName", usage[1]);
											props.put("BatchName", workflow[0]);
											props.put("WorkflowName", workflow[1]);
											props.put("QualifiedName", workflow[0] + ":" + workflow[1]);
											
											dmclObj.setPropertyMap(props);
										}
										
										break;
									case "LOCALHKEY":
										props = new HashMap<String, String>();
										for (int i = attrCols.length; i-- > 0; ) 
											props.put(attrCols[i], tgtKeyValues[i]);
						            	props.put("RawType", "EXCEL");
										props.put("SequenceNo", "999");
										props.put("EntityOwner", tgtEnt.getPropertyAsString("Owner"));
										props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
										props.put("TableName", tgtEnt.getPropertyAsString("TableName"));
										props.put("AttributeName", attribute.getPropertyAsString("ColumnName") + "_HKEY");
										props.put("ColumnName", attribute.getPropertyAsString("ColumnName") + "_HKEY");
										props.put("SourceDirectTransformationLogic", "MD5:$$RECORD_SOURCE$$");
										props.put("SourceDirectTransformationDescription", "");

										userKeys.put(tgtEnt.getPropertyAsString("EntityName") + ":" + attribute.getPropertyAsString("AttributeName"), props);		
										
										props = new HashMap<String, String>();
										props.put("RawType", "EXCEL");
										props.put("SOURCEENTITYGUID", entity.getPropertyAsString("GUID"));
										props.put("SOURCEENTITYOwner", entity.getPropertyAsString("Owner"));
										props.put("SOURCEENTITYEntityName", entity.getPropertyAsString("EntityName"));
										props.put("SrcTrgtTableName", entity.getPropertyAsString("TableName"));
										props.put("SrcTrgtColumnName", attribute.getPropertyAsString("ColumnName"));
										props.put("SOURCEATTRIBUTEGUID", attribute.getPropertyAsString("GUID"));
										props.put("SOURCEATTRIBUTEAttributeName", attribute.getPropertyAsString("AttributeName"));
										props.put("Owner", tgtEnt.getPropertyAsString("Owner"));
										props.put("EntityOwner", tgtEnt.getPropertyAsString("Owner"));
										props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
										props.put("AttributeName", attribute.getPropertyAsString("AttributeName") + "_HKEY");
										props.put("BatchName", workflow[0]);
										props.put("WorkflowName", workflow[1]);
										props.put("QualifiedName", workflow[0] + ":" + workflow[1]);
										
										dmclObj.setPropertyMap(props);
		
										break;
									default:
									}
								
							props = new HashMap<String, String>();
							for (String col : attrCols)
								props.put(col, attribute.getPropertyAsString(col));
			            	props.put("RawType", "EXCEL");
							props.put("SequenceNo", String.valueOf(++maxCol));
							props.put("GUID", "");
							props.put("EntityGUID", "");
							props.put("EntityOwner", tgtEnt.getPropertyAsString("Owner"));
							props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
							props.put("TableName", tgtEnt.getPropertyAsString("TableName"));
							props.put("SourceDirectTransformationLogic", "");
							props.put("SourceDirectTransformationDescription", "");
							props.put("Nullable", nullable);

							obj.setPropertyMap(props);		
							
							props = new HashMap<String, String>();
							props.put("RawType", "EXCEL");
							props.put("SOURCEENTITYGUID", entity.getPropertyAsString("GUID"));
							props.put("SOURCEENTITYOwner", entity.getPropertyAsString("Owner"));
							props.put("SOURCEENTITYEntityName", entity.getPropertyAsString("EntityName"));
							props.put("SrcTrgtTableName", entity.getPropertyAsString("TableName"));
							props.put("SrcTrgtColumnName", attribute.getPropertyAsString("ColumnName"));
							props.put("SOURCEATTRIBUTEGUID", attribute.getPropertyAsString("GUID"));
							props.put("SOURCEATTRIBUTEAttributeName", attribute.getPropertyAsString("AttributeName"));
							props.put("SourceDirectTransformationLogic", "");
							props.put("SourceDirectTransformationDescription", "");
							props.put("Owner", tgtEnt.getPropertyAsString("Owner"));
							props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
							props.put("AttributeName", ("".equals(attribute.getPropertyAsString("LogicalRoleName")) ? attribute.getPropertyAsString("AttributeName") : attribute.getPropertyAsString("LogicalRoleName")));
							props.put("BatchName", workflow[0]);
							props.put("WorkflowName", workflow[1]);
							props.put("QualifiedName", workflow[0] + ":" + workflow[1]);
							
							dmclObj.setPropertyMap(props);
							
							props = new HashMap<String, String>();
							props.put("RawType", "EXCEL");
							props.put("SOURCEENTITYGUID", entity.getPropertyAsString("GUID"));
							props.put("SOURCEENTITYOwner", entity.getPropertyAsString("Owner"));
							props.put("SOURCEENTITYEntityName", entity.getPropertyAsString("EntityName"));
							props.put("SrcTrgtTableName", entity.getPropertyAsString("TableName"));
							props.put("SrcTrgtColumnName", attribute.getPropertyAsString("ColumnName"));
							props.put("SOURCEATTRIBUTEGUID", attribute.getPropertyAsString("GUID"));
							props.put("SOURCEATTRIBUTEAttributeName", attribute.getPropertyAsString("AttributeName"));
							props.put("SourceDirectTransformationLogic", "");
							props.put("SourceDirectTransformationDescription", "");
							props.put("Owner", tgtEnt.getPropertyAsString("Owner"));
							props.put("EntityName", tgtEnt.getPropertyAsString("EntityName"));
							props.put("AttributeName", "Row Hash Key");
							props.put("BatchName", workflow[0]);
							props.put("WorkflowName", workflow[1]);
							props.put("QualifiedName", workflow[0] + ":" + workflow[1]);
							
							dmclObj.setPropertyMap(props);
							
							curCols.put(tgtEnt.getPropertyAsString("EntityName"), new Integer(maxCol));
						}
					}
				}
		}
		
//		obj.initialize();
//		while (obj.hasNext()) {
//			props = obj.getRecord();
//			System.out.print("[");
//			for (Map.Entry<String, String> me : props.entrySet())
//				System.out.print(me.getKey() + "=\"" + me.getValue() + "\",");
//			System.out.println("]");
//		}
//		
		for (Map.Entry<String, HashMap<String, String>> me : userKeys.entrySet()) {
			String entityName;
			int maxCol = curCols.get((entityName = me.getKey().split(":")[0]));
			props = me.getValue();
			props.put("SequenceNo", String.valueOf(++maxCol));
			obj.setPropertyMap(props);
			curCols.put(entityName, new Integer(maxCol));
		}
		
		for (Entity entity : entities) {
			cl = entity.getChildCollection("ATTRIBUTE");
			ArrayList<Attribute> attrAl = Attribute.create(entity, objects, entity.getPropertyAsString("Owner") + "|" + entity.getPropertyAsString("EntityName"));
			cl.addAll(attrAl);
		}

		objects.put("DataMovementColumnLink", dmclObj);
		for (Entity entity : entities) {
			if (entity.getPropertyAsString("EntityName").startsWith("DI "))
				for (ModelingObject attribute : entity.getChildren("ATTRIBUTE")) {
					cl = attribute.getChildCollection("DATAMOVEMENTCOLUMNLINK");
					ArrayList<DataMovementColumnLink> attrAl = DataMovementColumnLink.create(attribute, objects, attribute.getPropertyAsString("EntityOwner") + "|" + attribute.getPropertyAsString("EntityName") + "|" 
					+ ("".equals(attribute.getPropertyAsString("LogicalRoleName")) ? attribute.getPropertyAsString("AttributeName") : attribute.getPropertyAsString("LogicalRoleName")));
					cl.addAll(attrAl);			
				}
		}

		if (0==entities.size())
			return null;
		
		return entities;
	}
	
}
