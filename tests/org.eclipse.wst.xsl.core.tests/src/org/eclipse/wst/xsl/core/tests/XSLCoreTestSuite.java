/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.tests;


import org.eclipse.wst.xsl.core.internal.utils.tests.TestXMLContentType;
import org.eclipse.wst.xsl.internal.core.tests.TestIncludedTemplates;
import org.eclipse.wst.xsl.internal.core.tests.TestStructuredTextPartitionerForXSL;
import org.eclipse.wst.xsl.internal.core.tests.TestXSLCore;
import org.eclipse.wst.xsl.internal.model.tests.TestStylesheet;
import org.eclipse.wst.xsl.internal.model.tests.TestStylesheetModel;

import junit.framework.Test;
import junit.framework.TestSuite;


public class XSLCoreTestSuite extends TestSuite {
	public static Test suite() {
		return new XSLCoreTestSuite();
	}

	public XSLCoreTestSuite() {
		super("XSL Core Test Suite");
		addTestSuite(TestIncludedTemplates.class);
		addTestSuite(TestXSLCore.class);
		addTestSuite(TestStylesheet.class);
		addTestSuite(TestStylesheetModel.class);
		addTestSuite(TestXMLContentType.class);
		addTestSuite(TestStructuredTextPartitionerForXSL.class);
	}
}
