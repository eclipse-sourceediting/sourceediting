/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.dtd.core.internal.DTDCorePlugin;


public class VerifyPlugin extends TestCase {

	public void testPluginExists() {
		// TODO: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=81527
		assertTrue(true);
		Object plugin = null;
		try {
			plugin = DTDCorePlugin.getPlugin();
		}
		catch (Exception e) {
			plugin = null;
		}
		assertNotNull("DTD core plugin could not be instantiated", plugin);
	}
}
