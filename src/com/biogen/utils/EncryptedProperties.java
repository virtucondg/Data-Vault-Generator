package com.biogen.utils;

import java.util.Properties;

/**
 * This class extends Properties to enable encrypted properties.  
 * 
 * If a property file is not encrypted, prefixing "ENCRYPT:" to the property value (prop=ENCRYPT:value) will signal that this property will
 * become encrypted once passed through PropertiesEncryptor.
 * 
 * If a property file is encrypted, a value bracketed by ENC() (property=ENC(value)" will automatically decrypt the value before 
 * returning from a getProperty() call.
 * 
 * This subclass only overrides the constructors and the getProperty() method.
 * 
 * @author drothste
 *
 */
public class EncryptedProperties extends Properties {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public EncryptedProperties() {
		super();
	}

	/**
	 * Constructor
	 * @param defaults default values for the internal Properties object
	 */
	public EncryptedProperties(Properties defaults) {
		super(defaults);
	}

	private final String ENCRYPTABLE = "ENCRYPT:";
	private final String ENCRYPTED = "ENC(";
	
	@Override
	public String getProperty(String arg0) {
		String value = super.getProperty(arg0);
		
		if (null!=value)
			if (value.startsWith(ENCRYPTED)) {
				value = Crypto.decrypt(value.substring(ENCRYPTED.length(), value.length() - 1));
			} else if (value.startsWith(ENCRYPTABLE)) {
				value = value.substring(ENCRYPTABLE.length());
			}
		return value;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		String value = getProperty(name);
		
		return (null==value) ? defaultValue : value;
	}
	
	public boolean isPropertyTrue(String arg0) {
		String value = getProperty(arg0, "").toUpperCase();
		return ("TRUE".equals(value)) || ("Y".equals(value.substring(1))); 
	}
	
	/**
	 * Main method; this tests decrypting an encrypted value.
	 * 
	 * @param args arguments (ignored)
	 */
	public static void main(String[] args) {
		EncryptedProperties ep = new EncryptedProperties();
		ep.put("password", "ENC(e3fTWdryjKqE1E7Qv9Y=)");
		System.out.println(ep.getProperty("password"));
	}
}
