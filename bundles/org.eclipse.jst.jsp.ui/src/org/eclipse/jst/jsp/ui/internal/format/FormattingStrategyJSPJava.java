/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.format;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationUtil;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;


public class FormattingStrategyJSPJava extends ContextBasedFormattingStrategy {

	/** Documents to be formatted by this strategy */
	private final LinkedList fDocuments = new LinkedList();
	/** Partitions to be formatted by this strategy */
	private final LinkedList fPartitions = new LinkedList();
	JSPTranslation translation = null;
	/* Keep track of last JSP document modification to reduce formatting calls */
	private long fLastModified = -1;
	/**
	 * Creates a new java formatting strategy.
	 */
	public FormattingStrategyJSPJava() {
		super();
	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#format()
	 */
	public void format() {
		super.format();

		final IDocument document = (IDocument) fDocuments.removeFirst();
		final TypedPosition partition = (TypedPosition) fPartitions.removeFirst();

		if (document instanceof IDocumentExtension4 && ((IDocumentExtension4) document).getModificationStamp() == fLastModified)
			return;

		if (document != null && partition != null) {
			try {
				JSPTranslationUtil translationUtil = new JSPTranslationUtil(document);
				IDocument javaDoc = translationUtil.getTranslation().getJavaDocument();

				if (javaDoc != null) {
					String javaSource = javaDoc.get();
					TextEdit textEdit = formatString(CodeFormatter.K_COMPILATION_UNIT, javaSource, 0, TextUtilities.getDefaultLineDelimiter(document), getPreferences());

					TextEdit jspEdit = translationUtil.getTranslation().getJspEdit(textEdit);
					if (jspEdit != null && jspEdit.hasChildren()) {
						jspEdit.apply(document);

						if (document instanceof IDocumentExtension4) {
							fLastModified = ((IDocumentExtension4) document).getModificationStamp();
						}
					}

				}

			}
			catch (MalformedTreeException exception) {
				Logger.logException(exception);
			}
			catch (BadLocationException exception) {
				// Can only happen on concurrent document modification - log
				// and bail out
				Logger.logException(exception);
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
	 */
	public void formatterStarts(final IFormattingContext context) {
		super.formatterStarts(context);

		fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
		fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStops()
	 */
	public void formatterStops() {
		super.formatterStops();

		fPartitions.clear();
		fDocuments.clear();
	}

	public TextEdit formatString(int kind, String string, int indentationLevel, String lineSeparator, Map options) {
		return ToolFactory.createCodeFormatter(options).format(kind, string, 0, string.length(), indentationLevel, lineSeparator);
	}
}
