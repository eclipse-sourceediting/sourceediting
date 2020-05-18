/*******************************************************************************
 * Copyright (c) 2004, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.html.core.internal.contentmodel.chtml.CHCMDocImpl;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 * INodeAdapter factory for HTML and JSP documents.
 */
public final class HTMLCMDocumentFactory {

	private static Map<String, CMDocument> cmdocs = new HashMap<>();
	private static List<String> supportedCMtypes = Arrays.asList(new String[]{CMDocType.HTML_DOC_TYPE, CMDocType.CHTML_DOC_TYPE, CMDocType.JSP11_DOC_TYPE, CMDocType.JSP12_DOC_TYPE, CMDocType.JSP20_DOC_TYPE, CMDocType.TAG20_DOC_TYPE, CMDocType.TAG21_DOC_TYPE, CMDocType.JSP21_DOC_TYPE, CMDocType.HTML5_DOC_TYPE});

	private static JCMDocImpl jsp11doc = null;

	/**
	 * HTMLCMAdapterFactory constructor.
	 */
	private HTMLCMDocumentFactory() {
		super();
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 * @param cmtype
	 *            java.lang.String
	 */
	public static CMDocument getCMDocument(String cmtype) {
		CMDocument obj = cmdocs.get(cmtype);
		if (obj == null && cmtype != null) {
			if (supportedCMtypes.contains(cmtype)) {
				obj = doCreateCMDocument(cmtype);
				cmdocs.put(cmtype, obj);
			}
		}

		return obj;
	}

	private static CMDocument doCreateCMDocument(String cmtype) {
		if (CMDocType.HTML_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl h40ns = new CMNamespaceImpl(HTML40Namespace.HTML40_URI, HTML40Namespace.HTML40_TAG_PREFIX);
			HCMDocImpl html40doc = new HCMDocImpl(CMDocType.HTML_DOC_TYPE, h40ns);
			return html40doc;
		}

		else if (CMDocType.HTML5_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl h50ns = new CMNamespaceImpl(HTML50Namespace.HTML50_URI, HTML50Namespace.HTML50_TAG_PREFIX);
			H5CMDocImpl html50doc = new H5CMDocImpl(CMDocType.HTML5_DOC_TYPE, h50ns);
			return html50doc;
		}
		
		else if (CMDocType.JSP20_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl j20ns = new CMNamespaceImpl(JSP20Namespace.JSP20_URI, JSP11Namespace.JSP_TAG_PREFIX);
			JCM20DocImpl jsp20doc = new JCM20DocImpl(CMDocType.JSP20_DOC_TYPE, j20ns);
			return jsp20doc;
		}

		else if (CMDocType.JSP21_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl j21ns = new CMNamespaceImpl(JSP21Namespace.JSP21_URI, JSP11Namespace.JSP_TAG_PREFIX);
			JCM21DocImpl jsp21doc = new JCM21DocImpl(CMDocType.JSP21_DOC_TYPE, j21ns);
			return jsp21doc;
		}

		else if (CMDocType.TAG20_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl j20ns = new CMNamespaceImpl(JSP20Namespace.JSP20_URI, JSP11Namespace.JSP_TAG_PREFIX);
			TagCMDocImpl tag20doc = new TagCMDocImpl(CMDocType.TAG20_DOC_TYPE, j20ns);
			return tag20doc;
		}

		else if (CMDocType.TAG21_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl j21ns = new CMNamespaceImpl(JSP21Namespace.JSP21_URI, JSP11Namespace.JSP_TAG_PREFIX);
			TagCM21DocImpl tag21doc = new TagCM21DocImpl(CMDocType.TAG21_DOC_TYPE, j21ns);
			return tag21doc;
		}

		else if (CMDocType.JSP11_DOC_TYPE.equals(cmtype) || CMDocType.JSP12_DOC_TYPE.equals(cmtype)) {
			if (jsp11doc == null) {
				CMNamespaceImpl j11ns = new CMNamespaceImpl(JSP11Namespace.JSP11_URI, JSP11Namespace.JSP_TAG_PREFIX);
				jsp11doc = new JCMDocImpl(CMDocType.JSP11_DOC_TYPE, j11ns);
			}
			return jsp11doc;
		}

		else if (CMDocType.CHTML_DOC_TYPE.equals(cmtype)) {
			CMNamespaceImpl cH40ns = new CMNamespaceImpl(HTML40Namespace.HTML40_URI, HTML40Namespace.HTML40_TAG_PREFIX);
			CHCMDocImpl chtmldoc = new CHCMDocImpl(CMDocType.CHTML_DOC_TYPE, cH40ns);
			return chtmldoc;
		}

		return null;
	}
}
