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
package org.eclipse.jst.jsp.core.internal.contentmodel;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 * CMDocument factory for JSP documents (which for now live in the HTML Core
 * plugin).
 */
public final class JSPCMDocumentFactory {

	private JSPCMDocumentFactory() {
		super();
	}

	public static CMDocument getCMDocument() {
		return getCMDocument(CMDocType.JSP21_DOC_TYPE);
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 * @param cmtype
	 *            java.lang.String
	 */
	public static CMDocument getCMDocument(String cmtype) {
		if (cmtype == null)
			return getCMDocument();
		return HTMLCMDocumentFactory.getCMDocument(cmtype);
	}

	public static CMDocument getCMDocument(float jspVersion) {
		if (jspVersion >= 2.1f)
			return getCMDocument(CMDocType.JSP21_DOC_TYPE);
		else if (jspVersion >= 2)
			return getCMDocument(CMDocType.JSP20_DOC_TYPE);
		else if (jspVersion >= 1.2f)
			return getCMDocument(CMDocType.JSP12_DOC_TYPE);
		else if (jspVersion >= 1)
			return getCMDocument(CMDocType.JSP11_DOC_TYPE);
		return getCMDocument();
	}
}
