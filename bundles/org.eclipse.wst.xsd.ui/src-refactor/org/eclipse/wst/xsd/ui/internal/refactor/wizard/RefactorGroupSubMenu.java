package org.eclipse.wst.xsd.ui.internal.refactor.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

public class RefactorGroupSubMenu extends CompoundContributionItem {

	RefactorActionGroup fRefactorMenuGroup;
	

	public RefactorGroupSubMenu(RefactorActionGroup refactorMenuGroup) {
		super();
		fRefactorMenuGroup = refactorMenuGroup;
	}

	public RefactorGroupSubMenu(String id) {
		super(id);
	}

	protected IContributionItem[] getContributionItems() {
		  ArrayList actionsList = new ArrayList();
		  ArrayList contribList = new ArrayList();
		  fRefactorMenuGroup.fillActions(actionsList);
	         
	        if (actionsList != null && !actionsList.isEmpty()) {
	            for (Iterator iter = actionsList.iterator(); iter.hasNext();) {
	     			IAction action = (IAction) iter.next();
	     			contribList.add(new ActionContributionItem(action));
	     		}
	        } else {
	            Action dummyAction = new Action(RefactoringWizardMessages.RefactorActionGroup_no_refactoring_available) {
	                // dummy inner class; no methods
	            };
	            dummyAction.setEnabled(false);
	            contribList.add(new ActionContributionItem(dummyAction));
	        }
	        return (IContributionItem[]) contribList.toArray(new IContributionItem[contribList.size()]);

	}

}
