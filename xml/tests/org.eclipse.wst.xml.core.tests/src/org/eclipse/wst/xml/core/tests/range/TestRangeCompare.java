/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.range;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ranges.Range;

public class TestRangeCompare extends TestCase {
	private static final String decl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$
	
	public void testRangeCompare() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {

		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("parent");//$NON-NLS-1$
		document.appendChild(root);
		Element child = document.createElement("child");//$NON-NLS-1$
		root.appendChild(child);
		child.appendChild(document.createElement("child1-1"));//$NON-NLS-1$
		Text textnode = document.createTextNode("Text Node");//$NON-NLS-1$
		root.appendChild(textnode);
		NodeList children = root.getElementsByTagName("child");//$NON-NLS-1$
		
		Range range = ((DocumentImpl)document).createRange();
		range.setEnd(children.item(0), 1);
		
		Range sourceRange = ((DocumentImpl)document).createRange();
		sourceRange.setEnd(textnode, 0);
		int result = range.compareBoundaryPoints(Range.END_TO_END, sourceRange);

		assertEquals(-1, result);
	}

}