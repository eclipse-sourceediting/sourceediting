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
package org.eclipse.wst.html.core.internal.htmlcss;



import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetListAdapter;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 */
public class StyleAdapterFactory implements INodeAdapterFactory {

	private static StyleAdapterFactory instance = null;

	//	private static String CSS_CONTENT_TYPE = "text/css";//$NON-NLS-1$
	/**
	 */
	private StyleAdapterFactory() {
		super();
	}

	/**
	 */
	public INodeAdapter adapt(INodeNotifier notifier) {
		if (notifier == null)
			return null;

		Node node = (Node) notifier;
		short nodeType = node.getNodeType();
		if (nodeType == Node.DOCUMENT_NODE) {
			INodeAdapter adapter = notifier.getExistingAdapter(IStyleSheetListAdapter.class);
			if (adapter != null)
				return adapter;
			HTMLDocumentAdapter newAdapter = new HTMLDocumentAdapter();
			newAdapter.setDocument((Document) node);
			notifier.addAdapter(newAdapter);
			return newAdapter;
		}
		if (nodeType != Node.ELEMENT_NODE)
			return null;

		Element element = (Element) node;
		String tagName = element.getTagName();
		if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE)) {
			if (!isTagAvailable(element.getOwnerDocument(), HTML40Namespace.ElementName.STYLE)) {
				return null;
			}
			//		String type = element.getAttribute(HTML40Namespace.ATTR_NAME_TYPE);
			//		if (type != null && ! type.equalsIgnoreCase(CSS_CONTENT_TYPE)) {
			//			return null;
			//		}
			INodeAdapter adapter = notifier.getExistingAdapter(IStyleSheetAdapter.class);
			if (adapter != null)
				return adapter;
			StyleElementAdapter newAdapter = new StyleElementAdapter();
			newAdapter.setElement(element);
			notifier.addAdapter(newAdapter);
			return newAdapter;
		}
		else if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.LINK)) {
			if (!isTagAvailable(element.getOwnerDocument(), HTML40Namespace.ElementName.LINK)) {
				return null;
			}
			INodeAdapter adapter = notifier.getExistingAdapter(IStyleSheetAdapter.class);
			if (adapter != null)
				return adapter;
			LinkElementAdapter newAdapter = new LinkElementAdapter();
			newAdapter.setElement(element);
			notifier.addAdapter(newAdapter);
			return newAdapter;
		}

		INodeAdapter adapter = notifier.getExistingAdapter(IStyleDeclarationAdapter.class);
		if (adapter != null)
			return adapter;

		if (!isAttributeAvailable(element, HTML40Namespace.ATTR_NAME_STYLE)) {
			return null;
		}
		StyleAttrAdapter newAdapter = new StyleAttrAdapter();
		newAdapter.setElement(element);
		notifier.addAdapter(newAdapter);
		return newAdapter;
	}

	/**
	 */
	public synchronized static StyleAdapterFactory getInstance() {
		if (instance == null)
			instance = new StyleAdapterFactory();
		return instance;
	}

	/**
	 */
	public boolean isFactoryForType(Object type) {
		return (type == IStyleSheetAdapter.class || type == IStyleDeclarationAdapter.class || type == IStyleSheetListAdapter.class);
	}

	public void release() {
		// default is to do nothing
	}

	private static boolean isTagAvailable(Document document, String elementName) {
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
		if (modelQuery != null) {
			CMDocument cmdoc = modelQuery.getCorrespondingCMDocument(document);
			CMNamedNodeMap map = cmdoc.getElements();
			if ((CMElementDeclaration) map.getNamedItem(elementName) != null) {
				return true;
			}
		}

		return false;
	}

	private static boolean isAttributeAvailable(Element element, String attrName) {
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
		if (modelQuery != null) {
			CMElementDeclaration decl = modelQuery.getCMElementDeclaration(element);
			if (decl != null) {
				CMNamedNodeMap map = decl.getAttributes();
				if ((CMAttributeDeclaration) map.getNamedItem(attrName) != null) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Overriding Object's clone() method
	 */
	public INodeAdapterFactory copy() {
		return getInstance();
	}

}
