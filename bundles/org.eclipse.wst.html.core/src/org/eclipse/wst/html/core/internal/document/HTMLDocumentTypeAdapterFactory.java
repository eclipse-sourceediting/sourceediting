/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;



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

	private int tagNameCase = DocumentTypeAdapter.UPPER_CASE;
	private int attrNameCase = DocumentTypeAdapter.LOWER_CASE;
	private Preferences preferences = null;

	// for removal later on release()
	private DocumentTypeAdapter fAdapter = null;
	private INodeNotifier fNotifier = null;
	
	/**
	 */
	public HTMLDocumentTypeAdapterFactory() {
		super();
		this.preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
		//this.store = CommonPreferencesPlugin.getDefault().getPreferenceStore(ContentTypeRegistry.HTML_ID);
		if (this.preferences != null) {
			updateCases(); // initialize
			this.preferences.addPropertyChangeListener(this);
		}
	}

	/**
	 * Method that returns the adapter associated with the given object.
	 * It may be a singleton or not ... depending on the needs of the INodeAdapter  ...
	 * but in general it is recommended for an adapter to be stateless, 
	 * so the efficiencies of a singleton can be gained.
	 *
	 * The implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter.
	 */
	public INodeAdapter adapt(INodeNotifier notifier) {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=85484
		
		// remove old adapter, or else they may collect
		DocumentTypeAdapter oldAdapter = (DocumentTypeAdapter)notifier.getExistingAdapter(DocumentTypeAdapter.class);
		if(oldAdapter != null) {
			oldAdapter.release();
			notifier.removeAdapter(oldAdapter);
		}
		
		DocumentTypeAdapter adapter = null;
//		if (adapter != null && adapter instanceof HTMLDocumentTypeAdapter)
//			return adapter;
		if (!(notifier instanceof IDOMDocument))
			return null;
		adapter = new HTMLDocumentTypeAdapter((IDOMDocument) notifier, this);
		notifier.addAdapter(adapter);
		
		fAdapter = adapter;
		fNotifier = notifier;
		
		return adapter;
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
		this.tagNameCase = DocumentTypeAdapter.UPPER_CASE;
		this.attrNameCase = DocumentTypeAdapter.LOWER_CASE;

		if (this.preferences == null)
			return;

		if (this.preferences.getInt(HTMLCorePreferenceNames.TAG_NAME_CASE) == HTMLCorePreferenceNames.LOWER) {
			this.tagNameCase = DocumentTypeAdapter.LOWER_CASE;
		}
		if (this.preferences.getInt(HTMLCorePreferenceNames.ATTR_NAME_CASE) == HTMLCorePreferenceNames.UPPER) {
			this.attrNameCase = DocumentTypeAdapter.UPPER_CASE;
		}
	}

	/**
	 */
	public void release() {
	    if(fAdapter != null && fNotifier != null) {
	        fAdapter.release();
	        fNotifier.removeAdapter(fAdapter);
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