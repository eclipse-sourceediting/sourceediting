/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.search.matching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchRequestor;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * This class contains matching implementation.  
 * <p>
 * Subclasses of the PatternMatches should provide an implementation of the specific pattern matching.
 * </p>
 * <p>
 * Subclasses of the PatternMatches should be set as adapters for the patterns that they match.
 * </p>
 * An implementation of {@link org.eclipse.wst.common.core.search.SearchParticipant#selectDocumentLocations()} will call 
 * { @link PatternMatcher#matches() } on the adapter on pattern matcher.
 * 
 * An implementation of {@link org.eclipse.wst.common.core.search.SearchParticipant#locateMatches()} will call 
 * { @link PatternMatcher#locateMatches() } on the adapter on pattern matcher.
 *
 */
public abstract class PatternMatcher {
	
	
	/**
	 * This method does dive actual match location to the requestor if there are matches
	 */
	public abstract void locateMatches(SearchPattern pattern, IFile file, Element element, SearchRequestor requestor);

	/**
	 * This method only answers if the pattern matches element, it does not give actual match location
	 */
	public abstract boolean matches(SearchPattern pattern, Object element);
    
    protected SearchMatch createSearchMatch(IFile file, Attr attributeNode)
    {
        int start = 0;
        int length = 0;
        if(attributeNode instanceof IDOMAttr){
            IDOMAttr domAttr = (IDOMAttr)attributeNode;
            start = domAttr.getValueRegionStartOffset();
            length = domAttr.getValueRegionText().length();
        }
        SearchMatch match = new SearchMatch(attributeNode, start, length, file);
        return match;
    }
	
	protected void addMatch(SearchPattern pattern, IFile file, Attr attributeNode, SearchRequestor requestor) {
        //System.out.println("addMatch " + pattern + " " + attributeNode.getName() + "=" + attributeNode.getValue());
		if (attributeNode != null) {
				SearchMatch match = createSearchMatch(file, attributeNode);                
				if(requestor != null){
					try {
						requestor.acceptSearchMatch(match);
					} catch (CoreException e) {
						//do nothing
					}
				}
		}
	}


}
