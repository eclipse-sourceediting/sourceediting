/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.IJavaWebNode;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JsJfaceNode;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author childsb
 * 
 */
public class StandardEditorActionsAction implements IObjectActionDelegate {
	private static final boolean APPEND_NEW_LINES_TO_COPY = true;
	protected static final String COPY = "org.eclipse.wst.jsdt.web.ui.copy"; //$NON-NLS-1$
	protected static final String CUT = "org.eclipse.wst.jsdt.web.ui.cut"; //$NON-NLS-1$
	protected static final String DELETE = "org.eclipse.wst.jsdt.web.ui.delete"; //$NON-NLS-1$
	private static final char NEW_LINE = '\n';
	protected static final String PASTE_AFTER = "org.eclipse.wst.jsdt.web.ui.paste.after"; //$NON-NLS-1$
	protected static final String PASTE_BEFORE = "org.eclipse.wst.jsdt.web.ui.paste.before"; //$NON-NLS-1$
	protected ISelection selection;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	private void copy(IAction action) {
		JsJfaceNode[] nodes = parseSelection();
		if (nodes == null || nodes.length == 0) {
			return;
		}
		Clipboard clipboard = null;
		StringBuffer text = new StringBuffer();
		if (StandardEditorActionsAction.APPEND_NEW_LINES_TO_COPY) {
			text.append(StandardEditorActionsAction.NEW_LINE);
		}
		try {
			for (int i = 0; i < nodes.length; i++) {
				JsJfaceNode currentNode = nodes[i];
				int start = currentNode.getStartOffset();
				int length = currentNode.getLength();
				IStructuredDocument doc = currentNode.getStructuredDocument();
				try {
					String elementText = doc.get(start, length);
					text.append(elementText);
				} catch (BadLocationException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				if (StandardEditorActionsAction.APPEND_NEW_LINES_TO_COPY) {
					text.append(StandardEditorActionsAction.NEW_LINE);
				}
				clipboard = new Clipboard(Display.getCurrent());
				clipboard.setContents(new Object[] { text.toString() }, new Transfer[] { TextTransfer.getInstance() });
			}
		} finally {
			if (clipboard != null) {
				clipboard.dispose();
			}
		}
	}
	
	private void delete(IAction action) {
		JsJfaceNode[] nodes = parseSelection();
		if (nodes == null || nodes.length == 0) {
			return;
		}
		IStructuredDocument lastDoc = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		try {
			int start;
			int length;
			for (int i = 0; i < nodes.length; i++) {
				JsJfaceNode currentNode = nodes[i];
				start = currentNode.getStartOffset();
				length = currentNode.getLength();
				IStructuredDocument doc = currentNode.getStructuredDocument();
				if (doc != lastDoc) {
					lastDoc = doc;
					if (model != null) {
						model.endRecording(action);
						model.changedModel();
						model.releaseFromEdit();
					}
					if (modelManager != null) {
						model = modelManager.getExistingModelForEdit(doc);
						model.aboutToChangeModel();
						model.beginRecording(action, "Delete JavaScript Element", "Delete JavaScript Element");
					}
				}
				doc.replaceText(action, start, length, ""); //$NON-NLS-1$
			}
			model.endRecording(action);
		} catch (Exception e) {
			System.out.println(Messages.getString("StandardEditorActionsAction.8") + e); //$NON-NLS-1$
		} finally {
			if (model != null) {
				model.changedModel();
				model.releaseFromEdit();
			}
		}
	}
	
	private JsJfaceNode[] parseSelection() {
		if (selection == null) {
			return new JsJfaceNode[0];
		}
		ArrayList elements = new ArrayList();
		if (selection instanceof IStructuredSelection) {
			Iterator itt = ((IStructuredSelection) selection).iterator();
			while (itt.hasNext()) {
				Object element = itt.next();
				if (element instanceof IJavaScriptElement) {
					elements.add(element);
				}
				if (element instanceof IJavaWebNode) {
					elements.add(element);
				}
			}
			return (JsJfaceNode[]) elements.toArray(new JsJfaceNode[elements.size()]);
		}
		return new JsJfaceNode[0];
	}
	
	private void paste(IAction action, boolean atEnd) {
		JsJfaceNode[] nodes = parseSelection();
		if (nodes == null || nodes.length == 0) {
			return;
		}
		int startOfPaste = -1;
		IStructuredDocument doc = null;
		/* Figure out where to paste the content */
		if (atEnd) {
			for (int i = 0; i < nodes.length; i++) {
				if ((nodes[i].getStartOffset() + nodes[i].getLength()) > startOfPaste) {
					startOfPaste = (nodes[i].getStartOffset() + nodes[i].getLength());
					doc = nodes[i].getStructuredDocument();
				}
			}
		} else {
			for (int i = 0; i < nodes.length; i++) {
				if ((nodes[i].getStartOffset() < startOfPaste || startOfPaste < 0)) {
					startOfPaste = nodes[i].getStartOffset();
					doc = nodes[i].getStructuredDocument();
				}
			}
		}
		Clipboard clipboard = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		try {
			clipboard = new Clipboard(Display.getCurrent());
			String pasteString = (String) clipboard.getContents(TextTransfer.getInstance());
			model = modelManager.getExistingModelForEdit(doc);
			model.aboutToChangeModel();
			model.beginRecording(action, Messages.getString("StandardEditorActionsAction.9") + (atEnd ? Messages.getString("StandardEditorActionsAction.10") : Messages.getString("StandardEditorActionsAction.11")) + Messages.getString("StandardEditorActionsAction.12"), Messages.getString("StandardEditorActionsAction.13") + (atEnd ? Messages.getString("StandardEditorActionsAction.14") : Messages.getString("StandardEditorActionsAction.15")) + Messages.getString("StandardEditorActionsAction.16")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			doc.replaceText(action, startOfPaste, 0, pasteString);
		} finally {
			if (clipboard != null) {
				clipboard.dispose();
			}
			if (model != null) {
				model.endRecording(action);
				model.changedModel();
				model.releaseFromEdit();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (action.getId().equals(StandardEditorActionsAction.CUT)) {
			copy(action);
			delete(action);
		} else if (action.getId().equals(StandardEditorActionsAction.COPY)) {
			copy(action);
		} else if (action.getId().equals(StandardEditorActionsAction.PASTE_BEFORE)) {
			paste(action, false);
		} else if (action.getId().equals(StandardEditorActionsAction.PASTE_AFTER)) {
			paste(action, true);
		} else if (action.getId().equals(StandardEditorActionsAction.DELETE)) {
			delete(action);
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
}
