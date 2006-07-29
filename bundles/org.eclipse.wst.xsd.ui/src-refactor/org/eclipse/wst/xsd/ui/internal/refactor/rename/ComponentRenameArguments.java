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
