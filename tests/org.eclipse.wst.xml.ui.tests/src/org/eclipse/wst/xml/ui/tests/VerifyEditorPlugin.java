/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * 
 ****************************************************************************/
package org.eclipse.wst.xml.ui.tests;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.xml.ui.XMLEditorPlugin;

import junit.framework.TestCase;


public class VerifyEditorPlugin extends TestCase {

	public void testPluginExists() {
		Plugin plugin = null;
		try {
			plugin = XMLEditorPlugin.getDefault();

		} catch (Exception e) {
			plugin = null;
		}
		assertNotNull("xml editor plugin could not be instantiated", plugin);

	}
}
