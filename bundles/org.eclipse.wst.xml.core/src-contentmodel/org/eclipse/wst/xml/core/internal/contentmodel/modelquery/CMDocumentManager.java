/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;


/**
 * The CMDocumentManager can be visualized as a table of CMDocument
 * references, each with a corresponding entry in a CMDocument cache. The
 * CMDocumentManager also performs the task of loading CMDocuments providing
 * support for synchronous and asynchronous loading.
 * 
 * publicIdTable CMDocumentCache ---------------------------
 * --------------------------------------- | publicId | resolvedURI | -> |
 * resolvedURI | status | CMDocument | ---------------------------
 * --------------------------------------- | (null) | file:/x.dtd | |
 * file:/x.dtd | loading | (null) | ---------------------------
 * --------------------------------------- | http:/... | file:/y.xsd | |
 * file:/y.xsd | loaded | | ---------------------------
 * ---------------------------------------
 * 
 */
public interface CMDocumentManager {
	/**
	 * This property specifies WHEN CMDocuments are loaded. Setting this
	 * property to true allows the CMDocumentManager to load CMDocuments on
	 * demand. Settings this property a false puts the onus on the client to
	 * call addCMDocumentReference() to explicity trigger a load. This allows
	 * the client to control exactly when loading should take place. )
	 */
	public static final String PROPERTY_AUTO_LOAD = "autoLoad"; //$NON-NLS-1$

	/**
	 * This property specifies HOW CMDocuments are loaded. When set to false,
	 * the getCMDocument() method will load the CMDocument synchronously and
	 * return a CMDocument object when loading is successful. When set to
	 * true, the getCMDocument() will load the CMDocument asynchronously and
	 * will immediately return null. When loading is complete, the
	 * CMDocumentManager will inform its listeners that the CMDocument has
	 * been loaded.
	 */
	public static final String PROPERTY_ASYNC_LOAD = "asyncLoad"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String PROPERTY_USE_CACHED_RESOLVED_URI = "useCachedResovledURI"; //$NON-NLS-1$

	/**
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=136399
	 * Allows a client to control whether the document manager attempts to resolve a
	 * CM document's URI. When set to false, the CM systemID will be assumed to point
	 * to an already resolved URI. By default this property is set to true.
	 */
	public static final String PROPERTY_PERFORM_URI_RESOLUTION = "performURIResolution"; //$NON-NLS-1$
	
	/**
	 * Set the enabled state of a property.
	 */
	public void setPropertyEnabled(String propertyName, boolean enabled);

	/**
	 * Get the enabled state of the property.
	 */
	public boolean getPropertyEnabled(String propertyName);

	/**
	 * Adds a listener. Listeners should expect to receive call backs on a
	 * secondary thread when asynchronously loading is used.
	 */
	public void addListener(CMDocumentManagerListener listener);

	/**
	 * Removes a listener.
	 */
	public void removeListener(CMDocumentManagerListener listener);

	/**
	 * Lookup or create a CMDocument (depending on PROPERTY_AUTO_LOAD).
	 * 
	 * @param publicId
	 * @param systemId
	 * @param type -
	 *            'dtd', 'xsd', 'tld', etc. if null is given as the type, then
	 *            the type is calculated as last segment (e.g. file extension)
	 *            of the resolved URI
	 * @return
	 */
	public CMDocument getCMDocument(String publicId, String systemId, String type);

	/**
	 * Lookup a CMDocument.
	 */
	public CMDocument getCMDocument(String publicId);

	/**
	 * Get the status of a CMDocument.
	 */
	public int getCMDocumentStatus(String publicId);

	/**
	 * Creates a CMDocument and adds the associated CMDocument reference
	 * information to the table. Calling this method always triggers a
	 * CMDocument load.
	 */
	public void addCMDocumentReference(String publicId, String systemId, String type);

	/**
	 * Add an existingCMDocument and the reference information to the table.
	 */
	public void addCMDocument(String publicId, String systemId, String resolvedURI, String type, CMDocument cmDocument);

	/**
	 * Remove all entries from the table.
	 */
	public void removeAllReferences();

	/**
	 * Get the CMDocumentCache that is used to store loaded CMDocuments and
	 * associated status.
	 */
	public CMDocumentCache getCMDocumentCache();

	/**
	 * Builds a CMDocument given a resoulvedURI. Note that no entries are
	 * added to the table.
	 */
	public CMDocument buildCMDocument(String publicId, String resolvedURI, String type);
}
