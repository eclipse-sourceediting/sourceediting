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
package org.eclipse.wst.xml.core.commentelement.impl;



import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.wst.xml.core.Logger;
import org.eclipse.wst.xml.core.commentelement.CommentElementAdapter;
import org.eclipse.wst.xml.core.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 */
public class CommentElementConfiguration {
	CommentElementConfiguration() {
		super();
	}

	CommentElementConfiguration(IConfigurationElement element) {
		super();
		fElement = element;
		fCustom = (element.getName().equalsIgnoreCase("handler-custom")) ? true : false; //$NON-NLS-1$

		fillAttributes(element);

		fXMLComment = fJSPComment = false;
		String commentType = getProperty("commenttype"); //$NON-NLS-1$
		if (commentType.equalsIgnoreCase("xml")) { //$NON-NLS-1$
			fXMLComment = true;
		}
		else if (commentType.equalsIgnoreCase("jsp")) { //$NON-NLS-1$
			fJSPComment = true;
		}
		else if (commentType.equalsIgnoreCase("both")) { //$NON-NLS-1$
			fXMLComment = fJSPComment = true;
		}
		String empty = getProperty("isempty"); //$NON-NLS-1$
		fEmpty = (empty != null && !empty.equals("false")) ? true : false; //$NON-NLS-1$
	}

	private void fillAttributes(IConfigurationElement element) {
		if (fAttributes == null) {
			fAttributes = new HashMap();
		}
		else {
			fAttributes.clear();
		}
		String[] names = element.getAttributeNames();
		if (names == null) {
			return;
		}
		int length = names.length;
		for (int i = 0; i < length; i++) {
			String name = names[i];
			fAttributes.put(name.toLowerCase(), element.getAttribute(name));
		}
	}

	public String getProperty(String name) {
		return (fAttributes != null) ? (String) fAttributes.get(name) : null;
	}

	public boolean acceptXMLComment() {
		return fXMLComment;
	}

	public boolean acceptJSPComment() {
		return fJSPComment;
	}

	public String[] getPrefix() {
		if (fPrefix == null) {
			if (fElement != null) {
				if (isCustom()) { // custom
					IConfigurationElement[] prefixes = fElement.getChildren("startwith"); //$NON-NLS-1$	
					if (prefixes != null) {
						fPrefix = new String[prefixes.length];
						for (int i = 0; i < prefixes.length; i++) {
							fPrefix[i] = prefixes[i].getAttribute("prefix"); //$NON-NLS-1$	
						}
					}
				}
				else { // basic 
					String name = getProperty("elementname"); //$NON-NLS-1$
					if (name != null) {
						if (isEmpty()) {
							fPrefix = new String[1];
							fPrefix[0] = name;
						}
						else {
							fPrefix = new String[2];
							fPrefix[0] = name;
							fPrefix[1] = '/' + name;
						}
					}
				}
			}
		}
		if (fPrefix == null) {
			fPrefix = new String[1];
			fPrefix[0] = ""; //$NON-NLS-1$
		}
		return fPrefix;
	}

	public CommentElementHandler getHandler() {
		if (fHandler == null) {
			if (fElement != null) {
				try {
					if (isCustom()) {
						fHandler = (CommentElementHandler) fElement.createExecutableExtension("class"); //$NON-NLS-1$
					}
					else {
						String elementName = getProperty("elementname"); //$NON-NLS-1$
						fHandler = new BasicCommentElementHandler(elementName, fEmpty);
					}
					//					((AbstractCommentElementHandler)fHandler).setElementPrefix(fElement.getAttribute("prefix"));
				}
				catch (Throwable e) {
					// catch and log (and ignore) ANY exception created
					// by executable extension.
					Logger.logException(e);
					fHandler = null;
				}
			}
			if (fHandler == null) {
				fHandler = new CommentElementHandler() {
					public Element createElement(Document document, String data, boolean isJSPTag) {
						return null;
					}

					public String generateStartTagContent(XMLElement element) {
						return null;
					}

					public String generateEndTagContent(XMLElement element) {
						return null;
					}

					public String getElementPrefix() {
						return null;
					}

					public boolean isCommentElement(XMLElement element) {
						return false;
					}

					public boolean isEmpty() {
						return false;
					}
				};
			}
		}
		return fHandler;
	}

	private boolean isCustom() {
		return fCustom;
	}

	private boolean isEmpty() {
		return fEmpty;
	}

	void setupCommentElement(XMLElement element) {
		element.setCommentTag(true);
		CommentElementAdapter adapter = new CommentElementAdapter(false, fHandler);
		adapter.setConfiguration(this);
		element.addAdapter(adapter);
	}

	public Element createElement(Document document, String data, boolean isJSPTag) {
		XMLElement element = (XMLElement) getHandler().createElement(document, data, isJSPTag);
		if (element != null) {
			CommentElementAdapter adapter = (CommentElementAdapter) element.getAdapterFor(CommentElementAdapter.class);
			if (adapter != null) {
				adapter.setConfiguration(this);
			}
		}
		return element;
	}

	public IPluginDescriptor getHandlerPluginDescriptor() {
		return fElement.getDeclaringExtension().getDeclaringPluginDescriptor();
	}

	public String getHandlerID() {
		if (fID == null) {
			fID = getProperty("id"); //$NON-NLS-1$
			if (fID == null) {
				if (isCustom()) {
					fID = getProperty("class"); //$NON-NLS-1$				
				}
				else {
					StringBuffer buf = new StringBuffer();
					buf.append(getHandlerPluginDescriptor().getUniqueIdentifier());
					buf.append('.');
					buf.append(getProperty("elementname")); //$NON-NLS-1$
					fID = buf.toString();
				}
			}
		}
		return fID;
	}

	private boolean fEmpty;
	private boolean fCustom;
	private boolean fXMLComment;
	private boolean fJSPComment;
	private String fID = null;
	private String[] fPrefix = null;
	private IConfigurationElement fElement = null;
	private CommentElementHandler fHandler = null;
	private Map fAttributes = null;
}
