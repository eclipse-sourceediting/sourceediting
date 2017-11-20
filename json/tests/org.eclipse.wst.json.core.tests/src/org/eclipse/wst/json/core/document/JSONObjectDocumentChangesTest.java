/**
 *  Copyright (c) 2015, 2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.document;

import org.eclipse.wst.json.core.TestUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.junit.Assert;
import org.junit.Test;

public class JSONObjectDocumentChangesTest {

	@Test
	public void insertCloseObject() {
		IJSONModel model = TestUtil.loadModel("{");
		IJSONDocument document = model.getDocument();

		// Document contains an Object which is not closed
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);
		IJSONObject object = (IJSONObject) document.getFirstChild();
		Assert.assertFalse(object.isClosed());

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		// insert at end '}'
		structuredDocument.replaceText(structuredDocument, 1, 0, "}");
		object = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object.isClosed());
	}

	@Test
	public void removeObject() {
		IJSONModel model = TestUtil.loadModel("{}");
		IJSONDocument document = model.getDocument();

		// Document contains an Object which is not closed
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);
		IJSONObject object = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object.isClosed());

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		// remove {}
		structuredDocument.replaceText(structuredDocument, 0, 2, "");
		Assert.assertNull(document.getFirstChild());
	}

	@Test
	public void insertSpaces() {
		IJSONModel model = TestUtil.loadModel("{}");
		IJSONDocument document = model.getDocument();

		// Document contains an Object which is not closed
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);
		IJSONObject object = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object.isClosed());

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		// remove {}
		structuredDocument.replaceText(structuredDocument, 1, 0, "\r\n");
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);
		IJSONObject object2 = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object2.isClosed());
	}

	@Test
	public void insertPair() {
		IJSONModel model = TestUtil.loadModel("{}");
		IJSONDocument document = model.getDocument();

		// Document contains an Object which is not closed
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);
		IJSONObject object = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object.isClosed());

		// INSERT PAIR NAME : "" => {""}
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.replaceText(structuredDocument, 1, 0, "\"\"");
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONObject);
		IJSONObject object2 = (IJSONObject) document.getFirstChild();
		Assert.assertTrue(object2.isClosed());

		// check pair name
		Assert.assertTrue(object2.getFirstChild() instanceof IJSONPair);
		IJSONPair pair = (IJSONPair) object2.getFirstChild();
		Assert.assertEquals("", pair.getName());
		Assert.assertNull(pair.getValue());

		// INSERT COLON ':' => {"":}
		structuredDocument.replaceText(structuredDocument, 3, 0, ":");
		Assert.assertTrue(document.getFirstChild().getFirstChild() instanceof IJSONPair);
		pair = (IJSONPair) ((IJSONObject) document.getFirstChild())
				.getFirstChild();
		Assert.assertTrue(pair instanceof IJSONPair);
		Assert.assertEquals("", pair.getName());
		Assert.assertNull(pair.getValue());

		// INSERT OBJECT VALUE {} => {"":{}}
		structuredDocument.replaceText(structuredDocument, 4, 0, "{}");
		Assert.assertTrue(document.getFirstChild().getFirstChild() instanceof IJSONPair);
		pair = (IJSONPair) ((IJSONObject) document.getFirstChild())
				.getFirstChild();
		Assert.assertTrue(pair instanceof IJSONPair);
		Assert.assertEquals("", pair.getName());
		Assert.assertNotNull(pair.getValue());

		// UPDATE PAIR NAME : "aa" => {"aa":{}}
		structuredDocument.replaceText(structuredDocument, 2, 0, "a");
		Assert.assertTrue(document.getFirstChild().getFirstChild() instanceof IJSONPair);
		pair = (IJSONPair) ((IJSONObject) document.getFirstChild())
				.getFirstChild();
		Assert.assertTrue(pair instanceof IJSONPair);
		Assert.assertEquals("a", pair.getName());
		Assert.assertNotNull(pair.getValue());
	}
}
