/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.wst.jsdt.core.IJavaElement;
import org.w3c.dom.Node;

/**
 * @author childsb
 * 
 */
public interface IJavaWebNode {
	public IJavaElement getJavaElement();
	
	public Node getParentNode();
		
	public boolean hasChildren();
	
	
}
