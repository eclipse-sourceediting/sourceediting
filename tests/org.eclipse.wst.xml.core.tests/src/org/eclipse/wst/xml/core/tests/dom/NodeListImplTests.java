/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.NodeListImpl;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class NodeListImplTests extends TestCase {

	private static class AccessorNodeList extends NodeListImpl {
		public Node appendNode(Node node) {
			return super.appendNode(node);
		}

		public Node insertNode(Node node, int index) {
			return super.insertNode(node, index);
		}

		public Node removeNode(int index) {
			return super.removeNode(index);
		}

	}

	public NodeListImplTests() {
	}

	public NodeListImplTests(String name) {
		super(name);
	}

	public void testInsertAtIndex() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		assertEquals("non-zero length at start", 0, list.getLength());

		list.appendNode(document.createElement("test0"));
		list.appendNode(document.createElement("test1"));
		list.appendNode(document.createElement("test2"));
		list.appendNode(document.createElement("test3"));
		list.appendNode(document.createElement("test4"));
		list.appendNode(document.createElement("test5"));
		list.appendNode(document.createElement("test6"));

		list.insertNode(document.createElement("test"), 3);
		
		assertEquals("list size did not increment", 8, list.getLength());
		
		assertEquals("test0 was not at expected index", "test0", list.item(0).getLocalName());
		assertEquals("test1 was not at expected index", "test1", list.item(1).getLocalName());
		assertEquals("test2 was not at expected index", "test2", list.item(2).getLocalName());
		assertEquals("test was not at expected index",  "test",  list.item(3).getLocalName());
		assertEquals("test3 was not at expected index", "test3", list.item(4).getLocalName());
		assertEquals("test4 was not at expected index", "test4", list.item(5).getLocalName());
		assertEquals("test5 was not at expected index", "test5", list.item(6).getLocalName());
		assertEquals("test6 was not at expected index", "test6", list.item(7).getLocalName());
	}

	public void testRemoveFromIndex() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		list.appendNode(document.createElement("test0"));
		list.appendNode(document.createElement("test1"));
		list.appendNode(document.createElement("test2"));
		list.appendNode(document.createElement("test3"));
		list.appendNode(document.createElement("test4"));
		list.appendNode(document.createElement("test5"));
		list.appendNode(document.createElement("test6"));
		assertEquals("wrong length after setup", 7, list.getLength());

		Node excised = list.removeNode(3);
		assertEquals("test3 was not the one removed", "test3", excised.getLocalName());

		assertEquals("wrong length after removal", 6, list.getLength());		
		assertEquals("test0 was not at expected index", "test0", list.item(0).getLocalName());
		assertEquals("test1 was not at expected index", "test1", list.item(1).getLocalName());
		assertEquals("test2 was not at expected index", "test2", list.item(2).getLocalName());
		assertEquals("test4 was not at expected index", "test4", list.item(3).getLocalName());
		assertEquals("test5 was not at expected index", "test5", list.item(4).getLocalName());
		assertEquals("test6 was not at expected index", "test6", list.item(5).getLocalName());
	}

	public void testInsertAtNegativeIndex() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		// appends on bad value
		list.insertNode(document.createElement("test-1"),-1);
		assertEquals("wrong length after insert at negative index", 1, list.getLength());
	}

	public void testRemoveFromNegativeIndex() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		list.appendNode(document.createElement("test-1"));
		// ignores bad index
		list.removeNode(-1);
		assertEquals("wrong length after removal at negative index", 1, list.getLength());
	}

	public void testInsertAtExcessiveIndex() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		// appends on bad value
		list.insertNode(document.createElement("test3"), 3);
		assertEquals("wrong length after insert at out of bounds index", 1, list.getLength());
	}

	public void testRemoveFromExcessiveIndex() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		list.appendNode(document.createElement("test0"));
		// ignores bad index
		list.removeNode(3);
		assertEquals("wrong length after removal at nonexistent index", 1, list.getLength());
	}

	public void testAppend() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Document document = model.getDocument();

		AccessorNodeList list = new AccessorNodeList();

		assertEquals("non-zero length at start", 0, list.getLength());

		list.appendNode(document.createElement("test1"));
		assertEquals("list size did not increment", 1, list.getLength());

		list.appendNode(document.createElement("test2"));
		assertEquals("list size did not increment", 2, list.getLength());

		list.appendNode(document.createElement("test3"));
		assertEquals("list size did not increment", 3, list.getLength());

		Node element4 = list.appendNode(document.createElement("test4"));
		assertEquals("list size did not increment", 4, list.getLength());
		assertEquals("test4 not returned from append", "test4", element4.getLocalName());
		
		Node element5 = list.appendNode(document.createElement("test5"));
		assertEquals("list size did not increment", 5, list.getLength());
		assertEquals("test5 not returned from append", "test5", element5.getLocalName());
		
		Node element6 = list.appendNode(document.createElement("test6"));
		assertEquals("list size did not increment", 6, list.getLength());
		assertEquals("test6 not returned from append", "test6", element6.getLocalName());
		
		Node element7 = list.appendNode(document.createElement("test7"));
		assertEquals("list size did not increment", 7, list.getLength());
		assertEquals("test7 not returned from append", "test7", element7.getLocalName());

	}
}
