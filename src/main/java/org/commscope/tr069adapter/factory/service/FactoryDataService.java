package org.commscope.tr069adapter.factory.service;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/
import java.util.List;
import java.util.Optional;

import org.commscope.tr069adapter.factory.exceptions.InvalidFactoryDataReferenceException;
import org.commscope.tr069adapter.factory.model.FactoryData;
import org.commscope.tr069adapter.factory.parser.FactoryDataFileParser;
import org.commscope.tr069adapter.factory.repository.FactoryDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */
@Service
public class FactoryDataService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FactoryDataRepository factoryDataRepository;
	
	@Autowired
	FactoryDataFileParser factoryDataFileParser;
	
	public Iterable<FactoryData> getFactoryDataHistory() {
		return factoryDataRepository.findAll();
	}
	
	public Optional<FactoryData> getFactoryData(String serialNumber) {
		return factoryDataRepository.findById(serialNumber);
	}
	
	public void saveConfigFileContents(MultipartFile file) throws InvalidFactoryDataReferenceException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		if(fileName.contains("..")) {
			throw new InvalidFactoryDataReferenceException("Filename contains invalid path sequence");
		} 

		logger.debug("Parsing factory data file "+fileName);
		List<FactoryData> factoryDataList = factoryDataFileParser.parseFile(file);
		logger.debug("Parsing of file "+fileName+" is completed");

		logger.debug("Saving devices to database");
		factoryDataRepository.saveAll(factoryDataList);	


		logger.debug("All devices from factory data file "+fileName+" have been saved successfully");    		
	}
}