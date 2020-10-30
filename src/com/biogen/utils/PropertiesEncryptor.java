package com.biogen.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.ArrayList;

/**
 * This class allows one to easily create encrypted passwords in a properties file
 * @author drothste
 *
 */
public class PropertiesEncryptor {

	static private String ENCRYPT_TOKEN = "ENCRYPT:";
	/**
	 * Constructor - not used
	 */
	public PropertiesEncryptor() {
	}
	
	static public void encryptFile(String fileName) {
		ArrayList<String> properties = new ArrayList<String>();
		String line;
		
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
			while (null!=(line = lnr.readLine())) {
				String[] prop = line.split("=", 2);
				if (2 > prop.length)
					properties.add(line);
				else {
					if (prop[1].startsWith(ENCRYPT_TOKEN)) {
						prop[1] = "ENC(" + Crypto.encrypt(prop[1].substring(ENCRYPT_TOKEN.length())) + ")";
						properties.add(prop[0] + "=" + prop[1]);
					} else {
						properties.add(line);
					}
				}
			}
			
			lnr.close();
			
			int propPos = fileName.lastIndexOf(".properties");
			String outFileName = fileName.substring(0, propPos) + "_enc" + fileName.substring(propPos);
			FileWriter fw = new FileWriter(outFileName);
			for (String row: properties) {
				fw.write(row);
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (Exception ignored) {ignored.printStackTrace();}
	}
	/**
	 * Main function
	 * @param args the file name to encrypt.  Anything past the first argument is ignored.
	 */
	public static void main(String[] args) {
		if (args.length==0) {
			Debug.printError("PropertiesEncryptor: missing configuration properties file: usage:  PropertiesEncryptor <folder>/<config file>.properties");
			System.exit(0);
		}

		PropertiesEncryptor.encryptFile(args[0]);
	}

}
