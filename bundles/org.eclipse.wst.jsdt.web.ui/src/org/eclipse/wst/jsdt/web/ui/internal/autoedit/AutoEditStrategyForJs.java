/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.autoedit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.ui.text.java.JavaAutoIndentStrategy;
import org.eclipse.wst.jsdt.internal.ui.text.java.SmartSemicolonAutoEditStrategy;
import org.eclipse.wst.jsdt.internal.ui.text.javadoc.JavaDocAutoIndentStrategy;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class AutoEditStrategyForJs implements IAutoEditStrategy {
	private IAutoEditStrategy[] fStrategies;
	
	public AutoEditStrategyForJs() {
		super();
	}
	
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		IAutoEditStrategy[] strats = getAutoEditStrategies(document);
		for (int i = 0; i < strats.length; i++) {
			strats[i].customizeDocumentCommand(document, command);
		}
	}
	
	public IAutoEditStrategy[] getAutoEditStrategies(IDocument document) {
		if (fStrategies != null) {
			return fStrategies;
		}
		String partitioning = IHTMLPartitions.SCRIPT;
		fStrategies = new IAutoEditStrategy[] { new SmartSemicolonAutoEditStrategy(partitioning),
				new JavaAutoIndentStrategy(partitioning, getJavaProject(document), null), new JavaDocAutoIndentStrategy(partitioning) };
		/* new AutoEditStrategyForTabs() */
		return fStrategies;
	}
	
	private IJavaScriptProject getJavaProject(IDocument document) {
		IDOMModel model = null;
		IJavaScriptProject javaProject = null;
		try {
			model = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(document);
			String baseLocation = model.getBaseLocation();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IProject project = null;
			if (filePath.segmentCount() > 0) {
				project = root.getProject(filePath.segment(0));
			}
			if (project != null) {
				javaProject = JavaScriptCore.create(project);
			}
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return javaProject;
	}
}
