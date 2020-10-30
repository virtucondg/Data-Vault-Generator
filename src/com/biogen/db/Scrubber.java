package com.biogen.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Scrubber {

	static private String HEX_VALUES = new String("0123456789ABCDEF");
	
	public static byte[] convertString(String inbound) {
		byte[] retVal = new byte[inbound.length() / 2];
		for (int i = 0; i < inbound.length(); i+=2) {
			retVal[i / 2] = (byte)(HEX_VALUES.indexOf(inbound.substring(i, i + 1)) * 16 + HEX_VALUES.indexOf(inbound.substring(i + 1, i + 2))); 
		}
		return retVal;
	}
	
	public static byte[] decryptPassword(byte[] result) throws GeneralSecurityException {
		if (null==result) return null;
		if (0==result.length) return null;
		
	    byte constant = result[0];
	    if (constant != 5) {
	        throw new IllegalArgumentException();
	    }

	    byte[] secretKey = new byte[8];
	    System.arraycopy(result, 1, secretKey, 0, 8);

	    byte[] encryptedPassword = new byte[result.length - 9];
	    System.arraycopy(result, 9, encryptedPassword, 0, encryptedPassword.length);

	    byte[] iv = new byte[8];
	    for (int i = 0; i < iv.length; i++) {
	        iv[i] = 0;
	    }

	    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "DES"), new IvParameterSpec(iv));
	    return cipher.doFinal(encryptedPassword);
	}
	
	public void dump (String filename) {
		dump(filename, null);
	}
	
	public void dumpTab (String filename) {
		dumpTab(filename, null);
	}
	
	static String[] dbSIDs = new String[] {"DVCDHB","TSCDHB","STCDHB","PRCDHB"};
	
	public static HashMap<String, String[]> newGetSchemas(String connectionName) {
	    String line;

	    HashMap<String, String[]> dbList = new HashMap<String, String[]>();
		Pattern pattern = null;
		
		if (null!=connectionName)
			pattern = Pattern.compile(connectionName);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("C:/Users/drothste/DB_Connections.txt"));
			line = br.readLine();
			
			while ((line = br.readLine()) != null) {
		    	String[] tokens = line.split("\t");
		    	
		    	for (int i = 0; i < dbSIDs.length; i++) {
			    	String conn = tokens[0].toLowerCase() + "_" + dbSIDs[i];

			    	if (null==pattern || pattern.matcher(conn).matches()) {
						String pw = tokens[i + 1];
						String userName = tokens[0];
						String jdbcConn = dbSIDs[i] + ".world";
					
						String[] tns = {jdbcConn, userName, pw};
						if (!tns[2].equals(""))	
							dbList.put(conn, tns);
		    		}
		    	}
		    }
		} catch (Exception ex) {ex.printStackTrace();}
		
		return dbList;
	}
	
	public static HashMap<String, String[]> getSchemas(String connectionName) {
		HashMap<String, String[]> dbList = new HashMap<String, String[]>();
		Pattern pattern = null;
		if (null!=connectionName)
			pattern = Pattern.compile(connectionName);
		
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new File("C:/Users/drothste/documents/connections.xml"));
			XPath path = XPathFactory.newInstance().newXPath();
			XPath path2 = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList) path.compile("//References/Reference").evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n =  nl.item(i);
				if (null==pattern || pattern.matcher(n.getAttributes().getNamedItem("name").getNodeValue()).matches()) {
					String conn = n.getAttributes().getNamedItem("name").getNodeValue().toString();
					String pw = path2.compile("RefAddresses/StringRefAddr[@addrType=\"password\"]/Contents").evaluate(n.getChildNodes());
					String userName = path2.compile("RefAddresses/StringRefAddr[@addrType=\"user\"]/Contents").evaluate(n.getChildNodes());
					String jdbcConn = path2.compile("RefAddresses/StringRefAddr[@addrType=\"customUrl\"]/Contents").evaluate(n.getChildNodes());
					jdbcConn = jdbcConn.substring(jdbcConn.indexOf("@") + 1);
					if (-1 == jdbcConn.indexOf(".world")) jdbcConn += ".world";
					
					String[] tns = new String[3];
					
					byte[] password = decryptPassword(convertString(new String(pw)));
					if (conn.contains("_")) {
						tns[2] = (null==password)?"" : new String(password, "UTF-8");
						tns[1] = userName; //conn.substring(0,  conn.lastIndexOf("_"));
						tns[0] = jdbcConn; //conn.substring(conn.lastIndexOf("_") + 1) + ".world";
						dbList.put(n.getAttributes().getNamedItem("name").getNodeValue().toString(), tns);
					}
				}
			}			
		} catch (Exception ex) {ex.printStackTrace();}
		
		return dbList;
	}
	
	public void dump (String filename, String connectionName) {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new File(filename));
			XPath path = XPathFactory.newInstance().newXPath();
			XPath path2 = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList) path.compile("//References/Reference").evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n =  nl.item(i);
				if (null==connectionName || connectionName.equalsIgnoreCase(n.getAttributes().getNamedItem("name").getNodeValue())) {
					String pw = path2.compile("RefAddresses/StringRefAddr[@addrType=\"password\"]/Contents").evaluate(n.getChildNodes());
					
					byte[] password = decryptPassword(convertString(new String(pw)));
					pw = (null==password)?"" : new String(password, "UTF-8");
					
					System.out.println(n.getAttributes().getNamedItem("name").getNodeValue() + "=>" + pw);
				}
			}			
		} catch (Exception ex) {ex.printStackTrace();}
	}

	public void dumpTab (String filename, String connectionName) {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new File(filename));
			XPath path = XPathFactory.newInstance().newXPath();
			XPath path2 = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList) path.compile("//References/Reference").evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n =  nl.item(i);
				if (null==connectionName || connectionName.equalsIgnoreCase(n.getAttributes().getNamedItem("name").getNodeValue())) {
					String pw = path2.compile("RefAddresses/StringRefAddr[@addrType=\"password\"]/Contents").evaluate(n.getChildNodes());
					
					byte[] password = decryptPassword(convertString(new String(pw)));
					pw = (null==password)?"" : new String(password, "UTF-8");
					
					System.out.println(n.getAttributes().getNamedItem("name").getNodeValue() + "\t" + pw);
				}
			}			
		} catch (Exception ex) {ex.printStackTrace();}
	}

	public static void main(String[] args)  {
		try {
			Scrubber sc = new Scrubber();
			sc.dumpTab("C:/Users/drothste/documents/connections.xml"); //C:\Users\drothste\AppData\Roaming\SQL Developer\connections3.xml
			HashMap<String, String[]> hm = Scrubber.newGetSchemas("di_intl_.+CDHB");
			for (Map.Entry<String, String[]> me : hm.entrySet()) 
				System.out.println(me.getKey() + "=[" + "]");
		} catch (Exception ignored) {ignored.printStackTrace();}
	}
}
