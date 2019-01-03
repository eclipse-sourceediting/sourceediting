/*******************************************************************************
 * Copyright (c) 2009, 2017 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     David Carver (STAR) - bug 259053 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.tests.testcase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.views.ResultRunnable;

public class MockResultRunnable extends ResultRunnable {

	public MockResultRunnable(SourceViewer viewer, String results,
			IWorkbenchPartSite site) {
		super(viewer, results, site);
		// TODO Auto-generated constructor stub
	}
	
	public IDocument testCreateDocument() {
		return createDocument();
	}

}
