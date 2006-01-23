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

import java.util.Map;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.wst.xsd.ui.internal.refactor.TextChangeManager;

public class ComponentRenameArguments extends RenameArguments {
	
	TextChangeManager changeManager;
	Map matches;
	String qualifier;

	public ComponentRenameArguments(String newName, boolean updateReferences) {
		super(newName, updateReferences);
	}

	public TextChangeManager getChangeManager() {
		return changeManager;
	}

	public void setChangeManager(TextChangeManager changeManager) {
		this.changeManager = changeManager;
	}

	public Map getMatches() {
		return matches;
	}

	public void setMatches(Map matches) {
		this.matches = matches;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

}
