package org.eclipse.jst.jsp.ui.internal.hyperlink;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;

/**
 * Hyperlink for JSP Java elements
 */
class JSPJavaHyperlink implements IHyperlink {
	private IRegion fRegion;
	private IJavaElement fElement;

	public JSPJavaHyperlink(IRegion region, IJavaElement element) {
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
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		return NLS.bind(JSPUIMessages.Open, JavaElementLabels.getElementLabel(fElement, JavaElementLabels.ALL_POST_QUALIFIED));
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
		}
		catch (Exception e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
	}
}
