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
package org.eclipse.wst.jsdt.web.core.internal.contentmodel;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 * CMDocument factory for JSP documents.
 */
public final class JSPCMDocumentFactory {

	static class CMDocImpl implements CMDocument {
		public CMDocImpl() {
			super();
		}

		private static CMDocument jcm = HTMLCMDocumentFactory
				.getCMDocument(CMDocType.JSP11_DOC_TYPE);

		public String getNodeName() {
			return jcm.getNodeName();
		}

		public int getNodeType() {
			return jcm.getNodeType();
		}

		public CMNamedNodeMap getElements() {
			return jcm.getElements();
		}

		public CMNamedNodeMap getEntities() {
			return jcm.getEntities();
		}

		public CMNamespace getNamespace() {
			return jcm.getNamespace();
		}

		public Object getProperty(String propertyName) {
			return null;
		}

		public boolean supports(String propertyName) {
			return false;
		}
	}

	private static CMDocument mycm;

	private JSPCMDocumentFactory() {
		super();
	}

	public static CMDocument getCMDocument() {
		if (mycm == null) {
			mycm = new CMDocImpl();
		}
		return mycm;
	}
}