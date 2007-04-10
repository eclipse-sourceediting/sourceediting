/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.modelhandler;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;


/**
 *  
 */
public class EmbeddedTypeRegistryImpl implements EmbeddedTypeRegistry {

	private static EmbeddedTypeRegistry instance = null;

	public synchronized static EmbeddedTypeRegistry getInstance() {
		if (instance == null) {
			instance = new EmbeddedTypeRegistryImpl();
		}
		return instance;
	}

	private HashSet hashSet = null;
	private EmbeddedTypeHandler registryDefaultHandler = null;

	/*
	 * @see ContentTypeRegistry#getTypeFor(String)
	 */
	/**
	 * Constructor for ContentTypeRegistryImpl.
	 */
	private EmbeddedTypeRegistryImpl() {
		super();
		hashSet = new HashSet();
		new EmbeddedTypeRegistryReader().readRegistry(hashSet);
	}

	/**
	 * @see ContentTypeRegistry#add(ContentTypeDescription)
	 */
	void add(IDocumentTypeHandler contentTypeDescription) {
		hashSet.add(contentTypeDescription);
	}

	private EmbeddedTypeHandler getJSPDefaultEmbeddedType() {
		return getTypeFor("text/html"); //$NON-NLS-1$
	}

	/**
	 * Method getRegistryDefault. We cache the default handler, since can't
	 * change once plugin descriptors are loaded.
	 * 
	 * @return EmbeddedTypeHandler
	 */
	private EmbeddedTypeHandler getRegistryDefault() {
		if (registryDefaultHandler == null) {
			Iterator it = hashSet.iterator();
			while ((registryDefaultHandler == null) && (it.hasNext())) { // safe
				// cast
				// since
				// 'add'
				// requires
				// EmbeddedContentTypeDescription
				EmbeddedTypeHandler item = (EmbeddedTypeHandler) it.next();
				if ((item != null) && (item.isDefault())) {
					registryDefaultHandler = item;
					break;
				}
			}
		}
		return registryDefaultHandler;
	}

	/**
	 * Finds the contentTypeDescription based on literal id. Its basically a
	 * "first found first returned". Note the order is fairly unpredictable,
	 * so non-unique ids would cause problems.
	 */
	public EmbeddedTypeHandler getTypeFor(String mimeType) {
		// Note: the reason we have this precondition is that the
		// default is different inside the registry than when called,
		// for example, from the JSPLoader. For the JSPLoader, if there
		// is no mimetype, the default should be HTML. Here, if there is
		// some mimetype, but it is not recognized, we return a default
		// for XML. This is required for various voice xml types, etc.
		EmbeddedTypeHandler found = null;
		if (mimeType == null || mimeType.trim().length() == 0) {
			found = getJSPDefaultEmbeddedType();
		} else {
			Iterator it = hashSet.iterator();
			while ((found == null) && (it.hasNext())) { // safe cast since
				// 'add' requires
				// EmbeddedContentTypeDescription
				EmbeddedTypeHandler item = (EmbeddedTypeHandler) it.next();
				if ((item != null) && (item.getSupportedMimeTypes().contains(mimeType))) {
					found = item;
					break;
				}
			}
			// if no exact match, do the "looser" check
			if(found == null) {
				it = hashSet.iterator();
				while ((found == null) && (it.hasNext())) { 
					EmbeddedTypeHandler item = (EmbeddedTypeHandler) it.next();
					if ((item != null) && (item.canHandleMimeType(mimeType))) {
						found = item;
						break;
					}
				}
			}
		}
		// no matches, use default
		if (found == null) {
			found = getRegistryDefault();
		}
		return found;
	}

}
