/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.misc;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.html.core.HTMLFilesPreferenceNames;



public class TestPreferenceValues extends TestCase {
	public static final boolean DEBUG = false;

	public void testProductName() {
		IProduct product = Platform.getProduct();
		String generator = HTMLFilesPreferenceNames.GENERATOR;
		if (product == null) {
			assertTrue("WTP".equals(generator));
		} else {
			String productName = product.getName();
			if (DEBUG) {
				System.out.println("GENERATOR " + productName);
			}
			assertTrue(productName.equals(generator));
		}
	}

}