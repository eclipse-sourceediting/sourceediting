/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.java.search;

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.Match;
import org.eclipse.wst.sse.ui.internal.search.OccurrencesSearchResult;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author pavery
 */
public class JsOccurrencesSearchResult extends OccurrencesSearchResult {
	public JsOccurrencesSearchResult(ISearchQuery query) {
		super(query);
	}
	
	
	public Match[] getMatches() {
		return super.getMatches();
	}
}
