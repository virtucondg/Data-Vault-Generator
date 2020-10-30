package com.biogen.generator.ertools.erstudio.raw;

import java.util.HashMap;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * This class acts like an ActiveX component with an mepty object.  
 * @author drothste
 *
 */
public class NullActiveXComponent extends ActiveXComponent {
	private HashMap<String, Variant> properties = new HashMap<String, Variant>();	
	private HashMap<String, String> header = new HashMap<String, String>();

	/**
	 * Constructor
	 */
	public NullActiveXComponent() {
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
		return null;
	}

	@Override
	public ActiveXComponent getPropertyAsComponent(String propertyName) {
		return null;
	}

	@Override
	public boolean getPropertyAsBoolean(String propertyName) {
		return false;
	}

	@Override
	public byte getPropertyAsByte(String propertyName) {
		return -1;
	}

	@Override
	public String getPropertyAsString(String propertyName) {
		return null;

	}

	@Override
	public int getPropertyAsInt(String propertyName) {
		return -1;
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
