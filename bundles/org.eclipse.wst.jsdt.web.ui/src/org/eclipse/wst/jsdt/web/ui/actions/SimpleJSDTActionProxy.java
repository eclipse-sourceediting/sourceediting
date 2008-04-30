/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JsJfaceNode;

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
public class SimpleJSDTActionProxy implements IObjectActionDelegate {
	public static Object executeMethod(Object handler, String methodName, Class[] paramaterTypes, Object[] paramaterValues) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class target = handler.getClass();
		Method m = target.getMethod(methodName, paramaterTypes);
		return m.invoke(handler, paramaterValues);
	}
	
	public static JsJfaceNode[] getJsJfaceNodesFromSelection(ISelection selection) {
		if (selection == null) {
			return new JsJfaceNode[0];
		}
		ArrayList elements = new ArrayList();
		if (selection instanceof IStructuredSelection) {
			Iterator itt = ((IStructuredSelection) selection).iterator();
			while (itt.hasNext()) {
				Object element = itt.next();
				if (element instanceof JsJfaceNode) {
					elements.add(element);
				}
			}
			return (JsJfaceNode[]) elements.toArray(new JsJfaceNode[elements.size()]);
		}
		return new JsJfaceNode[0];
	}
	protected Object actionHandlerTarget;
	private ISelection currentSelection;
	protected IAction handlerTargetAction;
	protected IWorkbenchPart targetWorkbenchPart;
	
	public SimpleJSDTActionProxy() {}
	
	/*
	 * Most handlers look for WorkBenchSite, so we convert the WorkBenchPart to
	 * WorkBenchSite for init
	 */
	protected Object getActionHandler(IAction action) throws IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			InvocationTargetException {
		/* Target class is cached */
		if (handlerTargetAction == action && actionHandlerTarget != null) {
			return actionHandlerTarget;
		}
		Class target = null;
		String className = getActionHandlerClassName(action);
		try {
			target = Class.forName(className);
		} catch (ClassNotFoundException ex) {
			System.out.println("Error.. Class target of action handler not found: " + action);
			System.out.println(Messages.getString("SimpleJSDTActionProxy.0") + ex); //$NON-NLS-1$
		}
		// use the loaded class
		Class[] types = new Class[] { IWorkbenchSite.class };
		Constructor cons = target.getConstructor(types);
		Object[] args = new Object[] { targetWorkbenchPart.getSite() };
		
		actionHandlerTarget = cons.newInstance(args);
		handlerTargetAction = action;
		return actionHandlerTarget;
	}
	
	/*
	 * Default way of determining the action handlers class name.. may be
	 * subclassed/overridden
	 */
	protected String getActionHandlerClassName(IAction action) {
		return action.getId();
	}
	
	public ISelection getCurrentSelection() {
		return currentSelection;
	}
	
	public Object[] getRunArgs(IAction action) {
		return new Object[] { getCurrentSelection() };
	}
	
	public Class[] getRunArgTypes() {
		return new Class[] { IStructuredSelection.class };
	}
	
	public Object[] getSelectionChangeArgs(IAction action, ISelection selection) {
		return new Object[] { getCurrentSelection() };
	}
	
	public Class[] getSelectionChangeArgTypes() {
		return new Class[] { IStructuredSelection.class };
	}
	
	/*
	 * Generic reflection util method to execute a named method with unknown
	 * paramaters on an object
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			Object handler = getActionHandler(action);
			SimpleJSDTActionProxy.executeMethod(handler, "run", getRunArgTypes(), getRunArgs(action)); //$NON-NLS-1$
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
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		currentSelection = selection;
		if(targetWorkbenchPart==null) return;
		
		try {
			Object handler = getActionHandler(action);
			SimpleJSDTActionProxy.executeMethod(handler, "selectionChanged", getSelectionChangeArgTypes(), getSelectionChangeArgs(action, selection)); //$NON-NLS-1$
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
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetWorkbenchPart = targetPart;
	}
	
	protected void setSelection(ISelection selection) {
		this.currentSelection = selection;
	}
}
