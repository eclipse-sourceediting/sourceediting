/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.w3c.dom.Node;

/**
 * @author childsb
 * 
 */
public interface IJavaWebNode {
	public IJavaScriptElement getJavaElement();
	
	public Node getParentNode();
		
	public boolean hasChildren();
	
	
}
