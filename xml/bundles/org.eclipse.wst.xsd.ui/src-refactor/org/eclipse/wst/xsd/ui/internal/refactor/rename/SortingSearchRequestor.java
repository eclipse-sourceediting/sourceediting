/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchRequestor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SortingSearchRequestor extends SearchRequestor {
	
	public static String NONAMESPACE = "nonamespace";

	
		private Map fFound;

		public SortingSearchRequestor() {
			fFound= new HashMap();
		}
		

		
		/**
		 * @return a List of {@link SearchMatch}es (sorted by namespace)
		 */
		public Map/* namespace - <SearchMatch>*/ getResults() {
			return fFound;
		}



		/* (non-Javadoc)
		 * @see org.eclipse.wst.common.core.search.internal.provisional.SearchRequestor#acceptSearchMatch(org.eclipse.wst.common.search.internal.provisional.SearchMatch)
		 */
		public void acceptSearchMatch(SearchMatch match) throws CoreException {

			
			if(match != null && match.getObject() instanceof Node){
				Node node = (Node)match.getObject();
				Element domElement = null;
				switch (node.getNodeType()) {
				case Node.ATTRIBUTE_NODE:
					domElement = ((Attr)node).getOwnerElement();
					break;
				case Node.ELEMENT_NODE:
					domElement = ((Element)node);
					break;
				default:
					break;
				}
				String namespace = domElement.getNamespaceURI();
				if(namespace == null || namespace.equals("")){
					namespace = NONAMESPACE;
				}
				List matches = getMatches(namespace);
				matches.add(match);			
			}
			
		}
		
		private List getMatches(String namespace){
			Object matches = fFound.get(namespace);
			if(!(matches instanceof List)){
				matches = new ArrayList();
				fFound.put(namespace, matches);
			}
			return (List)matches;
			
		}
		
	
}
