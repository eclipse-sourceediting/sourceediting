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
package org.eclipse.wst.xml.tests.encoding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.tests.encoding.properties.TestCommonNames;
import org.eclipse.wst.xml.tests.encoding.properties.TestOverrides;
import org.eclipse.wst.xml.tests.encoding.read.TestCodedReader;
import org.eclipse.wst.xml.tests.encoding.read.TestCodedReaderOnGennedFiles;
import org.eclipse.wst.xml.tests.encoding.read.TestContentDescription;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypeDescriptionOnGennedFiles;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypeDetectionForXML;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypes;
import org.eclipse.wst.xml.tests.encoding.write.TestCodedWrite;
import org.eclipse.wst.xml.tests.encoding.xml.XMLEncodingTests;
import org.eclipse.wst.xml.tests.encoding.xml.XMLHeadTokenizerTester;


public class EncodingTestSuite extends TestSuite {

	// FIXME: commented out lang. spec. tests, until all migrated to org.eclipse 
	// [258343] jUnit failures after org.eclipse.core.runtime.xml content type is disabled
	private static Class[] classes = new Class[]{TestOverrides.class, TestCodedReader.class, TestCodedWrite.class, XMLEncodingTests.class, XMLHeadTokenizerTester.class, TestContentTypeDescriptionOnGennedFiles.class, TestCodedReaderOnGennedFiles.class, TestContentDescription.class, TestContentTypes.class, TestCommonNames.class, TestContentTypeDetectionForXML.class};
//	private static Class[] classes = new Class[]{TestOverrides.class, TestCodedReader.class, TestCodedWrite.class, XMLEncodingTests.class, XMLHeadTokenizerTester.class, TestContentTypeDescriptionOnGennedFiles.class, TestCodedReaderOnGennedFiles.class, TestContentDescription.class, TestContentTypes.class, TestCommonNames.class};
	//private static Class[] classes = new Class[]{TestOverrides.class, CSSEncodingTester.class, CSSHeadTokenizerTester.class, HTMLEncodingTests.class, HTMLHeadTokenizerTester.class, JSPEncodingTests.class, JSPHeadTokenizerTester.class, TestCodedReader.class, TestCodedWrite.class, XMLEncodingTests.class, XMLHeadTokenizerTester.class, XMLMalformedInputTests.class, TestContentTypeDescriptionOnGennedFiles.class, TestCodedReaderOnGennedFiles.class, TestContentTypeDetection.class, TestContentDescription.class, TestContentTypes.class, TestCommonNames.class};
	public EncodingTestSuite() {
		super("XML Encoding Test Suite");
		for (int i = 0; i < classes.length; i++) {
			addTest(new TestSuite(classes[i], classes[i].getName()));
		}
	}

	/**
	 * @param theClass
	 * @param name
	 */
	public EncodingTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	/**
	 * @param theClass
	 */
	public EncodingTestSuite(Class theClass) {
		super(theClass);
	}

	/**
	 * @param name
	 */
	public EncodingTestSuite(String name) {
		super(name);
	}

	public static Test suite() {
		return new EncodingTestSuite();
	}
}
