/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.correction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationUtil;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class ASTRewriteCorrectionProposalJSP extends ASTRewriteCorrectionProposal {

	public ASTRewriteCorrectionProposalJSP(String name, ICompilationUnit cu, ASTRewrite rewrite, int relevance, Image image) {
		super(name, cu, rewrite, relevance, image);
	}

	public void apply(IDocument document) {
		Change change = getChange();
		if (change instanceof DocumentChange) {
			try {
				getPreviewContent();
				JSPTranslationUtil translationUtil = new JSPTranslationUtil(document);
				TextEdit textEdit = ((TextChange) change).getEdit();
				TextEdit translatedTextEdit = translationUtil.translateTextEdit(textEdit);
				if (translatedTextEdit != null)
					translatedTextEdit.apply(document);
			}
			catch (MalformedTreeException e) {
				Logger.logException(e);
			}
			catch (BadLocationException e) {
				Logger.logException(e);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
		else
			super.apply(document);
	}
}