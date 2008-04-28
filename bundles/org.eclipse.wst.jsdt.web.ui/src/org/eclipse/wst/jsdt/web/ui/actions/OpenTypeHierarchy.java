/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;

/**
 * @author childsb
 * 
 */
public class OpenTypeHierarchy extends JsElementActionProxy {
	
	public void selectionChanged(IAction action, ISelection selection) {
		IJavaScriptElement[] elements = JsElementActionProxy.getJsElementsFromSelection(selection);
		/* Open call hierarchy needs to be disabled for TYPEs */
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].getElementType() != IJavaScriptElement.TYPE) {
				action.setEnabled(false);
				return;
			}
		}
		super.selectionChanged(action, selection);
	}
}
