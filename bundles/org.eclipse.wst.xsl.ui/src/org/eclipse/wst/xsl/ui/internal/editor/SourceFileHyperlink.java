/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xsl.core.internal.model.SourceArtifact;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

class SourceFileHyperlink implements IHyperlink
{
	private IRegion hyperLinkRegion;
	private IFile linkedFile;
	private SourceArtifact sourceArtifact;

	public SourceFileHyperlink(IRegion hyperLinkRegion, IFile linkedFile)
	{
		this.hyperLinkRegion = hyperLinkRegion;
		this.linkedFile = linkedFile;
	}

	public SourceFileHyperlink(IRegion hyperLinkRegion, IFile linkedFile, SourceArtifact sourceArtifact)
	{
		this.hyperLinkRegion = hyperLinkRegion;
		this.linkedFile = linkedFile;
		this.sourceArtifact = sourceArtifact;
	}

	public IRegion getHyperlinkRegion()
	{
		return hyperLinkRegion;
	}

	public String getTypeLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getHyperlinkText()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void open()
	{
		if (linkedFile != null && linkedFile.exists())
		{
			try
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart editor = IDE.openEditor(page, linkedFile, true);
				ITextEditor textEditor = (ITextEditor)editor.getAdapter(ITextEditor.class);
				if (sourceArtifact != null && textEditor != null)
				{
					IDocument openedDocument = (IDocument)editor.getAdapter(IDocument.class);
					if (openedDocument != null)
					{
						IRegion lineRegion = openedDocument.getLineInformation(sourceArtifact.getLineNumber()-1);
						textEditor.setHighlightRange(lineRegion.getOffset(), 0, true);
					}
				}
			}
			catch (PartInitException pie)
			{
				XSLUIPlugin.log(pie);
			}
			catch (BadLocationException e)
			{
				XSLUIPlugin.log(e);
			}
		}
	}

}
