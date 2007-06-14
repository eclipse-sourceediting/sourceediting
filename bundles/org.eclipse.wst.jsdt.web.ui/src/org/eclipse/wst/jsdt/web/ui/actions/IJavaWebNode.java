/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import org.eclipse.wst.jsdt.core.IJavaElement;
import org.w3c.dom.Node;

/**
 * @author childsb
 * 
 */
public interface IJavaWebNode {
	public IJavaElement getJavaElement();
	
	public Node getParentNode();
}
