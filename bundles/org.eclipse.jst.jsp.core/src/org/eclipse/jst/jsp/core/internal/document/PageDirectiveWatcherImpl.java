/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.document;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;



/** 
 * The responsibility of this class is to monitor page directives and if 
 * a change in embedded type is is made, it will signal 
 * the structuredModel that it needs to reinitialize itself.
 */
class PageDirectiveWatcherImpl implements PageDirectiveWatcher {

	private static Object adapterType = PageDirectiveWatcher.class;
	IDOMElement targetElement;

	/**
	 * Constructor for PageDirectiveWatcherImpl.
	 */
	public PageDirectiveWatcherImpl(IDOMElement target) {
		super();
		targetElement = target;
		if (target.hasAttribute("contentType")) { //$NON-NLS-1$
			String contentTypeValue = target.getAttribute("contentType"); //$NON-NLS-1$
			// using concrete class below, since "changed" is something of an internal method
			PageDirectiveAdapterImpl pageDirectiveAdapter = (PageDirectiveAdapterImpl) ((IDOMDocument) targetElement.getOwnerDocument()).getAdapterFor(PageDirectiveAdapter.class);
			pageDirectiveAdapter.changedContentType(((IndexedRegion) targetElement).getStartOffset(), contentTypeValue);
		}
		if (target.hasAttribute("language")) { //$NON-NLS-1$
			String languageValue = target.getAttribute("language"); //$NON-NLS-1$
			// using concrete class below, since "changed" is something of an internal method
			PageDirectiveAdapterImpl pageDirectiveAdapter = (PageDirectiveAdapterImpl) ((IDOMDocument) targetElement.getOwnerDocument()).getAdapterFor(PageDirectiveAdapter.class);
			pageDirectiveAdapter.changedLanguage(((IndexedRegion) targetElement).getStartOffset(), languageValue);
		}
		if (target.hasAttribute("isELIgnored")) { //$NON-NLS-1$
			String elIgnored = target.getAttribute("isELIgnored"); //$NON-NLS-1$
			PageDirectiveAdapterImpl pageDirectiveAdapter = (PageDirectiveAdapterImpl) ((IDOMDocument) targetElement.getOwnerDocument()).getAdapterFor(PageDirectiveAdapter.class);
			pageDirectiveAdapter.setElIgnored(elIgnored);
		}

	}

	public boolean isAdapterForType(Object type) {
		return (type == adapterType);
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// we should only be added to page directives, so if we see a page directive
		// change, we need to check its attributes, and notify the PageDirectiveAdapter when 
		// certain ones chane, so it can make its "centralized" decisions.
		if (notifier instanceof IDOMNode) {

			switch (eventType) {
				case INodeNotifier.CHANGE :
					if (changedFeature instanceof AttrImpl) {
						AttrImpl attribute = (AttrImpl) changedFeature;
						String name = attribute.getName();
						if (name.equals("contentType")) { //$NON-NLS-1$
							// using concrete class below, since "changed" is something of an internal method
							PageDirectiveAdapterImpl pageDirectiveAdapter = (PageDirectiveAdapterImpl) ((IDOMDocument) targetElement.getOwnerDocument()).getAdapterFor(PageDirectiveAdapter.class);
							pageDirectiveAdapter.changedContentType(((IndexedRegion) targetElement).getStartOffset(), (String) newValue);
						}
						if (name.equals("language")) { //$NON-NLS-1$ //$NON-NLS-2$
							// using concrete class below, since "changed" is something of an internal method
							PageDirectiveAdapterImpl pageDirectiveAdapter = (PageDirectiveAdapterImpl) ((IDOMDocument) targetElement.getOwnerDocument()).getAdapterFor(PageDirectiveAdapter.class);
							pageDirectiveAdapter.changedLanguage(((IndexedRegion) targetElement).getStartOffset(), (String) newValue);
						}
					}

					break;
				case INodeNotifier.REMOVE :
					//System.out.println("removed"+new Date().toString());
					break;


				default :
					break;
			}
		}

	}

	public String getContentType() {
		String contentTypeValue = targetElement.getAttribute("contentType"); //$NON-NLS-1$
		return contentTypeValue;
	}

	public String getLanguage() {
		String languageValue = targetElement.getAttribute("language"); //$NON-NLS-1$
		return languageValue;
	}

	public int getOffset() {
		return targetElement.getStartOffset();
	}

}
