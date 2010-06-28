/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.html.core.tests.parser.AttrTest;
import org.eclipse.wst.html.core.tests.parser.AttrTest2;
import org.eclipse.wst.html.core.tests.parser.CDATASectionTest;
import org.eclipse.wst.html.core.tests.parser.CDATASectionTest2;
import org.eclipse.wst.html.core.tests.parser.CDATASectionTest3;
import org.eclipse.wst.html.core.tests.parser.DocTypeTest;
import org.eclipse.wst.html.core.tests.parser.ElementTest;
import org.eclipse.wst.html.core.tests.parser.ElementTest2;
import org.eclipse.wst.html.core.tests.parser.ElementTest3;
import org.eclipse.wst.html.core.tests.parser.EmptyTextTest;
import org.eclipse.wst.html.core.tests.parser.EmptyTextTest2;
import org.eclipse.wst.html.core.tests.parser.EmptyTextTest3;
import org.eclipse.wst.html.core.tests.parser.EntityTest;
import org.eclipse.wst.html.core.tests.parser.EntityTest2;
import org.eclipse.wst.html.core.tests.parser.EntityTest3;
import org.eclipse.wst.html.core.tests.parser.EntityTest4;
import org.eclipse.wst.html.core.tests.parser.EntityTest5;
import org.eclipse.wst.html.core.tests.parser.EntityTest6;
import org.eclipse.wst.html.core.tests.parser.EntityTest7;
import org.eclipse.wst.html.core.tests.parser.EntityTest8;
import org.eclipse.wst.html.core.tests.parser.EntityTest9;
import org.eclipse.wst.html.core.tests.parser.OmissibleTest;
import org.eclipse.wst.html.core.tests.parser.PITest;
import org.eclipse.wst.html.core.tests.parser.ParserTest;
import org.eclipse.wst.html.core.tests.parser.ParserTest2;
import org.eclipse.wst.html.core.tests.parser.ParserTest3;
import org.eclipse.wst.html.core.tests.parser.ParserTest4;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest2;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest3;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest4;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest5;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest6;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest7;
import org.eclipse.wst.html.core.tests.parser.SplitTextTest8;
import org.eclipse.wst.html.core.tests.parser.TableTest;
import org.eclipse.wst.html.core.tests.parser.TextTest;
import org.eclipse.wst.html.core.tests.parser.TextTest2;
import org.eclipse.wst.html.core.tests.parser.TextTest3;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest10;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest11;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest12;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest13;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest14;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest2;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest3;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest4;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest5;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest6;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest7;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest8;
import org.eclipse.wst.html.core.tests.parser.UpdaterTest9;



public class ModelParserTests extends TestSuite {
	public static Test suite() {
		return new ModelParserTests();
	}

	public ModelParserTests() {
		super("ModelParserTests");

		addTest(new TestSuite(ParserTest.class, "ParserTest"));
		addTest(new TestSuite(ParserTest2.class, "ParserTest2"));
		addTest(new TestSuite(ParserTest3.class, "ParserTest3"));
		addTest(new TestSuite(ParserTest4.class, "ParserTest4"));
		addTest(new TestSuite(UpdaterTest.class, "UpdaterTest"));
		addTest(new TestSuite(UpdaterTest2.class, "UpdaterTest2"));
		addTest(new TestSuite(UpdaterTest3.class, "UpdaterTest3"));
		addTest(new TestSuite(UpdaterTest4.class, "UpdaterTest4"));
		addTest(new TestSuite(UpdaterTest5.class, "UpdaterTest5"));
		addTest(new TestSuite(UpdaterTest6.class, "UpdaterTest6"));
		addTest(new TestSuite(UpdaterTest7.class, "UpdaterTest7"));
		addTest(new TestSuite(UpdaterTest8.class, "UpdaterTest8"));
		addTest(new TestSuite(UpdaterTest9.class, "UpdaterTest9"));
		addTest(new TestSuite(UpdaterTest10.class, "UpdaterTest10"));
		addTest(new TestSuite(UpdaterTest11.class, "UpdaterTest11"));
		addTest(new TestSuite(UpdaterTest12.class, "UpdaterTest12"));
		addTest(new TestSuite(UpdaterTest13.class, "UpdaterTest13"));
		addTest(new TestSuite(UpdaterTest14.class, "UpdaterTest14"));
		addTest(new TestSuite(ElementTest.class, "ElementTest"));
		addTest(new TestSuite(ElementTest2.class, "ElementTest2"));
		addTest(new TestSuite(ElementTest3.class, "ElementTest3"));
		addTest(new TestSuite(TableTest.class, "TableTest"));
		addTest(new TestSuite(TextTest.class, "TextTest"));
		addTest(new TestSuite(TextTest2.class, "TextTest2"));
		addTest(new TestSuite(TextTest3.class, "TextTest3"));
		addTest(new TestSuite(SplitTextTest.class, "SplitTextTest"));
		addTest(new TestSuite(SplitTextTest2.class, "SplitTextTest2"));
		addTest(new TestSuite(SplitTextTest3.class, "SplitTextTest3"));
		addTest(new TestSuite(SplitTextTest4.class, "SplitTextTest4"));
		addTest(new TestSuite(SplitTextTest5.class, "SplitTextTest5"));
		addTest(new TestSuite(SplitTextTest6.class, "SplitTextTest6"));
		addTest(new TestSuite(SplitTextTest7.class, "SplitTextTest7"));
		addTest(new TestSuite(SplitTextTest8.class, "SplitTextTest8"));
		addTest(new TestSuite(EmptyTextTest.class, "EmptyTextTest"));
		addTest(new TestSuite(EmptyTextTest2.class, "EmptyTextTest2"));
		addTest(new TestSuite(EmptyTextTest3.class, "EmptyTextTest3"));
		addTest(new TestSuite(AttrTest.class, "AttrTest"));
		addTest(new TestSuite(AttrTest2.class, "AttrTest2"));
		addTest(new TestSuite(EntityTest.class, "EntityTest"));
		addTest(new TestSuite(EntityTest2.class, "EntityTest2"));
		addTest(new TestSuite(EntityTest3.class, "EntityTest3"));
		addTest(new TestSuite(EntityTest4.class, "EntityTest4"));
		addTest(new TestSuite(EntityTest5.class, "EntityTest5"));
		addTest(new TestSuite(EntityTest6.class, "EntityTest6"));
		addTest(new TestSuite(EntityTest7.class, "EntityTest7"));
		addTest(new TestSuite(EntityTest8.class, "EntityTest8"));
		addTest(new TestSuite(EntityTest9.class, "EntityTest9"));
		addTest(new TestSuite(DocTypeTest.class, "DocTypeTest"));
		addTest(new TestSuite(CDATASectionTest.class, "CDATASectionTest"));
		addTest(new TestSuite(CDATASectionTest2.class, "CDATASectionTest2"));
		addTest(new TestSuite(CDATASectionTest3.class, "CDATASectionTest3"));
		addTest(new TestSuite(PITest.class, "PITest"));
		addTest(new TestSuite(OmissibleTest.class, "Omissible tags test"));

	}
}
