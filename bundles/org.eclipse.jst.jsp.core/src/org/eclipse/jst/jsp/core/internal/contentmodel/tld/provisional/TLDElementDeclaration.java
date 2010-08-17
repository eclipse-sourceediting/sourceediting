/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;

import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

public interface TLDElementDeclaration extends CMElementDeclaration {
	/** 
	 * Property stating whether the {@link CMElementDeclaration}
	 * is a library tag or not
	 */
	String IS_LIBRARY_TAG = "isLibraryTag"; //$NON-NLS-1$
	
	String TAG_SOURCE = "Tag Definition Source"; //$NON-NLS-1$
	String SOURCE_JSP_TLD = "JSP Tag Library Descriptor"; //$NON-NLS-1$
	String SOURCE_TAG_FILE = "JSP Tag 2.0 File"; //$NON-NLS-1$

	/**
	 * The body content type
	 * 
	 * @see JSP 1.1
	 */
	String getBodycontent();

	/**
	 * Optional tag-specific information
	 * 
	 * @see JSP 1.2
	 */
	String getDescription();

	/**
	 * A short name that is intended to be displayed by tools
	 * 
	 * @see JSP 1.2
	 */
	String getDisplayName();


	/**
	 * Indicates that the declared tag handler supports dynamic attributes. It
	 * is not a guarantee that the class itself implements
	 * javax.servlet.jsp.tagext.DynamicAttributes.
	 * 
	 * @see JSP 2.0
	 */
	String getDynamicAttributes();
	
	/**
	 * Optional informal description of an example of a use of this tag
	 * 
	 * @see JSP 2.0
	 */
	String getExample();

	/**
	 * Zero or more extensions that provide extra information about this tag,
	 * for tool consumption
	 * 
	 * @see JSP 2.0
	 */
	List getExtensions();

	/**
	 * Optional tag-specific information
	 * 
	 * @see JSP 1.1
	 */
	String getInfo();

	/**
	 * Name of an optional large icon that can be used by tools
	 * 
	 * @see JSP 1.2
	 */
	String getLargeIcon();

	CMDocument getOwnerDocument();

	/**
	 * Where to find the .tag file implementing this action, relative to the
	 * root of the web application or the root of the JAR file for a tag
	 * library packaged in a JAR. This must begin with /WEB-INF/tags if the
	 * .tag file resides in the WAR, or /META-INF/tags if the .tag file
	 * resides in a JAR.
	 * 
	 * 
	 * @return the path to the .tag(x) file as defined in the .tld file, null
	 *         if internal to the .tld
	 * @see JSP 2.0
	 */
	String getPath();

	/**
	 * Name of an optional small icon that can be used by tools
	 * 
	 * @see JSP 1.2
	 */
	String getSmallIcon();

	/**
	 * The name of the tag handler class implementing
	 * javax.servlet.jsp.tagext.Tag
	 * 
	 * @see JSP 1.1
	 */
	String getTagclass();

	/**
	 * The name of an optional subclass of
	 * javax.servlet.jsp.tagext.TagExtraInfo
	 * 
	 * @see JSP 1.1
	 */
	String getTeiclass();

	/**
	 * @see JSP 1.2
	 * @return List of TLDVariables
	 */
	List getVariables();
}
