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
package org.eclipse.jst.jsp.ui.openon;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.html.ui.openon.DefaultOpenOnHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * This action opens classes referenced in JSP directive tags of a JSP page.
 */
public class JSPDirectiveOpenOnJSP extends DefaultOpenOnHTML {
	JSPJavaOpenOnJSP jspJavaOpenOn;

	private JSPJavaOpenOnJSP getJSPJavaOpenOn() {
		if (jspJavaOpenOn == null) {
			jspJavaOpenOn = new JSPJavaOpenOnJSP();
			// set the document to current document
			jspJavaOpenOn.setDocument(getDocument());
		}
		return jspJavaOpenOn;
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.openon.AbstractOpenOn#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument doc) {
		super.setDocument(doc);

		// also set the document for jspJavaOpenOn
		if (jspJavaOpenOn != null) {
			jspJavaOpenOn.setDocument(doc);
		}
	}

	/**
	 * Get JSP translation object
	 * 
	 * @return JSPTranslation if one exists, null otherwise
	 */
	private JSPTranslation getJSPTranslation() {
		// get JSP translation object for this action's editor's document
		XMLModel xmlModel = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
		if (xmlModel != null) {
			XMLDocument xmlDoc = xmlModel.getDocument();
			xmlModel.releaseFromRead();
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			if (adapter != null) {
				return adapter.getJSPTranslation();
			}
		}
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.ibm.sse.editor.AbstractOpenOn#doOpenOn(org.eclipse.jface.text.IRegion)
	 */
	protected void doOpenOn(IRegion region) {
		IRegion newRegion = doGetOpenOnRegion(region.getOffset());
		// if there is a corresponding java offset, then use JSPJavaOpenOnJSP
		JSPTranslation jspTranslation = getJSPTranslation();
		if ((jspTranslation != null) && (newRegion != null) && (jspTranslation.getJavaOffset(newRegion.getOffset()) > -1)) {
			getJSPJavaOpenOn().doOpenOn(newRegion);
		}
		else {
			// otherwise use DefaultOpenOnHTML
			super.doOpenOn(newRegion);
		}
	}

	/**
	 * Return an attr of element that is of type URI if one exists.  or if element is jsp:usebean
	 * return the type or class attribute.  null otherwise.
	 * @param element - cannot be null
	 * @return Attr
	 */
	protected Attr getLinkableAttr(Element element) {
		String tagName = element.getTagName();

		// usebean
		if (JSP11Namespace.ElementName.USEBEAN.equalsIgnoreCase(tagName)) {
			// get the list of attributes for this node
			NamedNodeMap attrs = element.getAttributes();
			for (int i = 0; i < attrs.getLength(); ++i) {
				Attr att = (Attr) attrs.item(i);
				String attName = att.getName();
				// look for the type or class attribute
				if ((JSP11Namespace.ATTR_NAME_TYPE.equalsIgnoreCase(attName)) || (JSP11Namespace.ATTR_NAME_CLASS.equalsIgnoreCase(attName))) {
					return att;
				}
			}
		}

		// otherwise, just look for attribute value of type URI
		return super.getLinkableAttr(element);
	}
}