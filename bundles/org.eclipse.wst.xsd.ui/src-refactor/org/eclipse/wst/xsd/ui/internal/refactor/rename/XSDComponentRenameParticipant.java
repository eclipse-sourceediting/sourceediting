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
