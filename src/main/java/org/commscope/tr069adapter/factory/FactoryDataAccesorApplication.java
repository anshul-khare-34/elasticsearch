package org.commscope.tr069adapter.factory;

/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */

@SpringBootApplication
public class FactoryDataAccesorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FactoryDataAccesorApplication.class, args);
	}

	// Serialize message content to json using TextMessage
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
}
