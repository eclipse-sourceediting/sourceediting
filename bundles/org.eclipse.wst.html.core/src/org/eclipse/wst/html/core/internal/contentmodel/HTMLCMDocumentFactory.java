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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Hashtable;

import org.eclipse.wst.html.core.internal.contentmodel.chtml.CHCMDocImpl;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 * INodeAdapter factory for HTML and JSP documents.
 */
public final class HTMLCMDocumentFactory {

	private static Hashtable cmdocs = new Hashtable();

	static {
		CMNamespaceImpl h40ns = new CMNamespaceImpl(HTML40Namespace.HTML40_URI, HTML40Namespace.HTML40_TAG_PREFIX);
		CMNamespaceImpl j11ns = new CMNamespaceImpl(JSP11Namespace.JSP11_URI, JSP11Namespace.JSP_TAG_PREFIX);

		HCMDocImpl html40doc = new HCMDocImpl(CMDocType.HTML_DOC_TYPE, h40ns);
		CHCMDocImpl chtmldoc = new CHCMDocImpl(CMDocType.CHTML_DOC_TYPE, h40ns);
		JCMDocImpl jsp11doc = new JCMDocImpl(CMDocType.JSP11_DOC_TYPE, j11ns);

		cmdocs.put(CMDocType.HTML_DOC_TYPE, html40doc);
		cmdocs.put(CMDocType.CHTML_DOC_TYPE, chtmldoc);
		cmdocs.put(CMDocType.JSP11_DOC_TYPE, jsp11doc);
	}

	/**
	 * HTMLCMAdapterFactory constructor.
	 */
	private HTMLCMDocumentFactory() {
		super();
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 * @param cmtype java.lang.String
	 */
	public static CMDocument getCMDocument(String cmtype) {
		Object obj = cmdocs.get(cmtype);
		if (obj == null)
			return null;

		return (CMDocument) obj;
	}
}
