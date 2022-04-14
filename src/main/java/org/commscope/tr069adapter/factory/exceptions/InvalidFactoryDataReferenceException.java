package org.commscope.tr069adapter.factory.exceptions;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/

/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */
public class InvalidFactoryDataReferenceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidFactoryDataReferenceException(String errorMessage) {
		super(errorMessage);
	}

}