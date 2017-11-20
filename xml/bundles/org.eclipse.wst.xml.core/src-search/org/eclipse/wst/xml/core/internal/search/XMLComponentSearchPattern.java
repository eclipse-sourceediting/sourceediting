/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.search;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.core.search.SearchParticipant;
import org.eclipse.wst.common.core.search.pattern.ComponentSearchPattern;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;

/**
 * XML component search pattern is a composite pattern that combines XML element pattern.
 *
 */
public class XMLComponentSearchPattern extends ComponentSearchPattern {
	
	/**
	 * Containing element patterns
	 * @deprecated
	 */
	XMLSearchPattern[] children = new XMLSearchPattern[0];
	
	/**
	 * A map of XMLSearchPattern[] by {@link SearchParticipant search participant}.
	 */
	protected Map childPatternsByParticipant = new HashMap();
	
	public XMLComponentSearchPattern(IFile file, QualifiedName elementQName, QualifiedName typeQName, int matchRule) {
		super(file, elementQName, typeQName, matchRule);
		
	}


	public XMLComponentSearchPattern(IFile file, QualifiedName elementQName, QualifiedName typeQName) {
		super(file, elementQName, typeQName);
		
	}

	/**
	 * @deprecated Use {@link #getChildren(SearchParticipant)} instead.
	 */
	public XMLSearchPattern[] getChildren(){
		return children;
	}
	
    /**
     * @deprecated Use {@link #addChildren(SearchParticipant, XMLSearchPattern[])} instead.
     */
	public void setChildren(XMLSearchPattern[] patterns){
		children = patterns;
		
	}
	
	/**
	 * Provides the child patterns contributed by the given search participant. 
	 * @param searchParticipant the {@link SearchParticipant search participant} that contributed the patterns.
     * @see XMLComponentSearchPattern#addChildren(SearchParticipant, XMLSearchPattern[]) addChildren
	 * @return an array with the {@link XMLSearchPattern patterns} contributed by the {@link SearchParticipant search participant}.
	 */
	public XMLSearchPattern[] getChildren(SearchParticipant searchParticipant){
	  XMLSearchPattern[] childPatterns = getChildren();
	  if (searchParticipant != null){
	    childPatterns = (XMLSearchPattern[])childPatternsByParticipant.get(searchParticipant);
	    if (childPatterns == null){
	      childPatterns = getChildren();
	    }
	  }
	  return childPatterns;
	}

	/**
	 * Saves the child patterns contributed by the given search participant.
	 * @param searchParticipant the {@link SearchParticipant search participant} that is contributing the child search patterns.
	 * @param childPatterns the child patterns contributed by the {@link SearchParticipant searchParticipant search participant}.
	 * @see XMLComponentSearchPattern#getChildren(SearchParticipant) getChildren
	 */
	public void addChildren(SearchParticipant searchParticipant, XMLSearchPattern[] childPatterns){
	  if (searchParticipant != null && childPatterns != null) {
	    childPatternsByParticipant.put(searchParticipant, childPatterns);
	  }
	  else {
	    children = childPatterns;
	  }
	}
}
