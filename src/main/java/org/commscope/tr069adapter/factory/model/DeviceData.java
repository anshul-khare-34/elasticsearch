package org.commscope.tr069adapter.factory.model;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/
import java.io.Serializable;
/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */
public class DeviceData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1408846578781735000L;
	
	String serialNumber;
	String autenticationString;
	String oui;
	String productClass;
	
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getAutenticationString() {
		return autenticationString;
	}

	public void setAutenticationString(String autenticationString) {
		this.autenticationString = autenticationString;
	}

	public String getOui() {
		return oui;
	}

	public void setOui(String oui) {
		this.oui = oui;
	}

	public String getProductClass() {
		return productClass;
	}

	public void setProductClass(String productClass) {
		this.productClass = productClass;
	}

}
