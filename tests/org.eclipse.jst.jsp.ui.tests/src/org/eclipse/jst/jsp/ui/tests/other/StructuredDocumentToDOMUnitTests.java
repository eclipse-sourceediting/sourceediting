/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.other;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jst.jsp.ui.tests.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StructuredDocumentToDOMUnitTests implements IStructuredDocumentListener {
	public StructuredDocumentToDOMUnitTests() {
		super();
	}

	String getTestString1() {
		return "<a>a<b />b</a>"; // -> <a>ab</a>;
	}

	String getTestString2() {
		return "<menu>\n<rname>\nLiam's Chowder House and Grill\n</rname>\n</menu>";
	}

	String getTestString3() {
		// return " <?xml version=\"1.0\" ?>\n
		// <testTopSibling><p>text</p></testTopSibling>";
		return "<test/>\n\n <html>\n</html>";
	}

	String getTestString4() {
		return "<?xml version=\"1.0\" ?>";
	}

	/**
	 * 
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(String[] args) {

		try {
			StructuredDocumentToDOMUnitTests instance = new StructuredDocumentToDOMUnitTests();
			instance.testNodeDeletion4();
		}
		catch (Exception t) {
			t.printStackTrace();
		}
	}

	void makeChange1(IDOMModel tree) {

		//	
		// pick a parent and ones of its children to delete
		// (note: validity of this code is highly dependent on input string)
		org.w3c.dom.Document dom = tree.getDocument();
		NodeList nodes = dom.getChildNodes();
		//
		/*
		 * for "first" test case Node testParentNode = nodes.item(0); Node
		 * testChildNode = testParentNode.getFirstChild();
		 */
		// for "second" test case
		Node testParentNode = nodes.item(0);
		Node testChildNode = testParentNode.getFirstChild();
		testChildNode = testChildNode.getNextSibling();

		testParentNode.removeChild(testChildNode);

	}

	void makeChange2(IStructuredDocument structuredDocument) {

		// delete the apostrope in test string // 4, 4?
		structuredDocument.replaceText(this, 19, 1, null);
	}

	void makeChange3(IDOMModel tree) {

		//	
		// pick a parent and ones of its children to delete
		// (note: validity of this code is highly dependent on input string)
		org.w3c.dom.Document dom = tree.getDocument();
		// NodeList nodes = dom.getChildNodes();
		//
		Node testChildNode = dom.getLastChild();

		dom.removeChild(testChildNode);

	}

	/**
	 * newModel method comment.
	 */
	public void newModel(NewDocumentEvent structuredDocumentEvent) {
		System.out.println("newModel:");
	}

	/**
	 * noChange method comment.
	 */
	public void noChange(NoChangeEvent structuredDocumentEvent) {
		// log for now, unless we find reason not to
		Logger.log(Logger.INFO, "StructuredDocumentToDOMUnitTests::noChange needs to be implemented");
	}

	/**
	 * nodesReplaced method comment.
	 */
	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
		System.out.println("nodesReplaced:");
		System.out.println("\tNewFlatNodes:");
		IStructuredDocumentRegionList flatNodeList = structuredDocumentEvent.getNewStructuredDocumentRegions();
		int len = flatNodeList.getLength();
		for (int i = 0; i < len; i++) {
			String outString = flatNodeList.item(i).toString();
			outString = StringUtils.escape(outString);
			System.out.println("\t\t" + outString);
		}
		System.out.println("\tOldFlatNodes:");
		flatNodeList = structuredDocumentEvent.getOldStructuredDocumentRegions();
		len = flatNodeList.getLength();
		for (int i = 0; i < len; i++) {
			String outString = flatNodeList.item(i).toString();
			outString = StringUtils.escape(outString);
			System.out.println("\t\t" + outString);
		}
	}

	/**
	 * regionChanged method comment.
	 */
	public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
		System.out.println("regionChanged:");
	}

	/**
	 * regionsReplaced method comment.
	 */
	public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
		System.out.println("regionsReplaced:");
	}

	/**
	 * Unit test -- tests basic parsing results of inserting a test string
	 * into an initial string.
	 */
	void testNodeDeletion() {
		// String initialString =
		// "<par><x>\ntextx\n</x>\n<y>\ntexty\n</y></par>";
		// String initialString = "<par><x>textx</x><y>texty</y></par>";
		// test cases for two text nodes left together (remove <b/>):
		// first case works, second doesn't
		// String initialString = "<a>a<b /></a>"; // -> <a>a</a>
		// String initialString = "<a>a<b />b</a>"; // -> <a>ab</a>
		// String initialString = getTestString1();
		String initialString = getTestString2();

		// print out what we always can
		System.out.println();
		System.out.println("----------------");
		System.out.println("Test Node Deletion");
		String outString = StringUtils.escape(initialString);
		System.out.println("Initial String: " + outString);
		// always start with fresh model
		IStructuredDocument f = null;

		IModelManager mm = StructuredModelManager.getModelManager();
		try {
			f = mm.createStructuredDocumentFor("dummy.xml", (InputStream) null, null);
		}
		catch (IOException e) {
			// do nothing, since dummy
		}


		//
		// we'll listen to structuredDocument events to print out diagnostics
		f.addDocumentChangedListener(this);
		//

		IDOMModel tree = new DOMModelImpl();
		f.addDocumentChangingListener((IStructuredDocumentListener) tree);

		// set text to structuredDocument (which updates tree)
		f.setText(null, initialString);

		// dump initial structuredDocument
		Debug.dump(f);
		// dump initial dom
		DebugDocument.dump(tree.getDocument());
		//
		//
		// makeChange1(tree);
		makeChange2(f);

		// display resulting text
		System.out.println("resultString (from structuredDocument): ");
		System.out.println(StringUtils.escape(f.getText()));
		//
		//
		// dump post change structuredDocument
		Debug.dump(f);
		// dump post change DOM
		DebugDocument.dump(tree.getDocument());

		//

	}

	/**
	 * Unit test -- tests basic parsing results of inserting a test string
	 * into an initial string.
	 */
	void testNodeDeletion3() {
		String initialString = getTestString3();

		// print out what we always can
		System.out.println();
		System.out.println("----------------");
		System.out.println("Test Node Deletion");
		String outString = StringUtils.escape(initialString);
		System.out.println("Initial String: " + outString);
		// always start with fresh model
		IStructuredDocument f = null;

		IModelManager mm = StructuredModelManager.getModelManager();
		try {
			f = mm.createStructuredDocumentFor("dummy.xml", (InputStream) null, null);
		}
		catch (IOException e) {
			// do nothing, since dummy
		}
		//
		// we'll listen to structuredDocument events to print out diagnostics
		f.addDocumentChangedListener(this);
		//

		IDOMModel tree = new DOMModelImpl();
		f.addDocumentChangingListener((IStructuredDocumentListener) tree);

		// set text to structuredDocument (which updates tree)
		f.setText(null, initialString);

		// dump initial structuredDocument
		Debug.dump(f);
		// dump initial dom
		DebugDocument.dump(tree.getDocument());
		//
		//
		// makeChange1(tree);
		makeChange3(tree);

		// display resulting text
		System.out.println("resultString (from structuredDocument): ");
		System.out.println(StringUtils.escape(f.getText()));
		//
		//
		// dump post change structuredDocument
		Debug.dump(f);
		// dump post change DOM
		DebugDocument.dump(tree.getDocument());

		//

	}

	/**
	 * Unit test -- test insert followed by delete at beginning of string.
	 */
	void testNodeDeletion4() {
		String initialString = getTestString4();

		// print out what we always can
		System.out.println();
		System.out.println("----------------");
		System.out.println("Test Node Insert and Delete");
		String outString = StringUtils.escape(initialString);
		System.out.println("Initial String: " + outString);
		// always start with fresh model
		IStructuredDocument f = null;

		IModelManager mm = StructuredModelManager.getModelManager();
		try {
			f = mm.createStructuredDocumentFor("dummy.xml", (InputStream) null, null);
		}
		catch (IOException e) {
			// do nothing, since dummy
		}
		//
		// we'll listen to structuredDocument events to print out diagnostics
		f.addDocumentChangedListener(this);
		//

		IDOMModel tree = new DOMModelImpl();
		f.addDocumentChangingListener((IStructuredDocumentListener) tree);

		// set text to structuredDocument (which updates tree)
		f.setText(null, initialString);

		// dump initial structuredDocument
		Debug.dump(f);
		// dump initial dom
		DebugDocument.dump(tree.getDocument());
		//
		//
		f.replaceText(null, 0, 0, "a");

		System.out.println(" ==== Results after insert");
		// display resulting text
		System.out.println("resultString (from structuredDocument): ");
		System.out.println(StringUtils.escape(f.getText()));
		//
		//
		// dump post change structuredDocument
		Debug.dump(f);
		// dump post change DOM
		DebugDocument.dump(tree.getDocument());

		//

		f.replaceText(null, 0, 1, "");

		System.out.println(" ==== Results after delete");
		// display resulting text
		System.out.println("resultString (from structuredDocument): ");
		System.out.println(StringUtils.escape(f.getText()));
		//
		//
		// dump post change structuredDocument
		Debug.dump(f);
		// dump post change DOM
		DebugDocument.dump(tree.getDocument());

		//

	}
}
