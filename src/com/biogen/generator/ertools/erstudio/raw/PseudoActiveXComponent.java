package com.biogen.generator.ertools.erstudio.raw;

import java.util.HashMap;
import java.util.Map;

import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * This class acts like an ActiveX component but uses a preloaded properties list.  This enables perforance improvements as the 
 * raw .DM1 file can be read and processed instead of having to go through the ER/Studio tool directly.
 * @author drothste
 *
 */
public class PseudoActiveXComponent extends ActiveXComponent {
	private HashMap<String, Variant> properties = new HashMap<String, Variant>();	
//	private HashMap<String, String> header = new HashMap<String, String>();

	/**
	 * Constructor
	 * @param obj the subclass of ModelingObject wrapping this object
	 * @param data the raw property data that will be access by the wrapping subclass of ModelingObject  
	 */
	public PseudoActiveXComponent(ModelingObject obj, HashMap<String, String> data) {
		this();
		
//		this.header = obj.getHeaderColumns();
		
		for (Map.Entry<String, String> col : data.entrySet()) {
			String value = data.get(col.getKey());
			Variant variant = null;
			
//			switch(col.getValue()) {
//			case "BYTE" : variant = new Variant(Byte.valueOf(value));
//				break;
//			case "INT" : variant = new Variant(Integer.valueOf(value));
//				break;
//			case "BOOLEAN" : variant = new Variant(Boolean.valueOf(value));
//				break;
//			default:
				variant = new Variant(value);
//			}
			
			properties.put(col.getKey().toUpperCase(), variant);
		}
		
	}
	
	/**
	 * Constructor
	 */
	private PseudoActiveXComponent() {
		super(new Dispatch());
	}

	@Override
	public Dispatch getObject() {
		return this;
	}

//	public static ActiveXComponent createNewInstance(String pRequestedProgramId) {
//		return null;
//	}

//	public static ActiveXComponent connectToActiveInstance(String pRequestedProgramId) {
//		return null;
//	}

	@Override
	protected void finalize() {}

	@Override
	public Variant getProperty(String propertyName) {
		return properties.get(propertyName.toUpperCase());
	}

	@Override
	public ActiveXComponent getPropertyAsComponent(String propertyName) {
		return null;
	}

	@Override
	public boolean getPropertyAsBoolean(String propertyName) {
		return getProperty(propertyName).getBoolean();
	}

	@Override
	public byte getPropertyAsByte(String propertyName) {
		return getProperty(propertyName).getByte();
	}

	@Override
	public String getPropertyAsString(String propertyName) {
		return getProperty(propertyName).getString();

	}

	@Override
	public int getPropertyAsInt(String propertyName) {
		return getProperty(propertyName).getInt();
	}

	@Override
	public void setProperty(String propertyName, Variant arg) {
	}

	@Override
	public void setProperty(String propertyName, Dispatch arg) {
	}

	@Override
	public void setProperty(String propertyName, String propertyValue) {
	}

	@Override
	public void setProperty(String propertyName, boolean propValue) {
	}

	@Override
	public void setProperty(String propertyName, byte propValue) {
	}

	@Override
	public void setProperty(String propertyName, int propValue) {
	}

	@Override
	public void logCallbackEvent(String description, Variant[] args) {
	}

	@Override
	public ActiveXComponent invokeGetComponent(String callAction) {
		return null;
	}

	@Override
	public ActiveXComponent invokeGetComponent(String callAction, Variant... parameters) {
		return null;
	}

	@Override
	public Variant invoke(String actionCommand, String parameter) {
		return null;
	}

	@Override
	public Variant invoke(String actionCommand, boolean parameter) {
		return null;
	}

	@Override
	public Variant invoke(String actionCommand, int parameter) {
		return null;
	}

	@Override
	public Variant invoke(String actionCommand, String parameter1, int parameter2) {
		return null;
	}

	@Override
	public Variant invoke(String actionCommand, int parameter1, int parameter2) {
		return null;
	}

	@Override
	public Variant invoke(String callAction) {
		return null;
	}

	@Override
	public Variant invoke(String name, Variant... args) {
		return null;
	}

}
