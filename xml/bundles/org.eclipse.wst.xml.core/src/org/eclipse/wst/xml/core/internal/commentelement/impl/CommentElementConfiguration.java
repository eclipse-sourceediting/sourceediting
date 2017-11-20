/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.commentelement.impl;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.commentelement.CommentElementAdapter;
import org.eclipse.wst.xml.core.internal.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 */
public class CommentElementConfiguration {
	private Map fAttributes = null;
	private boolean fCustom;
	private IConfigurationElement fElement = null;

	private boolean fEmpty;
	private CommentElementHandler fHandler = null;
	private String fID = null;
	private boolean fJSPComment;
	private String[] fPrefix = null;
	private boolean fXMLComment;

	CommentElementConfiguration(IConfigurationElement element) {
		super();
		fElement = element;
		fCustom = "handler-custom".equalsIgnoreCase(element.getName()); //$NON-NLS-1$

		fillAttributes(element);

		fXMLComment = fJSPComment = false;
		String commentType = getProperty("commenttype"); //$NON-NLS-1$
		if ("xml".equalsIgnoreCase(commentType)) { //$NON-NLS-1$
			fXMLComment = true;
		} else if ("jsp".equalsIgnoreCase(commentType)) { //$NON-NLS-1$
			fJSPComment = true;
		} else if ("both".equalsIgnoreCase(commentType)) { //$NON-NLS-1$
			fXMLComment = fJSPComment = true;
		}
		String empty = getProperty("isempty"); //$NON-NLS-1$
		fEmpty = Boolean.valueOf(empty).booleanValue(); //$NON-NLS-1$
	}

	public boolean acceptJSPComment() {
		return fJSPComment;
	}

	public boolean acceptXMLComment() {
		return fXMLComment;
	}

	public Element createElement(Document document, String data, boolean isJSPTag) {
		IDOMElement element = (IDOMElement) getHandler().createElement(document, data, isJSPTag);
		if (element != null) {
			CommentElementAdapter adapter = (CommentElementAdapter) element.getAdapterFor(CommentElementAdapter.class);
			if (adapter != null) {
				adapter.setConfiguration(this);
			}
		}
		return element;
	}

	private void fillAttributes(IConfigurationElement element) {
		if (fAttributes == null) {
			fAttributes = new HashMap();
		} else {
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

	public CommentElementHandler getHandler() {
		if (fHandler == null) {
			if (fElement != null) {
				try {
					if (isCustom()) {
						fHandler = (CommentElementHandler) fElement.createExecutableExtension("class"); //$NON-NLS-1$
					} else {
						String elementName = getProperty("elementname"); //$NON-NLS-1$
						if (elementName != null) {
							fHandler = new BasicCommentElementHandler(elementName, fEmpty);
						}
					}
					//					((AbstractCommentElementHandler)fHandler).setElementPrefix(fElement.getAttribute("prefix"));
				} catch (Exception e) {
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

					public String generateEndTagContent(IDOMElement element) {
						return null;
					}

					public String generateStartTagContent(IDOMElement element) {
						return null;
					}

// removed in RC2, ro removed "unused" error/warning
//					public String getElementPrefix() {
//						return null;
//					}

					public boolean isCommentElement(IDOMElement element) {
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

	public String getHandlerID() {
		if (fID == null) {
			fID = getProperty("id"); //$NON-NLS-1$
			if (fID == null) {
				if (isCustom()) {
					fID = getProperty("class"); //$NON-NLS-1$				
				} else {
					StringBuffer buf = new StringBuffer();
					buf.append(fElement.getDeclaringExtension().getNamespace());
					buf.append('.');
					buf.append(getProperty("elementname")); //$NON-NLS-1$
					fID = buf.toString();
				}
			}
		}
		return fID;
	}


	public String[] getPrefix() {
		if (fPrefix == null) {
			if (fElement != null) {
				if (isCustom()) { // custom
					IConfigurationElement[] prefixes = fElement.getChildren("startwith"); //$NON-NLS-1$	
					if (prefixes != null) {
						List prefixValues = new ArrayList(prefixes.length);
						for (int i = 0; i < prefixes.length; i++) {
							String prefix = prefixes[i].getAttribute("prefix"); //$NON-NLS-1$
							if (prefix != null) {
								prefixValues.add(prefix);
							}
							else {
								try {
									Logger.log(Logger.WARNING, "misconfigured comment element in" + fElement.getContributor().getName(), new IllegalArgumentException("startwith")); //$NON-NLS-1$ //$NON-NLS-2$
								}
								catch (InvalidRegistryObjectException e) {
									// stale bundle?
								}
							}
						}
						if (!prefixValues.isEmpty()) {
							fPrefix = (String[]) prefixValues.toArray(new String[prefixValues.size()]);
						}
					}
				} else { // basic
					String name = getProperty("elementname"); //$NON-NLS-1$
					if (name != null) {
						if (isEmpty()) {
							fPrefix = new String[1];
							fPrefix[0] = name;
						} else {
							fPrefix = new String[2];
							fPrefix[0] = name;
							fPrefix[1] = '/' + name;
						}
					}
				}
			}
		}
		if (fPrefix == null) {
			fPrefix = new String[0];
		}
		return fPrefix;
	}

	public String getProperty(String name) {
		return (fAttributes != null) ? (String) fAttributes.get(name) : null;
	}

	private boolean isCustom() {
		return fCustom;
	}

	private boolean isEmpty() {
		return fEmpty;
	}

	void setupCommentElement(IDOMElement element) {
		element.setCommentTag(true);
		CommentElementAdapter adapter = new CommentElementAdapter(false, fHandler);
		adapter.setConfiguration(this);
		element.addAdapter(adapter);
	}
}
