/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.sse.core.internal.validate;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.IValidatorGroupListener;
import org.eclipse.wst.validation.ValidationState;

public class ValidatorGroupListener implements IValidatorGroupListener {

	Map fDiagnosticMap = new HashMap();
	private static final boolean _debug = false;

	public ValidatorGroupListener() {
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (fDiagnosticMap != null && !fDiagnosticMap.isEmpty()) {
			Object[] paths = fDiagnosticMap.keySet().toArray();
			for (int i = 0; i < paths.length; i++) {
				Logger.log(Logger.ERROR, "Leaked model: " + paths[i]);
				validationFinishing(ResourcesPlugin.getWorkspace().getRoot().getFile((IPath) paths[i]), new NullProgressMonitor(), null);
			}
		}
	}

	public void validationFinishing(IResource resource, IProgressMonitor monitor, ValidationState state) {
		if (_debug)
			System.out.println("Finishing:" + resource.getFullPath());
		if (resource.getType() != IResource.FILE)
			return;

		IStructuredModel model = (IStructuredModel) fDiagnosticMap.remove(resource.getFullPath());
		if (model != null) {
			model.releaseFromRead();
		}
	}

	public void validationStarting(IResource resource, IProgressMonitor monitor, ValidationState state) {
		if (_debug)
			System.out.println("Starting: " + resource.getFullPath());
		try {
			if (monitor != null && !monitor.isCanceled()) {
				if (resource.getType() != IResource.FILE)
					return;

				IModelManager modelManager = StructuredModelManager.getModelManager();
				// possible when shutting down
				if (modelManager != null) {
					IStructuredModel model = modelManager.getModelForRead((IFile) resource);
					if (model != null) {
						fDiagnosticMap.put(resource.getFullPath(), model);
					}
				}
			}
		}
		catch (Exception e) {
			Logger.logException(e);
		}
	}
}
