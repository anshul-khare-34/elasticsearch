package org.commscope.tr069adapter.factory.parser;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commscope.tr069adapter.factory.constant.FactoryDataConstant;
import org.commscope.tr069adapter.factory.constant.Utility;
import org.commscope.tr069adapter.factory.exceptions.InvalidFactoryDataReferenceException;
import org.commscope.tr069adapter.factory.model.FactoryData;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */
@Component
public class FactoryDataFileParser {

	private static final Log logger = LogFactory.getLog(FactoryDataFileParser.class);

	private String fieldValue;
//	private String refurbishedState;	
	
	private String fileName;
	
	
	public List<FactoryData> parseFile(MultipartFile factoryDataFile) throws InvalidFactoryDataReferenceException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		
		List<FactoryData> factoryDataList = null;
		
		try {
			fileName = factoryDataFile.getName();
			byte[] byteArray = factoryDataFile.getBytes();
			String fileContent = new String(byteArray);
			ByteArrayInputStream xmlFileContentInputStream = new ByteArrayInputStream(byteArray);
		    
			logger.debug("Validating factory data XML file "+fileName);
			
			validateXmlWithSchema(factory, xmlFileContentInputStream);
			
			logger.debug("Factory XML file validation is successful");
			
//			readRefurbishedState(fileContent);
			
			factoryDataList = parseNext(fileContent);

		} catch (InvalidFactoryDataReferenceException e) {
			logger.error("Error while parsing the XML file "+fileName+". ERROR MSG: " + e.getMessage());
			throw new InvalidFactoryDataReferenceException("Parsing error in file. "+e.getMessage());
		} catch(Exception e) {
			logger.error("Unknown error occurred while parsing factory data XML file"+fileName+". "+e.getMessage());
			throw new InvalidFactoryDataReferenceException("UNKNOWN ERROR : "+e.getMessage());
		}
		
		return factoryDataList;	
	}

	public void validateXmlWithSchema(SAXParserFactory saxFactory, ByteArrayInputStream xmlFileContentInputStream) throws InvalidFactoryDataReferenceException {
		logger.info("Validating the XML file against XSD file: "+ FactoryDataConstant.FACTORY_DATA_XSD_PATH);
		SchemaFactory factory = SchemaFactory.newInstance(FactoryDataConstant.XML_SCHEMA_NS);

		try {

			InputStream xsdInputStream = getClass().getResourceAsStream(FactoryDataConstant.FACTORY_DATA_XSD_PATH);
			if(xsdInputStream == null) {
				throw new InvalidFactoryDataReferenceException("File parsing error: Unable to find XSD "+ FactoryDataConstant.FACTORY_DATA_XSD_PATH);
			}
			
			Schema schema = factory.newSchema(new StreamSource(xsdInputStream));	
			
			Validator validator = schema.newValidator();

			 final StringBuffer exceptions = new StringBuffer();
			  validator.setErrorHandler(new ErrorHandler()
			  {
				  @Override
				    public void warning(SAXParseException exception) {
				        handleMessage("Warning", exception);
				    }

				    @Override
				    public void error(SAXParseException exception) {
				        handleMessage("Error", exception);
				    }

				    @Override
				    public void fatalError(SAXParseException exception) {
				        handleMessage("Fatal", exception);
				    }

				    private void handleMessage(String level, SAXParseException exception)  {
				        int lineNumber = exception.getLineNumber();
				        int columnNumber = exception.getColumnNumber();
				        String message = exception.getMessage();
				        exceptions.append("\n" + lineNumber + ":" + columnNumber + ": " + message);
				    }
			  });

			validator.validate(new StreamSource(xmlFileContentInputStream));
			if(exceptions!=null && exceptions.length()>0){
				throw new SAXException(" Line=" +exceptions);
			}
		} catch (Exception e) {
			logger.error("Error while parsing the XML file "+fileName+". ERROR MSG: " + e.getMessage());
			throw new InvalidFactoryDataReferenceException("Parsing error in file "+fileName+", ERROR MSG: "+e.getMessage());
		}

		logger.debug("File is valid.");
	}
	

//	private void readRefurbishedState(String fileContent) {
//		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
//		XMLEventReader xmlEventReader = null;
//
//		try {
//			byte[] byteArray = fileContent.getBytes("UTF-8");
//		    ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
//			xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);			
//			
//			while (xmlEventReader.hasNext()) {
//				XMLEvent event = (XMLEvent) xmlEventReader.next();
//				if(event.isCharacters()) {
//					fieldValue = event.asCharacters().getData();
//				}
//				if(event.isEndElement()) {
//					refurbishedState = fieldValue;
//					EndElement endElement = event.asEndElement();
// 					if (endElement.getName().getLocalPart().equals(FactoryDataConstant.GEDF_REFURBISHEDSTATE)) {
//						if (refurbishedState.equals(FactoryDataConstant.RO) || refurbishedState.equals(FactoryDataConstant.RW)) {							
//							FactoryDataConstant.header_phoneType = true;
//							return;
//						} else if (refurbishedState.equals(FactoryDataConstant.NW)) {
//							FactoryDataConstant.header_phoneType = false;
//							return;
//						} else {
//							throw new InvalidFactoryDataReferenceException("Invalid value "+refurbishedState+" for field "+FactoryDataConstant.GEDF_REFURBISHEDSTATE+". Allowed values are RW or NW");
//						}
//					} 
//				}
//			}
//		} catch (InvalidFactoryDataReferenceException ex) {
//			throw ex;
//		} catch (Exception e) {
//			logger.error("Unknown error while reading factory xml file "+fileName, e);
//			throw new InvalidFactoryDataReferenceException("Unknown error while reading factory xml file "+fileName+". ERROR MSG: "+e.getMessage());
//		} finally {
//			if (xmlEventReader != null) {
//				try {
//					xmlEventReader.close();
//				} catch (XMLStreamException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
		
	public List<FactoryData> parseNext(String fileContent) throws InvalidFactoryDataReferenceException {
		String licenseKey =null ;
		String softwareVersion=null ;
		String macId=null ;
		String uniqueSharedSecret=null ;
		String serialNumber=null;
		String productClass = null;
		String productName = null;
		
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		List<FactoryData> factoryDataList = new ArrayList<FactoryData>();
		
		long startTime = System.currentTimeMillis();
		XMLEventReader xmlEventReader = null;
		try {
			byte[] byteArray = fileContent.getBytes("UTF-8");
		    ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
			xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);
			
			while (xmlEventReader.hasNext()) {
				XMLEvent event = (XMLEvent) xmlEventReader.next();
				if(event.isCharacters()) {
					fieldValue = event.asCharacters().getData();
				}
				if(event.isEndElement()) {
					
					EndElement element = event.asEndElement();
					
					if (element.getName().getLocalPart().equals(FactoryDataConstant.LICENSE_KEY)) {
						if(!Utility.isAsciiString(fieldValue)) {
 							logger.error("The value for device :"+macId+" " +FactoryDataConstant.LICENSE_KEY + " contains a non ASCII character");
 							throw new InvalidFactoryDataReferenceException("Invalid license key. Device license key value "+fieldValue+" contains a non ASCII character");
 						}
						licenseKey = fieldValue;
					 } else if (element.getName().getLocalPart().equals(FactoryDataConstant.GEDF_PRODUCTNAME)) {
 						if(!Utility.isAsciiString(fieldValue)) {
 							logger.error("The value "+ fieldValue +" for " +FactoryDataConstant.GEDF_PRODUCTNAME+ " contains a non ASCII character");
 							throw new InvalidFactoryDataReferenceException("Invalid product name value. Product name value "+fieldValue+ " contains a non ASCII character");
 						}
 						productName = fieldValue;
					}  else if (element.getName().getLocalPart().equals(FactoryDataConstant.GEDF_PRODUCTCLASS)) {
 						if(!Utility.isAsciiString(fieldValue)) {
 							logger.error("The value "+ fieldValue +" for " +FactoryDataConstant.GEDF_PRODUCTCLASS+ " contains a non ASCII character");
 							throw new InvalidFactoryDataReferenceException("Invalid product class value. Product class value "+fieldValue+ " contains a non ASCII character");
 						}
 						productClass = fieldValue;
					} else if (element.getName().getLocalPart().equals(FactoryDataConstant.GEDF_SW_VERSION)) {
						softwareVersion = fieldValue;
					} else if (element.getName().getLocalPart().equals(FactoryDataConstant.GEDF_MACID)) {
						if(!Utility.isAsciiString(fieldValue)) {
 							logger.error("The value "+ fieldValue +" for " +FactoryDataConstant.GEDF_MACID+ " contains a non ASCII character");
 							throw new InvalidFactoryDataReferenceException("Invalid MacID found. Device macid value "+fieldValue+" contains a non ASCII character");
 						}
						macId = fieldValue;
					} else if (element.getName().getLocalPart().equals(FactoryDataConstant.GEDF_SERIAL_NUMBER)) {
						if(!Utility.isAsciiString(fieldValue)) {
 							logger.error("The value "+ fieldValue +" for " +FactoryDataConstant.GEDF_SERIAL_NUMBER+ " contains a non ASCII character");
 							throw new InvalidFactoryDataReferenceException("Invalid serialnumber found. Device serial number value "+fieldValue+" contains a non ASCII character"); 							
 						}
						serialNumber = fieldValue;
					} else if (element.getName().getLocalPart().equals(FactoryDataConstant.GEDF_KEY)) {
						if(!Utility.isAsciiString(fieldValue)) {
 							logger.error("The value "+ fieldValue +" for " +FactoryDataConstant.GEDF_KEY+ " contains a non ASCII character");
 							throw new InvalidFactoryDataReferenceException("Invalid "+FactoryDataConstant.GEDF_KEY+" found. Device "+FactoryDataConstant.GEDF_KEY+" value "+fieldValue+" contains a non ASCII character"); 							
 						}
						uniqueSharedSecret = fieldValue;
					} else if (element.getName().getLocalPart().equals(FactoryDataConstant.HEADER)) {
						logger.info("TOTAL Time(ms) taken to parse xml file is " + (System.currentTimeMillis() - startTime));
						return factoryDataList;
					} else if (element.getName().getLocalPart().equals("device")) {
						// construct data record
						try {
							FactoryData factoryData = populateFactoryData(macId, serialNumber, uniqueSharedSecret, licenseKey, softwareVersion, productClass, productName);							
							factoryDataList.add(factoryData);
							
							licenseKey =null;
							softwareVersion=null ;
							macId=null ;
							uniqueSharedSecret=null ;
							serialNumber=null;
						} catch (InvalidFactoryDataReferenceException iofe) {
							throw iofe;
						} catch (Exception e) {
							logger.error("unknown exception while creating domain object in parser ", e);
							throw new InvalidFactoryDataReferenceException("Error occurred while importing factory file. ERROR MSG: "+e.getMessage());
						}
					}
				}
				
			}
			
		} catch (InvalidFactoryDataReferenceException imp) {
			throw imp;
		} catch (Exception e) {
			logger.error("Unknown error while reading xml file "+fileName, e);
			throw new InvalidFactoryDataReferenceException(e.getMessage());
		} finally {
			if (xmlEventReader != null) {
				try {
					xmlEventReader.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return factoryDataList;
	}
	
	public FactoryData populateFactoryData(String macId, String serialNumber, String uniquessc, String licenseKey , String softwareVersion, String productClass, String productName) throws InvalidFactoryDataReferenceException {

		String oui = macId.substring(0, 6);	
		String httpUserName = oui + "-" + productClass + "-" + macId;
		
		validateSwVersion(softwareVersion, macId);
		
		FactoryData factoryData = new FactoryData();
		
		factoryData.setSerialNumber(macId);
		factoryData.setFactorySerialNumber(serialNumber);
		factoryData.setHttpUser(httpUserName);
		factoryData.setHttpPassCode(uniquessc);		
		factoryData.setOui(oui);
		factoryData.setProductClass(productClass);	
		factoryData.setSku(productName);
		factoryData.setSoftwareVersion(softwareVersion);
		factoryData.setLicenseKey(licenseKey);
		
		return factoryData;

	}	

	private boolean validateSwVersion(String swVer, String macId){
		if(null==swVer) {
			return true;
		}
		
		if(swVer.trim().length()>=15){
			throw new InvalidFactoryDataReferenceException("Invalid software version. SWVersion field max length is 15 characters but its current length is "+swVer.trim().length());
		}
		String[] versions= swVer.split("\\.");
		if(versions.length==3 || versions.length==4){
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher matcher1 = pattern.matcher(versions[0]);
			Matcher matcher2 = pattern.matcher(versions[1]);
			Matcher matcher3 = pattern.matcher(versions[2]);
			if(matcher1.matches() && matcher2.matches() && matcher3.matches()){
				return true;
			}
		}

		throw new InvalidFactoryDataReferenceException("Invalid SW Version:"+swVer +" for MAC ID:"+macId);
	}
}