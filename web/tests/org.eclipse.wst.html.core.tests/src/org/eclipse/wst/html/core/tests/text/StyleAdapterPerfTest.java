/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Masaki Saitoh (MSAITOH@jp.ibm.com)
 *		See Bug 153000  Style Adapters should be lazier
 *		https://bugs.eclipse.org/bugs/show_bug.cgi?id=153000
 *
 *     
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.text;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.stylesheets.LinkStyle;

import junit.framework.TestCase;

public class StyleAdapterPerfTest extends TestCase {

	/**
	 * criteria for "fail", in msecs
	 */
	private static final long MAX_EXPECTED_TIME = 4000;

	public void testPerformance() {
		try {
			String filename = "testfiles/html/example04.html";
			IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit(filename, getClass().getResourceAsStream(filename), null);
			if (model instanceof IDOMModel) {
				IDOMDocument doc = ((IDOMModel) model).getDocument();
				NodeList nodes = doc.getElementsByTagName(HTML40Namespace.ElementName.STYLE);
				if (nodes != null && nodes.getLength() > 0) {
					Node node = nodes.item(0);
					if (node instanceof LinkStyle) {
						long start = System.currentTimeMillis();
						((LinkStyle) node).getSheet();
						// System.out.println("elapsed time = " + (System.currentTimeMillis() - start));
						// TODO: we should probably use something likse o.e.core.runtime.PerformanceStats
						// I picked the following fail criteria simple since before fix, the printlin reports about 7000 
						// on my computer, but with fix, reported about 1000. 
						// The appropriate time may vary greatly depending on the build/test machine. 
						// This is just to catch some large change in behavior
						long elapsedTime = System.currentTimeMillis() - start;
						if (elapsedTime > MAX_EXPECTED_TIME) {
							fail("getSheet took longer than expected the expected " + MAX_EXPECTED_TIME +"ms");
						} 
					}
				}
			}
			if (model != null)
				model.releaseFromRead();
		}
		catch (Exception e) {
		}
	}

}
