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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.IJavaWebNode;

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
public class JsElementActionProxy extends SimpleJSDTActionProxy {
	/* Util method to get all the java elements in a selection */
	public static IJavaScriptElement[] getJsElementsFromSelection(ISelection selection) {
		if (selection == null) {
			return new IJavaScriptElement[0];
		}
		ArrayList elements = new ArrayList();
		if (selection instanceof IStructuredSelection) {
			Iterator itt = ((IStructuredSelection) selection).iterator();
			while (itt.hasNext()) {
				Object element = itt.next();
				if (element instanceof IJavaScriptElement) {
					elements.add(element);
				}
				if (element instanceof IJavaWebNode) {
					elements.add(((IJavaWebNode) element).getJavaElement());
				}
			}
			return (IJavaScriptElement[]) elements.toArray(new IJavaScriptElement[elements.size()]);
		}
		return new IJavaScriptElement[0];
	}
	
	
	public Object[] getRunArgs(IAction action) {
		/*
		 * Needs to return an array of IJavaElements. Since its one arg of type
		 * IJavaScriptElement[] need to put into an object array
		 */
		return new Object[] { JsElementActionProxy.getJsElementsFromSelection(getCurrentSelection()) };
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.actions.SimpleJSDTActionProxy#getRunArgTypes()
	 */
	
	public Class[] getRunArgTypes() {
		return new Class[] { (new IJavaScriptElement[0]).getClass() };
	}
}
