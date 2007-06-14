package org.eclipse.wst.jsdt.web.ui.internal.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.ui.JavaUI;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;

/**
 * Hyperlink for JSP Java elements
 */
class JSDTHyperlink implements IHyperlink {
	private IJavaElement fElement;
	private IRegion fRegion;
	
	public JSDTHyperlink(IRegion region, IJavaElement element) {
		fRegion = region;
		fElement = element;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkRegion()
	 */
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#open()
	 */
	public void open() {
		try {
			IEditorPart editor = JavaUI.openInEditor(fElement);
			if (editor != null) {
				JavaUI.revealInEditor(editor, fElement);
			}
		} catch (Exception e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
	}
}
