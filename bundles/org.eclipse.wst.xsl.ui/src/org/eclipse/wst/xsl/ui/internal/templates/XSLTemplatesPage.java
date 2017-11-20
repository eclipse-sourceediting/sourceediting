/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *     David Carver (STAR) - bug 297027 - compilation error on getTemplateStore()
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.templates;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.texteditor.templates.AbstractTemplatesPage;
import org.eclipse.wst.xml.xpath.ui.internal.XPathUIPlugin;
import org.eclipse.wst.xml.xpath.ui.internal.templates.TemplateContextTypeIdsXPath;
import org.eclipse.wst.xsl.ui.internal.editor.XSLEditor;
import org.eclipse.wst.xsl.ui.provisional.contentassist.CustomCompletionProposal;

/**
 * The XSL Templates Page is used by the TemplatesViewer to display available
 * XSL Templates for use within a XSL Structured Text Editor. It is provided as
 * an example on how other SSE editors can contribute their templates to the
 * page.
 * 
 * @author dcarver
 * 
 */

public class XSLTemplatesPage extends AbstractTemplatesPage {

	private static final String PREFERENCE_PAGE_ID = "org.eclipse.wst.xsl.ui.XPath"; //$NON-NLS-1$

	private static final TemplateStore TEMPLATE_STORE = XPathUIPlugin
			.getDefault().getXPathTemplateStore();

	private static final IPreferenceStore PREFERENCE_STORE = XPathUIPlugin
			.getDefault().getPreferenceStore();

	private static final ContextTypeRegistry TEMPLATE_CONTEXT_REGISTRY = XPathUIPlugin
			.getDefault().getXPathTemplateContextRegistry();

	private XSLEditor fTextEditor;

	public XSLTemplatesPage(XSLEditor xslTextEditor) {
		super(xslTextEditor, xslTextEditor.getTextViewer());
		fTextEditor = xslTextEditor;
	}

	@Override
	protected String[] getContextTypeIds(IDocument document, int offset) {
		return new String[] { TemplateContextTypeIdsXPath.XPATH,
				TemplateContextTypeIdsXPath.AXIS,
				TemplateContextTypeIdsXPath.OPERATOR };
	}

	@Override
	protected ContextTypeRegistry getContextTypeRegistry() {
		return TEMPLATE_CONTEXT_REGISTRY;
	}

	@Override
	protected String getPreferencePageId() {
		return PREFERENCE_PAGE_ID;
	}

	@Override
	protected IPreferenceStore getTemplatePreferenceStore() {
		return PREFERENCE_STORE;
	}

	/**
	 * 
	 * @since 1.1 Changed due to platform change
	 */
	@Override
	public TemplateStore getTemplateStore() {
		return TEMPLATE_STORE;
	}

	// This code based on the JavaTemplatesPage code. Modified to work with
	// SSE

	@Override
	protected void insertTemplate(Template template, IDocument document) {
		ISourceViewer contextViewer = fTextEditor.getTextViewer();
		ITextSelection textSelection = (ITextSelection) contextViewer
				.getSelectionProvider().getSelection();

		if (!isValidTemplate(document, template, textSelection.getOffset(),
				textSelection.getLength()))
			return;

		beginCompoundChange(contextViewer);

		/*
		 * The Editor checks whether a completion for a word exists before it
		 * allows for the template to be applied. We pickup the current text at
		 * the selection position and replace it with the first char of the
		 * template name for this to succeed. Another advantage by this method
		 * is that the template replaces the selected text provided the
		 * selection by itself is not used in the template pattern.
		 */

		String savedText;

		try {
			savedText = document.get(textSelection.getOffset(), textSelection
					.getLength());

			if (savedText.length() == 0) {
				String prefix = getIdentifierPart(document, template,
						textSelection.getOffset(), textSelection.getLength());

				if (prefix.length() > 0
						&& !template.getName().startsWith(prefix)) {
					return;
				}

				if (prefix.length() > 0) {
					contextViewer.setSelectedRange(textSelection.getOffset()
							- prefix.length(), prefix.length());

					textSelection = (ITextSelection) contextViewer
							.getSelectionProvider().getSelection();
				}
			}

			document.replace(textSelection.getOffset(), textSelection
					.getLength(), template.getName().substring(0, 1));

		} catch (BadLocationException e) {
			endCompoundChange(contextViewer);
			return;
		}

		Region region = new Region(textSelection.getOffset() + 1, 0);

		contextViewer.getSelectionProvider().setSelection(
				new TextSelection(textSelection.getOffset(), 1));

		DocumentTemplateContext context = getContext(document, template,
				textSelection.getOffset(), textSelection.getLength());

		context.setVariable("selection", savedText); //$NON-NLS-1$

		if (context.getKey().length() == 0) {
			try {
				document.replace(textSelection.getOffset(), 1, savedText);
			} catch (BadLocationException e) {
				endCompoundChange(contextViewer);
				return;
			}
		}

		ITextViewer viewer = fTextEditor.getTextViewer();
		int offset = viewer.getTextWidget().getCaretOffset();
		int startLength = offset - region.getOffset();
		String pattern = template.getPattern().replace("${cursor}", ""); //$NON-NLS-1$ //$NON-NLS-2$
		CustomCompletionProposal proposal = new CustomCompletionProposal(
				pattern, offset, 0, startLength + pattern.length(),
				getImage(template), template.getName(), null, null, 0);

		fTextEditor.getSite().getPage().activate(fTextEditor);
		proposal.apply(fTextEditor.getTextViewer(), ' ', 0, offset);
		viewer.getTextWidget().setCaretOffset(offset + pattern.length() - 1);
		endCompoundChange(contextViewer);
	}

	@Override
	protected boolean isValidTemplate(IDocument document, Template template,
			int offset, int length) {

		String[] contextIds = getContextTypeIds(document, offset);

		for (int i = 0; i < contextIds.length; i++) {
			if (contextIds[i].equals(template.getContextTypeId())) {
				DocumentTemplateContext context = getContext(document,
						template, offset, length);
				return context.canEvaluate(template);
			}
		}

		return false;
	}

	/**
	 * Undomanager - end compound change
	 * 
	 * @param viewer
	 */

	private void endCompoundChange(ISourceViewer viewer) {

		if (viewer instanceof ITextViewerExtension)
			((ITextViewerExtension) viewer).getRewriteTarget()
					.endCompoundChange();
	}

	/**
	 * Undomanager - begin a compound change
	 * 
	 * @param viewer
	 */

	private void beginCompoundChange(ISourceViewer viewer) {

		if (viewer instanceof ITextViewerExtension)
			((ITextViewerExtension) viewer).getRewriteTarget()
					.beginCompoundChange();
	}

	/**
	 * Get context
	 * 
	 * @param document
	 * @param template
	 * @param offset
	 * @param length
	 * @return the context
	 */

	private DocumentTemplateContext getContext(IDocument document,
			Template template, final int offset, int length) {

		TemplateContextType contextType = XPathUIPlugin.getDefault()
				.getXPathTemplateContextRegistry().getContextType(
						template.getContextTypeId());

		return new DocumentTemplateContext(contextType, document, offset,
				length);
	}

	/**
	 * Get the xml identifier terminated at the given offset
	 * 
	 * @param document
	 * @param template
	 * @param offset
	 * @param length
	 * @return the identifier part
	 * @throws BadLocationException
	 */

	private String getIdentifierPart(IDocument document, Template template,
			int offset, int length) throws BadLocationException {
		return getContext(document, template, offset, length).getKey();
	}
}