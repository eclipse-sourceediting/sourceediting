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
package org.eclipse.wst.sse.contentproperties;



import java.util.Map;

import org.eclipse.core.resources.IResource;

public interface IContentSettings {

	/**
	 * 
	 */
	public final String DOCUMENT_TYPE = "document-type"; //$NON-NLS-1$
	/**
	 * 
	 */
	public final String HTML_DOCUMENT_TYPE = "html-document-type"; //$NON-NLS-1$
	/**
	 * 
	 */
	public final String CSS_PROFILE = "css-profile"; //$NON-NLS-1$
	/**
	 *  
	 */
	public final String DEVICE_PROFILE = "target-device"; //$NON-NLS-1$
	/**
	 * 
	 */
	public final String JSP_PAGE_ENCODING = "jsp-page-encoding"; //$NON-NLS-1$
	/**
	 * 
	 */
	public final String JSP_LANGUAGE = "jsp-language"; //$NON-NLS-1$
	/**
	 *  
	 */
	public final String JSP_CONTENT_TYPE = "jsp-content-type"; //$NON-NLS-1$

	/**
	 * 
	 */
	public String getProperty(final IResource resource, final String propertyName);

	/**
	 * 
	 */
	public void setProperty(final IResource resource, final String propertyName, final String propertyValue);

	/**
	 * 
	 */
	public void deleteProperty(final IResource resource, final String propertyName);

	/**
	 * release cache of DOM tree in .contentsettings
	 */
	public void releaseCache();

	/**
	 * 
	 */
	public void deleteAllProperties(final IResource deletedFile);

	/**
	 * 
	 */
	public void setProperties(final IResource resource, final Map properties);

	/**
	 * 
	 */
	public Map getProperties(final IResource resource);

	/**
	 * 
	 */
	public boolean existsProperties(IResource resource);
}
