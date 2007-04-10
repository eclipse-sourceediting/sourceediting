/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;



import java.util.HashMap;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;

/**
 */
public class HTMLDocumentTypeAdapterFactory implements INodeAdapterFactory, Preferences.IPropertyChangeListener {

	private int tagNameCase = DocumentTypeAdapter.LOWER_CASE;
	private int attrNameCase = DocumentTypeAdapter.LOWER_CASE;
	private Preferences preferences = null;

	// for removal later on release()
	private HashMap fDoc2AdapterMap = new HashMap();
	
	/**
	 */
	public HTMLDocumentTypeAdapterFactory() {
		super();
		this.preferences = HTMLCorePlugin.getDefault().getPluginPreferences();

		if (this.preferences != null) {
			updateCases(); // initialize
			this.preferences.addPropertyChangeListener(this);
		}
	}

	/**
	 * Method that returns the adapter associated with the given object. It
	 * may be a singleton or not ... depending on the needs of the
	 * INodeAdapter ... but in general it is recommended for an adapter to be
	 * stateless, so the efficiencies of a singleton can be gained.
	 * 
	 * The implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter.
	 */
	public INodeAdapter adapt(INodeNotifier notifier) {
		INodeAdapter result = null;
		// only adapt IDOMDocument
		if (notifier instanceof IDOMDocument) {

			// if already has an adapter, no need to recreate/initialize.
			// Note: this means if "doctype" for DOM changes,
			// theDocumentTypeAdatper for that DOM
			// should be removed (and released) and it will be re-created next
			// time required.
			DocumentTypeAdapter oldAdapter = (DocumentTypeAdapter) notifier.getExistingAdapter(DocumentTypeAdapter.class);
			if (oldAdapter != null) {
				result = oldAdapter;
			}
			else {
				
				// if there already was an adapter
				//if(fAdapter != null) 
				//	fAdapter.release();
				
				// note, the factory is included in this case to have a central place
				// to come back to for case preferences.
				result = new HTMLDocumentTypeAdapter((IDOMDocument) notifier, this);
				notifier.addAdapter(result);
				
				fDoc2AdapterMap.put(notifier, result);
			}
		}
		return result;
	}

	/**
	 */
	public int getAttrNameCase() {
		return this.attrNameCase;
	}

	/**
	 */
	public int getTagNameCase() {
		return this.tagNameCase;
	}

	/**
	 */
	public boolean isFactoryForType(Object type) {
		return (type == DocumentTypeAdapter.class);
	}

	/**
	 */
	public void propertyChange(Preferences.PropertyChangeEvent event) {
		if (event == null)
			return;
		String property = event.getProperty();
		if (property == null)
			return;

		if (property.equals(HTMLCorePreferenceNames.TAG_NAME_CASE) || property.equals(HTMLCorePreferenceNames.ATTR_NAME_CASE)) {
			updateCases();
		}
	}

	/**
	 */
	private void updateCases() {
		this.tagNameCase = DocumentTypeAdapter.LOWER_CASE;
		this.attrNameCase = DocumentTypeAdapter.LOWER_CASE;

		if (this.preferences == null)
			return;

		int tagCase = this.preferences.getInt(HTMLCorePreferenceNames.TAG_NAME_CASE);
		if (tagCase == HTMLCorePreferenceNames.LOWER)
			this.tagNameCase = DocumentTypeAdapter.LOWER_CASE;
		else if (tagCase == HTMLCorePreferenceNames.UPPER)
			this.tagNameCase = DocumentTypeAdapter.UPPER_CASE;
		
		int attCase = this.preferences.getInt(HTMLCorePreferenceNames.ATTR_NAME_CASE);
		if (attCase == HTMLCorePreferenceNames.LOWER)
			this.attrNameCase = DocumentTypeAdapter.LOWER_CASE;
		else if (attCase == HTMLCorePreferenceNames.UPPER)
			this.tagNameCase = DocumentTypeAdapter.UPPER_CASE;
	}

	/**
	 */
	public void release() {
		
		if(!fDoc2AdapterMap.isEmpty()) {
			Object[] docs = fDoc2AdapterMap.keySet().toArray();
			DocumentTypeAdapter adapter = null;
			for (int i = 0; i < docs.length; i++) {
				adapter = (DocumentTypeAdapter)fDoc2AdapterMap.get(docs[i]);
				adapter.release();
				((IDOMDocument)docs[i]).removeAdapter(adapter);
			}
			fDoc2AdapterMap.clear();
		}
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=95960
		if (this.preferences != null) {
			this.preferences.removePropertyChangeListener(this);
		}
	}

	/**
	 * Overriding copy method
	 */
	public INodeAdapterFactory copy() {
		return new HTMLDocumentTypeAdapterFactory();
	}
}
