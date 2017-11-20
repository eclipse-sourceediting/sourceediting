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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({TestIncludedTemplates.class, TestXSLCore.class, TestStylesheet.class, TestStylesheetModel.class, TestXMLContentType.class,
	 TestStructuredTextPartitionerForXSL.class})
public class XSLCoreTestSuite {
	
}
