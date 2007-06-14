/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;

/**
 * @author childsb
 * 
 */
public class JsContentOutlineConfig extends HTMLContentOutlineConfiguration {
	public static final boolean USE_ADVANCED = false;
	ILabelProvider fLabelProvider = null;
	
	public JsContentOutlineConfig() {}
	
	private ILabelProvider getJavaLabelProvider() {
		if (fLabelProvider == null) {
			fLabelProvider = new JsLabelProvider();
		}
		return fLabelProvider;
	}
	
	@Override
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (!JsContentOutlineConfig.USE_ADVANCED) {
			return super.getLabelProvider(viewer);
		}
		return getJavaLabelProvider();
	}
	
	@Override
	public IMenuListener getMenuListener(TreeViewer treeViewer) {
		// if(!USE_ADVANCED)
		// return super.getMenuListener(treeViewer);
		return new JsMenuListener(treeViewer);
	}
	
	@Override
	public ILabelProvider getStatusLineLabelProvider(TreeViewer treeViewer) {
		if (!JsContentOutlineConfig.USE_ADVANCED) {
			return super.getStatusLineLabelProvider(treeViewer);
		}
		return getJavaLabelProvider();
	}
}
