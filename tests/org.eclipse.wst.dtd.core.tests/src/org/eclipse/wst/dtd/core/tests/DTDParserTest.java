/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.tests;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDUtil;
import org.eclipse.wst.dtd.core.tests.internal.DTDCoreTestsPlugin;

public class DTDParserTest extends TestCase {
	
	private static final String UNEXPECTED_FILE_CONTENTS = "Unexpected file contents";
	
	public void testMultipleCommentParsing() {
	    DTDUtil util = new DTDUtil();
	    URL fileURL = FileLocator.find(DTDCoreTestsPlugin.getDefault().getBundle(), new Path("resources/dtdParserTest/sample.dtd"), null);
	    util.parse(fileURL.toExternalForm());
	    DTDFile dtdFile = util.getDTDFile();
	    if(dtdFile.getDTDContent().size() == 1) {
		    Object object = dtdFile.getDTDContent().get(0);
		    if(object instanceof DTDElement) {
		    	DTDElement dtdElement = (DTDElement)object;
		    	String comment = dtdElement.getComment();
		    	assertEquals(" line one \n line two ", comment);
		    } else {
		    	fail(UNEXPECTED_FILE_CONTENTS);	
		    }
	    }else {
	    	fail(UNEXPECTED_FILE_CONTENTS);
	    }

	}

}
