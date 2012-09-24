/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.internal.contentproperties;



import java.util.Map;

import org.eclipse.core.resources.IResource;

/**
 * @deprecated See
 *             org.eclipse.html.core.internal.contentproperties.HTMLContentProperties
 */
public interface IContentSettings {
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
	public final String DOCUMENT_TYPE = "document-type"; //$NON-NLS-1$
	/**
	 * 
	 */
	public final String HTML_DOCUMENT_TYPE = "html-document-type"; //$NON-NLS-1$

	/**
	 * 
	 */
	public void deleteAllProperties(final IResource deletedFile);

	/**
	 * 
	 */
	public void deleteProperty(final IResource resource, final String propertyName);

	/**
	 * 
	 */
	public boolean existsProperties(IResource resource);

	/**
	 * 
	 */
	public Map getProperties(final IResource resource);

	/**
	 * 
	 */
	public String getProperty(final IResource resource, final String propertyName);

	/**
	 * release cache of DOM tree in .contentsettings
	 */
	public void releaseCache();

	/**
	 * 
	 */
	public void setProperties(final IResource resource, final Map properties);

	/**
	 * 
	 */
	public void setProperty(final IResource resource, final String propertyName, final String propertyValue);
}
