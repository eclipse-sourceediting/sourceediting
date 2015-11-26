package org.eclipse.wst.sse.ui.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

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

public class VerifyEditorPlugin extends TestCase {

	List colorList = new ArrayList();

	public void testPluginExists() {
		Plugin plugin = null;
		try {
			plugin = SSEUIPlugin.getDefault();

		}
		catch (Exception e) {
			plugin = null;
		}
		assertNotNull("sse editor plugin could not be instantiated", plugin);

	}

	/**
	 * The purpose of this test is just to test if we can directly create lots
	 * of RBG colors, even if display set to "256" (on Linux). And, BTW, it
	 * does not fail even with 256 colors set, at least on Linux, in dev. env.
	 * Varying all three RGB numbers, though, it is easy to run out of memory!
	 * 
	 */
	public void testColorHandles() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		assertNotNull("display could not be instantiated", display);
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				// 256 cubed runs out of memory
				// for (int k = 0; k < 256; k++) {
				colorList.add(new Color(display, new RGB(i, j, 50)));
				// }

			}

		}
		// if we get this far without error, all is ok.
		assertTrue(true);
	}
}
