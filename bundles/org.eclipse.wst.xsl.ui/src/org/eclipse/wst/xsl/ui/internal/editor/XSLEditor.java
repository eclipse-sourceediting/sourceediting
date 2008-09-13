/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class XSLEditor extends StructuredTextEditor
{
	private OverrideIndicatorManager fOverrideIndicatorManager;

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException
	{
		super.doSetInput(input);
		installOverrideIndicator();
	}
	
	public OverrideIndicatorManager getOverrideIndicatorManager()
	{
		return fOverrideIndicatorManager;
	}
	
	protected void installOverrideIndicator()
	{
		// uninstallOverrideIndicator();
		if (getEditorInput() instanceof FileEditorInput)
		{
			IAnnotationModel model = getDocumentProvider().getAnnotationModel(getEditorInput());
			IFile file = ((FileEditorInput)getEditorInput()).getFile();
			fOverrideIndicatorManager = new OverrideIndicatorManager(model, file);
		}
	}
}
