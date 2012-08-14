/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
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
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;

/**
 */
public class HTMLDocumentTypeAdapterFactory implements INodeAdapterFactory {

	private static HTMLDocumentTypeAdapterFactory factory;
	private static final CasePreferenceListener listener = new CasePreferenceListener();
	/**
	 * @deprecated The getInstance() factory method should be used; however, this has
	 * been left in place for legacy reasons
	 */
	public HTMLDocumentTypeAdapterFactory() {
	}

	public static synchronized HTMLDocumentTypeAdapterFactory getInstance() {
		if (factory == null) {
			factory = new HTMLDocumentTypeAdapterFactory();
		}
		return factory;
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
			}
		}
		return result;
	}

	/**
	 */
	public int getAttrNameCase() {
		return listener.getAttrNameCase();
	}

	/**
	 */
	public int getTagNameCase() {
		return listener.getTagNameCase();
	}

	/**
	 */
	public boolean isFactoryForType(Object type) {
		return (type == DocumentTypeAdapter.class);
	}

	/**
	 */
	public void release() {
		
	}

	/**
	 * Overriding copy method
	 */
	public INodeAdapterFactory copy() {
		return getInstance();
	}

	private static class CasePreferenceListener implements Preferences.IPropertyChangeListener {

		private Preferences preferences;

		private int tagNameCase = DocumentTypeAdapter.LOWER_CASE;
		private int attrNameCase = DocumentTypeAdapter.LOWER_CASE;

		CasePreferenceListener() {
			preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
			if (preferences != null) {
				updateCases();
				preferences.addPropertyChangeListener(this);
			}
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (event == null)
				return;
			String property = event.getProperty();
			if (property == null)
				return;

			if (property.equals(HTMLCorePreferenceNames.TAG_NAME_CASE) || property.equals(HTMLCorePreferenceNames.ATTR_NAME_CASE)) {
				updateCases();
			}
		}

		public int getTagNameCase() {
			return tagNameCase;
		}

		public int getAttrNameCase() {
			return attrNameCase;
		}

		/**
		 */
		private void updateCases() {
			tagNameCase = DocumentTypeAdapter.LOWER_CASE;
			attrNameCase = DocumentTypeAdapter.LOWER_CASE;

			if (this.preferences == null)
				return;

			tagNameCase = getCase(HTMLCorePreferenceNames.TAG_NAME_CASE);
			attrNameCase = getCase(HTMLCorePreferenceNames.ATTR_NAME_CASE);
		}

		private int getCase(String property) {
			int result = DocumentTypeAdapter.LOWER_CASE;
			if (preferences != null) {
				if (preferences.getInt(property) == HTMLCorePreferenceNames.UPPER)
					result = DocumentTypeAdapter.UPPER_CASE;
			}
			return result;
		}
	}
}
