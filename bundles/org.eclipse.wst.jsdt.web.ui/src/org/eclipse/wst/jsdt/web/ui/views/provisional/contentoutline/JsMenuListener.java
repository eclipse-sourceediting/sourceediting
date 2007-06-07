/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import java.awt.Composite;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.internal.InternalHandlerUtil;
import org.eclipse.ui.part.Page;

import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.internal.ui.JavaPlugin;
import org.eclipse.wst.jsdt.internal.ui.actions.CompositeActionGroup;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditorActionContributor;
import org.eclipse.wst.jsdt.ui.actions.CCPActionGroup;
import org.eclipse.wst.jsdt.ui.actions.GenerateActionGroup;
import org.eclipse.wst.jsdt.ui.actions.JavaSearchActionGroup;
import org.eclipse.wst.jsdt.ui.actions.OpenViewActionGroup;
import org.eclipse.wst.jsdt.ui.actions.RefactorActionGroup;
import org.eclipse.wst.jsdt.web.ui.actions.JSDTActionSetUtil;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;

/**
 * @author childsb
 *
 */
public class JsMenuListener extends XMLNodeActionManager implements IMenuListener, IReleasable {
		private XMLNodeActionManager fActionManager;
		private TreeViewer fTreeViewer;
		private CompositeActionGroup fActionGroups;
		CompilationUnitEditorActionContributor contrib;
		ISelectionProvider selectionProvider;
		
		public JsMenuListener(TreeViewer viewer) {
			super((IStructuredModel) viewer.getInput(), viewer);
			contrib = new CompilationUnitEditorActionContributor();
			
			fTreeViewer = viewer;
			
			
			
//			
//			fActionGroups= new CompositeActionGroup(new ActionGroup[] {
//					new OpenViewActionGroup(getWorkbenchSite(), getSelectionProvider()),
//					new CCPActionGroup(getWorkbenchSite()),
//					new GenerateActionGroup(getWorkbenchSite()),
//					new RefactorActionGroup(getWorkbenchSite()),
//					new JavaSearchActionGroup(getWorkbenchSite())});
		}

		private IWorkbenchSite getWorkbenchSite() {
			
			return InternalHandlerUtil.getActiveSite(fTreeViewer);
			

		}
		
		private ISelectionProvider getSelectionProvider() {
			return getWorkbenchSite().getSelectionProvider();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
		 */
		
		
		public void menuAboutToShow(IMenuManager manager) {
			
			ISelection selection = fTreeViewer.getSelection();
			if(selection instanceof TreeSelection) {
				TreeSelection tselect = (TreeSelection)selection;
				Object[] elements = tselect.toArray();
				int javaCount=0;
				for(int i=0;i<elements.length;i++) {
					if(elements[i] instanceof IJavaElement) {
						javaCount++;
					}
				}
				
				if(javaCount==elements.length && javaCount!=0) {
					org.eclipse.swt.widgets.Composite parent = fTreeViewer.getTree().getParent();
					
					System.out.println("wait......");
					/* all Java Elements */
//					
//					
//					
//					JavaPlugin.createStandardGroups(manager);
//					String[] actionSets = JSDTActionSetUtil.getAllActionSets();
//
//					IAction[] actions = JSDTActionSetUtil.getActionsFromSet(actionSets);
//					for(int i = 0;i<actions.length;i++) {
//						manager.add(actions[i]);
//					}
//					fActionGroups.setContext(new ActionContext(selection));
//					fActionGroups.fillContextMenu(manager);
//					
				}else if(javaCount==0){
					fillContextMenu(manager, selection);
				}
			}
			
		}

		public IAction[] getAllJsActions() {
			
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.ui.internal.IReleasable#release()
		 */
		public void release() {
			fTreeViewer = null;
			if (fActionManager != null) {
				fActionManager.setModel(null);
			}
		}

		
}
