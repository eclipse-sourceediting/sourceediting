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
import org.eclipse.xsd.util.XSDConstants;

public class XSDSearchContributor extends ComponentSearchContributor  {
 
	
	protected void initializeReferences() {
		references = new HashMap();
		String ns = IXSDSearchConstants.XMLSCHEMA_NAMESPACE;

		List patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ELEMENT_ELEMENT_TAG, XSDConstants.REF_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ELEMENT_ELEMENT_TAG, XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE));
		references.put(IXSDSearchConstants.ELEMENT_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, XSDConstants.RESTRICTION_ELEMENT_TAG, XSDConstants.BASE_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.EXTENSION_ELEMENT_TAG, XSDConstants.BASE_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ELEMENT_ELEMENT_TAG, XSDConstants.TYPE_ATTRIBUTE));
		references.put(IXSDSearchConstants.COMPLEX_TYPE_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, XSDConstants.RESTRICTION_ELEMENT_TAG, XSDConstants.BASE_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ELEMENT_ELEMENT_TAG, XSDConstants.TYPE_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDConstants.TYPE_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.UNION_ELEMENT_TAG, XSDConstants.MEMBERTYPES_ATTRIBUTE));
		patterns.add(new XMLSearchPattern( ns, XSDConstants.LIST_ELEMENT_TAG, XSDConstants.ITEMTYPE_ATTRIBUTE));

		references.put(IXSDSearchConstants.SIMPLE_TYPE_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, XSDConstants.GROUP_ELEMENT_TAG, XSDConstants.REF_ATTRIBUTE));
		references.put(IXSDSearchConstants.GROUP_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDConstants.REF_ATTRIBUTE));
		references.put(IXSDSearchConstants.ATTRIBUTE_GROUP_META_NAME, patterns);

		patterns = new ArrayList();
		patterns.add(new XMLSearchPattern( ns, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDConstants.REF_ATTRIBUTE));
		references.put(IXSDSearchConstants.ATTRIBUTE_META_NAME, patterns);
	}

	protected void initializeDeclarations(){
		
		declarations = new HashMap();
		String ns = IXSDSearchConstants.XMLSCHEMA_NAMESPACE;

		SearchPattern pattern = new XMLSearchPattern( ns, XSDConstants.SCHEMA_ELEMENT_TAG, XSDConstants.ELEMENT_ELEMENT_TAG, XSDConstants.NAME_ATTRIBUTE);
		declarations.put(IXSDSearchConstants.ELEMENT_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDConstants.NAME_ATTRIBUTE);
		declarations.put(IXSDSearchConstants.COMPLEX_TYPE_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDConstants.NAME_ATTRIBUTE);
		declarations.put(IXSDSearchConstants.SIMPLE_TYPE_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDConstants.NAME_ATTRIBUTE);
		declarations.put(IXSDSearchConstants.ATTRIBUTE_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDConstants.NAME_ATTRIBUTE);
		declarations.put(IXSDSearchConstants.ATTRIBUTE_GROUP_META_NAME, pattern);

		pattern = new XMLSearchPattern(ns, XSDConstants.GROUP_ELEMENT_TAG, XSDConstants.NAME_ATTRIBUTE);
		declarations.put(IXSDSearchConstants.GROUP_META_NAME, pattern);

	}

	protected void initializeSupportedNamespaces() {
		namespaces = new String[]{ IXSDSearchConstants.XMLSCHEMA_NAMESPACE};
	}


}
