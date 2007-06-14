/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;

/**
 * @author childsb copyright ibm 2007
 */
public class JsLabelProvider extends XMLLabelProvider {
	JavaElementLabelProvider fLabelProvider = null;
	
	@Override
	public Image getImage(Object o) {
		if (o instanceof IJavaElement) {
			return getJavaElementLabelProvider().getImage(o);
		}
		return super.getImage(o);
	}
	
	private JavaElementLabelProvider getJavaElementLabelProvider() {
		if (fLabelProvider == null) {
			fLabelProvider = new JavaElementLabelProvider();
		}
		return fLabelProvider;
	}
	
	@Override
	public String getText(Object o) {
		if (o instanceof IJavaElement) {
			return getJavaElementLabelProvider().getText(o);
		}
		return super.getText(o);
	}
}
