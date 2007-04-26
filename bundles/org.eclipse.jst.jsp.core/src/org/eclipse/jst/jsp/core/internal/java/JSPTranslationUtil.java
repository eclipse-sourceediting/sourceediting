/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.text.edits.CopySourceEdit;
import org.eclipse.text.edits.CopyTargetEdit;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MoveTargetEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPTranslationUtil {
	protected IDocument fDocument = null;
	protected JSPTranslationExtension fTranslation = null;

	public JSPTranslationUtil(IDocument document) {
		fDocument = document;
	}

	public TextEdit translateTextEdit(TextEdit textEdit) {
		TextEdit translatedTextEdit = null;

		int javaOffset = textEdit.getOffset();
		int jspOffset = getTranslation().getJspOffset(textEdit.getOffset());
		int length = textEdit.getLength();

		if (textEdit instanceof MultiTextEdit) {
			translatedTextEdit = new MultiTextEdit();
			TextEdit[] children = ((MultiTextEdit) textEdit).getChildren();
			for (int i = 0; i < children.length; i++) {
				TextEdit translatedChildTextEdit = translateTextEdit(children[i]);
				if (translatedChildTextEdit != null)
					((MultiTextEdit) translatedTextEdit).addChild(translatedChildTextEdit);
			}
		}
		else if (textEdit instanceof ReplaceEdit) {
			if (jspOffset == -1)
				return null;

			if (!getTranslation().javaSpansMultipleJspPartitions(javaOffset, length))
				translatedTextEdit = new ReplaceEdit(jspOffset, length, ((ReplaceEdit) textEdit).getText());
		}
		else if (textEdit instanceof InsertEdit) {
			translatedTextEdit = new InsertEdit(jspOffset, ((InsertEdit) textEdit).getText());
		}
		else if (textEdit instanceof DeleteEdit) {
			translatedTextEdit = new DeleteEdit(jspOffset, length);
			TextEdit[] children = ((DeleteEdit) textEdit).getChildren();
			for (int i = 0; i < children.length; i++) {
				TextEdit translatedChildTextEdit = translateTextEdit(children[i]);
				if (translatedChildTextEdit != null)
					((DeleteEdit) translatedTextEdit).addChild(translatedChildTextEdit);
			}
		}
		else if (textEdit instanceof CopySourceEdit) {
			translatedTextEdit = new CopySourceEdit(jspOffset, length);
			((CopySourceEdit) translatedTextEdit).setTargetEdit(((CopySourceEdit) textEdit).getTargetEdit());
			((CopySourceEdit) translatedTextEdit).setSourceModifier(((CopySourceEdit) textEdit).getSourceModifier());
		}
		else if (textEdit instanceof CopyTargetEdit) {
			translatedTextEdit = new CopyTargetEdit(jspOffset);
			((CopyTargetEdit) textEdit).getSourceEdit().setTargetEdit((CopyTargetEdit) translatedTextEdit);
		}
		else if (textEdit instanceof MoveSourceEdit) {
			translatedTextEdit = new MoveSourceEdit(jspOffset, length);
			((MoveSourceEdit) translatedTextEdit).setTargetEdit(((MoveSourceEdit) textEdit).getTargetEdit());
		}
		else if (textEdit instanceof MoveTargetEdit) {
			translatedTextEdit = new MoveTargetEdit(jspOffset);
			((MoveTargetEdit) textEdit).getSourceEdit().setTargetEdit((MoveTargetEdit) translatedTextEdit);
		}
		else {
			System.out.println("Need to translate " + textEdit); //$NON-NLS-1$
		}

		return translatedTextEdit;
	}

	public JSPTranslationExtension getTranslation() {
		if (fTranslation == null) {
			IDOMModel xmlModel = (IDOMModel) getModelManager().getExistingModelForRead(fDocument);
			ModelHandlerForJSP.ensureTranslationAdapterFactory(xmlModel);
			try {
				IDOMDocument xmlDoc = xmlModel.getDocument();

				JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				if (translationAdapter != null)
					fTranslation = translationAdapter.getJSPTranslation();
			}
			finally {
				if (xmlModel != null) {
					xmlModel.releaseFromRead();
				}
			}
		}

		return fTranslation;
	}

	public ICompilationUnit getCompilationUnit() {
		return getTranslation().getCompilationUnit();
	}

	protected IModelManager getModelManager() {
		return StructuredModelManager.getModelManager();
	}
}
