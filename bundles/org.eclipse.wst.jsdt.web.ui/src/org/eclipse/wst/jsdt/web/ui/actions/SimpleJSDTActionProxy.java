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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.ui.actions.ShowInNavigatorViewAction;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JsJfaceNode;

/**
 * @author childsb
 *
 */
public class SimpleJSDTActionProxy implements IObjectActionDelegate  {

	protected IWorkbenchPart targetWorkbenchPart;
	
	
	protected Object 	actionHandlerTarget;
	protected IAction 	handlerTargetAction;
	private ISelection currentSelection;
	
	public SimpleJSDTActionProxy() {}
	
	protected void setSelection(ISelection selection) {
		this.currentSelection=selection;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetWorkbenchPart = targetPart;
	}

	public Class[] getRunArgTypes() {
		return new Class[] {IStructuredSelection.class};
	}
	
	public Class[] getSelectionChangeArgTypes() {
		return new Class[] {IStructuredSelection.class};
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			Object handler = getActionHandler(action);
			executeMethod(handler, "run", getRunArgTypes(), getRunArgs(action) );
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
	
	public Object[] getRunArgs(IAction action) {
		return new Object[] {getCurrentSelection()};
	}

	public Object[] getSelectionChangeArgs(IAction action,  ISelection selection) {
		return new Object[] {getCurrentSelection()};
	}
	
	public ISelection getCurrentSelection() {
		return currentSelection;
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		currentSelection = selection;
		try {
			Object handler = getActionHandler(action);
			Class[] paramTypes = new Class[] {IStructuredSelection.class};
			Object[] params = new Object[] {selection};
			executeMethod(handler, "selectionChanged", getSelectionChangeArgTypes(), getSelectionChangeArgs(action, selection) );
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
		System.out.println("Unimplemented method:JSDTActionProxy.selectionChanged");
	}
	
	
	/* Generic reflection util method to execute a named method with unknown paramaters on an object */
	
	public static Object executeMethod(Object handler, String methodName, Class[] paramaterTypes ,Object[] paramaterValues) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class target = handler.getClass();
	
		Method m = target.getMethod(methodName, paramaterTypes);
		return m.invoke(handler, paramaterValues);
	}
	
	/* Default way of determining the action handlers class name.. may be subclassed/overridden */
	protected String getActionHandlerClassName(IAction action) {
		return action.getId();
	}
	
	/* Most handlers look for WorkBenchSite, so we convert the WorkBenchPart to WorkBenchSite for init */
	protected Object getActionHandler(IAction action) throws IllegalAccessException, 
															 NoSuchMethodException, 
															 IllegalArgumentException, 
															 InstantiationException, 
															 InvocationTargetException{
		/* Target class is cached */
		
		if(handlerTargetAction==action && actionHandlerTarget!=null) return actionHandlerTarget;
		
		Class target = null;
		String className = getActionHandlerClassName(action);
		try {
		  target = Class.forName(className);
		} catch (ClassNotFoundException ex) {
		 System.out.println("Error.. Class target of action handler not found: " + action);
		 System.out.println("Exception : " + ex);
		}
		// use the loaded class
		
		Class[] types = new Class[] { IWorkbenchSite.class };
	    Constructor cons = target.getConstructor(types);
	    Object[] args = new Object[] { targetWorkbenchPart.getSite() };
	    
	    actionHandlerTarget = cons.newInstance(args);
	    handlerTargetAction = action;
	    return actionHandlerTarget;

	}
	public static JsJfaceNode[] getJsJfaceNodesFromSelection(ISelection selection) {
		if(selection==null) return new JsJfaceNode[0];
		ArrayList elements = new ArrayList();
		
		if(selection instanceof IStructuredSelection) {
			Iterator itt = ((IStructuredSelection)selection).iterator();
			while(itt.hasNext()) {
				Object element = itt.next();
				if(element instanceof JsJfaceNode) {
					elements.add(element);
				}
				
			}
			return (JsJfaceNode[])elements.toArray(new JsJfaceNode[elements.size()]);
		}
		return new JsJfaceNode[0];
	}

}
