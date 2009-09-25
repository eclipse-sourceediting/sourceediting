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
package org.eclipse.wst.xsl.exslt.core.tests;

import org.eclipse.wst.xsl.exslt.core.internal.resolver.tests.EXSLTResolverTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EXSLTCoreTestSuite extends TestSuite {
	public static Test suite() {
		return new EXSLTCoreTestSuite();
	}

	public EXSLTCoreTestSuite() {
		super("EXSLT Core Test Suite");
		addTestSuite(EXSLTResolverTest.class);
	}
}
