/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.eclipse.wst.sse.core.text.IStructuredDocument;

/**
 * @author nsd
 */
public class OffsetStatusLineContributionItem extends StatusLineContributionItem {

	class ShowPartitionAction extends Action {
		public ShowPartitionAction() {
			super();
		}

		public void run() {
			/**
			 * TODO: Provide a more useful control, maybe a table where the
			 * selection shows you the partition's text in a StyledText pane
			 * beneath it.
			 */
			super.run();
			ISelection sel = fTextEditor.getSelectionProvider().getSelection();
			ITextSelection textSelection = (ITextSelection) sel;
			IDocument document = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput());
			try {
				ITypedRegion[] partitions = TextUtilities.computePartitioning(document, IStructuredDocument.DEFAULT_STRUCTURED_PARTITIONING, textSelection.getOffset(), textSelection.getLength(), false);
				StringBuffer s = new StringBuffer();

				NumberFormat formatter = NumberFormat.getIntegerInstance();
				formatter.setMinimumIntegerDigits(1 + (int) (Math.log(document.getLength()) / Math.log(10)));

				for (int i = 0; i < partitions.length; i++) {
					s.append(formatter.format(partitions[i].getOffset()) + "-" + formatter.format(partitions[i].getOffset() + partitions[i].getLength()) + " - " + partitions[i].getType() + "\n"); //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
				}
				MessageDialog.openInformation(((Control) fTextEditor.getAdapter(Control.class)).getShell(), "Partitions", s.toString());
			}
			catch (BadLocationException e) {
				StringWriter s = new StringWriter();
				e.printStackTrace(new PrintWriter(s));
				MessageDialog.openError(((Control) fTextEditor.getAdapter(Control.class)).getShell(), "Partition Type", s.toString());
			}
		}
	}

	ITextEditor fTextEditor = null;
	IAction fShowPartitionAction = new ShowPartitionAction();

	/**
	 * @param id
	 */
	public OffsetStatusLineContributionItem(String id) {
		super(id);
	}

	/**
	 * @param id
	 * @param visible
	 * @param widthInChars
	 */
	public OffsetStatusLineContributionItem(String id, boolean visible, int widthInChars) {
		super(id, visible, widthInChars);
	}

	public void setActiveEditor(ITextEditor textEditor) {
		fTextEditor = textEditor;
		setActionHandler(fShowPartitionAction);
	}
}
