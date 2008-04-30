/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.wst.jsdt.internal.ui.util.ExceptionHandler;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JsJfaceNode;

/**
 * @author childsb
 * 
 */
public class AddJavaDocStubAction implements IObjectActionDelegate {
	private ISelection selection;
	private IWorkbenchPart targetPart;
	
	public String getDialogTitle() {
		return Messages.getString("AddJavaDocStubAction.0"); //$NON-NLS-1$
	}
	
	public Shell getShell() {
		return getSite().getShell();
	}
	
	public IWorkbenchPartSite getSite() {
		return targetPart.getSite();
	}
	
	public void run(IAction action) {
		IJavaScriptElement[] elements = JsElementActionProxy.getJsElementsFromSelection(selection);
		if (elements == null || elements.length < 1) {
			return;
		}
		IJavaScriptElement parent = elements[0].getParent();
		/* find the cu */
		while (parent != null && !(parent instanceof IJavaScriptUnit)) {
			
		}
		if (parent != null) {
			ArrayList members = new ArrayList();
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] instanceof IMember) {
					members.add(elements[i]);
				}
			}
			JsJfaceNode node[] = SimpleJSDTActionProxy.getJsJfaceNodesFromSelection(selection);
			/* only should be one node */
			run((IJavaScriptUnit) parent, (IMember[]) members.toArray(new IMember[members.size()]), node[0]);
		}
	}
	
	public void run(IJavaScriptUnit cu, IMember[] members, JsJfaceNode node) {
		try {
			AddJavaDocStubOperation op = new AddJavaDocStubOperation(members, node);
			PlatformUI.getWorkbench().getProgressService().runInUI(PlatformUI.getWorkbench().getProgressService(), new WorkbenchRunnableAdapter(op, op.getScheduleRule()), op.getScheduleRule());
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), getDialogTitle(), Messages.getString("AddJavaDocStubAction.1")); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// operation canceled
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
}
