package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.util.List;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDNamedComponent;

/**
 * This participant takes case of renaming matches that are XSD components
 */
public class XSDComponentRenameParticipant extends XMLComponentRenameParticipant {

protected boolean initialize(Object element) {
		super.initialize(element);
		if(element instanceof XSDNamedComponent){
			if(getArguments() instanceof ComponentRenameArguments){
				matches = (List)((ComponentRenameArguments)getArguments()).getMatches().get(IXSDSearchConstants.XMLSCHEMA_NAMESPACE);
			}
			if(matches != null){
				return true;
			}
		}
		return false;
	}

	public String getName() {
		
		return "XSD component rename participant";
	}



}
