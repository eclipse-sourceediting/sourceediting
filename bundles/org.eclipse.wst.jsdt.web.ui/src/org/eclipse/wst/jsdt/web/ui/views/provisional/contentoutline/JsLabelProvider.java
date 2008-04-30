/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.ui.JavaScriptElementLabelProvider;

/**
 * @author childsb copyright ibm 2007
 */
public class JsLabelProvider extends XMLLabelProvider {
	JavaScriptElementLabelProvider fLabelProvider = null;
	
	
	public Image getImage(Object o) {
		if (o instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getImage(o);
		}
		return super.getImage(o);
	}
	
	private JavaScriptElementLabelProvider getJavaElementLabelProvider() {
		if (fLabelProvider == null) {
			fLabelProvider = new JavaScriptElementLabelProvider();
		}
		return fLabelProvider;
	}
	
	
	public String getText(Object o) {
		if (o instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getText(o);
		}
		return super.getText(o);
	}
}
