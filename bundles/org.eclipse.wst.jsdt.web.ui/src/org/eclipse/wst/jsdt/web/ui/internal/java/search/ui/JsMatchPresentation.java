/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.java.search.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.jsdt.ui.search.IMatchPresentation;
import org.eclipse.wst.sse.ui.internal.search.BasicSearchLabelProvider;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author pavery
 */
public class JsMatchPresentation implements IMatchPresentation {
	/**
	 * @see org.eclipse.wst.jsdt.ui.search.IMatchPresentation#createLabelProvider()
	 */
	public ILabelProvider createLabelProvider() {
		return new BasicSearchLabelProvider();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.ui.search.IMatchPresentation#showMatch(org.eclipse.search.ui.text.Match,
	 *      int, int, boolean)
	 */
	public void showMatch(Match match, int currentOffset, int currentLength, boolean activate) throws PartInitException {
		// pa_TODO implement
		// Object obj = match.getElement();
		// show match in JSP editor
		if (activate) {
			// use show in target?
		} else {
			// just select
		}
	}
}
