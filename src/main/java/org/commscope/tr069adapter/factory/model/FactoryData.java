package org.commscope.tr069adapter.factory.model;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */

@Entity
@Table(name = "FACTORY_DATA")
public class FactoryData {
	
	@Id
	@Column(name = "SERIAL_NUMBER", length = 255)
	private String serialNumber;// DeviceSerialNumber
	
	@Column(name = "FACTORY_SERIAL_NUMBER", length = 32)
	private String factorySerialNumber;// DeviceSerialNumber
	
	@Column(name = "HTTP_USER", length = 255)
	private String httpUser;// HTTPusername
	
	@Column(name = "HTTP_PASS_CODE", length = 255)
	private String httpPassCode;// HTTPpassword		
	
//	@Column(name = "AKA_KEY", length = 255)
//	private String akaKey;// UniqueSharedSecret
//	
//	@Column(name = "DEVICE_TYPE_ID", length = 19)
//	private Long deviceTypeId; // DeviceTypeID
//	
//	@Column(name = "RECORD_STATE", length = 2)
//	private Integer state;
//	
//	@Column(name = "ENTERPRISE_CLASS_FLAG", length = 2)
//	private Integer enterpriseClassFlag;
		
	@Column(name = "SKU", length = 255)
	private String sku;
	
	@Column(name = "ADDITIONAL_INFO_JSON",length = 255)
	private String additionalInfoJson;
	
	@Column(name = "OUI", length = 10)
	private String oui;
	
	@Column(name = "PRODUCT_CLASS", length = 50)
	private String productClass;
	
	public String getSoftwareVersion() {
		String softwareVersion=null;
		if(null!=this.additionalInfoJson && this.additionalInfoJson.contains("\"swVersion\"")) {
			String[] jsonList = this.additionalInfoJson.split("},\\{");		
			for(String json : jsonList) {
				if(json.contains("\"swVersion\"")) {
					json=json+"}";
					softwareVersion=json.substring(json.indexOf("vl\":\"")+5, json.indexOf("\"}"));
					break;
				}
			}
		}
		
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		
		if(null==softwareVersion) {
			return;
		}
		softwareVersion = "{\"ky\":\"swVersion\",\"vl\":\""+softwareVersion+"\"}";
		
		if(null==this.additionalInfoJson){
			this.additionalInfoJson="["+softwareVersion+"]";
		}else {
			this.additionalInfoJson=this.additionalInfoJson.substring(0,this.additionalInfoJson.lastIndexOf("]"))+","+softwareVersion+"]";
		}
	}

	public String getLicenseKey() {
		String licenseKey=null;
		if(null!=this.additionalInfoJson && this.additionalInfoJson.contains("\"licensekey\"")) {
			String[] jsonList = this.additionalInfoJson.split("},\\{");		
			for(String json : jsonList) {
				if(json.contains("\"licensekey\"")) {
					json=json+"}";
					licenseKey=json.substring(json.indexOf("vl\":\"")+5, json.indexOf("\"}"));
					break;
				}
			}
		}
		
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		
		if(null==licenseKey) {		
			return;
		}
		
		licenseKey = "{\"ky\":\"licensekey\",\"vl\":\""+licenseKey+"\"}";
		
		if(null==this.additionalInfoJson){
			this.additionalInfoJson="["+licenseKey+"]";
		}else {
			this.additionalInfoJson=this.additionalInfoJson.substring(0,this.additionalInfoJson.lastIndexOf("]"))+","+licenseKey+"]";
		}
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getFactorySerialNumber() {
		return factorySerialNumber;
	}

	public void setFactorySerialNumber(String factorySerialNumber) {
		this.factorySerialNumber = factorySerialNumber;
	}

	public String getHttpUser() {
		return httpUser;
	}

	public void setHttpUser(String httpUser) {
		this.httpUser = httpUser;
	}

	public String getHttpPassCode() {
		return httpPassCode;
	}

	public void setHttpPassCode(String httpPassCode) {
		this.httpPassCode = httpPassCode;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getAdditionalInfoJson() {
		return additionalInfoJson;
	}

	public void setAdditionalInfoJson(String additionalInfoJson) {		
		this.additionalInfoJson=additionalInfoJson;
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