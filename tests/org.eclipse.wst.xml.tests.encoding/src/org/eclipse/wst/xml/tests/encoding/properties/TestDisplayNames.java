/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding.properties;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.encoding.CommonCharsetNames;


public class TestDisplayNames extends TestCase {
	private static final boolean DEBUG = false;

	public void testCommonName() {
		String displayName = CommonCharsetNames.getDisplayString("ISO-8859-2");
		assertNotNull("display name for charset could not be retrieved", displayName);
		if (DEBUG) {
			System.out.println(displayName);
		}
	}
}
