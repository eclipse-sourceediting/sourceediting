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
package org.eclipse.jst.jsp.ui.internal.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.jsp.ui.internal.java.search.JSPFindOccurrencesProcessor;
import org.eclipse.wst.html.ui.internal.search.HTMLFindOccurrencesProcessor;
import org.eclipse.wst.sse.ui.internal.handlers.FindOccurrencesHandler;

public class JSPFindOccurrencesHandler extends FindOccurrencesHandler {
	private List fProcessors;
	
	protected List getProcessors() {
		if (fProcessors == null) {
			fProcessors = new ArrayList();
			HTMLFindOccurrencesProcessor htmlProcessor = new HTMLFindOccurrencesProcessor();
			fProcessors.add(htmlProcessor);
			JSPFindOccurrencesProcessor jspProcessor = new JSPFindOccurrencesProcessor();
			fProcessors.add(jspProcessor);
		}
		return fProcessors;
	}

	

}
