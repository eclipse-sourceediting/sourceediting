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
package org.eclipse.wst.xsd.ui.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.xml.core.internal.search.ComponentSearchContributor;
import org.eclipse.wst.xml.core.internal.search.XMLSearchPattern;

public class XSDSearchContributor extends ComponentSearchContributor  {
 
	
	protected void initializeReferences() {
		references = new HashMap();
		String ns = IXSDSearchConstants.XMLSCHEMA_NAMESPACE;

		List patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, "element", "ref"));
		patterns.add(new XMLSearchPattern( ns, "element", "substitutionGroup"));
		references.put(IXSDSearchConstants.ELEMENT_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, "restriction", "base"));
		patterns.add(new XMLSearchPattern( ns, "extension", "base"));
		patterns.add(new XMLSearchPattern( ns, "element", "type"));
		references.put(IXSDSearchConstants.COMPLEX_TYPE_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, "restriction", "base"));
		patterns.add(new XMLSearchPattern( ns, "extension", "base"));
		patterns.add(new XMLSearchPattern( ns, "attribute", "type"));
		patterns.add(new XMLSearchPattern( ns, "union", "memberTypes"));

		references.put(IXSDSearchConstants.SIMPLE_TYPE_META_NAME, patterns);

	}

	protected void initializeDeclarations(){
		
		declarations = new HashMap();
		String ns = IXSDSearchConstants.XMLSCHEMA_NAMESPACE;

		SearchPattern pattern = new XMLSearchPattern( ns, "element", "name");
		declarations.put(IXSDSearchConstants.ELEMENT_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, "complexType", "name");
		declarations.put(IXSDSearchConstants.COMPLEX_TYPE_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, "simpleType", "name");
		declarations.put(IXSDSearchConstants.SIMPLE_TYPE_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, "attribute", "name");
		declarations.put(IXSDSearchConstants.ATTRIBUTE_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, "attributeGroup", "name");
		declarations.put(IXSDSearchConstants.ATTRIBUTE_GROUP_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, "group", "name");
		declarations.put(IXSDSearchConstants.GROUP_META_NAME, pattern);

	}

	protected void initializeSupportedNamespaces() {
		namespaces = new String[]{ IXSDSearchConstants.XMLSCHEMA_NAMESPACE};
	}


}
