/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.javascript;

import java.util.Arrays;

/**
 * @author childsb
 *
 */
public class Util {
	public static char[] getPad(int numberOfChars) {
		if(numberOfChars < 0) return new char[0];
		final char[] spaceArray = new char[numberOfChars];
		Arrays.fill(spaceArray, ' ');
		return spaceArray;
	}
	
	public static String removeAll(String source, char remove) {
		
		String newString = "";
		
		char[] oldStringArray = source.toCharArray();
		
		for(int i = 0;i<oldStringArray.length;i++) {
			if(oldStringArray[i]!=remove) newString+=oldStringArray[i];
		}
		return newString;
	}
}
