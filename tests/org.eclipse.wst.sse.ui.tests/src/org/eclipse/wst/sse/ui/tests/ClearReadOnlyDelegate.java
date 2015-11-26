package org.eclipse.wst.sse.ui.tests;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class ClearReadOnlyDelegate extends Action implements IEditorActionDelegate {

	ISelection fSelection = null;
	IEditorPart fEditor = null;

	public ClearReadOnlyDelegate() {
	}

	public ClearReadOnlyDelegate(String text) {
		super(text);
	}

	public ClearReadOnlyDelegate(String text, ImageDescriptor image) {
		super(text, image);
	}

	public ClearReadOnlyDelegate(String text, int style) {
		super(text, style);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		fEditor = targetEditor;
	}

	public void run(IAction action) {
		ITextEditor editor = (ITextEditor) fEditor.getAdapter(ITextEditor.class);
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		if (document instanceof IStructuredDocument) {
			((IStructuredDocument) document).clearReadOnly(0, document.getLength());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

}
