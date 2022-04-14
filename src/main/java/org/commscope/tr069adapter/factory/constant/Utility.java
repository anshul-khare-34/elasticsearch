package org.commscope.tr069adapter.factory.constant;

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

public class Utility {
	
	public static boolean isAsciiString(String text) {
		if (text == null) {
			return true;
		}

		int size = text.length();
		for (int i = 0; i < size; i++) {
			if (!isAsciiPrintable(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isAsciiPrintable(char ch) {
		return ch >= 32 && ch < 127;
	}	
}
