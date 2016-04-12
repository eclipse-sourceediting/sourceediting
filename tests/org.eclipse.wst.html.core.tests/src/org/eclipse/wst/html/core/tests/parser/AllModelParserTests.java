/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;


import junit.framework.TestSuite;


public class AllModelParserTests {

	//	public AllModelParserTests(String name) {
	//		//super(name);
	//	}

	public void holdOldTestMain() {
		runTest(new ParserTest());
		runTest(new ParserTest2());
		runTest(new ParserTest3());
		runTest(new ParserTest4());
		runTest(new UpdaterTest());
		runTest(new UpdaterTest2());
		runTest(new UpdaterTest3());
		runTest(new UpdaterTest4());
		runTest(new UpdaterTest5());
		runTest(new UpdaterTest6());
		runTest(new UpdaterTest7());
		runTest(new UpdaterTest8());
		runTest(new UpdaterTest9());
		runTest(new UpdaterTest10());
		runTest(new UpdaterTest11());
		runTest(new UpdaterTest12());
		runTest(new UpdaterTest13());
		runTest(new UpdaterTest14());
		runTest(new ElementTest());
		runTest(new ElementTest2());
		runTest(new ElementTest3());
		runTest(new TableTest());
		runTest(new TextTest());
		runTest(new TextTest2());
		runTest(new TextTest3());
		runTest(new SplitTextTest());
		runTest(new SplitTextTest2());
		runTest(new SplitTextTest3());
		runTest(new SplitTextTest4());
		runTest(new SplitTextTest5());
		runTest(new SplitTextTest6());
		runTest(new SplitTextTest7());
		runTest(new SplitTextTest8());
		runTest(new EmptyTextTest());
		runTest(new EmptyTextTest2());
		runTest(new EmptyTextTest3());
		runTest(new AttrTest());
		runTest(new AttrTest2());
		runTest(new EntityTest());
		runTest(new EntityTest2());
		runTest(new EntityTest3());
		runTest(new EntityTest4());
		runTest(new EntityTest5());
		runTest(new EntityTest6());
		runTest(new EntityTest7());
		runTest(new EntityTest8());
		runTest(new EntityTest9());
		runTest(new DocTypeTest());
		runTest(new CDATASectionTest());
		runTest(new CDATASectionTest2());
		runTest(new CDATASectionTest3());
		runTest(new PITest());
	}

	public static TestSuite getAllTests() {


		TestSuite testSuite = new TestSuite();

		testSuite.addTestSuite(ParserTest.class);
		testSuite.addTestSuite(ParserTest2.class);
		testSuite.addTestSuite(ParserTest3.class);
		testSuite.addTestSuite(ParserTest4.class);
		testSuite.addTestSuite(UpdaterTest.class);
		testSuite.addTestSuite(UpdaterTest2.class);
		testSuite.addTestSuite(UpdaterTest3.class);
		testSuite.addTestSuite(UpdaterTest4.class);
		testSuite.addTestSuite(UpdaterTest5.class);
		testSuite.addTestSuite(UpdaterTest6.class);
		testSuite.addTestSuite(UpdaterTest7.class);
		testSuite.addTestSuite(UpdaterTest8.class);
		testSuite.addTestSuite(UpdaterTest9.class);
		testSuite.addTestSuite(UpdaterTest10.class);
		testSuite.addTestSuite(UpdaterTest11.class);
		testSuite.addTestSuite(UpdaterTest12.class);
		testSuite.addTestSuite(UpdaterTest13.class);
		testSuite.addTestSuite(UpdaterTest14.class);
		testSuite.addTestSuite(ElementTest.class);
		testSuite.addTestSuite(ElementTest2.class);
		testSuite.addTestSuite(ElementTest3.class);
		testSuite.addTestSuite(TableTest.class);
		testSuite.addTestSuite(TextTest.class);
		testSuite.addTestSuite(TextTest2.class);
		testSuite.addTestSuite(TextTest3.class);
		testSuite.addTestSuite(SplitTextTest.class);
		testSuite.addTestSuite(SplitTextTest2.class);
		testSuite.addTestSuite(SplitTextTest3.class);
		testSuite.addTestSuite(SplitTextTest4.class);
		testSuite.addTestSuite(SplitTextTest5.class);
		testSuite.addTestSuite(SplitTextTest6.class);
		testSuite.addTestSuite(SplitTextTest7.class);
		testSuite.addTestSuite(SplitTextTest8.class);
		testSuite.addTestSuite(EmptyTextTest.class);
		testSuite.addTestSuite(EmptyTextTest2.class);
		testSuite.addTestSuite(EmptyTextTest3.class);
		testSuite.addTestSuite(AttrTest.class);
		testSuite.addTestSuite(AttrTest2.class);
		testSuite.addTest(new TestSuite(AttrTest4.class));
		testSuite.addTestSuite(EntityTest.class);
		testSuite.addTestSuite(EntityTest2.class);
		testSuite.addTestSuite(EntityTest3.class);
		testSuite.addTestSuite(EntityTest4.class);
		testSuite.addTestSuite(EntityTest5.class);
		testSuite.addTestSuite(EntityTest6.class);
		testSuite.addTestSuite(EntityTest7.class);
		testSuite.addTestSuite(EntityTest8.class);
		testSuite.addTestSuite(EntityTest9.class);
		testSuite.addTestSuite(DocTypeTest.class);
		testSuite.addTestSuite(CDATASectionTest.class);
		testSuite.addTestSuite(CDATASectionTest2.class);
		testSuite.addTestSuite(CDATASectionTest3.class);
		testSuite.addTestSuite(PITest.class);
		// the follow is fairly long running
		//testSuite.addTestSuite(TestExampleFiles.class);

		return testSuite;
	}



	private void runTest(ModelTest test) {
		try {
			test.testModel();
		}
		catch (Exception ex) {
			ModelTest.printException(ex);
		}
	}

}