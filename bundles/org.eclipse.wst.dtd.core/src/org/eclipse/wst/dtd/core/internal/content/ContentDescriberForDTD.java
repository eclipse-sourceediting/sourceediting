/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.content;

import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.wst.common.encoding.IResourceCharsetDetector;
import org.eclipse.wst.xml.core.internal.contenttype.XMLResourceEncodingDetector;



public class ContentDescriberForDTD extends AbstractContentDescriber implements ITextContentDescriber {

	// same rules as for XML
	protected IResourceCharsetDetector getDetector() {
		return new XMLResourceEncodingDetector();
	}

}
