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
package org.eclipse.wst.xsl.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xsl.ui.internal.editor.XSLHyperlinkDetector;

public class OpenDeclarationHandler extends AbstractHandler
{
	private XSLHyperlinkDetector detector = new XSLHyperlinkDetector();

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IEditorPart editor = HandlerUtil.getActiveEditor(event);

		ITextEditor textEditor = null;
		if (editor instanceof ITextEditor)
			textEditor = (ITextEditor) editor;
		else
		{
			Object o = editor.getAdapter(ITextEditor.class);
			if (o != null)
				textEditor = (ITextEditor) o;
		}
		if (textEditor != null)
		{
			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
			if (document != null)
			{
				// get current text selection
				ITextSelection textSelection = getCurrentSelection(textEditor);
				if (textSelection.isEmpty())
				{
					return null;
				}
				int offset = textSelection.getOffset();
				if (offset == -1)
					return null;
				IRegion region = new Region(offset, 0);
				IHyperlink[] links = detector.detectHyperlinks(document, region, true);
				if (links != null && links.length > 0)
				{
					IHyperlink link = links[0];
					link.open();
				}
			}
		}

		return null;
	}

	private ITextSelection getCurrentSelection(ITextEditor textEditor)
	{
		ISelectionProvider provider = textEditor.getSelectionProvider();
		if (provider != null)
		{
			ISelection selection = provider.getSelection();
			if (selection instanceof ITextSelection)
			{
				return (ITextSelection) selection;
			}
		}
		return TextSelection.emptySelection();
	}

}
