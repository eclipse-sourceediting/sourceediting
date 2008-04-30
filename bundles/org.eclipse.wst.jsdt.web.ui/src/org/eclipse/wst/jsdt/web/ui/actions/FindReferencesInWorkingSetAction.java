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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;
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
public class FindReferencesInWorkingSetAction extends FindReferencesAction {
	
	public void run(IAction action) {
		try {
			Object handler = getActionHandler(action);
			IJavaScriptElement elements[] = JsElementActionProxy.getJsElementsFromSelection(getCurrentSelection());
			if (elements == null || elements.length == 0) {
				return;
			}
			SimpleJSDTActionProxy.executeMethod(handler, "run", new Class[] { IJavaScriptElement.class }, new Object[] { elements[0] }); //$NON-NLS-1$
		} catch (IllegalArgumentException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (SecurityException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			// If user doesn't select a working set an exception is thrown so we
			// wont print stack trace here
		}
	}
}
