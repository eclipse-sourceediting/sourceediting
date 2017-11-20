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

import org.eclipse.wst.common.core.search.pattern.SearchPattern;

public class TargetNamespaceReferencePattern extends SearchPattern {
	
	String namespaceURI;
	
	public TargetNamespaceReferencePattern(int matchRule, String namespaceuri) {
		super(matchRule);
		namespaceURI = namespaceuri;
	}
	
	public TargetNamespaceReferencePattern(String namespaceuri) {
		super();
		namespaceURI = namespaceuri;
	}

}
