/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.internal.ui.IJsGlobalScopeContainerInitializerExtension;

/**
 * @author childsb
 *
 */
public class WebProjectJsGlobalScopeUIInitializer implements IJsGlobalScopeContainerInitializerExtension{
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.internal.ui.IJsGlobalScopeContainerInitialzerExtension#getImage(org.eclipse.core.runtime.IPath, java.lang.String, org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	public ImageDescriptor getImage(IPath containerPath, String element, IJavaProject project) {
		return ImageDescriptor.createFromFile(this.getClass(),"web1.JPG"); //$NON-NLS-1$
	}
}
