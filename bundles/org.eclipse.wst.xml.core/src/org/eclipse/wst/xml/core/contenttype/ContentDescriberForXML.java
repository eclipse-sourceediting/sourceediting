/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.contenttype;

import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.wst.encoding.AbstractContentDescriber;
import org.eclipse.wst.encoding.IResourceCharsetDetector;


public class ContentDescriberForXML extends AbstractContentDescriber implements ITextContentDescriber {
	protected IResourceCharsetDetector getDetector() {
		return new XMLResourceEncodingDetector();
	} 

}
