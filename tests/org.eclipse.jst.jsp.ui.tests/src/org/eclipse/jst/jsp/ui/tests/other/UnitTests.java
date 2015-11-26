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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class UnitTests extends TestCase {

	protected IStructuredDocument fModel;
	protected IDOMModel tree;
	protected int eventCase = 1;
	protected boolean eventResult;

	public static final int GENERIC_NODES_REPLACED_EVENT_CASE = 1001;
	public static final int GENERIC_REGIONS_REPLACED_EVENT_CASE = 1002;
	public static final int GENERIC_REGION_CHANGED_EVENT_CASE = 1003;
	public static final int GENERIC_NEW_MODEL_EVENT_CASE = 1004;
	public static final int GENERIC_NO_CHANGE_EVENT_CASE = 1005;

	protected class StructuredDocumentListenerProxy implements IStructuredDocumentListener {
		public void newModel(NewDocumentEvent structuredDocumentEvent) {
			handleEvent(structuredDocumentEvent);
		}

		public void noChange(NoChangeEvent structuredDocumentEvent) {
			handleEvent(structuredDocumentEvent);
		}

		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
			handleEvent(structuredDocumentEvent);
		}

		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
			handleEvent(structuredDocumentEvent);
		}

		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
			handleEvent(structuredDocumentEvent);
		}
	}

	protected StructuredDocumentListenerProxy proxy = new StructuredDocumentListenerProxy();

	/**
	 * MinimizationTest constructor comment.
	 */
	public UnitTests(String name) {
		super(name);
	}

	protected void handleEvent(StructuredDocumentEvent structuredDocumentEvent) {
		eventResult = false;
		switch (eventCase) {
			case 1 :
				{
					if (structuredDocumentEvent instanceof NoChangeEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 2 :
				{
					if (structuredDocumentEvent instanceof NoChangeEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 3 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 4 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 5 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (3 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (3 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								eventResult = true; // ok
					}
					break;
				}
			case 6 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (0 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (2 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								if ("<B>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().item(0).getText()))
									if ("</B>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().item(1).getText())) {
										eventResult = true; // ok
									}
					}
					break;
				}
			case 7 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (0 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (1 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								eventResult = true; // ok
					}

					break;
				}
			case 8 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (0 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (2 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								//if ("<B>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().item(0).getText()))
								//if ("</B>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().item(1).getText())) {
								eventResult = true; // ok
					}
					break;
				}
			case 9 :
			case 10 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (2 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (0 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								if ("<TD>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().item(0).getText()))
									if ("</TD>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().item(1).getText()))
										eventResult = true; // ok
					}
					break;
				}
			case 11 :
			case 12 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (2 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (0 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								if ("<TD>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().item(0).getText()))
									if ("</TD>".equals(((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().item(1).getText()))
										eventResult = true; // ok
					}
					break;
				}
			case 13 :
				{
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						if (3 == ((RegionsReplacedEvent) structuredDocumentEvent).getNewRegions().size())
							if (3 == ((RegionsReplacedEvent) structuredDocumentEvent).getOldRegions().size())
								eventResult = true; // ok
					}
					break;
				}
			case 14 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						if (3 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getOldStructuredDocumentRegions().getLength())
							if (1 == ((StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent).getNewStructuredDocumentRegions().getLength())
								eventResult = true; // ok
					}
					break;
				}
			case 15 :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						RegionChangedEvent regionChangedEvent = (RegionChangedEvent) structuredDocumentEvent;
						IStructuredDocumentRegion documentRegion = ((RegionChangedEvent) structuredDocumentEvent).getStructuredDocumentRegion();
						if ("b ".equals(documentRegion.getFullText(regionChangedEvent.getRegion()))) {
							eventResult = true; // ok
						}
					}
					break;
				}
			case 16 :
				{
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						if (0 == ((RegionsReplacedEvent) structuredDocumentEvent).getOldRegions().size()) {
							if (1 == ((RegionsReplacedEvent) structuredDocumentEvent).getNewRegions().size()) {
								eventResult = true; // ok
							}
						}
					}
					break;
				}
			case 17 :
				{
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						if (1 == ((RegionsReplacedEvent) structuredDocumentEvent).getOldRegions().size()) {
							if (3 == ((RegionsReplacedEvent) structuredDocumentEvent).getNewRegions().size()) {
								eventResult = true; // ok
							}
						}
					}
					break;
				}
			case 18 :
				{
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						if (2 == ((RegionsReplacedEvent) structuredDocumentEvent).getOldRegions().size()) {
							if (2 == ((RegionsReplacedEvent) structuredDocumentEvent).getNewRegions().size()) {
								eventResult = true; // ok
							}
						}
					}
					break;
				}
			case 19 :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 20 :
			case 21 :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 22 :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 23 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 24 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 25 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 26 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			// JSP5
			case 27 :
				{
					// changed 2002, 9/12 to fit
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 28 :
				{
					// changed 2002, 9/12 to fit
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 29 :
				{
					// changed 2002, 9/12 to fit
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 30 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			// JSP6
			case 31 :
				{
					// changed 2002, 9/12 to fit
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 32 :
				{
					// changed 2002, 9/12 to fit -- this is definitely a change
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						RegionsReplacedEvent regionsReplacedEvent = (RegionsReplacedEvent) structuredDocumentEvent;
						if (1 == regionsReplacedEvent.getOldRegions().size()) {
							ITextRegion region = regionsReplacedEvent.getOldRegions().get(0);
							IStructuredDocumentRegion documentRegion = regionsReplacedEvent.getStructuredDocumentRegion();
							String text = documentRegion.getText(region);
							if ("<% aaa %>".equals(text)) {
								eventResult = true; // ok
							}

						}
					}
					else if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						StructuredDocumentRegionsReplacedEvent nodesReplacedEvent = (StructuredDocumentRegionsReplacedEvent) structuredDocumentEvent;
						IStructuredDocumentRegionList regions = nodesReplacedEvent.getNewStructuredDocumentRegions();

						if (1 == regions.getLength()) {
							IStructuredDocumentRegion region = regions.item(0);
							String text = region.getText();
							//System.out.println(text);
							if ("<c<% aaa ".equals(text)) {
								eventResult = true;
							}
						}

					}
					break;
				}
			case 33 :
				{
					// changed 2002, 9/12 to fit
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						//					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 34 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}

			case 35 :
				{
					// chanced to RegionsReplaced 2002, 9/11 (attribute value to region container)
					//					String startString = "<p><img src=\"file.gif\"><p>";
					//					String changes = "<";
					//					String expectedString = "<p><img src=\"file<.gif\"><p>";

					//				if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 36 :
				{
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 37 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 38 :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 39 :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case 40 :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}

			case GENERIC_NODES_REPLACED_EVENT_CASE :
				{
					if (structuredDocumentEvent instanceof StructuredDocumentRegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case GENERIC_REGIONS_REPLACED_EVENT_CASE :
				{
					if (structuredDocumentEvent instanceof RegionsReplacedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case GENERIC_REGION_CHANGED_EVENT_CASE :
				{
					if (structuredDocumentEvent instanceof RegionChangedEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case GENERIC_NEW_MODEL_EVENT_CASE :
				{
					if (structuredDocumentEvent instanceof NewDocumentEvent) {
						eventResult = true; // ok
					}
					break;
				}
			case GENERIC_NO_CHANGE_EVENT_CASE :
				{
					if (structuredDocumentEvent instanceof NoChangeEvent) {
						eventResult = true; // ok
					}
					break;
				}

			default :
				{
					if (structuredDocumentEvent instanceof NewDocumentEvent) {
						eventResult = true; // ok
					}
				}
		}
	}

	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {
		if (args.length == 0) {
			runAll();
		}
		else if (args.length == 1) {
			String methodToRun = args[0].trim();
			runOne(methodToRun);
		}
	}

	protected static void runAll() {
		junit.textui.TestRunner.run(suite());

	}

	public static void runOne(String methodName) {
		TestSuite testSuite = new TestSuite();
		TestCase test = new UnitTests(methodName);
		testSuite.addTest(test);
		junit.textui.TestRunner.run(testSuite);

	}

	/**
	 * Set up an XML model
	 */
	protected void setUpXML() {

		IModelManager mm = StructuredModelManager.getModelManager();
		try {
			fModel = mm.createStructuredDocumentFor("dummy.xml", (InputStream) null, null);
		}
		catch (IOException e) {
			// do nothing, since dummy
		}
		fModel.addDocumentChangedListener(proxy);

		tree = new DOMModelImpl();

		if (tree != null) {
			fModel.addDocumentChangingListener((IStructuredDocumentListener) tree);
			tree.setStructuredDocument(fModel);
		}

	}

	/**
	 * Set up a JSP model
	 */
	protected void setUpJSP() {

		IModelManager mm = StructuredModelManager.getModelManager();
		try {
			fModel = mm.createStructuredDocumentFor("dummy.jsp", (InputStream) null, null);
		}
		catch (IOException e) {
			// do nothing, since dummy
		}
		fModel = StructuredDocumentFactory.getNewStructuredDocumentInstance(new JSPSourceParser());

		fModel.addDocumentChangedListener(proxy);

		tree = new DOMModelImpl();

		if (tree != null) {
			fModel.addDocumentChangingListener((IStructuredDocumentListener) tree);
			tree.setStructuredDocument(fModel);
		}

	}

	/**
	 */
	public void simpleTest()  {

		setUpJSP();
		eventCase = 99;
		String startString = "";
		String changes = "<a></a>";
		String expectedString = "<a></a>";
		int startOfChanges = 0;
		int lengthToReplace = 0;

		//
		fModel.setText(null, startString);

		//
		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	/**
	 */
	public void simpleTest2()  {

		setUpXML();
		eventCase = 99;
		String startString = "";
		String changes = "<a></a>";
		String expectedString = "<a></a>";
		int startOfChanges = 0;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	/**
	 */
	public void simpleTest3()  {

		setUpXML();
		eventCase = 99;
		String startString = "<a></a>";
		String changes = "";
		String expectedString = "<a></a>";
		int startOfChanges = 0;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:16:02 PM)
	 * @return junit.framework.Test
	 */
	protected static Test suite() {
		return new TestSuite(UnitTests.class);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:28:59 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testDeleteBeginning()  {

		setUpXML();
		eventCase = 4;
		String expectedString = "<GHI>";

		fModel.setText(null, "<ABC><DEF><GHI>");

		fModel.replaceText(null, 0, 10, "");
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:27:31 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testDeleteEnd()  {

		setUpXML();
		eventCase = 3;
		String expectedString = "<ABC><DEF>";

		fModel.setText(null, "<ABC><DEF><GHI>");

		fModel.replaceText(null, 10, 5, "");
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);
	}

	/**
	 */
	public void testEmbedded()  {

		setUpJSP();
		eventCase = GENERIC_NEW_MODEL_EVENT_CASE;
		String startString = "<p><img src=\"file.gif\"><p>";
		String changes = "<";
		String expectedString = "<p><img src=\"file<.gif\"><p>";
		int startOfChanges = 17;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		eventCase = 35;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("part 1: text update", result);
		assertTrue("part 1: event type", eventResult);

		eventCase = 36;
		startString = expectedString;
		changes = "%";
		expectedString = "<p><img src=\"file<%.gif\"><p>";
		startOfChanges = 18;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));

		assertTrue("part 2: text update", result);
		assertTrue("part 2: event type", eventResult);

		eventCase = 37;
		startString = expectedString;
		changes = " ";
		expectedString = "<p><img src=\"file<% .gif\"><p>";
		startOfChanges = 19;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));

		assertTrue("part 3: text update", result);
		assertTrue("part 3: event type", eventResult);

		eventCase = 38;
		startString = expectedString;
		changes = "ab ";
		expectedString = "<p><img src=\"file<% ab .gif\"><p>";
		startOfChanges = 20;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));

		assertTrue("part 4: text update", result);
		assertTrue("part 4: event type", eventResult);

		eventCase = 39;
		startString = expectedString;
		changes = "%";
		expectedString = "<p><img src=\"file<% ab %.gif\"><p>";
		startOfChanges = 23;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));

		assertTrue("part 5: text update", result);
		assertTrue("part 5: event type", eventResult);

		eventCase = 40;
		startString = expectedString;
		changes = ">";
		expectedString = "<p><img src=\"file<% ab %>.gif\"><p>";
		startOfChanges = 24;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));

		assertTrue("part 6: text update", result);
		assertTrue("part 6: event type", eventResult);

	}

	/**
	 */
	public void testEmbeddedJSP2()  {

		setUpJSP();
		eventCase = GENERIC_REGIONS_REPLACED_EVENT_CASE;
		String startString = "<a >c</a>";
		String changes = "<%= b %>";
		String expectedString = "<a <%= b %>>c</a>";
		int startOfChanges = 3;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	public void testDeepEmbeddedJSP()  {

		setUpJSP();
		eventCase = GENERIC_REGIONS_REPLACED_EVENT_CASE;
		String startString = "<script><a   >c</a></script>";
		String changes = "<%= b %>";
		String expectedString = "<script><a <%= b %>  >c</a></script>";
		int startOfChanges = 11;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

		IStructuredDocumentRegion testR = fModel.getRegionAtCharacterOffset(11);
		String testText = testR.getText();
		assertTrue("text retrieve", testText.equals("<a <%= b %>  >"));
		testText = testR.getFullText();
		assertTrue("text retrieve", testText.equals("<a <%= b %>  >"));

		ITextRegionList regionList = testR.getRegions();

		ITextRegion region = regionList.get(0);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals("<"));

		region = regionList.get(1);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals("a"));
		testText = testR.getFullText(region);
		assertTrue("text retrieve", testText.equals("a "));

		region = regionList.get(2);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals("<%= b %>"));
		testText = testR.getFullText(region);
		assertTrue("text retrieve", testText.equals("<%= b %>  "));

		// ===	

		ITextRegionContainer cRegion = (ITextRegionContainer) region;

		ITextRegionList eRegions = cRegion.getRegions();
		ITextRegion eRegion = eRegions.get(0);
		testText = cRegion.getText(eRegion);
		assertTrue("text retrieve", testText.equals("<%="));
		testText = cRegion.getFullText(eRegion);
		assertTrue("text retrieve", testText.equals("<%="));

		eRegion = eRegions.get(1);
		testText = cRegion.getText(eRegion);
		assertTrue("text retrieve", testText.equals(" b "));
		testText = cRegion.getFullText(eRegion);
		assertTrue("text retrieve", testText.equals(" b "));

		eRegion = eRegions.get(2);
		testText = cRegion.getText(eRegion);
		assertTrue("text retrieve", testText.equals("%>"));
		testText = cRegion.getFullText(eRegion);
		assertTrue("text retrieve", testText.equals("%>"));

		eRegion = eRegions.get(3);
		testText = cRegion.getText(eRegion);
		assertTrue("text retrieve", testText.equals(""));
		testText = cRegion.getFullText(eRegion);
		assertTrue("text retrieve", testText.equals("  "));

		// ====

		region = regionList.get(3);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals(">"));
		testText = testR.getFullText(region);
		assertTrue("text retrieve", testText.equals(">"));

	}

	public void testDeepEmbeddedJSP2()  {

		setUpJSP();
		eventCase = GENERIC_REGIONS_REPLACED_EVENT_CASE;
		String startString = "<script><a   >c</a></script>";
		String changes = "<%= b %";
		String expectedString = "<script><a <%= b %  >c</a></script>";
		int startOfChanges = 11;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

		IStructuredDocumentRegion testR = fModel.getRegionAtCharacterOffset(11);
		String testText = testR.getText();
		assertTrue("text retrieve", testText.equals("<a <%= b %  >"));
		testText = testR.getFullText();
		assertTrue("text retrieve", testText.equals("<a <%= b %  >"));

		ITextRegionList regionList = testR.getRegions();

		ITextRegion region = regionList.get(0);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals("<"));

		region = regionList.get(1);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals("a"));
		testText = testR.getFullText(region);
		assertTrue("text retrieve", testText.equals("a "));

		region = regionList.get(2);
		testText = testR.getText(region);
		assertTrue("text retrieve", testText.equals("<%= b %  >"));
		testText = testR.getFullText(region);
		assertTrue("text retrieve", testText.equals("<%= b %  >"));

		// ===	

		ITextRegionContainer cRegion = (ITextRegionContainer) region;

		ITextRegionList eRegions = cRegion.getRegions();
		ITextRegion eRegion = eRegions.get(0);
		testText = cRegion.getText(eRegion);
		assertTrue("text retrieve", testText.equals("<%="));
		testText = cRegion.getFullText(eRegion);
		assertTrue("text retrieve", testText.equals("<%="));

		eRegion = eRegions.get(1);
		testText = cRegion.getText(eRegion);
		assertTrue("text retrieve", testText.equals(" b %  >"));
		testText = cRegion.getFullText(eRegion);
		assertTrue("text retrieve", testText.equals(" b %  >"));


		// ====	
	}

	public void testDeepEmbeddedJSP3() {
		// CMVC 245586
		// this is a test to make sure ContextRegionContainer returns what we expect
		setUpJSP();
		String startString = "<html><head><script> <%! String testvar = \"testvar\"; %> var test = <%= testvar %> </script></head></html>";
		String expectedText = "<%! String testvar = \"testvar\"; %>";
		((XMLSourceParser) fModel.getParser()).addBlockMarker(new BlockMarker("script", null, DOMRegionContext.BLOCK_TEXT, false)); //$NON-NLS-1$
		fModel.setText(null, startString);

		fModel.getRegionList();

		IStructuredDocumentRegion scriptBlockRegion = fModel.getRegionAtCharacterOffset(21);
		ITextRegionList blockRegions = scriptBlockRegion.getRegions();
		ITextRegionContainer jspDecl = (ITextRegionContainer) blockRegions.get(1);
		String fullText = jspDecl.getFullText();

		//assertTrue("ContextRegionContainer.getFullText()", fullText.equals(expectedText));
		assertEquals("ContextRegionContainer.getFullText() value incorrect: ", expectedText, fullText);
	}

	public void testJSP1()  {

		setUpJSP();
		eventCase = 23;
		String startString = "abcd<%= abc %>efgh";
		String expectedString = "abcd< %= abc %>efgh";
		String changes = " ";
		int startOfChanges = 5;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	public void testJSP2()  {

		setUpJSP();
		eventCase = 24;
		String startString = "abcd<% abc %>efgh";
		String expectedString = "abcd< % abc %>efgh";
		String changes = " ";
		int startOfChanges = 5;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	public void testJSP3()  {

		setUpJSP();
		eventCase = 25;
		String startString = "<%= abc %>";
		String expectedString = "< %= abc %>";
		String changes = " ";
		int startOfChanges = 1;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);
	}

	public void testJSP4()  {

		setUpJSP();
		eventCase = 26;
		String startString = "<% abc %>";
		String expectedString = "< % abc %>";
		String changes = " ";
		int startOfChanges = 1;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		assertTrue("text update", result);
		assertTrue("event type", eventResult);
	}

	/**
	 Give a JSP scriptlet :
	 <% aaa %>
	 When an user types "<bb>" just before
	 the JSP scriptlet :
	 (1) When '<' is typed, regionsReplaced()
	 is called for "<%".
	 (2) When 'b' is typed, nodesReplaced()
	 is called for "<<%" and " aaa ".
	 (3) When 'b' is typed again, finally,
	 nodesReplaced() is called for
	 "<b<% aaa" and "%>".
	 The end JSP IStructuredDocumentRegion should be
	 reparsed at (1), but not wait until (3).
	 
	 */
	public void testJSP5()  {

		setUpJSP();
		eventCase = GENERIC_NEW_MODEL_EVENT_CASE;
		String startString = "<% aaa %><p>";
		String changes = "<";
		String expectedString = "<<% aaa %><p>";
		int startOfChanges = 0;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		eventCase = 27;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		assertEquals("part 1: text update", expectedString, resultString);
		assertTrue("part 1: event type", eventResult);

		// step 2
		// ? does assertTrue prevent rest from executing if 'false'?
		// ans: yes
		eventCase = 28;
		startString = expectedString;
		changes = "b";
		expectedString = "<b<% aaa %><p>";
		startOfChanges = 1;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		assertEquals("part 2: text update", expectedString, resultString);
		assertTrue("part 2: event type", eventResult);

		eventCase = 29;
		startString = expectedString;
		changes = "b";
		expectedString = "<bb<% aaa %><p>";
		startOfChanges = 2;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		assertEquals("part 3: text update", expectedString, resultString);
		assertTrue("part 3: event type", eventResult);

		eventCase = 30;
		startString = expectedString;
		changes = ">";
		expectedString = "<bb><% aaa %><p>";
		startOfChanges = 3;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		assertEquals("part 4: text update", expectedString, resultString);
		assertTrue("part 4: event type", eventResult);
	}

	/**
	 Example 4)
	 
	 Given two JSP scriptlets :
	 <% aaa %><% bbb %>
	 When an user types "<cccc>" just before
	 the JSP scriptlet, watch the IStructuredDocument events
	 on each character typing.
	 You will see the next JSP IStructuredDocumentRegion is reparsed
	 (to be broken into other type of IStructuredDocumentRegion)
	 one by one on each event.
	 But it should be at once at the first event.
	 Old Component: wsa.web.pageediting
	 New Component: wsa.web.xmleditor
	 
	 
	 */
	public void testJSP6()  {
		// caution ... we're re-using event checks from JSP5

		setUpJSP();

		eventCase = GENERIC_NEW_MODEL_EVENT_CASE;
		String startString = "<% aaa %><% bbb %>";
		String changes = "<";
		String expectedString = "<<% aaa %><% bbb %>";
		int startOfChanges = 0;
		int lengthToReplace = 0;

		fModel.setText(null, startString);
		eventCase = 31;
		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));
		assertTrue("part 1: text update", result);
		assertTrue("part 1: event type", eventResult);

		eventCase = 32;
		startString = expectedString;
		changes = "c";
		expectedString = "<c<% aaa %><% bbb %>";
		startOfChanges = 1;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));
		assertTrue("part 2: text update", result);
		assertTrue("part 2: event type", eventResult);

		eventCase = 33;
		startString = expectedString;
		changes = "c";
		expectedString = "<cc<% aaa %><% bbb %>";
		startOfChanges = 2;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));
		assertTrue("part 3: text update", result);
		assertTrue("part 3: event type", eventResult);

		eventCase = 34;
		startString = expectedString;
		changes = ">";
		expectedString = "<cc><% aaa %><% bbb %>";
		startOfChanges = 3;
		lengthToReplace = 0;

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();

		result = (expectedString.equals(resultString));
		assertTrue("part 4: text update", result);
		assertTrue("part 4: event type", eventResult);

	}

	/**
	 * this test easily returns "ok" even with the no-space bug. Its mostly to
	 * "hand examine" the structuredDocument created from the initial setText.
	 */
	public void testJSPDirective()  {

		setUpJSP();
		eventCase = GENERIC_NODES_REPLACED_EVENT_CASE;
		String startString = "<%@include%>";
		String changes = "T";
		String expectedString = "<%@include%>T";
		int startOfChanges = 12;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		//  ?
		//	assertTrue(result); // && eventResult);

		assertTrue("text update", result);
		assertTrue("event type", eventResult);

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:53:08 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod10()  {

		setUpXML();
		eventCase = 10;
		String expectedString = "<TD></TD>";

		fModel.setText(null, "<TD></TD><TD></TD>");

		fModel.replaceText(null, 9, 9, "");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:56:04 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod11()  {

		setUpXML();
		eventCase = 11;
		String expectedString = "<XXX><TD></TD><XXX>";

		fModel.setText(null, "<XXX><TD></TD><TD></TD><XXX>");

		fModel.replaceText(null, 5, 9, "");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:59:39 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod12()  {

		setUpXML();
		eventCase = 12;
		String expectedString = "<XXX><TD></TD><XXX>";

		fModel.setText(null, "<XXX><TD></TD><TD></TD><XXX>");

		fModel.replaceText(null, 14, 9, "");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:00:59 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod13()  {

		setUpXML();
		eventCase = 13;
		String expectedString = "<ABC><JKL><GHI>";

		fModel.setText(null, "<ABC><DEF><GHI>");

		fModel.replaceText(null, 5, 5, "<JKL>");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:02:19 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod14()  {

		setUpXML();
		eventCase = 14;
		String expectedString = "<a>\r\r  </a>";

		fModel.setText(null, "<a>\r<b>\r  </a>");

		fModel.replaceText(null, 4, 3, "");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:03:53 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod15()  {

		setUpXML();
		eventCase = 15;
		String expectedString = "<a><b ></b></a>";

		fModel.setText(null, "<a><b></b></a>");

		fModel.replaceText(null, 5, 0, " ");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:05:27 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod16()  {

		setUpXML();
		eventCase = 16;
		String expectedString = "<a><b z></b></a>";

		fModel.setText(null, "<a><b ></b></a>");

		fModel.replaceText(null, 6, 0, "z");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:06:33 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod17()  {

		setUpXML();
		eventCase = 17;
		String expectedString = "<a><b z=\"t\"></b></a>";

		fModel.setText(null, "<a><b z></b></a>");

		fModel.replaceText(null, 6, 1, "z=\"t\"");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:09:17 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod18()  {

		setUpXML();
		eventCase = 18;
		String expectedString = "<a><c z></b></a>";

		fModel.setText(null, "<a><b z></b></a>");

		fModel.replaceText(null, 4, 3, "c z");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:10:30 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod19()  {

		eventCase = 19;
		setUpJSP();
		fModel.setText(null, "<BODY>\r<IMG src=\"<%=\r</BODY>\r");
		String expectedString = "<BODY>\r<IMG src=\"<%=Q\r</BODY>\r";

		fModel.replaceText(null, 20, 0, "Q");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:12:05 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod20()  {

		setUpJSP();

		eventCase = 20;
		String expectedString = "<STYLE TYPE=\"text/css\">\r<!--\rBODY {\r\r}-->\r</STYLE>\r";

		fModel.setText(null, "<STYLE TYPE=\"text/css\">\r<!--\r-->\r</STYLE>\r");

		fModel.replaceText(null, 28, 1, "\rBODY {\r\r}");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 11:32:23 AM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod21()  {

		setUpJSP();

		eventCase = 21;
		String expectedString = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"\">\r<HTML>\r<HEAD>\r      <META content=\"text/html; charset=iso-8859-1\" http-equiv=\"Content-Type\">\r<META name=\"GENERATOR\" content=\"IBM WebSphere Page Designer V4.1.0 for Windows\">\r<META content=\"text/css\" http-equiv=\"Content-Style-Type\">\r<TITLE></TITLE>\r<STYLE TYPE=\"text/css\">\r<!--\rBODY {\r\r}-->\r</STYLE>\r";

		fModel.setText(null, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"\">\r<HTML>\r<HEAD>\r      <META content=\"text/html; charset=iso-8859-1\" http-equiv=\"Content-Type\">\r<META name=\"GENERATOR\" content=\"IBM WebSphere Page Designer V4.1.0 for Windows\">\r<META content=\"text/css\" http-equiv=\"Content-Style-Type\">\r<TITLE></TITLE>\r<STYLE TYPE=\"text/css\">\r<!--\r-->\r</STYLE>\r");

		fModel.replaceText(null, 343, 1, "\rBODY {\r\r}");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:45:29 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethod9()  {

		setUpXML();
		eventCase = 9;
		String expectedString = "<TD></TD>";

		fModel.setText(null, "<TD></TD><TD></TD>");

		fModel.replaceText(null, 0, 9, "");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:45:29 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testMethodAttributeNameReplace1()  {

		//testEvent = 22;
		//tags = "<tagName abc=\"bsf\">";
		//testStructuredDocumentReplacement(testEvent, tags, "abcde","<tagName abcde=\"bsf\">", 9, 3);

		setUpXML();
		eventCase = 22;
		String startString = "<tagName abc=\"bsf\">";
		String expectedString = "<tagName abcde=\"bsf\">";
		String changes = "abcde";
		int startOfChanges = 9;
		int lengthToReplace = 3;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:35:41 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testModifyMiddle()  {

		setUpXML();
		eventCase = 5;
		String expectedString = "<ABC><JKL><GHI>";

		fModel.setText(null, "<ABC><DEF><GHI>");

		fModel.replaceText(null, 0, 15, "<ABC><JKL><GHI>");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:06:33 PM)
	 */
	public void testNoChange1()  {

		setUpXML();
		eventCase = 1;
		String expectedString = "<ABC><DEF><GHI>";

		fModel.setText(null, "<ABC><DEF><GHI>");

		fModel.replaceText(null, 10, 0, "");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:26:05 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testNoChange2()  {

		setUpXML();
		eventCase = 2;
		String expectedString = "<ABC><DEF><GHI>";

		fModel.setText(null, "<ABC><DEF><GHI>");

		fModel.replaceText(null, 0, 15, "<ABC><DEF><GHI>");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:38:25 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testSimilarStart()  {

		setUpXML();
		eventCase = 6;
		String expectedString = "<P><B></B><B></B></P>";

		fModel.setText(null, "<P><B></B></P>");

		fModel.replaceText(null, 3, 0, "<B></B>");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:43:29 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testSimilarStartEnd()  {

		setUpXML();
		eventCase = 8;
		String expectedString = "<P><B><B></B></B></P>";

		fModel.setText(null, "<P><B></B></P>");

		fModel.replaceText(null, 6, 0, "<B></B>");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/1/2001 6:41:46 PM)
	 * @exception SourceEditingException The exception description.
	 */
	public void testSimiliarEnd()  {

		setUpXML();
		eventCase = 7;
		String expectedString = "<P><B></B><P><P>";

		fModel.setText(null, "<P><B></B><P>");

		fModel.replaceText(null, 10, 0, "<P>");
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);
	}

	/**
	 */
	public void testTagReDetection()  {

		setUpJSP();
		// (nsd) 2003.12.10 - changed from NodesReplaced to RegionsReplaced since the start condition was changed
		// from being 3 document regions to 4 since "script" is not 'nestable'
		//eventCase = GENERIC_REGIONS_REPLACED_EVENT_CASE;
		
		// reverted 2004.04.05
		eventCase = GENERIC_NODES_REPLACED_EVENT_CASE;
//		eventCase = GENERIC_REGIONS_REPLACED_EVENT_CASE;
		String startString = "<p>test<<SCRIPT>";
		String changes = "S";
		String expectedString = "<p>test<S<SCRIPT>";
		int startOfChanges = 8;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);

	}

	/**
	 */
	public void testTagReDetection2()  {

		setUpJSP();
		eventCase = GENERIC_REGION_CHANGED_EVENT_CASE;
		String startString = "<p>test<<SCRIPT>";
		String changes = "S";
		String expectedString = "<p>test<<SCRIPTS>";
		int startOfChanges = 15;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);

	}

	/**
	 */
	public void testTagReDetection3()  {

		setUpJSP();
		eventCase = GENERIC_NODES_REPLACED_EVENT_CASE;
		String startString = "<p>test<test>";
		String changes = "%";
		String expectedString = "<p>test<%test>";
		int startOfChanges = 8;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		assertEquals(expectedString, resultString);
		assertTrue("event type", eventResult);

	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (fModel != null)
			fModel.removeDocumentChangedListener(proxy);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPageDirective()  {
		boolean verbose = false;
		setUpJSP();
		eventCase = 999; //GENERIC_NODES_REPLACED_EVENT_CASE;
		String startString = "<%@page lang=\"java\" <SCRIPT>var <% test %> String</SCRIPT>";
		String changes = "s";
		String expectedString = "<%@page lang=\"javas\" <SCRIPT>var <% test %> String</SCRIPT>";
		int startOfChanges = 18;
		int lengthToReplace = 0;

		fModel.setText(null, startString);

		if (verbose) {
			Debug.dump(fModel, true);
		}

		StructuredDocumentEvent structuredDocumentEvent = fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		String resultString = fModel.getText();

		boolean result = (expectedString.equals(resultString));

		//	assertTrue(result); // && eventResult);
		assertTrue("text update", result);
		//assertTrue("event type", eventResult);

		if (verbose) {
			System.out.println();
			System.out.println("structured document:");
			IStructuredDocument document = structuredDocumentEvent.getStructuredDocument();
			Debug.dump(document, true);
		}

		// now delete quote
		changes = "";
		expectedString = "<%@page lang=\"javas\" <SCRIPT>var <% test %> String</SCRIPT>";
		startOfChanges = 19;
		lengthToReplace = 1;
		fModel.replaceText(null, startOfChanges, lengthToReplace, changes);
		resultString = fModel.getText();
		assertTrue("text update", result);

		if (verbose) {
			System.out.println();
			System.out.println("structured document:");
			IStructuredDocument document = structuredDocumentEvent.getStructuredDocument();
			Debug.dump(document, true);
		}

	}

}
