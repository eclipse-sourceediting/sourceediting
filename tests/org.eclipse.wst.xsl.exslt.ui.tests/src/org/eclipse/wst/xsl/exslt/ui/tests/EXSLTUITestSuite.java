/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.exslt.ui.tests;

import org.eclipse.wst.xsl.exslt.ui.internal.contentassist.test.EXSLTCommonContentAssistTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EXSLTUITestSuite extends TestSuite {
	public static Test suite() {
		return new EXSLTUITestSuite();
	}

	public EXSLTUITestSuite() {
		super("EXSLT UI Test Suite");
		addTestSuite(EXSLTCommonContentAssistTest.class);
	}
}
