package org.eclipse.wst.sse.ui.tests;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class ReadOnlyToggleDelegate extends Action implements IEditorActionDelegate {

	ISelection fSelection = null;
	IEditorPart fEditor = null;

	public ReadOnlyToggleDelegate() {
	}

	public ReadOnlyToggleDelegate(String text) {
		super(text);
	}

	public ReadOnlyToggleDelegate(String text, ImageDescriptor image) {
		super(text, image);
	}

	public ReadOnlyToggleDelegate(String text, int style) {
		super(text, style);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		fEditor = targetEditor;
	}

	public void run(IAction action) {
		ITextEditor editor = (ITextEditor) fEditor.getAdapter(ITextEditor.class);
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		if (document instanceof IStructuredDocument) {
			ITextSelection selection = (ITextSelection) fSelection;
			((IStructuredDocument) document).makeReadOnly(selection.getOffset(), selection.getLength());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
		setEnabled(!selection.isEmpty());
	}

}
