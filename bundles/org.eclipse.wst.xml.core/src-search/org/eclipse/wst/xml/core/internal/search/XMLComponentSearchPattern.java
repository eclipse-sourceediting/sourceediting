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

package org.eclipse.wst.xml.core.internal.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.core.search.pattern.ComponentSearchPattern;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;

/**
 * XML component search pattern is a composite pattern that combines XML element pattern.
 *
 */
public class XMLComponentSearchPattern extends ComponentSearchPattern {
	
	/**
	 * Containing element patterns
	 */
	XMLSearchPattern[] children = new XMLSearchPattern[0];
	

	public XMLComponentSearchPattern(IFile file, QualifiedName elementQName, QualifiedName typeQName, int matchRule) {
		super(file, elementQName, typeQName, matchRule);
		
	}


	public XMLComponentSearchPattern(IFile file, QualifiedName elementQName, QualifiedName typeQName) {
		super(file, elementQName, typeQName);
		
	}

	public XMLSearchPattern[] getChildren(){
		return children;
	}
	
	public void setChildren(XMLSearchPattern[] patterns){
		children = patterns;
		
	}

}
