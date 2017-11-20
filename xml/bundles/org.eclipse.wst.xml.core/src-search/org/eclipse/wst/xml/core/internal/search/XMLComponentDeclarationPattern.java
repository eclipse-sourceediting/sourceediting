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
import org.eclipse.wst.common.core.search.pattern.QualifiedName;

public class XMLComponentDeclarationPattern extends XMLComponentSearchPattern {

	public XMLComponentDeclarationPattern(QualifiedName elementQName, QualifiedName typeQName, int matchRule) {
		super(null, elementQName, typeQName, matchRule);
		
	}

	public XMLComponentDeclarationPattern(IFile file, QualifiedName elementQName, QualifiedName typeQName) {
		super(file, elementQName, typeQName);
	}
	
	public XMLComponentDeclarationPattern(QualifiedName elementQName, QualifiedName typeQName) {
		super(null, elementQName, typeQName);
	}

	
	
}
