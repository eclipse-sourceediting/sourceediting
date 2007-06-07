/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;
import org.eclipse.wst.jsdt.internal.ui.JavaPlugin;
import org.eclipse.wst.jsdt.internal.ui.actions.CompositeActionGroup;
import org.eclipse.wst.jsdt.ui.actions.CCPActionGroup;
import org.eclipse.wst.jsdt.ui.actions.GenerateActionGroup;
import org.eclipse.wst.jsdt.ui.actions.JavaSearchActionGroup;
import org.eclipse.wst.jsdt.ui.actions.OpenViewActionGroup;
import org.eclipse.wst.jsdt.ui.actions.RefactorActionGroup;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;



/**
 * @author childsb
 *
 */
public class JsContentOutlineConfig extends HTMLContentOutlineConfiguration{
	
	ILabelProvider fLabelProvider = null;
	private CompositeActionGroup fActionGroups;
	private IMenuListener fMenuListener;
	
	public static final boolean USE_ADVANCED = false;
	
	
	public JsContentOutlineConfig() {
	}
	
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if(!USE_ADVANCED) return super.getLabelProvider(viewer);
		return getJavaLabelProvider();
	}

	private ILabelProvider getJavaLabelProvider() {
		if(fLabelProvider==null) {
			fLabelProvider = new JsLabelProvider();
		}
		return fLabelProvider;
	}
	public ILabelProvider getStatusLineLabelProvider(TreeViewer treeViewer) {
		if(!USE_ADVANCED) return super.getStatusLineLabelProvider(treeViewer);
		return getJavaLabelProvider();
	}


	public IMenuListener getMenuListener(TreeViewer treeViewer) {
		if(!USE_ADVANCED) return super.getMenuListener(treeViewer);
		return new JsMenuListener(treeViewer);
		
	}

}
