/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.provisional.contenttype;

/**
 * This class, with its one field, is a convience to provide compile-time safety
 * when refering to a contentType ID. The value of the contenttype id field must
 * match what is specified in plugin.xml file.
 */
public class ContentTypeIdForEmbededJs {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String[] ContentTypeIds = ContentTypeIdForEmbededJs. getJsConstantString();

	static String[] getJsConstantString() {
		return new String[] {"org.eclipse.wst.html.core.htmlsource","org.eclipse.jst.jsp.core.jspsource"}; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private ContentTypeIdForEmbededJs() {
		super();
	}
}
