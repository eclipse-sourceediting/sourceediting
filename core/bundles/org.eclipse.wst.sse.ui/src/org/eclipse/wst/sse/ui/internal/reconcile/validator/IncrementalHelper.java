/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.reconcile.validator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;



public class IncrementalHelper implements IValidationContext {
	private IProject fProject;

	private String fURI = null;
	
	public IncrementalHelper(IDocument sourceDocument, IProject project) {
		super();
		fProject = project;
	}

	public String getPortableName(IResource resource) {
		return resource.getProjectRelativePath().toString();
	}

	public IProject getProject() {
		return fProject;
	}

	public Object loadModel(String symbolicName) {
		return null;
	}

	public Object loadModel(String symbolicName, Object[] parms) {
		return null;
	}

	public void setURI(String uri) {
		fURI = uri;
	}
	
	public String[] getURIs() {
		if(fURI != null)
			return new String[]{fURI};
		return new String[0];
	}
}
