package org.eclipse.wst.xsd.ui.internal.search;

import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.xml.core.internal.search.ComponentSearchContributor;
import org.eclipse.wst.xml.core.internal.search.XMLComponentSearchPattern;
import org.eclipse.wst.xml.core.internal.search.XMLSearchParticipant;

public class XSDSearchParticipant extends XMLSearchParticipant {

	private static String ID = "org.eclipse.wst.xsd.search.XSDSearchParticipant";

	public  boolean initialize(SearchPattern pattern, String[] contentTypes){
		
		super.initialize(pattern, contentTypes);
		id = ID;
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
