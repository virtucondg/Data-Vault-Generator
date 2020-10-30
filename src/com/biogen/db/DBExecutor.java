package com.biogen.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class DBExecutor {

	BufferedWriter bw;
	private HashMap<String, String> baseQueries = new HashMap<String, String>();
	private HashMap<String, String> patterns = new HashMap<String, String>();
	
	private void initMaps() {
		baseQueries.put("Connection Tester", "SELECT 1 FROM dual");
		baseQueries.put("Object List", "SELECT SYS_CONTEXT('USERENV', 'DB_NAME') DATABASE, SYS_CONTEXT('USERENV', 'SESSION_USER') OWNER, object_name, object_type FROM user_objects WHERE object_type in ('TABLE','VIEW','INDEX','MATERIALIZED VIEW','SEQUENCE','PROCEDURE','PACKAGE','FUNCTION','SYNONYM')");
		baseQueries.put("Grant List", "SELECT SYS_CONTEXT('USERENV', 'DB_NAME') DATABASE, a.*, b.object_type FROM user_tab_privs a, user_objects b where a.table_name = b.object_name AND a.grantee NOT LIKE USER || '\\_R_' ESCAPE '\\' AND a.owner NOT LIKE '%SYS%'");
		baseQueries.put("Grant List Custom", "SELECT SYS_CONTEXT('USERENV', 'DB_NAME') DATABASE, a.*, b.object_type FROM user_tab_privs a, user_objects b where a.table_name = b.object_name AND a.grantee NOT LIKE USER || '\\_R_' ESCAPE '\\' AND a.owner NOT LIKE '%SYS%' AND a.grantee IN (SELECT username from all_users WHERE username in ('AB','ABHOWMIC','ADAS6','AERZEN','AGHOSH7','AGIRIYAP','AGUPTA','AGUPTA11','AJENKINS','AKERN','ALOSKUTO','AMCLAUGH','AMOHAN1','AMUKHER4','ANANDA1','APP_ARCADE','APRADHAN','ARAO2','AROBERSO','ARYBAK','ASAHU3','ASHEIKMO','ASINGH3','ASR','AZHIDKOV','BALAJIGR','BALASUBJ','BAZIZI','BBALARAM','BGUPTA1','BHARPER1','BINAGANT','BINAGANT_DB','BIOVERATIV','BISCOMM_FAX_USER','BMANNIKE','BO_LANDING','BO_SUPPORT','BOEHLER','BOI_RPT','BSPERRY1','BVYAVAHA','CALL_MINER','CDW','CDW_DEV3','CGANDINO_DB','CHALLISS','CIM_RPT','COLER','CVENT','DANGELO1','DATAMASK','DBADMIN','DBSNMP','DCHAUDH2','DDEY01','DDWIVEDI','DDZUBA','DELPHIX','DELPHIX_DB','DEMANTRA_READ','DFRANCIS','DH_ABC','DH_ABC_DEV3','DH_ARCH','DH_ARCH_DEV3','DH_DEV_VALID','DH_ENV_MGMT','DH_IDS','DH_IDS_EU','DH_IDS_US','DH_LAND_ALANDA','DH_LAND_AXIS','DH_LAND_BBU','DH_LAND_BELG','DH_LAND_CEG','DH_LAND_COMMON','DH_LAND_CVENT','DH_LAND_FGS','DH_LAND_ININ','DH_LAND_ININ_DEV3','DH_LAND_INSIGHT','DH_LAND_LH','DH_LAND_MAR','DH_LAND_MDM','DH_LAND_MDM_DEV3','DH_LAND_MEDFORCE','DH_LAND_MIMI','DH_LAND_MKTG','DH_LAND_OM','DH_LAND_PS','DH_LAND_PW','DH_LAND_RRD','DH_LAND_SFDC','DH_LAND_SFDC_EU','DH_LAND_SFDC_PS','DH_LAND_SFDC_US','DH_LAND_SFDC_US_DEV3','DH_LAND_TRACKWISE','DH_LAND_VBM','DH_LAND_XPRIS','DH_LND_AFMON','DH_LND_BLISS','DH_LND_CCM','DH_LND_DIRECT_CHOICE','DH_LND_EPOC','DH_LND_EXTGT','DH_LND_MEDSCP','DH_LND_MENTOR_MENTEES','DH_LND_QUALITY_HEALTH','DH_MKTG_IDS','DH_RDM_PUBLISH','DH_RDM_PUBLISH_DEV3','DH_RPT_FEED','DH_SIT_VALID','DI_BBU','DI_INTL','DI_PS','DI_RDM','DII','DIP','DM_BBU','DM_EU','DM_EU_DEV3','DM_HEM','DM_HEM_PS','DM_INTL','DM_MAR_OPS','DM_MKTG','DM_MS','DM_MS_PS','DM_ORCH','DM_PS','DM_SMA','DM_TELE','DM_TOUCH','DM_US_DEV3','DM_US_FIELD','DMANNINO','DMARATT','DPA','DPOGORSK','DROTHSTE','DRUSSELL','DUSTINR','DZEYER','EDEFEO','EDIAZOUT_DB','EMDM_SVC','ERZENA','ETL','ETL_DEV','ETOMASI','FRANCISD','GANZENMA','GDPRDM','GDPRDM_TGT','GLEAVITT','GPALADUG','GSALAUDD','GTEEKARA','GVARADAP','HDHONGAD','HWANG1','IEXPENSE','IGAFTONY','IJAMADAR','IZUEV','JALLEN4','JANDERSON','JFOLLEN','JLEONI_DB','JMILNE1','JMS','JNADARAJ','JOBERTOVA_DB','JSANDER2','JSHRIKANT','JTALUKDA','JYAJEPEY','KACHARY','KBHAVE','KDOLLY','KDONOFRI','KDRAGNEV','KK','KKANAGAR','KKUILAUA','KLUMPENK','KRAMA','KVERBIEST','LDAVIS1','LM','LSPEZZAN','LWANG3','MABDULKA','MACDONAP','MALBRECH','MASHRAFI','MCHAVANO','MCUI1','MDUFF','MERUSAVA','MERUSAVADLA','METAMAN','MFLORES_DB','MFRISCH','MHSHAH1','MIG','MIGDV','MKUMAR5','MKUMAR9','MLARIN','MLBK','MMCGEE','MMCHUGH1','MPERUCCH','MSHARMA5_DB','MTADIC','MTADIC_DB','MTANNEERU','MUPSON','MVAIDYAN','NGADKARI','NGOYAL1','NNATARAJ','NPARULKAR','NPATEL','NPATEL12','NSAHA','NSHENDE','OUTLN','PANTONNI','PARORA1','PBENJAMI','PCM_DB','PDHARMARAJ','PGUNDA','PGUNDIME','PJAMDADE','PKANDASA','PKUMARCH','PMAJEE','PMOHANTY','PMOHAPAT','PMOHAPATRA','PRISM','PS_RPT','PUGINE','PVENKATA','QA','QLIKVIEW','RBALUSU','RBHOGADI','RCAMINIT','RDIMITRO','REMS_READ','RGHOSH1','RGUPTA','RKOSHEVO','RMAGIPOGU','RMARAPPAN','RMARTINS','RMULUKOO','RPAWAR','RSATALE','RSAXENA','RYAMMANU','SACHARJE','SAMIP','SBAL_DB','SBISWAS2','SBISWAS3','SBOLA_DB','SBRAHMA','SCHAKRA','SDHANDA1','SDUTT','SEATON','SGOPALA1','SGUPTA16','SJAIN7','SJESWANI','SJOSHI5','SKENNY_DB','SKICHAMB','SKUMAR','SKUMARI','SKURAM_DB','SMAHANAD','SMAHESHW_DB','SMAJUMD4_DB','SMESTAS','SMEWAR','SMOODIKK','SMOPIDEVI','SMUTHUKA','SMUTHUKRIS','SNAIK1','SOA','SOR_MAR','SOR_REMS_ZINBRYTA','SOR_SFDC_EU','SOR_SMA','SPAL_DB','SPARTA','SPERUGU','SPIAZZA','SQLTXADMIN','SQLTXPLAIN','SQOOP','SRAMANI','SREDDY','SROY2','SSAMPAT1','SSHEKHA1','STG_ALIGNMENT','STG_CCM','STG_CCS','STG_CHANNEL','STG_DQM_CCM','STG_DQM_VN','STG_MEDICAL','STG_MKTG','STG_MKTG_DQM','SUPADHYA','SVASUDEV','SVC_TW_REMS','SVC-QLIKVIEW','SVONZIRP','SYABANNA','SYADALA1','TPALAZOL','TRACKWISE','TS','TSUDAME','TTOEPFER','UGUNDA','UN','VKOTHAND','VPDMAN','VPERICHAR','VPUVVADA','VRATHI','VS','VSETHI','VSHAH1','VSHANKAR','VSRIVAS1','VSUKUMAR','WEB_READ','XDB','YMISHRA','ZTARUNNU'))");
		baseQueries.put("Index List", "SELECT SYS_CONTEXT('USERENV', 'DB_NAME') DATABASE, SYS_CONTEXT('USERENV', 'SESSION_USER') OWNER, i.table_owner, i.index_name, i.table_name, LISTAGG(ic.column_name, ',') WITHIN GROUP (ORDER BY ic.column_position) COLUMNS FROM user_indexes i, user_ind_columns ic WHERE i.index_name = ic.index_name GROUP BY i.table_owner, i.index_name, i.table_name");
		baseQueries.put("Grant Role List", "SELECT SYS_CONTEXT('USERENV', 'DB_NAME') DATABASE, a.* FROM dba_role_privs a WHERE a.granted_role LIKE '%\\_R_' ESCAPE '\\' OR granted_role IN ('BI_ARCHITECT','BI_LANDING','DEPLOYER','DEVELOPER','DEVELOPER_LEAD','QA_TESTING','REPORT_WRITER','RESOURCE') ORDER BY a.grantee, a.granted_role");
		baseQueries.put("Grants Corrector", "BEGIN VPDMAN.utils_pkg.update_role_access; END;");
		baseQueries.put("Tablespace Corrector", "BEGIN VPDMAN.refine_object_tablespaces; END;");

		patterns.put("All", "dh_.+CDHB||di_.+CDHB||dm_.+CDHB||etl_.+CDHB||cdw_.+CDHB||stg_.+CDHB||sor_.+CDHB");
		patterns.put("CDH", "dh_.+CDHB");
		patterns.put("CDW", "dm_.+CDHB||cdw_.+CDHB");
		patterns.put("DataVault", "di_.+CDHB||dm_.+CDHB");
		patterns.put("Production", "dh_.+PRCDHB||dm_.+PRCDHB||etl_.+PRCDHB||cdw_.+PRCDHB||stg_.+PRCDHB");
		patterns.put("UAT", "dh_.+STCDHB||dm_.+STCDHB||etl_.+STCDHB||cdw_.+STCDHB||stg_.+STCDHB");
		patterns.put("SIT", "dh_.+TSCDHB||dm_.+TSCDHB||etl_.+TSCDHB||cdw_.+TSCDHB||stg_.+TSCDHB");
		patterns.put("Development", "dh_.+DVCDHB||dm_.+DVCDHB||etl_.+DVCDHB||cdw_.+DVCDHB||stg_.+DVCDHB");
		patterns.put("Support", "dh_.+SPCDHB||dm_.+SPCDHB||etl_.+SPCDHB||cdw_.+SPCDHB||stg_.+SPCDHB");
	}

	
	public DBExecutor()	{
		try {
			Class.forName ("oracle.jdbc.driver.OracleDriver");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		initMaps();
	}

	private String[] splitStatements(String sqlStatements, String user) {
		String[] statements = sqlStatements.split("\n/\n");
		for (int i = statements.length; i-- > 0;) {
			statements[i] = statements[i].replace("{@}", user);
		}
		
		return statements;
	}

	private String getBaseQuery(String name) {
		StringBuilder sb = new StringBuilder();
		for (String conn : name.split("\n/\n")) { 
			if (null==baseQueries.get(conn)) 
				sb.append(conn).append("\n/\n"); 
			else sb.append(baseQueries.get(conn)).append("\n/\n"); ;
		}
		
		return sb.toString();
	}
	
	private String getPattern(String name) {
		StringBuilder sb = new StringBuilder();
		for (String conn : name.split("\\|\\|")) {
			if (null==patterns.get(conn))
				sb.append(conn).append("||");
			else sb.append(patterns.get(conn)).append("||");
		}
		
		return sb.toString();
	}
	
	public void executeStatement(String[] tnsConnection, String sql) {
		long sTime;
			if (null==System.getProperty("oracle.net.tns_admin"))
				setTnsAdmin();
			
		    Connection connection = null;
		    PreparedStatement preparedStatement = null;

		    try {
		    	if (tnsConnection[2].equals("")) {
		    	}
		    	else {
			    	System.out.print("Connecting to " + tnsConnection[1] + " on " + tnsConnection[0] + "...");
					connection = DriverManager.getConnection("jdbc:oracle:thin:@" + tnsConnection[0], tnsConnection[1], tnsConnection[2]);
						
					System.out.println("established");
					String[] sqls = splitStatements(sql, tnsConnection[1]);
					for (String iSql : sqls) {
						if (0 < iSql.trim().length()) {
							System.out.print("    Executing \"" + iSql.trim() + "\"...");
							sTime = System.currentTimeMillis();
						    preparedStatement = connection.prepareStatement(iSql.trim());
						    try {
							    preparedStatement.execute();
							    connection.commit(); //&&&&&&&&
							    System.out.println("success in " + String.valueOf((System.currentTimeMillis() - sTime) / 1000) + " seconds");
						    } catch (Exception e) {
							    System.out.println("failed:" + e.getMessage().trim());
						    }
						}
					}
		    	}
		    } catch (Exception e) {
			    System.out.println("failed:" + e.getMessage().trim());
		    }
		    finally {
		      if (null!=preparedStatement) 
		    	  try { 
		    		  preparedStatement.close(); 
		    	  } catch (Exception ignored) {}
		      if (null!=connection) 
		    	  try { 
		    		  connection.close(); 
	    		  } catch (Exception ignored) {}
		      System.out.println("");
		    }	
	}
	
	public void executeQuery(String[] tnsConnection, String sql) {
		int row = 0;
		long sTime;
		if (null==System.getProperty("oracle.net.tns_admin"))
			setTnsAdmin();
		
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	    	if (tnsConnection[2].equals("")) {
	    	}
	    	else {
		    	System.out.print("Connecting to " + tnsConnection[1] + " on " + tnsConnection[0] + "...");
				connection = DriverManager.getConnection("jdbc:oracle:thin:@" + tnsConnection[0], tnsConnection[1], tnsConnection[2]);
					
				System.out.println("established");
				String[] sqls = splitStatements(sql, tnsConnection[1]);
				for (String iSql : sqls) {
					if (0 < iSql.trim().length()) {
						row++;
						System.out.print("    Executing row " + String.valueOf(row) + "\"" + iSql.trim() + "\"...");
					    preparedStatement = connection.prepareStatement(iSql.trim());
					    preparedStatement.setFetchSize(500);
					    sTime = System.currentTimeMillis();
					    try {
						    ResultSet rs = preparedStatement.executeQuery();
						    int colCount = rs.getMetaData().getColumnCount();
						    int rowCount = 0;
						    while (rs.next()) {
						    	rowCount++;
						    	for (int i = 0; i < colCount; ) {
						    		bw.write("\"" + rs.getString(++i) + "\",");
						    	}
						    	bw.write("\"\"\n");
						    }
						    bw.flush();
						    System.out.println("success:" + String.valueOf(rowCount) + " records retrieved in " + String.valueOf((System.currentTimeMillis() - sTime) / 1000) + " seconds");
					    } catch (Exception e) {
						    System.out.println("failed:" + e.getMessage().trim());
					    }
					}
				}
	    	}
	    } catch (Exception e) {
		    System.out.println("failed:" + e.getMessage().trim());
	    }
	    finally {
	      if (null!=preparedStatement) 
	    	  try { 
	    		  preparedStatement.close(); 
	    	  } catch (Exception ignored) {}
	      if (null!=connection) 
	    	  try { 
	    		  connection.close(); 
    		  } catch (Exception ignored) {}
	      System.out.println("");
	    }	
	}

	static void setTnsAdmin() {
	    String tnsAdmin = System.getenv("TNS_ADMIN");
	    if (tnsAdmin == null) {
	        String oracleHome = System.getenv("ORACLE_HOME");
	        if (oracleHome == null) {
	            oracleHome = "C:" + File.separatorChar + "oracle" + File.separatorChar + "product" + File.separatorChar + "11.2.0" + File.separatorChar + "client_1"; //failed to find any useful env variables
	        }
	        tnsAdmin = oracleHome + File.separatorChar + "network" + File.separatorChar + "admin";
	    }
	    System.setProperty("oracle.net.tns_admin", tnsAdmin);
	}
	
	
	public void executeCommands(String dbPattern, String sqlScript) {
		String dbPat = getPattern(dbPattern);
		String sql = getBaseQuery(sqlScript);
		
		System.out.println("Processing Script for regex:\"" + dbPat + "\"");
		for (String pat : dbPat.split("\\|\\|")) {
			HashMap<String, String[]> hm = Scrubber.newGetSchemas(pat);
			for (String[] tns : hm.values()) {
				executeStatement(tns, sql);
			}
		}
	}

	public void executeCommands2(String tns, String user, String pw, String sqlScript) {
		String sql = getBaseQuery(sqlScript);
		
		System.out.println("Processing Script for tns:\"" + tns + "\"");
		executeStatement(new String[] {tns + ".world", user, pw}, sql);
	}

	public void executeQueries(String dbPattern, String sqlScript, String fileName) {
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
		} catch (Exception ignored) {}
		
		String dbPat = getPattern(dbPattern);
		String sql = getBaseQuery(sqlScript);
		
		System.out.println("Processing Script for regex:\"" + dbPat + "\"");
		for (String pat : dbPat.split("\\|\\|")) {
			HashMap<String, String[]> hm = Scrubber.newGetSchemas(pat);
			for (String[] tns : hm.values()) {
				executeQuery(tns, sql);
			}
		}
		
		try {
			bw.flush();
			bw.close();
		} catch (Exception ignored) {}
	}
	
	public void executeCommandsFromFile(String dbPattern, String fileName) throws FileNotFoundException {
		StringBuilder sqlScript = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;

			while (null!=(line = br.readLine()))
				sqlScript.append(line).append("\n");

			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		executeCommands(dbPattern, sqlScript.toString());
	}
	
	public void executeCommandsFromFile2(String tns, String user, String pw, String fileName) throws FileNotFoundException {
		StringBuilder sqlScript = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;

			while (null!=(line = br.readLine()))
				sqlScript.append(line).append("\n");

			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		executeCommands2(tns, user, pw, sqlScript.toString());
	}
	
	public void executeQueriesFromFile(String dbPattern, String fileName, String outFileName) {
		StringBuilder sqlScript = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;

			while (null!=(line = br.readLine()))
				sqlScript.append(line).append("\n");

			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		executeQueries(dbPattern, sqlScript.toString(), outFileName);
	}

	public void parseFile(String dbPattern, String fileName, String outFileName) {
		StringBuilder sqlScript = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;

			while (null!=(line = br.readLine()))
				sqlScript.append(line).append("\n");

			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		executeQueries(dbPattern, sqlScript.toString(), outFileName);
	}

	public static void main(String[] args) {
		DBExecutor executor = new DBExecutor();
		try {
//			executor.executeCommandsFromFile(".+DVCDHB", "c:\\Users\\drothste\\Documents\\SQL_2.txt");
//			executor.executeCommandsFromFile2("PRCDHB", "DM_PS", "Biogen@123", "c:\\Users\\drothste\\export2.sql");
//			executor.executeCommandsFromFile2("PRCDHB", "DM_PS", "Biogen@123", "c:\\Users\\drothste\\export.sql");
//			executor.executeQueriesFromFile(".+TSCDHB|.+STCDHB|.+PRCDHB", "c:/Users/drothste/Documents/SQL_1.txt", "C:/Users/drothste/parallel_issues.txt");/			executor.executeQueriesFromFile("etl_TSCDHB", "c:/Users/drothste/sqltest1 - Copy.sql", "C:/Users/drothste/sqltest1.csv");
//			executor.executeCommands("cim_rpt.+DVCDHB", "BEGIN VPDMAN.UTILS_PKG.initialize_data_schema; END;");
//			executor.executeQueriesFromFile("etl_TSCDHB", "c:/Users/drothste/sqltest2.sql", "C:/Users/drothste/sqltest2.csv");
//			executor.executeQueriesFromFile("etl_TSCDHB", "c:/Users/drothste/sqltest1.sql", "C:/Users/drothste/sqltest3.csv");
//		executor.executeCommands("dh_abc.+PRCDHB", 
//				"grant select, insert, update, delete ON inbound_queue_table to SOA\n/\ngrant select, insert, update, delete ON outbound_queue_table to SOA\n/\n");
//										"declare cursor c1 is SELECT  * FROM user_tab_privs where grantee = 'SVONZIRP'; BEGIN FOR i1 in c1 loop EXECUTE IMMEDIATE 'REVOKE ' || i1.privilege || ' ON \"' || i1.table_name || '\" FROM ' || i1.grantee; END LOOP; END;");
//			"DROP INDEX LND_ACCOUNT_EI_IX\n/\nCREATE INDEX LND_ACCOUNT_EI_IX ON LND_ACCOUNT(id, external_id_vod__c, isdeleted) TABLESPACE LANDING_ENC");
		executor.executeQueries("vpdman.+CDHB", "Grant Role List", "C:/Users/drothste/grant_roles.csv");
//		executor.executeQueries("dh.+||dm.+||stg.+||cdw.+||di.+||sor.+", "Grant List", "C:/Users/drothste/grants.csv");

//			executor.executeQueries("stg.+_DVCDHB", "SELECT * FROM user_jobs", "C:/Users/drothste/user_jobs.csv");
//		executor.executeQueries("dm_ms.+", "SELECT COUNT(*) FROM MV_MS_SALES_TERRITORY_HIER", "C:/Users/drothste/calc_params.csv");
//			executor.executeCommands("dh_ids.+STCDHB|dh_rdm_publish.+STCDHB", "BEGIN VPDMAN.UTILS_PKG.update_view_definitions; END;");
//  		executor.executeQueries("usdw_PUDW", "SELECT * FROM all_tab_columns WHERE owner NOT LIKE '%SYS%' ORDER BY owner, table_name, column_id", "C:/Users/drothste/DW1_Columns.csv");
//			executor.executeQueries(".+CDHB", "SELECT COUNT(*) FROM user_tables", "C:/Users/drothste/db_ping_test.csv");
//			executor.executeQueries("dh_ids.+", "SELECT COUNT(*) FROM IDS_TERRITORY_PRODUCT", "C:/Users/drothste/terr_prod.csv");
//			executor.executeQueries("All", "Grant List Custom", "C:/Users/drothste/User_Grants_2.csv");
//			executor.executeCommands("dh_ids_PRCDHB", "DECLARE CURSOR c1 IS SELECT 'REVOKE ' || privilege || ' ON ' || table_name || ' FROM ' || grantee REVOKE_SQL FROM user_tab_privs WHERE table_name LIKE 'XXX%'; BEGIN FOR i1 IN c1 LOOP BEGIN EXECUTE immediate i1.revoke_sql; EXCEPTION WHEN OTHERS THEN NULL END; END LOOP; END;");
//			executor.executeCommands(".+_DVCDHB||dh_land_medforce_DVCDHB||dm_intl_DVCDHB||dh_land_trackwise_DVCDHB||sor_sfdc_eu_DVCDHB||stg_medical_DVCDHB||sor_mar_DVCDHB||sor_rems_zymbryta_DVCDHB||dh_rpt_feed_DVCDHB||di_intl_DVCDHB||dm_us_field_DVCDHB||dm_orch_DVCDHB", "CREATE OR REPLACE TRIGGER AUDIT_DDL_TRG AFTER DDL ON SCHEMA DECLARE PRAGMA AUTONOMOUS_TRANSACTION; BEGIN VPDMAN.UTILS_PKG.audit_sql; END;");
//			executor.executeCommands(".+TSCDHB", "Grants Corrector"); 
//			executor.executeCommands(".+PRCDHB", "Tablespace Corrector"); 
//			executor.executeQueries("dh_ids_.+CDHB", "SELECT SYS_CONTEXT('USERENV', 'DB_NAME') DATABASE, SYS_CONTEXT('USERENV', 'SESSION_USER') OWNER, c.* From ids_country_code c where c.iso_3_cd = 'USA'", "C:/Users/drothste/countries2.csv");
		} catch (Exception ignored) {}
	}
}
