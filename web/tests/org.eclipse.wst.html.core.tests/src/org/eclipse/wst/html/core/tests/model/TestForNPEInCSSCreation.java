/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.html.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.tests.utils.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class TestForNPEInCSSCreation extends TestCase {


	public void testCSSModel() {

		IDOMModel model = FileUtil.createHTMLModel();

		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.set(getHTMLDocumentText());

			IDOMDocument doc = model.getDocument();

			// get head tag
			NodeList list = doc.getElementsByTagName(HTML40Namespace.ElementName.HEAD);
			Element head = (Element) list.item(0);

			// create and append style element
			Element ele = doc.createElement(HTML40Namespace.ElementName.STYLE);
			ele.setAttribute(HTML40Namespace.ATTR_NAME_TYPE, "text/css");

			String delim = model.getStructuredDocument().getLineDelimiter();
			if (delim == null)
				delim = "\n";//$NON-NLS-1$
			StringBuffer buffer = new StringBuffer(delim);
			buffer.append("<!--");//$NON-NLS-1$
			buffer.append(delim);
			buffer.append("-->");//$NON-NLS-1$
			buffer.append(delim);
			Text text = doc.createTextNode(buffer.toString());
			ele.appendChild(text);

			head.insertBefore(ele, null);

			// get adapter for StyleSheet
			ICSSStyleSheet sheet = (ICSSStyleSheet) ((IStyleSheetAdapter) ((INodeNotifier) ele).getAdapterFor(IStyleSheetAdapter.class)).getSheet();

			// create style declaration
			ICSSStyleRule rule = sheet.createCSSStyleRule();
			rule.getStyle().setProperty("background-color", "lime", "");
			rule.getStyle().setProperty("background-color", "blue", "");
			rule.getStyle().setProperty("background-color", "#0080ff", "");

			//model.save();


		}


		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}

	}

	private String getHTMLDocumentText() {
		return "<html><head></head><body></body></html>";
	}

}
