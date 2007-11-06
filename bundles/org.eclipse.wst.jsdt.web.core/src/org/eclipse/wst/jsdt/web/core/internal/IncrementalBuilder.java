/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author childsb
 * 
 * Project builder-- I thought having a builder would activate the plugin, but
 * it doesn't.
 * 
 * I'm leaving it here incase we need it later.
 * 
 */
public class IncrementalBuilder extends IncrementalProjectBuilder {
	public IncrementalBuilder() {
		System.out.println("Unimplemented method:IncrementalProjectBuilder()"); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:IncrementalBuilder.build"); //$NON-NLS-1$
		return null;
	}
}
