/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.search;

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.Match;
import org.eclipse.wst.sse.ui.internal.search.OccurrencesSearchResult;

/**
 * @author pavery
 */
public class JSPOccurrencesSearchResult extends OccurrencesSearchResult {
	
	public JSPOccurrencesSearchResult(ISearchQuery query) {
		super(query);
	}
	
	public Match[] getMatches() {
		return super.getMatches();
	}
}
