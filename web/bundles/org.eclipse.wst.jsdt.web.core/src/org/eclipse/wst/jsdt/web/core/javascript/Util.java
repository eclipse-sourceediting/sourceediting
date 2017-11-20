/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *     
 *******************************************************************************/


package org.eclipse.wst.jsdt.web.core.javascript;

import java.util.Arrays;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
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
