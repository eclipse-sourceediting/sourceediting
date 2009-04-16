/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.web.core.javascript.DocumentChangeListenerToTextEdit;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;

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
public class AddJavaDocStubOperation extends org.eclipse.wst.jsdt.internal.corext.codemanipulation.AddJavaDocStubOperation {
	private IDocument copy;
	/**
	 * @param members
	 */
	private JsJfaceNode node;
	private DocumentChangeListenerToTextEdit textEditListener;
	
	public AddJavaDocStubOperation(IMember[] members, JsJfaceNode node) {
		super(members);
		this.node = node;
	}
	
	protected void applyChanges() {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		IStructuredDocument doc = node.getStructuredDocument();
		try {
			MultiTextEdit edits = textEditListener.getTextEdits();
			model = modelManager.getExistingModelForEdit(doc);
			model.aboutToChangeModel();
			model.beginRecording(this, Messages.getString("AddJavaDocStubOperation.0"), Messages.getString("AddJavaDocStubOperation.1")); //$NON-NLS-1$ //$NON-NLS-2$
			edits.apply(doc);
		} catch (MalformedTreeException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (BadLocationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			if (model != null) {
				model.endRecording(this);
				model.changedModel();
				model.releaseFromEdit();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.internal.corext.codemanipulation.AddJavaDocStubOperation#getDocument(org.eclipse.wst.jsdt.core.IJavaScriptUnit,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	
	protected IDocument getDocument(IJavaScriptUnit cu, IProgressMonitor monitor) throws CoreException {
		return getJavaDocumentFromNode();
	}
	
	protected IDocument getJavaDocumentFromNode() {
		if (copy == null) {
			IJsTranslation tran = node.getTranslation();
			copy = new Document(tran.getJsText());
			textEditListener = new DocumentChangeListenerToTextEdit();
			copy.addDocumentListener(textEditListener);
		}
		return copy;
	}
	
	
	public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		super.run(monitor);
		applyChanges();
		/* need to apply the text edits back to the original doc */
	}
}
