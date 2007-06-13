/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IMember;

import org.eclipse.wst.jsdt.internal.ui.actions.ActionMessages;
import org.eclipse.wst.jsdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.wst.jsdt.internal.ui.util.ExceptionHandler;

/**
 * @author childsb
 *
 */
public class AddJavaDocStubAction implements IObjectActionDelegate{

	private IWorkbenchPart targetPart;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:AddJavaDocStubAction.run");
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:AddJavaDocStubAction.selectionChanged");
		
	}
	
	public void run(ICompilationUnit cu, IMember[] members) {
		try {
			AddJavaDocStubOperation op= new AddJavaDocStubOperation(members);
			PlatformUI.getWorkbench().getProgressService().runInUI(
				PlatformUI.getWorkbench().getProgressService(),
				new WorkbenchRunnableAdapter(op, op.getScheduleRule()),
				op.getScheduleRule());
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), getDialogTitle(), ActionMessages.AddJavaDocStubsAction_error_actionFailed); 
		} catch (InterruptedException e) {
			// operation canceled
		}
	}
	
	public String getDialogTitle() {
		return "JavaScript Doc";
	}
	public Shell getShell() {
		return getSite().getShell();
	}
	public IWorkbenchPartSite getSite() {
		return targetPart.getSite();
	}
}
