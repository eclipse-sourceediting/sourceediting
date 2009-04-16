/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
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
