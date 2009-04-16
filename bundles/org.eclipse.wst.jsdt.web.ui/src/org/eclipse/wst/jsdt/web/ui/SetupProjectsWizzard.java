/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author childsb
 *
 */
public class SetupProjectsWizzard implements IObjectActionDelegate, IActionDelegate {
	
	Object[] fTarget;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		//throw new UnimplementedException("setActivePart(IAction action, IWorkbenchPart targetPart)");
	}

	public void run(IAction action) {
		if(fTarget==null) return;
		
		for(int i=0;i<fTarget.length;i++) {
			if(fTarget[i] instanceof IProject) {
				IProject project = (IProject)fTarget[i];
				
					if(!JsWebNature.hasNature(project)) {
						JsWebNature nature = new JsWebNature(project,null);
						try {
							nature.configure();
							
						} catch (CoreException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}else {
						JavaProject jp = (JavaProject)JavaScriptCore.create(project);
						IIncludePathEntry[] rawClasspath = null;
						try {
							rawClasspath = jp.getRawIncludepath();
						} catch (JavaScriptModelException ex1) {
							// TODO Auto-generated catch block
							ex1.printStackTrace();
						}
						
						
						/* see if project has web cp entry and if it does leave */
						for(int k = 0;rawClasspath!=null && k<rawClasspath.length;k++) {
							if(rawClasspath[k].getPath().equals(JsWebNature.VIRTUAL_BROWSER_CLASSPATH)) {
								return;
							}
						}
						JsWebNature nature = new JsWebNature(project,null);
						try {
							nature.configure();
							
						} catch (CoreException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}
					
				
			}
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
		if(selection instanceof StructuredSelection) {
			fTarget = ((StructuredSelection)selection).toArray();
		}else {
			fTarget = null;
		}
	}
	

}