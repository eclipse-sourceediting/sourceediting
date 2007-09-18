/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;

public class TestCatalogContentModels extends TestCase {
	private static final String contentTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<!DOCTYPE html ${METHOD} ${PUBLIC} ${SYSTEM}>\n<html>\n<head>\n<title>Insert title here</title>\n</head>\n<body>\n</body>\n</html>";

	public TestCatalogContentModels() {
		this("HTML Content Models from XML Catalog");
	}

	public TestCatalogContentModels(String string) {
		super(string);
	}

	private void assertIsNotXHTMLContentModel(IDOMModel htmlModel) {
		Element html = htmlModel.getDocument().getDocumentElement();
		CMDocument correspondingCMDocument = ModelQueryUtil.getModelQuery(htmlModel).getCorrespondingCMDocument(html);
		assertNotNull("content model document not found", correspondingCMDocument);
		assertTrue("document is unexpectedly XHTML", correspondingCMDocument.supports(HTMLCMProperties.IS_XHTML));
	}

	private void assertIsXHTMLContentModel(IDOMModel htmlModel) {
		Element html = htmlModel.getDocument().getDocumentElement();
		CMDocument correspondingCMDocument = ModelQueryUtil.getModelQuery(htmlModel).getCorrespondingCMDocument(html);
		assertNotNull("content model document not found", correspondingCMDocument);
		assertTrue("document is not XHTML", correspondingCMDocument.supports(HTMLCMProperties.IS_XHTML));
	}

	private IDOMModel createHTMLModel(String publicId, String systemId) {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForHTML.ContentTypeID_HTML);
		String source = createTestContents(publicId, systemId);
		model.getStructuredDocument().set(source);
		return model;
	}

	private String createTestContents(String publicId, String systemId) {
		String result = null;
		if (systemId != null) {
			result = StringUtils.replace(contentTemplate, "${SYSTEM}", "\"" + systemId + "\"");
		}
		else {
			result = StringUtils.replace(contentTemplate, "${SYSTEM}", "");
		}
		if (publicId != null) {
			result = StringUtils.replace(contentTemplate, "${PUBLIC}", "\"" + publicId + "\"");
		}
		else {
			result = StringUtils.replace(contentTemplate, "${PUBLIC}", "");
		}
		if (publicId != null && systemId != null)
			result = StringUtils.replace(contentTemplate, "${METHOD}", "PUBLIC");
		else if (publicId == null && systemId != null)
			result = StringUtils.replace(contentTemplate, "${METHOD}", "SYSTEM");
		else
			result = StringUtils.replace(contentTemplate, "${METHOD}", "");

		return result;
	}

	public void testCHTMLdraft() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD Compact HTML 1.0 Draft//EN", null);
		assertIsNotXHTMLContentModel(htmlModel);
	}

	public void testHTML401Frameset() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD HTML 4.01 Frameset//EN", "http://www.w3.org/TR/html4/frameset.dtd");
		assertIsNotXHTMLContentModel(htmlModel);
	}

	public void testHTML401Strict() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD HTML 4.01//EN", "http://www.w3.org/TR/html4/strict.dtd");
		assertIsNotXHTMLContentModel(htmlModel);
	}

	public void testHTML401Transitional() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd");
		assertIsNotXHTMLContentModel(htmlModel);
	}

	public void testWML11() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//WAPFORUM//DTD WML 1.1//EN", null);
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testWML13() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//WAPFORUM//DTD WML 1.3//EN", "http://www.wapforum.org/DTD/wml13.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testXHTML10Basic() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD XHTML Basic 1.0//EN", "http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testXHTML10Frameset() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD XHTML 1.0 Frameset//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testXHTML10Mobile() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//WAPFORUM//DTD XHTML Mobile 1.0//EN", "http://www.wapforum.org/DTD/xhtml-mobile10.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testXHTML10Strict() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testXHTML10Transitional() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD XHTML 1.0 Transitional//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}

	public void testXHTML11() throws Exception {
		IDOMModel htmlModel = createHTMLModel("-//W3C//DTD XHTML 1.1//EN", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
		assertIsXHTMLContentModel(htmlModel);
	}
}
