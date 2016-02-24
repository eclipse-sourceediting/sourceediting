/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.document;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.wst.json.core.TestUtil;
import org.eclipse.wst.json.core.contenttype.ContentTypeIdForJSON;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.junit.Assert;
import org.junit.Test;

public class JSONObjectCreationTest {

	@Test
	public void createEmptyObjectNotClosed() throws Exception {
		IJSONModel model = (IJSONModel) TestUtil.createModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		IJSONDocument document = model.getDocument();
		assertNull(document.getFirstChild());

		// Load JSON Object
		structuredDocument.set("{");
		assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);

		// The JSON object is not closed.
		IJSONObject object = (IJSONObject) document.getFirstChild();
		Assert.assertFalse(object.isClosed());
	}

	@Test
	public void createEmptyObject() throws Exception {
		IJSONModel model = (IJSONModel) TestUtil.createModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		IJSONDocument document = model.getDocument();
		assertNull(document.getFirstChild());

		// Load JSON Object
		structuredDocument.set("{}");
		assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);

		// The JSON object is closed.
		IJSONObject object = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object.isClosed());
	}

	@Test
	public void createObjectWithObjectValue() throws Exception {

		IJSONModel model = (IJSONModel) TestUtil.createModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		IJSONDocument document = model.getDocument();
		assertNull(document.getFirstChild());

		structuredDocument.set("{\"object\": {}}");
		assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);

		// The JSON root object is closed.
		IJSONObject rootObject = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(rootObject.isClosed());

		// First child of root is a pair
		assertNotNull(rootObject.getFirstChild());
		Assert.assertTrue(rootObject.getFirstChild() instanceof IJSONPair);
	}

	@Test
	public void createComplexObject() throws Exception {

		IJSONModel model = (IJSONModel) TestUtil.createModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		IJSONDocument document = model.getDocument();
		assertNull(document.getFirstChild());

		structuredDocument
				.set("{\n\t\"aaaaa\": {},\n\t\"array\": [\n\t\t1,\n\t\t2,\n\t\t3],\n\t\"boolean\": true,\n\t\"null\": null,\n\t\"number\": 123,\n\t\"object\": {\n\t\t\"a\": \"b\",\n\t\t\"c\": \"d\"\n\t},\n\t\"string\": \"Hello World\"\n}");
		assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);

	}

}
