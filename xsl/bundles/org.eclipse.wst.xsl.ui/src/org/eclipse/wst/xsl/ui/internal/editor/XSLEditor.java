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
import org.eclipse.ui.texteditor.templates.ITemplatesPage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xsl.ui.internal.templates.XSLTemplatesPage;

/**
 * XSL specific extensions to the SSE UI that haven't yet been made
 * available to the SSE.
 * 
 * @since 1.0
 */
public class XSLEditor extends StructuredTextEditor
{
	private OverrideIndicatorManager fOverrideIndicatorManager;
	
	/**
	 * @since 1.0
	 */
	private XSLTemplatesPage fTemplatesPage;

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
	
	/** (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.StructuredTextEditor#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class required) {
		if (ITemplatesPage.class.equals(required)) {
			if (fTemplatesPage == null)
				fTemplatesPage = new XSLTemplatesPage(this);
			return fTemplatesPage;
		}

		return super.getAdapter(required);
	}
}
