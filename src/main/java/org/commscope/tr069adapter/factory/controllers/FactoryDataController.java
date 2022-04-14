package org.commscope.tr069adapter.factory.controllers;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


import org.commscope.tr069adapter.factory.constant.FactoryDataConstant;
import org.commscope.tr069adapter.factory.exceptions.InvalidFactoryDataReferenceException;
import org.commscope.tr069adapter.factory.model.FactoryData;
import org.commscope.tr069adapter.factory.model.DeviceData;
import org.commscope.tr069adapter.factory.service.FactoryDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */

@RestController
public class FactoryDataController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FactoryDataService factoryDataService;
	
	@GetMapping("/viewAll")
	public Iterable<FactoryData> viewAllFactoryDatas() {
		return factoryDataService.getFactoryDataHistory();
	}
	
	@GetMapping("/view/{id}")
	public FactoryData viewFactoryDataById(@PathVariable ("id") String serialNumber) {
		Optional<FactoryData> factoryData = factoryDataService.getFactoryData(serialNumber);
		if(factoryData.isPresent()) {
			return factoryData.get();
		}
		
		throw new InvalidFactoryDataReferenceException("Invalid factoryData reference provided");
	}
	
    @PostMapping("/importFactory")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws InvalidFactoryDataReferenceException{

    	if(null == files || files.length==0) {
    		logger.info("No file given for import");
    		return "No file given for import";
    	}

    	List<MultipartFile> fileList = Arrays.asList(files);

    	MultipartFile file = fileList.get(0);
    	String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    	logger.debug("Importing file "+fileName);
    	if(!fileName.endsWith(FactoryDataConstant.XML_EXTENSION)) {
    		throw new InvalidFactoryDataReferenceException("Unsupported file format for file "+fileName+". Only XML file is supported. Ignoring file import for file "+fileName);
    	}

    	try {
    		factoryDataService.saveConfigFileContents(file);
    	}catch(InvalidFactoryDataReferenceException ex) {
    		throw new InvalidFactoryDataReferenceException("Error occurred while import file "+fileName+". Cause : "+ex.getMessage());
    	}catch(Exception ex) {
    		throw new InvalidFactoryDataReferenceException("UNKNOWN error occurred while import file "+fileName+". Cause : "+ex.getMessage());
    	}    		

    	return "File "+fileName+" imported successfully";
    }    
 
	@PostMapping("/basicAuthenticate")
	public Boolean authenticateDevice(@RequestBody DeviceData deviceInfo) {
		Boolean isValid = false;
		String serialNumber = deviceInfo.getSerialNumber();
		if(serialNumber != null) {
			FactoryData factoryData = null;
			Optional<FactoryData> optionalFactoryData = factoryDataService.getFactoryData(serialNumber);
			if(optionalFactoryData.isPresent()) {
				factoryData = optionalFactoryData.get();
			} else {
				return isValid;
			}
			
			String userName = factoryData.getHttpUser();
			String password = factoryData.getHttpPassCode();
			
			String base64Credentials = deviceInfo.getAutenticationString();
			if(base64Credentials != null) {
			    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			    // credentials = username:password
			    final String[] values = credentials.split(":", 2);
			    if(values.length == 2) {
			    	if((userName != null && userName.equals(values[0])) && (password != null && password.equals(values[1]))) {
			    		isValid = true;
			    	}
			    } 
			} else {
				isValid = true;
			}
		}
		return isValid;
	}
	
	@PostMapping("/digestAuthenticate")
	public Boolean digestAuthenticateDevice(@RequestBody DeviceData deviceInfo) {
		Boolean isValid = false;
		String serialNumber = deviceInfo.getSerialNumber();
		if(serialNumber != null) {
			FactoryData factoryData = null;
			Optional<FactoryData> optionalFactoryData = factoryDataService.getFactoryData(serialNumber);
			if(optionalFactoryData.isPresent()) {
				factoryData = optionalFactoryData.get();
			} else {
				return isValid;
			}
			
			String userName = factoryData.getHttpUser();
			String password = factoryData.getHttpPassCode();
			
			String headerString = deviceInfo.getAutenticationString();
			if(headerString != null) {
				// parse the values of the Authentication header into a hashmap
                HashMap<String, String> headerValues = parseHeader(headerString);
				
                String method = "POST";
                String realm = headerValues.get("realm");
                String nonce = headerValues.get("nonce");
                String clientResponse = headerValues.get("response");
                String ha1 = DigestUtils.md5DigestAsHex(new String(userName + ":" + realm + ":" + password).getBytes());
                String ha2 = DigestUtils.md5DigestAsHex(new String(method + ":" + headerValues.get("uri")).getBytes());
                
                String serverResponse = DigestUtils.md5DigestAsHex(new String(ha1 + ":" + nonce + ":" + ha2).getBytes());
                if (serverResponse.equals(clientResponse)) {
                	isValid = true;
                }
			} else {
				isValid = true;
			}
		}
		return isValid;
	}
	
	@PostMapping("/validateDevice")
	public Boolean validateDeviceOUIPC(@RequestBody DeviceData deviceData) {
		Boolean isValid = false;
		String serialNumber = deviceData.getSerialNumber();
		if(serialNumber != null) {
			FactoryData factoryData = null;
			Optional<FactoryData> optionalFactoryData = factoryDataService.getFactoryData(serialNumber);
			if(optionalFactoryData.isPresent()) {
				factoryData = optionalFactoryData.get();
			} else {
				return isValid;
			}
			
			String oui = factoryData.getOui();
			String pc = factoryData.getProductClass();
			
	        if(!((oui != null && oui.equals(deviceData.getOui())) && 
	                (pc != null && pc.equals(deviceData.getProductClass())))) {
	            logger.error("OUI or Product class did not match. Factory OUI:" + oui + ", OUI:" + deviceData.getOui() +
	                    ". Factory PC:" + pc + ", PC:" + deviceData.getProductClass());
	        } else {
	        	isValid = true;
	        }
		}
		return isValid;
	}
	
	/**
     * Gets the Authorization header string minus the "AuthType" and returns a
     * hashMap of keys and values
     *
     * @param headerString
     * @return
     */
    private HashMap<String, String> parseHeader(String headerString) {
        // seperte out the part of the string which tells you which Auth scheme is it
        HashMap<String, String> values = new HashMap<String, String>();
        String keyValueArray[] = headerString.split(",");
        for (String keyval : keyValueArray) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                values.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }
        return values;
    }
}
