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

import java.util.Map;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.xml.core.internal.search.ComponentSearchContributor;
import org.eclipse.wst.xml.core.internal.search.XMLComponentSearchPattern;
import org.eclipse.wst.xml.core.internal.search.XMLSearchParticipant;

public class XSDSearchParticipant extends XMLSearchParticipant {

	private static String ID = "org.eclipse.wst.xsd.search.XSDSearchParticipant";

	public XSDSearchParticipant()
	{
	  super();
      id = ID;
	}	
	
	public String[] getSupportedContentTypes()
	{
	  String[] result = { "org.eclipse.wst.xsd.core.xsdsource" };
	  return result;
	}
	
	public boolean isApplicable(SearchPattern pattern, Map searchOptions)
	{
		if(pattern instanceof XMLComponentSearchPattern ){
			XMLComponentSearchPattern componentPattern = (XMLComponentSearchPattern)pattern;
			String namespace = componentPattern.getMetaName().getNamespace();
			if(IXSDSearchConstants.XMLSCHEMA_NAMESPACE.equals(namespace)){
				return true;
			}
		}
		return false;
	}
		
	public ComponentSearchContributor getSearchContributor() {		
		return new XSDSearchContributor();
	}
}
