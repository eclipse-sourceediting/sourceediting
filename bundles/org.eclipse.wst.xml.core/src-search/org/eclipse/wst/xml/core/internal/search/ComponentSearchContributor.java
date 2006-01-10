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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;

public abstract class ComponentSearchContributor  {

	protected HashMap declarations;

	protected HashMap references;

	protected String[] namespaces;

	public ComponentSearchContributor() {
		super();
	}

	public XMLSearchPattern getDeclarationPattern(QualifiedName componentName) {
		return (XMLSearchPattern) getDeclarations().get(componentName);
	}

	protected Map getDeclarations() {
		if (declarations == null) {
			initializeDeclarations();
		}
		return declarations;
	}

	protected Map getReferences() {
		if (references == null) {
			initializeReferences();
		}
		return references;
	}

	public XMLSearchPattern[] getReferencesPatterns(QualifiedName componentName) {
		List references = (List) getReferences().get(componentName);
		if (references != null) {
			return (XMLSearchPattern[]) references
					.toArray(new XMLSearchPattern[references.size()]);
		}
		return new XMLSearchPattern[0];
	}
	
	
	public boolean supports(QualifiedName componentName){
		return getReferencesPatterns(componentName).length > 0 ||
			getDeclarationPattern(componentName) != null;
	}

	public String[] getSupportedNamespaces() {
		return namespaces;
	}

	protected abstract void initializeDeclarations();

	protected abstract void initializeReferences();

	protected abstract void initializeSupportedNamespaces();
}
