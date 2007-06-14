/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.jsdt.core.IJavaElement;

/**
 * @author childsb
 * 
 */
public class FindReferencesAction extends JsElementActionProxy {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.actions.JsElementActionProxy#getRunArgs(org.eclipse.jface.action.IAction)
	 */
	@Override
	public Object[] getRunArgs(IAction action) {
		IJavaElement elements[] = JsElementActionProxy.getJsElementsFromSelection(getCurrentSelection());
		if (elements != null && elements.length > 0) {
			return new Object[] { elements[0] };
		}
		return new Object[0];
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.actions.JsElementActionProxy#getRunArgTypes()
	 */
	@Override
	public Class[] getRunArgTypes() {
		return new Class[] { IJavaElement.class };
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		setSelection(selection);
	}
}
