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
package org.eclipse.jst.jsp.ui.internal.java.search.ui;

import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.internal.search.BasicSearchLabelProvider;

/**
 * @author pavery
 */
public class JSPMatchPresentation implements IMatchPresentation {

	/**
	 * @see org.eclipse.jdt.ui.search.IMatchPresentation#createLabelProvider()
	 */
	public ILabelProvider createLabelProvider() {
		return new BasicSearchLabelProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.search.IMatchPresentation#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
	 */
	public void showMatch(Match match, int currentOffset, int currentLength, boolean activate) throws PartInitException {
		// pa_TODO implement
//		Object obj = match.getElement();
		// show match in JSP editor
		if(activate) {
			// use show in target?
		}
		else {
			// just select
		}
	}
}
