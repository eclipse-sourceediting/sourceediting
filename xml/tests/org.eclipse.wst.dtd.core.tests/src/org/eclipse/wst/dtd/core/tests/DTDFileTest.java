/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 245216 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.tests;

import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

import junit.framework.TestCase;

public class DTDFileTest extends TestCase {

	public DTDFileTest() {
	}

	public DTDFileTest(String name) {
		super(name);
	}
	
	public void testGetTopLevelNodeAt() throws Exception {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		DTDModelImpl model = (DTDModelImpl)modelManager.createUnManagedStructuredModelFor(ContentTypeIdForDTD.ContentTypeID_DTD);
		String dtdText = "<!ELEMENT NewElement3 (#PCDATA)><!ENTITY % NewEntity SYSTEM \"\" >";
		IStructuredDocument document = model.getStructuredDocument();
		document.set(dtdText);
		
		model.setStructuredDocument(document);
		DTDFile dtdFile = model.getDTDFile();
		
		DTDNode dtdNode = dtdFile.getTopLevelNodeAt(32);
		assertNotNull("Node is null", dtdNode);
		assertEquals("Unexpected Node Type.",  "NewEntity", dtdNode.getName());
	}

}
